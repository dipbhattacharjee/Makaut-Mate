const express = require('express');
const axios = require('axios');
const cheerio = require('cheerio');
const https = require('https');
const cors = require('cors');

const app = express();

const httpsAgent = new https.Agent({ rejectUnauthorized: false });

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors({ origin: '*', methods: ['GET', 'POST', 'OPTIONS'], allowedHeaders: ['Content-Type', 'Authorization'] }));

const PORT = process.env.PORT || 3000;
const NOTICE_URL = 'https://makautwb.ac.in/page.php?id=340';
const PORTAL_BASE = 'https://makaut1.ucanapply.com/smartexam/public';

// ============================================================
// ANSI COLOR UTILITIES
// ============================================================
const RESET = '\x1b[0m';
const BOLD = '\x1b[1m';
const DIM = '\x1b[2m';
const GREEN = '\x1b[32m';
const CYAN = '\x1b[36m';
const YELLOW = '\x1b[33m';
const RED = '\x1b[31m';
const MAGENTA = '\x1b[35m';
const BLUE = '\x1b[34m';
const WHITE = '\x1b[37m';

// ============================================================
// LOGGER HELPERS
// ============================================================
function logInfo(msg) { console.log(`${CYAN}ℹ${RESET}  ${msg}`); }
function logSuccess(msg) { console.log(`${GREEN}✓${RESET}  ${msg}`); }
function logError(msg) { console.log(`${RED}✗${RESET}  ${msg}`); }
function logWarn(msg) { console.log(`${YELLOW}⚠${RESET}  ${msg}`); }

// ============================================================
// OPERATION TRACKER — shows current op, time, duration per step
// ============================================================
class OperationTracker {
    constructor(name) {
        this.name = name;
        this.startTime = Date.now();
        this.startTimeStr = new Date().toLocaleTimeString('en-GB');
        console.log(`\n${CYAN}┌──────────────────────────────────────────────────────────┐${RESET}`);
        console.log(`${CYAN}│${RESET} ${BOLD}▶ Operation:${RESET} ${YELLOW}${name}${RESET}`);
        console.log(`${CYAN}│${RESET} ${DIM}  Started at: ${this.startTimeStr}${RESET}`);
        console.log(`${CYAN}└──────────────────────────────────────────────────────────┘${RESET}`);
    }

    step(num, desc) {
        const now = new Date().toLocaleTimeString('en-GB');
        console.log(`${BLUE}  → Step ${num}:${RESET} ${desc} ${DIM}(${now})${RESET}`);
    }

    log(msg) {
        console.log(`${MAGENTA}  •${RESET} ${msg}`);
    }

    success(msg) {
        const elapsed = ((Date.now() - this.startTime) / 1000).toFixed(2);
        console.log(`${GREEN}  ✓${RESET} ${BOLD}${msg}${RESET} ${DIM}[${elapsed}s]${RESET}`);
    }

    error(msg) {
        const elapsed = ((Date.now() - this.startTime) / 1000).toFixed(2);
        console.log(`${RED}  ✗${RESET} ${BOLD}${msg}${RESET} ${DIM}[${elapsed}s]${RESET}`);
    }

    end() {
        const elapsed = ((Date.now() - this.startTime) / 1000).toFixed(2);
        console.log(`${CYAN}  ⏹ Completed in ${YELLOW}${elapsed}s${RESET}\n`);
    }
}

// ============================================================
// NOTICE API
// ============================================================
app.get('/api/notices', async (req, res) => {
    const tracker = new OperationTracker('Get Notices');
    try {
        tracker.step(1, 'Fetching notices from MAKAUT website...');
        const { data: html } = await axios.get(NOTICE_URL, { httpsAgent, timeout: 15000, headers: { 'User-Agent': 'Mozilla/5.0' } });

        tracker.step(2, 'Parsing HTML content...');
        const $ = cheerio.load(html);
        const notices = [];
        $('a').each((i, el) => {
            const $el = $(el);
            const rawLink = $el.attr('href') || '';
            const title = $el.text().trim();
            if (!title || title.length < 10) return;
            let link = rawLink;
            if (link && !link.startsWith('http')) {
                link = link.startsWith('/') ? `https://makautwb.ac.in${link}` : `https://makautwb.ac.in/${link}`;
            }
            notices.push({ id: i + 1, title, link, fullNoticeUrl: link });
        });

        tracker.success(`Retrieved ${notices.length} notices`);
        tracker.end();
        res.json({ success: true, source: NOTICE_URL, totalNotices: notices.length, notices });
    } catch (error) {
        tracker.error(`Failed to fetch notices: ${error.message}`);
        tracker.end();
        res.status(500).json({ success: false, error: 'Failed to fetch notices', message: error.message });
    }
});

// ============================================================
// MARKS API HELPERS
// ============================================================
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

