<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Healthcare Microservices Architecture</title>
</head>
<body>
    <h1>Healthcare Microservices Architecture: Patient, Doctor, Appointment, and Notification</h1>

    <h2>Overview</h2>
    <p>This project demonstrates a microservices architecture for a healthcare system, consisting of four primary services: <strong>Patient Service</strong>, <strong>Doctor Service</strong>, <strong>Appointment Service</strong>, and <strong>Notification Service</strong>. These services interact with each other to handle patient appointments, doctor availability, and communication via notifications.</p>

    <h3>Services Overview:</h3>
    <ul>
        <li><strong>Patient Service</strong>: Manages patient records, including personal details, medical history, and appointment requests.</li>
        <li><strong>Doctor Service</strong>: Manages doctor profiles, specialties, availability, and approval/rejection of appointments.</li>
        <li><strong>Appointment Service</strong>: Handles the creation, modification, and management of patient appointments.</li>
        <li><strong>Notification Service</strong>: Sends notifications to patients and doctors (e.g., appointment confirmation, approval, and reminders).</li>
    </ul>

    <h3>Flow:</h3>
    <ol>
        <li><strong>Patient Service</strong>: Receives patient appointment requests and notifies the <strong>Appointment Service</strong> for processing and availability check.</li>
        <li><strong>Appointment Service</strong>: Sends appointment requests to <strong>Doctor Service</strong> for approval or rejection and notifies the <strong>Notification Service</strong> for communication with the patient and doctor.</li>
        <li><strong>Doctor Service</strong>: Receives the appointment request from <strong>Appointment Service</strong> and sends approval or rejection status back.</li>
        <li><strong>Notification Service</strong>: Sends notifications to the patient and doctor based on the appointment status (e.g., confirmation, reminder, or cancellation).</li>
    </ol>

    <h2>Services Communication</h2>
    <p><strong>Synchronous</strong> communication via REST API (e.g., Appointment Service -&gt; Patient Service for appointment status updates).</p>
    <p><strong>Asynchronous</strong> communication using <strong>Webhook</strong> or <strong>Event-based</strong> messaging for notifications and appointment approvals.</p>

    <h2>Technologies Used</h2>
    <ul>
        <li><strong>Spring Boot</strong> for all services</li>
        <li><strong>Spring Cloud</strong> for service discovery, config management</li>
        <li><strong>RabbitMQ/Kafka</strong> for event-driven messaging (optional)</li>
        <li><strong>MySQL/PostgreSQL</strong> for database management</li>
        <li><strong>JWT</strong> for authentication and authorization</li>
        <li><strong>Docker</strong> for containerization</li>
        <li><strong>Kubernetes</strong> for orchestration (optional)</li>
    </ul>

    <h2>Running the Project</h2>
    <h3>Prerequisites:</h3>
    <ul>
        <li><strong>Docker</strong> and <strong>Docker Compose</strong> installed</li>
        <li><strong>JDK 11+</strong> for building Spring Boot services</li>
        <li><strong>RabbitMQ/Kafka</strong> running locally or on the cloud</li>
        <li><strong>MySQL/PostgreSQL</strong> database running locally or cloud</li>
    </ul>

    <h3>Steps to run:</h3>
    <ol>
        <li>Clone the repository:
            <pre><code>git clone https://github.com/your-username/healthcare-appointment-microservices.git
cd healthcare-appointment-microservices</code></pre>
        </li>
        <li>Build the services:
            <pre><code>mvn clean install</code></pre>
        </li>
        <li>Start the services using Docker Compose:
            <pre><code>docker-compose up --build</code></pre>
            This will start all microservices (Patient, Doctor, Appointment, Notification) in containers.
        </li>
        <li>Access services:
            <ul>
                <li><strong>Patient Service</strong>: <code>http://localhost:8081</code></li>
                <li><strong>Doctor Service</strong>: <code>http://localhost:8082</code></li>
                <li><strong>Appointment Service</strong>: <code>http://localhost:8083</code></li>
                <li><strong>Notification Service</strong>: <code>http://localhost:8084</code></li>
            </ul>
        </li>
        <li>Check the logs:
            <pre><code>docker-compose logs -f</code></pre>
        </li>
    </ol>

    <h2>API Endpoints</h2>
    <h3>Patient Service</h3>
    <ul>
        <li><strong>POST /api/patients/appointments</strong>: Request an appointment with a doctor.
            <pre><code>{
    "patientId": 123,
    "doctorId": 456,
    "appointmentDate": "2024-12-01T09:00:00"
}</code></pre>
        </li>
    </ul>

    <h3>Appointment Service</h3>
    <ul>
        <li><strong>POST /api/appointments</strong>: Create a new appointment.
            <pre><code>{
    "patientId": 123,
    "doctorId": 456,
    "appointmentDate": "2024-12-01T09:00:00"
}</code></pre>
        </li>
        <li><strong>GET /api/appointments/{id}</strong>: Get appointment details by ID.</li>
    </ul>

    <h3>Doctor Service</h3>
    <ul>
        <li><strong>POST /api/doctors/appointments/approve</strong>: Approve an appointment.
            <pre><code>{
    "appointmentId": 789,
    "status": "APPROVED"
}</code></pre>
        </li>
        <li><strong>POST /api/doctors/appointments/reject</strong>: Reject an appointment.
            <pre><code>{
    "appointmentId": 789,
    "status": "REJECTED"
}</code></pre>
        </li>
    </ul>

    <h3>Notification Service</h3>
    <ul>
        <li><strong>POST /api/notifications</strong>: Send a notification to the patient or doctor.
            <pre><code>{
    "recipientId": 123,
    "message": "Your appointment with Dr. Smith has been confirmed for 2024-12-01 at 9:00 AM."
}</code></pre>
        </li>
    </ul>

    <h2>Event Flow</h2>
    <p>Details of the event-driven flow between services for appointment management and notifications.</p>

    <h2>Future Enhancements</h2>
    <ul>
        <li>Add payment gateway integration for appointments.</li>
        <li>Implement doctor availability slots.</li>
        <li>Add cancellation policy for patients.</li>
        <li>Enhance security with OAuth2/JWT for service-to-service authentication.</li>
        <li>Implement monitoring and logging with Spring Actuator and ELK stack.</li>
    </ul>

    <h2>License</h2>
    <p>This project is licensed under the MIT License - see the <a href="LICENSE">LICENSE</a> file for details.</p>
</body>
</html>
