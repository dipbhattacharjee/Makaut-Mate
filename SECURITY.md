# Security Policy

## Supported Versions

The following versions of Makaut-Mate are currently supported with security updates:

| Version | Supported          |
| ------- | ------------------ |
| latest  | ✅ Yes             |
| older   | ❌ No              |

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security issue in **Makaut-Mate**, please follow the steps below:

### 🔒 Private Disclosure (Preferred)

**Do NOT open a public GitHub issue for security vulnerabilities.**

Instead, please report them privately via one of the following methods:

- **GitHub Security Advisories**: Use the [Report a Vulnerability](../../security/advisories/new) button on this repository.
- **Email**: Send details to `bhattacharjeedip3@gmail.com` *(replace with your actual email)*

### 📋 What to Include

Please provide as much of the following information as possible:

- Type of vulnerability (e.g., SQL Injection, XSS, Auth Bypass)
- Affected endpoint or component
- Step-by-step reproduction instructions
- Proof of concept (if available)
- Potential impact assessment

### ⏱️ Response Timeline

| Stage                        | Timeframe       |
| ---------------------------- | --------------- |
| Acknowledgement of report    | Within 48 hours |
| Initial assessment           | Within 5 days   |
| Fix & patch release          | Within 30 days  |

## Scope

### In Scope
- The Makaut-Mate web service and API endpoints
- Authentication and authorization mechanisms
- Data exposure or leakage vulnerabilities
- Third-party dependencies with known CVEs

### Out of Scope
- Social engineering attacks
- Vulnerabilities in services we don't control
- Denial of Service (DoS) attacks

## Security Best Practices for Contributors

- Never commit API keys, tokens, or credentials
- Use environment variables for sensitive configuration
- Follow OWASP Top 10 guidelines when contributing code

## Disclosure Policy

We follow a **coordinated disclosure** model. Once a fix is available, we will:
1. Release a patched version
2. Publish a GitHub Security Advisory
3. Credit the reporter (if they wish)

Thank you for helping keep Makaut-Mate secure! 🙏