function mergeCookies(...maps) {
    const merged = {};
    maps.forEach(m => Object.assign(merged, m));
    return merged;
}

function cookieStr(map) {
    return Object.entries(map).map(([k, v]) => `${k}=${v}`).join('; ');
}

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

// ============================================================
// SHARED LOGIN HELPER (with internal logging)
// ============================================================
async function performLogin(username, password) {
    logInfo(`[Login Helper] Starting login for ${username}`);
    const cookies1 = await getPortalCookies();
    logSuccess(`[Login Helper] Got initial cookies: ${Object.keys(cookies1).join(', ') || 'none'}`);

    const login = await doLogin(username, password, cookies1);
    logInfo(`[Login Helper] Login status: ${login.status}`);

    if (login.status !== 200 || !login.data.status) {
        throw new Error(`Login failed: ${JSON.stringify(login.data)}`);
    }

    const cookies2 = mergeCookies(cookies1, login.cookies);
    logSuccess(`[Login Helper] Merged cookies: ${Object.keys(cookies2).join(', ') || 'none'}`);

    const dash = await axios.get(`${PORTAL_BASE}/student/dashboard`, {
        httpsAgent, timeout: 30000, maxRedirects: 10,
        headers: {
            'Cookie': cookieStr(cookies2), 'Referer': `${PORTAL_BASE}/`,
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
    });
    logSuccess(`[Login Helper] Dashboard loaded: ${dash.status}`);

    const cookies3 = mergeCookies(cookies2, extractCookies(dash));
    logSuccess(`[Login Helper] Final cookies: ${Object.keys(cookies3).join(', ') || 'none'}`);
    return cookies3;
}

// ============================================================
// MARKS API
// ============================================================
app.post('/api/student/marks', async (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) {
        return res.status(400).json({ success: false, error: 'Username and password required' });
    }

    const tracker = new OperationTracker(`Get Marks [${username}]`);
    try {
        tracker.step(1, 'Performing login...');
        const cookies = await performLogin(username, password);

        tracker.step(2, 'Fetching marks page...');
        const marksRes = await axios.get(`${PORTAL_BASE}/student/student-marks-display`, {
            httpsAgent, timeout: 30000, maxRedirects: 10,
            headers: {
                'Cookie': cookieStr(cookies), 'Referer': `${PORTAL_BASE}/student/dashboard`,
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            }
        });
        tracker.log(`Marks page status: ${marksRes.status}`);

        tracker.step(3, 'Parsing marks tables...');
        const html = marksRes.data;
        const $ = cheerio.load(html);
        const marksData = [];
        $('table').each((idx, table) => {
            const $t = $(table);
            const headers = [];
            $t.find('thead th, tr:first-child th, tr:first-child td').each((i, th) => {
                headers.push($(th).text().trim());
            });
            const rows = [];
            $t.find('tbody tr, tr:not(:first-child)').each((i, tr) => {
                const row = {};
                $(tr).find('td').each((j, td) => {
                    const key = headers[j] || `col${j}`;
                    row[key] = $(td).text().trim();
                });
                if (Object.keys(row).length > 0) rows.push(row);
            });
            if (rows.length > 0) marksData.push({ tableIndex: idx + 1, headers, rows });
        });

        const studentName = $('.student-name, .profile-name, h4:contains("Welcome")').first().text().trim();
        const rollNo = $('td:contains("Roll"), td:contains(" roll")').first().next().text().trim();

        tracker.success(`Retrieved ${marksData.length} mark table(s)`);
        tracker.end();
        res.json({
            success: true,
            message: marksData.length > 0 ? 'Marks retrieved successfully' : 'No marks tables found',
            data: { studentInfo: { username, name: studentName || null, rollNo: rollNo || null }, marksTables: marksData }
        });
    } catch (error) {
        tracker.error(`Failed: ${error.message}`);
        tracker.end();
        res.status(500).json({ success: false, error: 'Failed to fetch marks', message: error.message });
    }
});

app.get('/api/student/marks', (req, res) => {
    res.json({ message: 'POST to this endpoint with {username, password}' });
});

