# Mini Expense Manager

A full-stack Expense Manager application to track daily expenses with automatic categorization, CSV import, anomaly detection, and dashboard analytics.

## Tech Stack

- Frontend: React 18 + TypeScript + Vite + Axios
- Backend: Java 17 + Spring Boot 3 + Spring Data JPA
- Database: PostgreSQL
- Build Tool: Maven

---

## Features

### Add Expense
- Add expenses manually
- Fields:
  - Date
  - Amount
  - Vendor
  - Description

### Automatic Categorization
Expenses are categorized automatically based on vendor keywords.

Examples:

| Vendor | Category |
|---------|----------|
| Swiggy | Food |
| Zomato | Food |
| Uber | Transport |
| Ola | Transport |
| Amazon | Shopping |
| Apple Store | Shopping |
| Myntra | Shopping |
| Netflix | Entertainment |
| Airtel | Utilities |
| Apollo Pharmacy | Health |

Vendor-category rules are stored in the database and can be managed through REST APIs.

---

### CSV Upload

Upload expenses in bulk.

Supported columns

```text
date,amount,vendor,description
```

Supported date formats

- yyyy-MM-dd
- dd/MM/yyyy
- MM/dd/yyyy
- dd-MM-yyyy

---

### Dashboard

Displays

- Monthly expense totals by category
- Top 5 vendors
- Total anomaly count

---

### Anomaly Detection

Expenses greater than **3× the average amount of their category** are automatically flagged.

Flagged expenses

- appear with a red **ANOMALY** badge
- are included in dashboard anomaly count

---

## Project Structure

```text
expense-manager/
│
├── backend/
│   ├── controller/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── resources/
│
├── frontend/
│   ├── src/
│   ├── components/
│   ├── services/
│   └── App.tsx
│
├── docker-compose.yml
├── sample-expenses.csv
└── README.md
```

---

# Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+
- npm
- PostgreSQL 14+

---

# Backend Setup

Go to backend

```bash
cd backend
```

Run

```bash
mvn spring-boot:run
```

Backend starts at

```
http://localhost:8080
```

---

# Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend

```
http://localhost:5173
```

---

# Database Configuration

Default configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/expense_manager
    username: postgres
    password: postgres
```

---

# Sample CSV

Upload

```
sample-expenses.csv
```

The sample file contains multiple expenses including a high-value **Apple Store** transaction that is automatically detected as an anomaly.

---

# REST APIs

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | /api/expenses | Get all expenses |
| POST | /api/expenses | Add expense |
| DELETE | /api/expenses/{id} | Delete expense |
| POST | /api/expenses/upload | Upload CSV |
| GET | /api/expenses/dashboard | Dashboard summary |
| GET | /api/rules | Vendor rules |
| POST | /api/rules | Add vendor rule |
| DELETE | /api/rules/{id} | Delete vendor rule |

---

# Design Highlights

- Rule-based automatic expense categorization
- Dashboard analytics
- CSV bulk upload
- Bean Validation
- OpenCSV parsing
- PostgreSQL persistence
- RESTful APIs
- Layered architecture (Controller → Service → Repository)
- Spring Data JPA
- Automatic anomaly recomputation after inserts/imports

---

# Build

Frontend

```bash
cd frontend
npm run build
```

Backend

```bash
cd backend
mvn clean package
```

Run Jar

```bash
java -jar target/expense-manager-0.0.1-SNAPSHOT.jar
```

---

## Future Improvements

- User authentication
- Monthly charts
- Edit expense
- Advanced anomaly detection using historical data
- Export reports to PDF/Excel