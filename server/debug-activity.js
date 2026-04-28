const axios = require('axios');
const cheerio = require('cheerio');
const https = require('https');
const fs = require('fs');

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
    const activityRes = await axios.get(`${PORTAL_BASE}/student/student-activity`, {
        httpsAgent, timeout: 30000, maxRedirects: 10,
        headers: {
            'Cookie': cookieStr(cookies), 'Referer': `${PORTAL_BASE}/student/dashboard`,
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
    });
    const html = activityRes.data;
    fs.writeFileSync('debug-activity.html', html);
    console.log('Raw HTML saved to debug-activity.html');
    console.log('HTML length:', html.length);

    const $ = cheerio.load(html);
    
    // Find ALL table cells and their HTML
    console.log('\n--- All table cells containing "Grade Card" ---');
    $('td').each((i, td) => {
        const $td = $(td);
        const text = $td.text().trim();
        if (text.toLowerCase().includes('grade card')) {
            console.log(`\nCell #${i}:`, text);
            console.log('HTML:', $td.html());
        }
    });

    // Find ALL links/buttons in the page
    console.log('\n--- All links in the page ---');
    $('a, button').each((i, el) => {
        const $el = $(el);
        const text = $el.text().trim();
        if (text.length > 0) {
            console.log(`Link #${i}: text="${text}" href="${$el.attr('href') || ''}" onclick="${$el.attr('onclick') || ''}"`);
        }
    });

})();

