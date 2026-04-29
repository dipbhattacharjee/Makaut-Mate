const http = require('http');

http.get('http://localhost:3000/api/notices', (res) => {
    let data = '';
    res.on('data', chunk => data += chunk);
    res.on('end', () => {
        const result = JSON.parse(data);
        console.log('API Response:');
        console.log(JSON.stringify(result, null, 2));
    });
}).on('error', (err) => {
    console.error('Error:', err.message);
});
