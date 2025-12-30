# Cronos – Distributed Job Scheduler System

Cronos is a **distributed, fault-tolerant job scheduling system** built using **Spring Boot, PostgreSQL, and Apache Kafka**.  
The entire system is fully **Dockerized** and can be started using a **single docker-compose command**.

---

## System Overview

The system supports:
- One-time jobs (immediate & scheduled)
- Recurring jobs (cron-based)
- Horizontal scalability
- Reliable execution using **DB + Kafka**
- Clear separation of responsibilities across services

### Core Services
- **Auth Service** – Authentication & JWT issuance
- **User Service** – User registration, roles & scopes
- **Job Service** – Job creation & management APIs
- **Scheduler Service** – Polls DB & dispatches jobs to Kafka
- **Executor Service** – Kafka consumer that executes jobs
- **PostgreSQL** – Source of truth for jobs & users
- **Kafka (KRaft mode)** – Event delivery backbone
- **AKHQ** – Kafka UI (monitoring & inspection)

---

## 🚀 How to Run the System (Single Command)

### Prerequisites
- Docker
- Docker Compose

> ⚠️ No need to install Java, Kafka, or PostgreSQL locally.

---

### Step 1️⃣ Clone the repository
```bash
git clone <your-github-repo-url>
cd cronos
```
Step 2️⃣ Start the entire system
```bash
docker-compose up --build
```

That’s it.
No manual steps. No additional commands.


#What Happens Automatically
When docker-compose up is executed:
1. PostgreSQL starts and initializes:
   1. userDb 
   2. jobs database 
2. Kafka (KRaft mode) starts with health checks 
3. Kafka topic bootstrap container creates required topics:
   1. job-events 
4. All application services start only after dependencies are healthy 
5. Scheduler begins polling jobs 
6. Executor starts consuming Kafka events

#Verifying the Setup
Check running containers
```bash
docker-compose ps
```


#Kafka UI (AKHQ)
To see the kafka topic on local we are making use of AKHQ(Kafka UI)

Open in browser:
```url
http://localhost:8085
```

You can:
1. Inspect topics 
2. View messages 
3. Monitor consumer groups


#Authentication Flow
1. User signs up via User Service 
2. Default role assigned: ROLE_USER 
3. Role scopes:
   1. jobs.create
   2. jobs.read
4. User signs in via Auth Service 
5. JWT issued 
6. JWT required for job creation APIs

#Job Scheduling Flow
**_Job Creation_** 
1. API exposed by Job Service 
2. Supports:
   1. ONE_TIME (IMMEDIATE / SCHEDULED)
   2. RECURRING (cron-based)
3. Job stored in PostgreSQL with SCHEDULED status

**_Scheduling_**
1. Scheduler polls DB for due jobs 
2. Uses SELECT … FOR UPDATE SKIP LOCKED 
3. Marks jobs as RUNNING 
4. Publishes JobEvent to Kafka

**_Execution_**

1. Executor consumes Kafka events 
2. Executes job using strategy pattern 
3. Handles:
   1. retries 
   2. recurring rescheduling 
   3. permanent failures

#Failure Handling

1. Validation failures → job marked FAILED (no retry)
2. Transient failures → Kafka retry 
3. Retries & backoff handled by Kafka semantics 
4. No exception swallowing

#Configuration Management
**_Each service reads configs from:_**
```/etc/service.properties
/etc/secrets.properties
```


## Testing the System (End-to-End Flow)

This section demonstrates how to test the complete system once all services are running via Docker Compose.

---

### Step 1️⃣ User Signup

Create a new user using the **User Service**.

```bash
curl --location 'http://localhost:8081/user-service/api/v1/user/signup' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "saurabh_rathi",
    "password": "airtribe1234",
    "email": "saurabh45rathi@airtribe.com"
}'
```
**_On successful signup:_**
1. User is created in userDb 
2. Default role ROLE_USER is assigned 
3. Scope jobs.create is mapped to the role


### Step 2️⃣ User Sign In
**Authenticate using the Auth Service to obtain a JWT token.**
```bash 
curl --location 'http://localhost:8080/auth-service/api/v1/auth/signin' \
--header 'Content-Type: application/json' \
--data '{
    "username": "saurabh_rathi",
    "password": "airtribe1234"
}'
```

**_Sample Response_**
```
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "message": "Login successful"
}
```
Copy the value of token.
This JWT will be used to authorize job creation requests.


###Step 3️⃣ Create Jobs (Job Service)

All job creation APIs require:
1. Authorization: Bearer <JWT_TOKEN>
2. Scope: jobs.create

###Job Types Supported
| Field          | Description                                |
| -------------- | ------------------------------------------ |
| jobType        | ONE_TIME or RECURRING                      |
| executionMode  | IMMEDIATE or SCHEDULED (only for ONE_TIME) |
| payload        | Job-specific execution data                |
| runAt          | Required for ONE_TIME + SCHEDULED          |
| cronExpression | Required for RECURRING jobs                |

Payload format:
```
"payload": {
  "task": "send_email | generate_report",
  "template": "any string"
}
```

####ONE_TIME – IMMEDIATE Job
```bash
curl --location 'http://localhost:8082/job-service/api/v1/jobs' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--data-raw '{
  "jobType": "ONE_TIME",
  "executionMode": "IMMEDIATE",
  "payload": {
    "task": "send_email",
    "template": "sending email job now"
  }
}'
```
Job executes immediately.


####ONE_TIME – SCHEDULED Job
runAt must be provided in UTC ISO-8601 format.
```bash
curl --location 'http://localhost:8082/job-service/api/v1/jobs' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--data-raw '{
  "jobType": "ONE_TIME",
  "executionMode": "SCHEDULED",
  "payload": {
    "task": "send_email",
    "template": "sending email at some time in future"
  },
  "runAt": "2025-12-28T08:26:00.197181Z"
}'
```

####RECURRING Job (Every 2 Minutes)
For recurring jobs:
1. executionMode is not required 
2. cronExpression is mandatory

Spring-compatible cron (every 2 minutes):
```markdown
0 */2 * * * *
```

```bash
curl --location 'http://localhost:8082/job-service/api/v1/jobs' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
--data-raw '{
  "jobType": "RECURRING",
  "payload": {
    "task": "generate_report",
    "template": "recurring job every 2 mins"
  },
  "cronExpression": "0 */2 * * * *"
}'
```


### To get the jobs
```bash
curl --location 'http://localhost:8082/job-service/api/v1/jobs' \
--header 'Authorization: Bearer  <JWT_TOKEN>'
```

This will return the list of jobs with the current state for the current user.



#What Happens Internally

1. Job is persisted in the jobs table with status SCHEDULED 
2. Scheduler Service polls the DB for due jobs 
3. Scheduler publishes a JobEvent to Kafka topic job-events 
4. Job status is updated to RUNNING 
5. Event is visible in AKHQ UI 
6. Executor Service consumes the event and executes the job

##Final Job State
1. ONE_TIME → COMPLETED 
2. RECURRING → SCHEDULED (nextRunAt recalculated)

#Stopping the System
```
docker-compose down
```
**_To remove volumes (fresh start):_**
```
docker-compose down -v
```
