# Executor Service

## Overview

The **Executor Service** is responsible for **executing jobs asynchronously**.  
It acts as a **Kafka consumer**, consumes job execution events, performs job execution using a strategy-based design, and updates job state in the Job Database.

This service owns the **actual execution lifecycle** of a job.

---

## Responsibilities

- Consume job execution events from Kafka
- Fetch job details from the Job Database
- Validate job execution conditions
- Execute job logic using a pluggable strategy pattern
- Update job state after execution
- Handle recurring job rescheduling

---

## Kafka Consumption

- The Executor Service subscribes to the configured Kafka topic
- Each Kafka message represents a **JobEvent**
- Messages are processed asynchronously
- Execution is decoupled from scheduling via Kafka

Kafka provides:
- Asynchronous execution
- Load buffering
- Horizontal scalability

---

## Job Execution Flow

For each consumed job event, the following steps are performed:

1. Fetch the job from the Job Database using `jobId`
2. Validate the job and execution attempt
3. Select the appropriate executor using **Strategy + Registry pattern**
4. Execute the job logic
5. Update job status and scheduling metadata
6. Acknowledge the Kafka message

---

## Job Validation

Before execution, the Executor Service validates:

- Job existence
- Job status correctness
- Attempt consistency
- Execution eligibility

Invalid jobs are treated as **terminal failures** and are not retried.

---

## Execution Strategy

The Executor Service uses a **Strategy Pattern** combined with a **Registry**:

- Each job type has a dedicated executor implementation
- Executors declare whether they support a given job
- The registry selects the correct executor at runtime

This design allows:
- Easy addition of new job types
- Clean separation of execution logic
- No conditional branching based on job type

---

## Job Completion Handling

### ONE_TIME Jobs

- On successful execution:
`  status = COMPLETED`

- No further scheduling occurs

---

### RECURRING Jobs

- After successful execution:
- Next execution time is computed
- Job status is reset to:
  ```
  status = SCHEDULED
  ```
- Retry counter is reset:
  ```
  attempts = 0
  ```

This ensures:
- No overlapping executions
- Clean retry semantics per run

---

## Failure Handling

- Validation failures are treated as **permanent failures**
- Transient execution failures are retried automatically via Kafka
- Retry semantics are driven by exception types

### Dead Letter Queue (DLQ)

- DLQ is **not implemented currently**
- The design supports adding DLQ and max-retry handling without refactoring
- DLQ can be introduced as a future enhancement

---

## Configuration Management

The Executor Service externalizes all configurations.

### Configuration Files

- **service.properties**
- Application-level configuration
- Kafka consumer settings, service ports
- **secrets.properties**
- Sensitive configuration
- Database credentials and secrets

Both files are loaded at runtime.

---

## Running the Service Locally

To run the Executor Service locally, configure the environment variable:

```bash
export SPRING_CONFIG_LOCATION=/absolute/path/to/service.properties,/absolute/path/to/secrets.properties
```

