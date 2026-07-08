# Mini Expense Manager

A small full-stack application to track daily expenses with automatic
rule-based categorization, CSV import, anomaly detection, and a dashboard
summary.

**Stack:** React + TypeScript · Java Spring Boot · PostgreSQL

---

## Features

- **Add expenses manually** — date, amount, vendor, description
- **Automatic categorization** — vendor keywords map to categories
  (Swiggy → Food, Uber → Transport, Amazon → Shopping, …).
  Rules are seeded on first run and can be extended via `/api/rules`.
- **CSV upload** — bulk import with per-row error reporting.
  Columns: `date, amount, vendor, description`.
- **Anomaly detection** — any expense **more than 3× the average of its
  category** is flagged. Flags are recomputed after every insert/import so
  the dataset stays consistent.
- **Dashboard** — monthly totals per category, top 5 vendors by spend,
  and total anomaly count.

---

## Project Layout

```
expense-manager/
├── backend/              Spring Boot 3 · Java 17 · JPA · OpenCSV
├── frontend/             React 18 · TypeScript · Vite · Axios
├── docker-compose.yml    PostgreSQL 16 for local dev
├── sample-expenses.csv   Example CSV (includes one anomaly row)
└── README.md
```

---

## Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **Node 18+** and npm
- **Docker** (optional — for the bundled PostgreSQL) or a running local
  PostgreSQL 14+

---

## Quick Start

### 1. Start PostgreSQL

Using Docker:

```bash
docker compose up -d
```

This starts PostgreSQL 16 on `localhost:5432` with database
`expense_manager` and credentials `postgres / postgres`.

Or point the backend at your own database by editing
`backend/src/main/resources/application.yml`.

### 2. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.

Prefer to run without PostgreSQL? Use the bundled in-memory H2 profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**. Vite proxies `/api` calls to the backend.

### 4. Try it

- Add an expense manually (the category is auto-assigned).
- Or click **Upload CSV** and pick `sample-expenses.csv` — the Amazon
  ₹15,999 row will be flagged as an anomaly.

---

## API Reference

| Method | Path                          | Purpose                              |
|--------|-------------------------------|--------------------------------------|
| GET    | `/api/expenses`               | List all expenses (newest first)     |
| POST   | `/api/expenses`               | Create an expense                    |
| DELETE | `/api/expenses/{id}`          | Delete an expense                    |
| POST   | `/api/expenses/upload`        | Upload CSV (`multipart/form-data`)   |
| GET    | `/api/expenses/dashboard`     | Aggregated dashboard summary         |
| GET    | `/api/rules`                  | List vendor → category rules         |
| POST   | `/api/rules`                  | Add a rule                           |
| DELETE | `/api/rules/{id}`             | Remove a rule                        |

**Create expense payload:**

```json
{
  "date": "2026-07-07",
  "amount": 450.00,
  "vendor": "Swiggy",
  "description": "Team lunch"
}
```

---

## Design Notes

- **Categorization rules** live in a `vendor_category_rules` table and
  are matched via case-insensitive `contains` on the vendor string.
  This keeps the mapping data-driven and editable at runtime without
  code changes.
- **Anomaly recomputation** — the naive "flag on insert" approach breaks
  down for the very first entries in a category (average = the row
  itself). After each write we recompute the category's average and
  re-flag *all* rows in that category, keeping the dataset consistent.
- **CSV parsing** uses OpenCSV with a header-index map so column order
  doesn't matter, and multiple date formats (`yyyy-MM-dd`,
  `dd/MM/yyyy`, `MM/dd/yyyy`, `dd-MM-yyyy`) are accepted.
- **Validation** — `ExpenseRequest` uses Bean Validation; a global
  exception handler returns structured 400 responses.
- **CORS** is enabled for the Vite dev server; Vite also proxies `/api`
  during development so the browser never crosses origins.

---

## Building for Production

```bash
# Frontend bundle
cd frontend
npm run build           # outputs to frontend/dist

# Backend jar
cd ../backend
mvn clean package       # outputs backend/target/expense-manager-0.0.1-SNAPSHOT.jar
java -jar target/expense-manager-0.0.1-SNAPSHOT.jar
```

You can either serve `frontend/dist` from any static host (nginx,
CloudFront, etc.) pointing at the Spring Boot API, or copy the built
assets into `backend/src/main/resources/static/` before packaging to
ship a single fat jar.
