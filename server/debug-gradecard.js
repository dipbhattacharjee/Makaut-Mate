const axios = require('axios');
const https = require('https');
const fs = require('fs');
const cheerio = require('cheerio');

const httpsAgent = new https.Agent({ rejectUnauthorized: false });
const PORTAL_BASE = 'https://makaut1.ucanapply.com/smartexam/public';

function extractCookies(response) {
    const cookies = response.headers['set-cookie'];
    if (!cookies) return {};
    const cookieMap = {};
    cookies.forEach(cookie => {
        const [nameValue] = cookie.split(';');
        const [name, value] = nameValue.trim().split('=');
        if (name && value) cookieMap[name] = value;
    });
    return cookieMap;
}
function mergeCookies(...maps) { const merged = {}; maps.forEach(m => Object.assign(merged, m)); return merged; }
function cookieStr(map) { return Object.entries(map).map(([k, v]) => `${k}=${v}`).join('; '); }

async function getPortalCookies() {
    const res = await axios.get(`${PORTAL_BASE}/`, { httpsAgent, timeout: 30000, withCredentials: true });
    return extractCookies(res);
}

async function getLoginToken(cookies) {
    const res = await axios.get(`${PORTAL_BASE}/get-login-form?typ=5`, {
        httpsAgent, timeout: 30000,
        headers: { 'Cookie': cookieStr(cookies), 'X-Requested-With': 'XMLHttpRequest', 'Referer': `${PORTAL_BASE}/` }
    });
    if (!res.data.success) throw new Error('Failed to get login form');
    const m = res.data.html.match(/name="_token"\s+type="hidden"\s+value="([^"]+)"/);
    if (!m) throw new Error('CSRF token not found');
    return m[1];
}

async function doLogin(username, password, cookies) {
    const token = await getLoginToken(cookies);
    const res = await axios.post(`${PORTAL_BASE}/checkLogin`,
        `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}&typ=5&_token=${encodeURIComponent(token)}`,
        {
            httpsAgent, timeout: 30000, maxRedirects: 0, validateStatus: () => true,
            headers: {
                'Cookie': cookieStr(cookies), 'Content-Type': 'application/x-www-form-urlencoded',
                'Referer': `${PORTAL_BASE}/`, 'X-Requested-With': 'XMLHttpRequest', 'Origin': PORTAL_BASE
            }
        }
    );
    return { status: res.status, data: res.data, cookies: extractCookies(res) };
}

async function performLogin(username, password) {
    const cookies1 = await getPortalCookies();
    const login = await doLogin(username, password, cookies1);
    if (login.status !== 200 || !login.data.status) {
        throw new Error(`Login failed: ${JSON.stringify(login.data)}`);
    }
    const cookies2 = mergeCookies(cookies1, login.cookies);
    const dash = await axios.get(`${PORTAL_BASE}/student/dashboard`, {
        httpsAgent, timeout: 30000, maxRedirects: 10,
        headers: {
            'Cookie': cookieStr(cookies2), 'Referer': `${PORTAL_BASE}/`,
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
    });
    const cookies3 = mergeCookies(cookies2, extractCookies(dash));
    return cookies3;
}

(async () => {
    const cookies = await performLogin('23642724005', '04092006');
    
    // Get activity page to extract form URL
    const activityRes = await axios.get(`${PORTAL_BASE}/student/student-activity`, {
        httpsAgent, timeout: 30000, maxRedirects: 10,
        headers: {
            'Cookie': cookieStr(cookies), 'Referer': `${PORTAL_BASE}/student/dashboard`,
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
    });
    
    const $ = cheerio.load(activityRes.data);
    let formUrl = null;
    let formData = {};
    
    $('td').each((i, td) => {
        const $td = $(td);
        if ($td.text().toLowerCase().includes('grade card')) {
            const $form = $td.find('form');
            if ($form.length > 0 && !formUrl) {
                formUrl = $form.attr('action');
                $form.find('input[type="hidden"]').each((j, input) => {
                    const name = $(input).attr('name');
                    const value = $(input).attr('value');
                    if (name) formData[name] = value;
                });
                return false; // break after first
            }
        }
    });
    
    console.log('Form URL:', formUrl);
    console.log('Form Data:', formData);
    
    if (!formUrl) {
        console.log('No grade card form found');
        return;
    }
    
    // POST to grade card URL
    const gradeRes = await axios.post(formUrl,
        `_token=${encodeURIComponent(formData._token)}&rollno=${encodeURIComponent(formData.rollno)}&provisional=${encodeURIComponent(formData.provisional)}`,
        {
            httpsAgent, timeout: 30000, maxRedirects: 10,
            responseType: 'arraybuffer',
            headers: {
                'Cookie': cookieStr(cookies),
                'Referer': `${PORTAL_BASE}/student/student-activity`,
                'Content-Type': 'application/x-www-form-urlencoded',
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            }
        }
    );
    
    console.log('Grade card status:', gradeRes.status);
    console.log('Content-Type:', gradeRes.headers['content-type']);
    console.log('Content-Length:', gradeRes.data.length);
    
    const contentType = gradeRes.headers['content-type'] || '';
    
    if (contentType.includes('pdf')) {
        fs.writeFileSync('grade-card.pdf', gradeRes.data);
        console.log('PDF saved to grade-card.pdf');
    } else if (contentType.includes('html')) {
        const html = Buffer.from(gradeRes.data).toString('utf-8');
        fs.writeFileSync('grade-card.html', html);
        console.log('HTML saved to grade-card.html');
        console.log('HTML preview:', html.substring(0, 500));
    } else {
        fs.writeFileSync('grade-card.bin', gradeRes.data);
        console.log('Binary saved to grade-card.bin');
        console.log('First 200 chars as text:', Buffer.from(gradeRes.data).toString('utf-8').substring(0, 200));
    }
})();