// ============================================================
// STUDENT ACTIVITY API
// ============================================================
app.post('/api/student/activity', async (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) {
        return res.status(400).json({ success: false, error: 'Username and password required' });
    }

    const tracker = new OperationTracker(`Get Activity [${username}]`);
    try {
        tracker.step(1, 'Performing login...');
        const cookies = await performLogin(username, password);

        tracker.step(2, 'Fetching activity page...');
        const activityRes = await axios.get(`${PORTAL_BASE}/student/student-activity`, {
            httpsAgent, timeout: 30000, maxRedirects: 10,
            headers: {
                'Cookie': cookieStr(cookies), 'Referer': `${PORTAL_BASE}/student/dashboard`,
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            }
        });
        tracker.log(`Activity page status: ${activityRes.status}`);

        tracker.step(3, 'Parsing activity data...');
        const html = activityRes.data;
        const $ = cheerio.load(html);

        const studentName = $('.student-name, .profile-name, h4:contains("Welcome")').first().text().trim();
        const rollNo = $('td:contains("Roll"), td:contains(" roll")').first().next().text().trim();

        let gradeCardUrl = null;
        let allGradeCardLinks = [];
        const gradeCardKeywords = ['grade card', 'gradecard', 'grade-card'];

        $('td').each((i, td) => {
            const $td = $(td);
            const cellText = $td.text().toLowerCase();
            if (gradeCardKeywords.some(k => cellText.includes(k))) {
                const $form = $td.find('form');
                if ($form.length > 0) {
                    let url = $form.attr('action') || '';
                    if (url && !url.startsWith('http')) {
                        url = url.startsWith('/') ? `${PORTAL_BASE}${url}` : `${PORTAL_BASE}/${url}`;
                    }
                    if (url) {
                        gradeCardUrl = url;
                        const formData = {};
                        $form.find('input[type="hidden"]').each((j, input) => {
                            const name = $(input).attr('name');
                            const value = $(input).attr('value');
                            if (name) formData[name] = value;
                        });
                        allGradeCardLinks.push({
                            text: $td.text().trim(),
                            url: url,
                            method: $form.attr('method') || 'POST',
                            formData: formData
                        });
                    }
                }
            }
        });

        const activityData = [];
        $('table').each((idx, table) => {
            const $t = $(table);
            const headers = [];
            $t.find('thead th, tr:first-child th, tr:first-child td').each((i, th) => {
                headers.push($(th).text().trim());
            });
            const rows = [];
            $t.find('tbody tr, tr:not(:first-child)').each((i, tr) => {
                const row = {};
                $(tr).find('td').each((j, td) => {
                    const key = headers[j] || `col${j}`;
                    row[key] = $(td).text().trim();
                });
                if (Object.keys(row).length > 0) rows.push(row);
            });
            if (rows.length > 0) activityData.push({ tableIndex: idx + 1, headers, rows });
        });

        tracker.success(`Retrieved ${activityData.length} activity table(s), ${allGradeCardLinks.length} grade card link(s)`);
        tracker.end();
        res.json({
            success: true,
            message: 'Student activity retrieved',
            data: {
                studentInfo: { username, name: studentName || null, rollNo: rollNo || null },
                gradeCardUrl: gradeCardUrl,
                allGradeCardLinks: allGradeCardLinks,
                activityTables: activityData
            }
        });
    } catch (error) {
        tracker.error(`Failed: ${error.message}`);
        tracker.end();
        res.status(500).json({ success: false, error: 'Failed to fetch student activity', message: error.message });
    }
});

app.get('/api/student/activity', (req, res) => {
    res.json({ message: 'POST to this endpoint with {username, password}' });
});

// ============================================================
// GRADE CARD PDF API
// ============================================================
app.post('/api/student/grade-card', async (req, res) => {
    const { username, password, semester } = req.body;
    if (!username || !password) {
        return res.status(400).json({ success: false, error: 'Username and password required' });
    }

    const tracker = new OperationTracker(`Get Grade Card [${username}]`);
    try {
        tracker.step(1, 'Performing login...');
        const cookies = await performLogin(username, password);

        tracker.step(2, 'Fetching activity page to find grade card forms...');
        const activityRes = await axios.get(`${PORTAL_BASE}/student/student-activity`, {
            httpsAgent, timeout: 30000, maxRedirects: 10,
            headers: {
                'Cookie': cookieStr(cookies), 'Referer': `${PORTAL_BASE}/student/dashboard`,
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            }
        });

        tracker.step(3, 'Parsing grade card forms...');
        const $ = cheerio.load(activityRes.data);
        const gradeCardForms = [];
        $('tr').each((i, tr) => {
            const $tr = $(tr);
            const rowText = $tr.text();
            if (rowText.toLowerCase().includes('grade card')) {
                const semesterText = $tr.find('td').eq(1).text().trim();
                const pubDate = $tr.find('td').eq(2).text().trim();
                const $form = $tr.find('form');
                if ($form.length > 0) {
                    const formData = {};
                    $form.find('input[type="hidden"]').each((j, input) => {
                        const name = $(input).attr('name');
                        const value = $(input).attr('value');
                        if (name) formData[name] = value;
                    });
                    let url = $form.attr('action') || '';
                    if (url && !url.startsWith('http')) {
                        url = url.startsWith('/') ? `${PORTAL_BASE}${url}` : `${PORTAL_BASE}/${url}`;
                    }
                    gradeCardForms.push({ semesterText, pubDate, url, formData });
                }
            }
        });

        if (gradeCardForms.length === 0) {
            tracker.error('No grade cards found');
            tracker.end();
            return res.status(404).json({ success: false, error: 'No grade cards found' });
        }
        tracker.log(`Found ${gradeCardForms.length} grade card form(s)`);

        let selectedForm;
        if (semester) {
            const semStr = String(semester).toLowerCase();
            selectedForm = gradeCardForms.find(f => f.semesterText.toLowerCase().includes(semStr));
        }
        if (!selectedForm) {
            selectedForm = gradeCardForms[0];
        }
        tracker.log(`Selected semester: ${selectedForm.semesterText}`);

        tracker.step(4, 'Downloading grade card PDF...');
        const formBody = Object.entries(selectedForm.formData)
            .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
            .join('&');

        const gradeRes = await axios.post(selectedForm.url, formBody, {
            httpsAgent, timeout: 30000, maxRedirects: 10, responseType: 'arraybuffer',
            headers: {
                'Cookie': cookieStr(cookies),
                'Referer': `${PORTAL_BASE}/student/student-activity`,
                'Content-Type': 'application/x-www-form-urlencoded',
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            }
        });

        const contentType = gradeRes.headers['content-type'] || 'application/pdf';
        const pdfBase64 = Buffer.from(gradeRes.data).toString('base64');

        tracker.success(`Grade card downloaded (${gradeRes.data.length} bytes)`);
        tracker.end();
        res.json({
            success: true,
            message: 'Grade card PDF retrieved',
            data: {
                studentInfo: { username },
                semester: selectedForm.semesterText,
                publicationDate: selectedForm.pubDate,
                contentType: contentType,
                pdfSizeBytes: gradeRes.data.length,
                pdfBase64: pdfBase64,
                downloadUrl: `data:${contentType};base64,${pdfBase64}`
            }
        });
    } catch (error) {
        tracker.error(`Failed: ${error.message}`);
        tracker.end();
        res.status(500).json({ success: false, error: 'Failed to fetch grade card', message: error.message });
    }
});

