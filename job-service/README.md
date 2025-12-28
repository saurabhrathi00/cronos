# Job Service

## Overview

The **Job Service** is responsible for accepting and managing job creation requests.  
It exposes REST APIs that allow authenticated users to create **one-time** and **recurring** jobs, validates job requests, and persists job metadata in the **Job Database**.

This service handles **job definition and scheduling metadata only**.  
Actual job execution is performed asynchronously by the Scheduler and Executor services.

---

## Responsibilities

- Expose REST APIs for job creation
- Enforce authentication and authorization
- Validate job requests based on job type
- Persist jobs in the Job Database
- Compute initial scheduling metadata (`nextRunAt`)

---

## Authentication & Authorization

### JWT Authentication

- A **JWT filter** is enabled for all job-related APIs
- Every request must include a **valid JWT token**
- Requests with invalid or missing tokens are rejected

### Scope-Based Authorization

After JWT validation, authorization is enforced at the controller level.

Required scope:


- If the token contains the required scope, the request proceeds
- Otherwise, the request is rejected with an authorization error

This ensures only authorized users can create jobs.

---

## Job Creation Flow

1. Client sends a job creation request to the Job Service
2. JWT token is validated by the JWT filter
3. Required scope (`jobs.create`) is checked at the controller level
4. Request payload is validated
5. Job metadata is persisted in the **Job Database**
6. Job status is initialized as `SCHEDULED`

---

## Supported Job Types

### ONE_TIME Jobs

ONE_TIME jobs are executed only once.

#### Execution Modes

- **IMMEDIATE**
    - `nextRunAt` is set to the current timestamp (`now()`)
- **SCHEDULED**
    - `nextRunAt` is set to the timestamp provided in the request

---

### RECURRING Jobs

- Recurring jobs are defined using a **cron expression**
- The Job Service validates the cron expression
- `nextRunAt` is calculated based on the cron schedule
- The job is persisted with an initial status of `SCHEDULED`

---

## Job Status Initialization

All newly created jobs are persisted with: 
`status = SCHEDULED`


Execution is triggered later by the Scheduler service based on `nextRunAt`.

---

## Scheduling Metadata (`nextRunAt`)

The Job Service computes the initial `nextRunAt` value as follows:

| Job Type  | Mode / Rule        | nextRunAt Calculation |
|---------|--------------------|-----------------------|
| ONE_TIME | IMMEDIATE          | Current time (`now()`) |
| ONE_TIME | SCHEDULED          | Time provided in request |
| RECURRING | Cron-based        | Computed from cron expression |

This ensures all jobs are ready for scheduling immediately after creation.

---

## Configuration Management

The Job Service externalizes all configurations for flexibility and security.

### Configuration Files

- **service.properties**
    - Application-level configuration
    - Ports, service name, feature flags
- **secrets.properties**
    - Sensitive configuration
    - Database credentials and secrets

Both files are loaded at runtime.

---

## Running the Service Locally

To run the Job Service locally, configure the following environment variable:

```bash
export SPRING_CONFIG_LOCATION=/absolute/path/to/service.properties,/absolute/path/to/secrets.properties
