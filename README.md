# Healthcare Appointment Management System

This project is a microservices-based healthcare appointment management system. It consists of multiple microservices that work together to handle patient appointments, doctor validations, notifications, and user management. The system is built using Java, Spring Boot, Reactive Programming, and integrates with Kafka, PostgreSQL, Hibernate, Braintree, and Keycloak.

---

## Table of Contents
1. [Project Structure](#project-structure)
2. [Microservices Overview](#microservices-overview)
3. [Tech Stack](#tech-stack)
4. [Getting Started](#getting-started)
5. [API Documentation](#api-documentation)
6. [Contributing](#contributing)
7. [License](#license)

---

## Project Structure

The project is structured as a Maven multi-module project. The parent `pom.xml` includes the following modules:

1. **Patient Service**
2. **Appointment Service**
3. **Doctor Service**
4. **Notification Service**
5. **User Service**

Each module is a standalone microservice that communicates with others via Kafka or Google Pub/Sub.

---

## Microservices Overview

### 1. Patient Service
- **Functionality**: Allows patients to request appointments.
- **Communication**: Uses Kafka/Google Pub/Sub to send appointment requests to the Appointment Service.
- **Key Features**:
  - Publishes appointment requests to a Kafka topic.
  - Handles patient-related data.

### 2. Appointment Service
- **Functionality**: Validates patient and doctor details, checks slot availability, and forwards requests to the Doctor Service.
- **Communication**: Communicates with Patient Service and Doctor Service via Kafka/Google Pub/Sub.
- **Key Features**:
  - Validates patient, doctor, and slot availability.
  - Dumps appointment requests into the database.
  - Forwards requests to the Doctor Service for review.

### 3. Doctor Service
- **Functionality**: Handles appointment reviews, approvals, rejections, and payment requests.
- **Communication**: Receives appointment requests from the Appointment Service and sends notifications via the Notification Service.
- **Key Features**:
  - Allows doctors to approve or reject appointments.
  - Updates appointment status in the database.
  - Initiates payment requests via Braintree (supports GPay and card payments).
  - Allows doctors to cancel scheduled appointments.

### 4. Notification Service
- **Functionality**: Sends email notifications for every Kafka message or event.
- **Communication**: Listens to Kafka topics and sends notifications.
- **Key Features**:
  - Sends emails for appointment requests, approvals, rejections, and cancellations.

### 5. User Service
- **Functionality**: Manages user creation and authentication.
- **Communication**: Integrates with Keycloak for OAuth2 authentication.
- **Key Features**:
  - Creates users in Keycloak and dumps them into the database.
  - Handles common properties for both patient and doctor entities.
  - Maintains user tables and links them to patient/doctor tables.

---

## Tech Stack

- **Programming Language**: Java
- **Framework**: Spring Boot, Reactive Programming
- **Database**: PostgreSQL
- **ORM**: Hibernate
- **Messaging**: Kafka, Google Pub/Sub
- **Payment Gateway**: Braintree (supports GPay and card payments)
- **Authentication**: Keycloak, OAuth2
- **Other Tools**: Maven, Docker

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.x
- PostgreSQL
- Kafka or Google Pub/Sub
- Keycloak server
- Braintree account for payment processing

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/kagit00/medica.git

2. Navigate to the project directory:
   ```bash
   cd medica

3. Build the project:
   ```bash
   mvn clean install

4. Configure the application.yml files for each microservice with the required credentials (database, Kafka, Keycloak, Braintree, etc.).
5. Run each microservice individually:
   ```bash
   mvn spring-boot:run -pl <module-name>
