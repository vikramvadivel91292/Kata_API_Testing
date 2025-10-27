# API Automation Framework — Booking API (Java + Cucumber + RestAssured + TestNG)

This project implements a test automation framework for RESTful APIs built on top of Java and the most widely used automation libraries.
It supports full CRUD operations for the Booking API — including Create, Get, Update, and Delete — and demonstrates modern best practices such as BDD, reusable step definitions, schema validation, and CI/CD integration.

## Overview

The framework follows a Behavior Driven Development (BDD) approach using Cucumber, allowing scenarios to be written in plain English for easy collaboration between technical and non-technical stakeholders.

**Base API URL:**
🔗 https://automationintesting.online/

**Swagger References:**

Auth API → https://automationintesting.online/auth/swagger-ui/index.html

Booking API → https://automationintesting.online/booking/swagger-ui/index.html

## Technologies Used

| Technology                      | Purpose                            |
|---------------------------------|------------------------------------|
| **Java 17**                     | Core programming language          |
| **Maven 3.5.3**                 | Build and dependency management    |
| **Rest Assured 5.5.2**          | API testing library                |
| **Cucumber 7.22.2**             | BDD test framework                 |
| **TestNG 7.10.2**               | Test execution and reporting       |
| **JSON Schema Validator 5.5.5** | Schema validation utility          |
| **Lombok 1.18.38**              | Reduces boilerplate code           |
| **Jackson 2.17.2**              | JSON serialization/deserialization |
| **JSONassert**                  | Validate JSON response content     |    

## Folder Structure
| Path                                           | Description                                                        |
|------------------------------------------------|--------------------------------------------------------------------|
| **pom.xml**                                    | Maven project configuration file                                   |
| **README.md**                                  | Project documentation                                              |
| **src/main/java/com/booking/pojo/**            | POJO classes (BookingRequest, BookingResponse)                     |
| **src/main/java/com/booking/utils/**           | Utility classes (JsonUtils, ConfigReader)                          |
| **src/test/java/com/booking/stepdefinitions/** | Cucumber step definitions (CreateBookingSteps, UpdateBookingSteps) |
| **src/test/java/com/booking/runners/**         | Test runner files (CucumberTestNGRunner)                           |
| **src/test/java/com/booking/hooks/**           | Common hooks (setup, teardown, token management)                   |
| **src/test/java/com/booking/pages/**           | Contains API layer (BookingAPI, AuthAPI)                           |
| **src/test/resources/features/**               | Contains feature files                                             |
| **src/test/resources/schemas/**                | JSON schema validation files                                       |
| **src/test/resources/testdata/**               | External test data (JSON/CSV)                                      |
| **src/test/resources/config.properties**       | Environment configuration file (base URLs, tokens, timeouts, etc.) |
| **reports/findings**                           | API issues and observations                                        |
| **target/**                                    | Build output directory                                             |

## Features Covered
| Module                 | Endpoints Covered                                                                                                                                        |
| ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Auth Controller**    | `POST /auth` → Token generation                                                                                                                          |
| **Booking Controller** | `POST /booking` → Create Booking<br>`GET /booking/{id}` → Get Booking<br>`PUT /booking/{id}` → Update Booking<br>`DELETE /booking/{id}` → Delete Booking |

## Setup Guide

1. Install Java 17 and Maven 4.0.0+

2. Clone this repository

git clone https://github.com/vikramvadivel91292/Kata_API_Testing.git

3. Open in your IDE (IntelliJ / Eclipse)

4. Allow Maven to auto-download dependencies

5. Verify structure → src/test/resources/features and src/test/java are recognized as test sources

## Running the Tests

1️⃣ Run via Feature File

Open any .feature file under src/test/resources/features
→ Right-click → “Run Feature File”

2️⃣ Run via Maven Command

Run all tests:

mvn clean test

Run only specific tags:

mvn clean test -Dcucumber.filter.tags="@positive or @negative"

## Tags Convention

| Tag                                            | Purpose                                      |
|------------------------------------------------|----------------------------------------------|
| `@Positive`                                    | Positive test scenarios                      |
| `@Negative`                                    | Negative test scenarios                      |
| `@knownIssue`                                  | Test scenarios which fails with known issues |

## Reporting

After test execution, an HTML report is generated at:
📄 target/cucumber-reports.html

You can open it in a browser to view a detailed execution summary with steps, screenshots (if integrated), and logs.

## JSON Schema Validation

Each response is validated against a pre-defined schema which is present in this path:

**src/test/resources/schemas/**

## Scenarios Implemented
| Feature            | Scenarios                                                                                |
| ------------------ | ---------------------------------------------------------------------------------------- |
| **Create Booking** | ✅ Valid booking creation (201)<br>❌ Invalid data scenarios (missing fields, wrong types) |
| **Get Booking**    | ✅ Get by valid ID (200)<br>❌ Invalid / non-existent ID handling                          |
| **Update Booking** | ✅ Update existing booking (200)<br>❌ Invalid ID / data validation                        |
| **Delete Booking** | ✅ Delete by valid ID (201)<br>❌ Invalid / non-existent ID (400 / 404)                    |

## Observations / Known Issues

Observations are present in this path:

**reports/findings**

## Future Enhancements

⚙️ Integration with CI/CD (Jenkins) pipeline

🌐 Dynamic booking date generator

🧩 Parallel execution for faster runs

📊 Allure or ExtentReports integration

☁️ API environment switch via config (DEV/STAGE/PROD)
