const http = require('http');

const postData = JSON.stringify({
    username: '',
    password: ''
});

const options = {
    hostname: 'localhost',
    port: 3000,
    path: '/api/student/activity',
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
            console.log(JSON.stringify(json, null, 2));
        } catch (e) {
            console.log('Raw:', data.substring(0, 2000));
        }
    });
});

req.on('error', (e) => console.error('Error:', e.message));
req.write(postData);
req.end();
