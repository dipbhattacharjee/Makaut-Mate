# 🐘 PostgreSQL Setup Guide for MAKAUT Mate

Follow these steps to get your backend database live.

## 1. Install PostgreSQL
1. Download **PostgreSQL 15+** for Windows: [Download Link](https://www.postgresql.org/download/windows/)
2. Run the installer.
3. **Important**: Remember the password you set for the `postgres` user during installation.
4. Keep the default port as `5432`.

## 2. Create the Database
Open **pgAdmin 4** (installed with PostgreSQL) or use the command line:

```sql
CREATE DATABASE makaut_mate;
```

## 3. Initialize Schema
Run the following command in your terminal from the `server` directory:

```bash
psql -U postgres -d makaut_mate -f schema.sql
```
*(You will be prompted for the password you set in Step 1)*

## 4. Configure Environment
1. Rename `.env.example` to `.env`.
2. Update `DB_PASSWORD` with your PostgreSQL password.

## 5. Start the Server
```bash
npm install
npm start
```

Your backend is now ready to serve the Android app! 🚀
