# 📢 MAKAUT APIs Server

A Node.js/Express backend API that provides:
1. **Notice Scraper** — Scrapes notices from MAKAUT website
2. **Student Marks** — Logs into the MAKAUT student portal and retrieves marks data
3. **Student Activity** — Retrieves student activity records and grade card links
4. **Grade Card PDF** — Downloads grade card PDFs from the student portal

Perfect for mobile apps and websites.

---

## 🚀 Features

- ✅ Scrapes latest notices from `https://makautwb.ac.in/page.php?id=340`
- ✅ Student marks retrieval from `https://makaut1.ucanapply.com/smartexam/public/`
- ✅ Student activity & grade card link extraction
- ✅ Grade card PDF download with base64 encoding
- ✅ Returns clean JSON with notice titles, links, and full notice URLs
- ✅ **CORS enabled** — works from any mobile app, website, or frontend
- ✅ Handles SSL certificate issues automatically
- ✅ **Styled terminal output** with operation tracking and timing
- ✅ Lightweight and fast

---

## 📦 Installation

```bash
# Clone or download the project
cd server

# Install dependencies
npm install

# Start the server
npm start
```

Server will run on `http://localhost:3000`

---

## 🔌 API Endpoints

### 1. Get All Notices

```http
GET http://localhost:3000/api/notices
```

**Response:**

```json
{
  "success": true,
  "source": "https://makautwb.ac.in/page.php?id=340",
  "totalNotices": 82,
  "notices": [
    {
      "id": 1,
      "title": "Academics",
      "link": "https://makautwb.ac.in/list.php?c=Academics",
      "fullNoticeUrl": "https://makautwb.ac.in/list.php?c=Academics"
    }
  ]
}
```

### 2. Get Student Marks

```http
POST http://localhost:3000/api/student/marks
Content-Type: application/json

{
  "username": "your_student_id",
  "password": "your_password"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Marks data retrieved",
  "data": {
    "studentInfo": {
      "username": "your_student_id",
      "name": "John Doe",
      "rollNo": "123456"
    },
    "marksTables": [
      {
        "tableIndex": 1,
        "headers": ["Subject", "Marks", "Grade"],
        "rows": [
          {
            "Subject": "Mathematics",
            "Marks": "85",
            "Grade": "A"
          }
        ]
      }
    ]
  }
}
```

### 3. Get Student Activity

```http
POST http://localhost:3000/api/student/activity
Content-Type: application/json

{
  "username": "your_student_id",
  "password": "your_password"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Student activity retrieved",
  "data": {
    "studentInfo": {
      "username": "your_student_id",
      "name": "John Doe",
      "rollNo": "123456"
    },
    "gradeCardUrl": "https://makaut1.ucanapply.com/smartexam/public/...",
    "allGradeCardLinks": [
      {
        "text": "Grade Card",
        "url": "https://makaut1.ucanapply.com/smartexam/public/...",
        "method": "POST",
        "formData": { "key": "value" }
      }
    ],
    "activityTables": [
      {
        "tableIndex": 1,
        "headers": ["Semester", "Publication Date", "Action"],
        "rows": [...]
      }
    ]
  }
}
```

### 4. Get Grade Card PDF

```http
POST http://localhost:3000/api/student/grade-card
Content-Type: application/json

{
  "username": "your_student_id",
  "password": "your_password",
  "semester": "3rd" // optional — defaults to first available
}
```

**Response:**

```json
{
  "success": true,
  "message": "Grade card PDF retrieved",
  "data": {
    "studentInfo": { "username": "your_student_id" },
    "semester": "3rd Semester",
    "publicationDate": "2024-01-15",
    "contentType": "application/pdf",
    "pdfSizeBytes": 45231,
    "pdfBase64": "JVBERi0xLjQK...",
    "downloadUrl": "data:application/pdf;base64,JVBERi0xLjQK..."
  }
}
```

### 5. Home / Status Check

```http
GET http://localhost:3000/
```

---

## 🖥 Terminal Output Preview

When you start the server, you'll see a **styled startup banner**:

```
╔════════════════════════════════════════════════════════╗
║  MAKAUT API Server                                      ║
╠════════════════════════════════════════════════════════╣
║  Status:     ● Running                                  ║
║  Port:       3000                                       ║
║  URL:        http://localhost:3000                      ║
║  Time:       28/04/2026, 22:44:37                       ║
╠════════════════════════════════════════════════════════╣
║  Endpoints:                                             ║
║    GET  /api/notices                                    ║
║    POST /api/student/marks                              ║
║    POST /api/student/activity                           ║
║    POST /api/student/grade-card                         ║
╚════════════════════════════════════════════════════════╝

ℹ  Server ready! Waiting for requests...
```

When an API is called, you'll see **operation tracking** with timestamps and duration:

```
┌──────────────────────────────────────────────────────────┐
│ ▶ Operation: Get Notices
│   Started at: 22:45:11
└──────────────────────────────────────────────────────────┘
  → Step 1: Fetching notices from MAKAUT website... (22:45:11)
  → Step 2: Parsing HTML content... (22:45:11)
  ✓ Retrieved 325 notices [0.63s]
  ⏹ Completed in 0.63s
```

