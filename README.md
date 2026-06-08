# Construction Workforce Attendance & Overtime Management System

## Overview

This project extends an existing Spring Boot HRMS application with a Workforce Attendance and Overtime Settlement Engine designed for construction industry workforce management.

The system enables site supervisors and payroll teams to:

- Track worker attendance through clock-in and clock-out operations
- Monitor active workers across construction sites in real time
- Automatically calculate overtime hours and overtime payouts
- Generate monthly overtime summaries
- Settle overtime payments for completed months
- Improve performance using Redis-based active worker caching

---

## Assignment Context

This implementation was developed as part of a Java Backend Developer Hiring Assignment focused on:

- Schema Design
- REST API Development
- Business Rule Enforcement
- Redis Caching
- Data Integrity
- Overtime Processing Logic
- Production-Oriented Backend Development

---

# Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL (Supabase Compatible)
- Redis
- Maven
- Lombok

---

# Features Implemented (Part 1)

## Worker Management

Workers contain:

- Name
- Phone Number
- Designation
- Daily Wage Rate
- Active Status

Supported Designations:

- MASON
- ELECTRICIAN
- PLUMBER
- SUPERVISOR
- HELPER

---

## Site Management

Construction site information:

- Site Name
- Location
- Active Status

---

## Attendance Management

### Clock In

Workers can clock in to an active site.

Validation Rules:

- Worker must exist
- Worker must be active
- Site must exist
- Site must be active
- Worker cannot clock in twice

---

### Clock Out

Workers can clock out from an active attendance session.

System automatically calculates:

- Total Hours Worked
- Overtime Hours
- Attendance Status

Validation Rules:

- Worker must currently be clocked in
- Attendance records exceeding 16 hours are flagged for review

---

## Active Workers Tracking

Active workers are stored in Redis.

Stored Information:

- Worker ID
- Site Information
- Clock-In Time

Benefits:

- Fast retrieval
- Reduced database load
- Real-time workforce visibility

---

## Attendance History

Supports:

- Worker-wise filtering
- Date range filtering
- Pagination

---

## Overtime Management

### Overtime Calculation Rules

Standard Shift:

- 8 Hours

Overtime:

- Any hours beyond 8 hours

Overtime Rate:

- First 2 Overtime Hours → 1.5x Wage Rate
- Additional Overtime Hours → 2x Wage Rate

---

### Monthly Overtime Cap

Maximum overtime allowed:

- 60 Hours Per Month

If a worker exceeds the cap:

- Attendance is still recorded
- Overtime entry is capped at remaining allowable hours

---

## Overtime Summary

Provides:

- Monthly Overtime Hours
- Daily Breakdown
- Total Overtime Amount
- Settlement Status

---

## Overtime Settlement

Supports settlement for completed months only.

Business Rules:

- Current month cannot be settled
- Future months cannot be settled
- Settled entries become immutable
- Settlement response includes total payout amount

---

# Database Design

## Entities

### Worker

Represents construction workers.

### Site

Represents construction project sites.

### AttendanceLog

Stores:

- Clock In Timestamp
- Clock Out Timestamp
- Total Hours Worked
- Overtime Hours
- Attendance Flag Status

### OvertimeEntry

Stores:

- Overtime Date
- Overtime Hours
- Applied Rate
- Amount
- Settlement Status

---

# Redis Caching Strategy

## Active Workers Cache

Purpose:

Provide real-time visibility of workers currently present on sites.

### Cache Population

Clock In:

- Worker added to Redis

Clock Out:

- Worker removed from Redis

### Cache Read

Active worker endpoint reads directly from Redis.

### TTL Protection

A TTL mechanism is used to prevent indefinitely active attendance records when clock-out events are missed.

---

# API Endpoints

## Attendance APIs

### Clock In

```http
POST /api/attendance/clock-in
```

Request:

```json
{
  "workerId": 1,
  "siteId": 1
}
```

---

### Clock Out

```http
POST /api/attendance/clock-out
```

Request:

```json
{
  "workerId": 1
}
```

---

### Active Workers

```http
GET /api/attendance/active
```

---

### Attendance History

```http
GET /api/attendance/log
```

Query Parameters:

```text
workerId
from
to
page
size
```

---

## Overtime APIs

### Monthly Summary

```http
GET /api/overtime/summary/{workerId}?month=YYYY-MM
```

---

### Overtime Settlement

```http
POST /api/overtime/settle/{workerId}?month=YYYY-MM
```

---

# Error Handling

Structured JSON responses are returned for validation failures.

Example:

```json
{
  "error": "DUPLICATE_CLOCK_IN",
  "message": "Worker is already clocked in",
  "timestamp": "2026-05-25T10:30:00Z"
}
```

HTTP Status Codes Used:

| Status | Purpose |
|----------|----------|
| 400 | Validation Error |
| 404 | Resource Not Found |
| 409 | Business Conflict |

---

# Setup Instructions

## Clone Repository

```bash
git clone <your-repository-url>
```

---

## Configure PostgreSQL

Update:

```properties
application.yml
```

Example:

```yaml
spring:
  datasource:
    url: YOUR_POSTGRES_URL
    username: YOUR_USERNAME
    password: YOUR_PASSWORD
```

---

## Configure Redis

Example:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

---

## Run Application

```bash
mvn clean install
mvn spring-boot:run
```

---

# Postman Collection

The Postman collection is available in:

```text
https://github.com/Ashish-S-dev/spring-boot-fullstack-professional/blob/main/Postman/Attendance-Overtime.postman_collection.json
```

Import the collection into Postman to test all APIs.

---

# Design Decisions

### Why Redis?

The active workers endpoint is expected to be accessed frequently by site supervisors.

Redis provides:

- Faster response times
- Reduced database queries
- Real-time workforce visibility

---

### Why Separate OvertimeEntry?

Separating overtime records from attendance records:

- Improves reporting
- Simplifies settlement processing
- Supports future payroll integrations

---

### Why Settlement Restrictions?

Current month settlements are blocked to:

- Prevent incomplete payroll calculations
- Ensure attendance records are finalized before payment processing

---

# AI Tools Used

The following AI tools were used during development:

- ChatGPT
  - Architecture discussions
  - Business rule validation
  - API design review
  - Redis strategy discussions

- GitHub Copilot
  - Boilerplate code generation
  - Development assistance

All implementation decisions, testing, debugging, and final integration were performed manually.

---

# Future Improvements

The assignment also included production support tickets (LF-201 to LF-205).

Given the submission timeline, the focus was placed on delivering the complete Attendance & Overtime Engine (Part 1).

Future enhancements may include:

- Advanced CORS Configuration
- Redis Failure Recovery
- Query Optimization and N+1 Prevention
- Transactional Event Processing
- Connection Pool Optimization

---

# Hand Drawn Architecture Diagram

A hand-drawn architecture diagram has been included separately as required by the assignment.

The diagram illustrates:

- Site Supervisor Flow
- Worker Attendance Flow
- Controller → Service → Repository Architecture
- Redis Integration
- PostgreSQL/Supabase Integration
- Overtime Settlement Workflow
- Payroll Process Flow

---
<img width="1280" height="933" alt="WhatsApp Image 2026-06-08 at 6 00 28 PM" src="https://github.com/user-attachments/assets/c4a2d810-7904-4247-82b4-9209dfccffc2" />


