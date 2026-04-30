package com.dev.makautmate.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.os.Handler
import android.os.Looper
import android.webkit.WebSettings
import com.dev.makautmate.domain.model.StudentProfile
import com.google.gson.Gson

class PortalScraper(private val context: Context) {

    private var webView: WebView? = null
    private val handler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun startScraping(roll: String, dob: String, onFinished: (Result<StudentProfile>) -> Unit) {
        handler.post {
            val jsInterface = PortalJavaScriptInterface(onFinished)

            webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                // Some portals require a mobile-like user agent to load correctly in some environments
                settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

                addJavascriptInterface(jsInterface, "PortalInterface")
                
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("PortalScraper", "Loaded URL: $url")
                        
                        when {
                            url?.contains("public/student") == true -> {
                                val loginJs = """
                                    (function() {
                                        const userField = document.getElementById('username') || document.querySelector('input[name="username"]');
                                        const passField = document.getElementById('password') || document.querySelector('input[name="password"]');
                                        const loginBtn = document.querySelector('button[type="submit"]') || document.querySelector('.btn-primary') || document.querySelector('#login-btn');
                                        
                                        if(userField && passField && loginBtn) {
                                            userField.value = '$roll';
                                            passField.value = '$dob';
                                            loginBtn.click();
                                        } else {
                                            // Fallback: try to find any input fields if IDs don't match
                                            const inputs = document.querySelectorAll('input');
                                            if(inputs.length >= 2) {
                                                inputs[0].value = '$roll';
                                                inputs[1].value = '$dob';
                                                const btn = document.querySelector('button') || document.querySelector('input[type="submit"]');
                                                if(btn) btn.click();
                                                else PortalInterface.onError("Login button not found.");
                                            } else {
                                                PortalInterface.onError("Login fields not found on this page.");
                                            }
                                        }
                                    })();
                                """.trimIndent()
                                view?.evaluateJavascript(loginJs, null)
                            }
                            url?.contains("student/dashboard") == true || url?.contains("student/home") == true || url?.contains("student/profile") == true -> {
                                injectDataExtractor(view, roll)
                            }
                            url?.contains("login") == true && !url.contains("public/student") -> {
                                jsInterface.onError("Login failed - redirected to login page")
                            }
                        }
                    }
                    
                    override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
                        super.onReceivedError(view, request, error)
                        if (request?.isForMainFrame == true) {
                            handler.post {
                                cancelTimeout()
                                onFinished(Result.failure(Exception("Network error: ${error?.description}")))
                                cleanup()
                            }
                        }
                    }
                }
                
                loadUrl("https://makaut1.ucanapply.com/smartexam/public/student")
            }

            // Set a 60 second timeout for background sync as it might be slower
            timeoutRunnable = Runnable {
                onFinished(Result.failure(Exception("Scraping timed out after 60 seconds")))
                cleanup()
            }
            handler.postDelayed(timeoutRunnable!!, 60000)
        }
    }

    private fun cancelTimeout() {
        timeoutRunnable?.let { handler.removeCallbacks(it) }
        timeoutRunnable = null
    }

    private fun injectDataExtractor(view: WebView?, roll: String) {
        val extractJs = """
            (function() {
                try {
                    const name = document.querySelector('.user-profile-name')?.innerText?.trim() || 'Student';
                    const cgpaText = document.querySelector('.cgpa-value')?.innerText || '0.0';
                    const attendanceText = document.querySelector('.attendance-percentage')?.innerText || 'N/A';
                    
                    let caMarks = {};
                    let sgpa = {};
                    const tables = document.querySelectorAll('table');
                    tables.forEach(table => {
                        const text = table.innerText;
                        if (text.includes('CA') || text.includes('Continuous Assessment')) {
                            table.querySelectorAll('tr').forEach(row => {
                                let cells = row.querySelectorAll('td');
                                if(cells.length >= 2) {
                                    let subject = cells[0].innerText.trim();
                                    let mark = parseFloat(cells[1].innerText) || 0.0;
                                    if(subject && !isNaN(mark)) caMarks[subject] = mark;
                                }
                            });
                        }
                        if (text.includes('Semester') && (text.includes('SGPA') || text.includes('GPA'))) {
                             table.querySelectorAll('tr').forEach(row => {
                                let cells = row.querySelectorAll('td');
                                if(cells.length >= 2) {
                                    let sem = cells[0].innerText.trim();
                                    let val = parseFloat(cells[1].innerText) || 0.0;
                                    if(sem && !isNaN(val)) sgpa[sem] = val;
                                }
                            });
                        }
                    });

                    const profile = {
                        name: name,
                        roll: '$roll',
                        cgpa: parseFloat(cgpaText) || 0.0,
                        sgpa: sgpa,
                        caMarks: caMarks,
                        attendance: attendanceText
                    };
                    
                    PortalInterface.onDataExtracted(JSON.stringify(profile));
                } catch(e) {
                    PortalInterface.onError("Extraction error: " + e.toString());
                }
            })();
        """.trimIndent()
        view?.evaluateJavascript(extractJs, null)
    }

    private inner class PortalJavaScriptInterface(private val onFinished: (Result<StudentProfile>) -> Unit) {
        @JavascriptInterface
        fun onDataExtracted(json: String) {
            handler.post {
                try {
                    val profile = Gson().fromJson(json, StudentProfile::class.java)
                    cancelTimeout()
                    onFinished(Result.success(profile))
                    cleanup()
                } catch (e: Exception) {
                    cancelTimeout()
                    onFinished(Result.failure(Exception("Parsing error: ${e.message}")))
                    cleanup()
                }
            }
        }

        @JavascriptInterface
        fun onError(error: String) {
            handler.post {
                cancelTimeout()
                onFinished(Result.failure(Exception(error)))
                cleanup()
            }
        }
    }

    private fun cleanup() {
        webView?.post {
            webView?.destroy()
            webView = null
        }
    }
}
