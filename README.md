# Healthcare Microservices Architecture: Patient, Doctor, Appointment, and Notification

## Overview
This project demonstrates a microservices architecture for a healthcare system, consisting of four primary services: **Patient Service**, **Doctor Service**, **Appointment Service**, and **Notification Service**. These services interact with each other to handle patient appointments, doctor availability, and communication via notifications.

### Services Overview:
- **Patient Service**: Manages patient records, including personal details, medical history, and appointment requests.
- **Doctor Service**: Manages doctor profiles, specialties, availability, and approval/rejection of appointments.
- **Appointment Service**: Handles the creation, modification, and management of patient appointments.
- **Notification Service**: Sends notifications to patients and doctors (e.g., appointment confirmation, approval, and reminders).

### Flow:
1. **Patient Service**: Receives patient appointment requests and notifies the **Appointment Service** for processing and availability check.
2. **Appointment Service**: Sends appointment requests to **Doctor Service** for approval or rejection and notifies the **Notification Service** for communication with the patient and doctor.
3. **Doctor Service**: Receives the appointment request from **Appointment Service** and sends approval or rejection status back.
4. **Notification Service**: Sends notifications to the patient and doctor based on the appointment status (e.g., confirmation, reminder, or cancellation).

## Services Communication
- **Synchronous** communication via REST API (e.g., Appointment Service -> Patient Service for appointment status updates).
- **Asynchronous** communication using **Webhook** or **Event-based** messaging for notifications and appointment approvals.

## Technologies Used
- **Spring Boot** for all services
- **Spring Cloud** for service discovery, config management
- **RabbitMQ/Kafka** for event-driven messaging (optional)
- **MySQL/PostgreSQL** for database management
- **JWT** for authentication and authorization
- **Docker** for containerization
- **Kubernetes** for orchestration (optional)

## Running the Project

### Prerequisites:
- **Docker** and **Docker Compose** installed
- **JDK 11+** for building Spring Boot services
- **RabbitMQ/Kafka** running locally or on the cloud
- **MySQL/PostgreSQL** database running locally or cloud

### Steps to run:
1. Clone the repository:
    ```bash
    git clone https://github.com/kagit00/medica.git
    
    ```
2. Build the services:
    ```bash
    mvn clean install
    ```
3. Start the services using Docker Compose:
    ```bash
    docker-compose up --build
    ```
    This will start all microservices (Patient, Doctor, Appointment, Notification) in containers.
4. Access services:
    - **Patient Service**: `http://localhost:8081`
    - **Doctor Service**: `http://localhost:8082`
    - **Appointment Service**: `http://localhost:8083`
    - **Notification Service**: `http://localhost:8084`

5. Check the logs:
    ```bash
    docker-compose logs -f
    ```

## API Endpoints

### Patient Service
- **POST /api/patients/appointments**: Request an appointment with a doctor.
    ```json
    {
        "patientId": 123,
        "doctorId": 456,
        "appointmentDate": "2024-12-01T09:00:00"
    }
    ```

### Appointment Service
- **POST /api/appointments**: Create a new appointment.
    ```json
    {
        "patientId": 123,
        "doctorId": 456,
        "appointmentDate": "2024-12-01T09:00:00"
    }
    ```
- **GET /api/appointments/{id}**: Get appointment details by ID.

### Doctor Service
- **POST /api/doctors/appointments/approve**: Approve an appointment.
    ```json
    {
        "appointmentId": 789,
        "status": "APPROVED"
    }
    ```
- **POST /api/doctors/appointments/reject**: Reject an appointment.
    ```json
    {
        "appointmentId": 789,
        "status": "REJECTED"
    }
    ```

### Notification Service
- **POST /api/notifications**: Send a notification to the patient or doctor.
    ```json
    {
        "recipientId": 123,
        "message": "Your appointment with Dr. Smith has been confirmed for 2024-12-01 at 9:00 AM."
    }
    ```

## Event Flow
Details of the event-driven flow between services for appointment management and notifications.

## Future Enhancements
- Add payment gateway integration for appointments.
- Implement doctor availability slots.
- Add cancellation policy for patients.
- Enhance security with OAuth2/JWT for service-to-service authentication.
- Implement monitoring and logging with Spring Actuator and ELK stack.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