```
┌──────────────────────────────────────────────────────────┐
│ ▶ Operation: Get Marks [123456]
│   Started at: 22:46:02
└──────────────────────────────────────────────────────────┘
  → Step 1: Performing login... (22:46:02)
  ℹ  [Login Helper] Starting login for 123456
  ✓  [Login Helper] Got initial cookies: PHPSESSID
  ✓  [Login Helper] Merged cookies: PHPSESSID, XSRF-TOKEN
  ✓  [Login Helper] Dashboard loaded: 200
  ✓  [Login Helper] Final cookies: PHPSESSID, XSRF-TOKEN
  → Step 2: Fetching marks page... (22:46:04)
  • Marks page status: 200
  → Step 3: Parsing marks tables... (22:46:04)
  ✓ Retrieved 2 mark table(s) [2.15s]
  ⏹ Completed in 2.15s
```

---

## 🌐 CORS Support

This API has **CORS enabled** for all origins. You can call it from:

- ✅ React/Vue/Angular websites
- ✅ Android apps (Retrofit, OkHttp)
- ✅ iOS apps (URLSession, Alamofire)
- ✅ Flutter apps
- ✅ Any JavaScript frontend using `fetch` or `axios`

---

## 📱 Usage Examples

### Notices API

```javascript
// JavaScript (Fetch)
fetch('http://localhost:3000/api/notices')
  .then(res => res.json())
  .then(data => console.log(data.notices));
```

### Student Marks API

```javascript
// JavaScript (Fetch)
fetch('http://localhost:3000/api/student/marks', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'your_student_id',
    password: 'your_password'
  })
})
.then(res => res.json())
.then(data => console.log(data.data.marksTables));
```

### Student Activity API

```javascript
// JavaScript (Fetch)
fetch('http://localhost:3000/api/student/activity', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'your_student_id',
    password: 'your_password'
  })
})
.then(res => res.json())
.then(data => console.log(data.data.activityTables));
```

### Grade Card API

```javascript
// JavaScript (Fetch)
fetch('http://localhost:3000/api/student/grade-card', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'your_student_id',
    password: 'your_password',
    semester: '3rd' // optional
  })
})
.then(res => res.json())
.then(data => {
  // data.data.pdfBase64 contains the base64 PDF
  // data.data.downloadUrl is a data URI ready for download
  console.log(data.data.downloadUrl);
});
```

### Android (Java) — Notices

```java
OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder()
    .url("http://localhost:3000/api/notices")
    .build();

client.newCall(request).enqueue(new Callback() {
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String json = response.body().string();
        // Parse JSON
    }
});
```

### Android (Java) — Marks

```java
OkHttpClient client = new OkHttpClient();
MediaType JSON = MediaType.parse("application/json");
String body = "{\"username\":\"your_id\",\"password\":\"your_pass\"}";

Request request = new Request.Builder()
    .url("http://localhost:3000/api/student/marks")
    .post(RequestBody.create(JSON, body))
    .build();

client.newCall(request).enqueue(new Callback() {
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String json = response.body().string();
        // Parse JSON
    }
});
```

### Flutter / Dart

```dart
import 'package:http/http.dart' as http;
import 'dart:convert';

// Notices
final response = await http.get(Uri.parse('http://localhost:3000/api/notices'));
final data = jsonDecode(response.body);
final notices = data['notices'];

// Marks
final marksResponse = await http.post(
  Uri.parse('http://localhost:3000/api/student/marks'),
  headers: {'Content-Type': 'application/json'},
  body: jsonEncode({'username': 'your_id', 'password': 'your_pass'}),
);
final marksData = jsonDecode(marksResponse.body);

// Activity
final activityResponse = await http.post(
  Uri.parse('http://localhost:3000/api/student/activity'),
  headers: {'Content-Type': 'application/json'},
  body: jsonEncode({'username': 'your_id', 'password': 'your_pass'}),
);
final activityData = jsonDecode(activityResponse.body);

// Grade Card
final gradeResponse = await http.post(
  Uri.parse('http://localhost:3000/api/student/grade-card'),
  headers: {'Content-Type': 'application/json'},
  body: jsonEncode({'username': 'your_id', 'password': 'your_pass', 'semester': '3rd'}),
);
final gradeData = jsonDecode(gradeResponse.body);
```

---

## 📁 Project Structure

```
server/
├── server.js          # Main Express server with styled logging
├── package.json       # Dependencies
├── README.md          # This file
├── test.js            # Test script
├── test-marks.js      # Marks API test
├── test-activity.js   # Activity API test
├── test-gradecard.js  # Grade card API test
├── debug-activity.js  # Activity debug script
├── debug-gradecard.js # Grade card debug script
└── LICENSE            # License file
```

---

## 🛠 Technologies Used

- **Node.js** — Runtime
- **Express.js** — Web framework
- **Axios** — HTTP client
- **Cheerio** — Server-side HTML parsing
- **CORS** — Cross-origin resource sharing

---

## ⚠️ Notes

- The server must be running for the API to work
- If hosting online, replace `localhost:3000` with your server's IP or domain
- The API scrapes the live MAKAUT website, so response depends on their site being up
- For the marks/activity/grade-card APIs, valid student credentials are required
- Grade card PDF is returned as base64 — decode it on the client side or use the `downloadUrl`

---

## 📄 License

ISC

---

## 🤝 Developed By

Backend API for MAKAUT Notice Board & Student Portal
