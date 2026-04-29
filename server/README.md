# 📢 MAKAUT APIs Server ![npm](https://img.shields.io/npm/v/server?color=green) ![Node](https://img.shields.io/badge/node-%5E18-green.svg) ![License](https://img.shields.io/github/license/dbaidya811/makaut-api-server)

A **Node.js/Express** backend API that provides MAKAUT (Maulana Abul Kalam Azad University of Technology) services:

1. **Notice Scraper** — Scrapes latest notices from [MAKAUT website](https://makautwb.ac.in/page.php?id=340)
2. **Student Marks** — Retrieves semester marks via student portal login
3. **Student Activity** — Gets activity records & grade card links
4. **Grade Card PDF** — Downloads grade card PDFs as base64

Perfect for mobile apps, websites, or dashboards.

---

## 🚀 Features

- ✅ Real-time notice scraping with full URLs
- ✅ Secure login to `makaut1.ucanapply.com` student portal
- ✅ Parses HTML tables into clean JSON
- ✅ Grade card PDF download (base64 for easy frontend display)
- ✅ **CORS enabled** — Works from anywhere
- ✅ SSL issues auto-handled
- ✅ **Styled console output** with timing & progress
- ✅ **Docker support** for production deployment
- ✅ Lightweight (~50MB with deps), fast responses

---

## 📦 Installation & Run

### Local (Node.js 18+)

```bash
npm install
npm start
# or npm run dev
```

Server runs on `http://localhost:3000`

### Docker

```bash
docker build -t makaut-api .
docker run -p 3000:3000 makaut-api
```

---

## 📖 Usage Examples

### 1. Get Notices (No auth)

```bash
curl http://localhost:3000/api/notices
```

**Response**:
```json
{
  "success": true,
  "notices": [{"id":1, "title": "Notice Title", "link": "url", "fullNoticeUrl": "full url"}]
}
```

### 2. Student Marks

```bash
curl -X POST http://localhost:3000/api/student/marks \
  -H "Content-Type: application/json" \
  -d '{"username":"your_roll","password":"your_pass"}'
```

### 3. Student Activity & Grade Cards

```bash
curl -X POST http://localhost:3000/api/student/activity \
  -H "Content-Type: application/json" \
  -d '{"username":"your_roll","password":"your_pass"}'
```

### 4. Grade Card PDF (optional semester filter)

```bash
curl -X POST http://localhost:3000/api/student/grade-card \
  -H "Content-Type: application/json" \
  -d '{"username":"your_roll","password":"your_pass","semester":"6"}'
```

**Response**: `{pdfBase64: "...", downloadUrl: "data:application/pdf;base64,..."}`

**Root endpoint** `/` shows all available APIs.

---

## 🧪 Development & Testing

- **Test files**: `test.js`, `test-marks.js`, `test-activity.js`, `test-gradecard.js`
- **Debug helpers**: `debug-*.js`

```bash
node test.js  # Run tests
node server.js  # Server with logs
```

---

## 🔒 Security

- Credentials sent via HTTPS POST only (use HTTPS in production)
- No credentials logged
- Rate limiting recommended for public deployment
- Use environment `PORT` var

---

## 🛠 Troubleshooting

- **SSL errors**: Auto-handled, but check Node HTTPS agent
- **Login fails**: Verify rollno/password format, portal downtime
- **No marks/activity**: Empty semesters or access issues
- **Docker**: Ensure `docker build` succeeds (Playwright may need extra setup)

---

## 📄 License

ISC License - see [LICENSE](LICENSE)