app.get('/api/student/grade-card', (req, res) => {
    res.json({ message: 'POST to this endpoint with {username, password, semester(optional)}' });
});

// ============================================================
// HOME / STATUS CHECK
// ============================================================
app.get('/', (req, res) => {
    res.json({
        message: 'MAKAUT API Server',
        endpoints: {
            notices: 'GET /api/notices',
            marks: 'POST /api/student/marks',
            activity: 'POST /api/student/activity',
            gradeCard: 'POST /api/student/grade-card'
        }
    });
});

// ============================================================
// SERVER STARTUP — Styled Banner
// ============================================================
app.listen(PORT, () => {
    const now = new Date().toLocaleString('en-GB');
    const line = '═'.repeat(56);
    const pad = (str, len) => str + ' '.repeat(Math.max(0, len - str.length));

    console.log(`\n${CYAN}╔${line}╗${RESET}`);
    console.log(`${CYAN}║${RESET}  ${BOLD}${GREEN}MAKAUT API Server${RESET}${' '.repeat(38)}${CYAN}║${RESET}`);
    console.log(`${CYAN}╠${line}╣${RESET}`);
    console.log(`${CYAN}║${RESET}  ${BOLD}Status:${RESET}     ${GREEN}● Running${RESET}${' '.repeat(31)}${CYAN}║${RESET}`);
    console.log(`${CYAN}║${RESET}  ${BOLD}Port:${RESET}       ${YELLOW}${PORT}${RESET}${' '.repeat(49 - String(PORT).length)}${CYAN}║${RESET}`);
    console.log(`${CYAN}║${RESET}  ${BOLD}URL:${RESET}        ${WHITE}http://localhost:${PORT}${RESET}${' '.repeat(37 - String(PORT).length)}${CYAN}║${RESET}`);
    console.log(`${CYAN}║${RESET}  ${BOLD}Time:${RESET}       ${DIM}${now}${RESET}${' '.repeat(50 - now.length)}${CYAN}║${RESET}`);
    console.log(`${CYAN}╠${line}╣${RESET}`);
    console.log(`${CYAN}║${RESET}  ${BOLD}Endpoints:${RESET}${' '.repeat(44)}${CYAN}║${RESET}`);
    console.log(`${CYAN}║${RESET}    GET  /api/notices${' '.repeat(34)}${CYAN}║${RESET}`);
    console.log(`${CYAN}║${RESET}    POST /api/student/marks${' '.repeat(28)}${CYAN}║${RESET}`);
    console.log(`${CYAN}║${RESET}    POST /api/student/activity${' '.repeat(25)}${CYAN}║${RESET}`);
    console.log(`${CYAN}║${RESET}    POST /api/student/grade-card${' '.repeat(23)}${CYAN}║${RESET}`);
    console.log(`${CYAN}╚${line}╝${RESET}\n`);

    logInfo('Server ready! Waiting for requests...\n');
});
