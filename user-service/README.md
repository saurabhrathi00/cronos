# User Service

## Overview

The **User Service** is responsible for user registration and basic user management.  
It provides APIs to **sign up users**, assigns default roles, and persists user data in the **User Database**.

This service focuses only on **user identity and role assignment** and does not handle authentication token generation.

---

## Responsibilities

- Register new users (sign-up)
- Persist user details in User DB
- Assign default roles to users
- Serve as the source of truth for user identity data

---

## Sign Up Flow

1. User invokes the **sign-up API**
2. User Service validates the request
3. A new user record is created
4. User data is persisted in **User DB**
5. A default role is assigned to the user

---

## Role Assignment

### Default Role

On successful registration, the user is assigned the following role:


### Role Capabilities

| Role       | Permissions                |
|-----------|----------------------------|
| ROLE_USER | Can create jobs (`jobs.create`) |

The role-based design allows easy extension for additional roles in the future.

---

## Configuration Management

The User Service externalizes all configurations to support multiple environments.

### Configuration Files

- **service.properties**
    - Application-level configuration
    - Service ports, feature flags, service name
- **secrets.properties**
    - Sensitive configuration
    - Database credentials and secrets

Both files are loaded at runtime.

---

## Running the Service Locally

To run the User Service locally, set the configuration file locations using the following environment variable:

```bash
export SPRING_CONFIG_LOCATION=/absolute/path/to/service.properties,/absolute/path/to/secrets.properties
```

#Future Enhancements

1. Support for additional roles (e.g., ROLE_ADMIN)
2. Fine-grained permission management
3. User deactivation and lifecycle management