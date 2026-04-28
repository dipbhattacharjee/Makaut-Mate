const http = require('http');

const postData = JSON.stringify({
    username: '',
    password: '',
    semester: '' // optional: 'first', 'second', 'third', etc.
});

const options = {
    hostname: 'localhost',
    port: 3000,
    path: '/api/student/grade-card',
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(postData)
    }
};

const req = http.request(options, (res) => {
    let data = '';
    res.on('data', (chunk) => { data += chunk; });
    res.on('end', () => {
        console.log('Status:', res.statusCode);
        try {
            const json = JSON.parse(data);
            console.log('Response:');
            // Truncate pdfBase64 for readability
            const display = JSON.parse(JSON.stringify(json));
            if (display.data && display.data.pdfBase64) {
                display.data.pdfBase64 = display.data.pdfBase64.substring(0, 100) + '... (truncated)';
                display.data.downloadUrl = display.data.downloadUrl.substring(0, 100) + '... (truncated)';
            }
            console.log(JSON.stringify(display, null, 2));
        } catch (e) {
            console.log('Raw:', data.substring(0, 500));
        }
    });
});

req.on('error', (e) => console.error('Error:', e.message));
req.write(postData);
req.end();

