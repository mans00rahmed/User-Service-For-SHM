# Smart Home Maintenance Platform

## Overview
The **Smart Home Maintenance Platform** is a distributed, cloud-native microservices application designed to manage and coordinate maintenance activities in smart residential properties. The system enables efficient handling of maintenance requests, task assignments, service tracking, and notifications while ensuring scalability, fault tolerance, and modularity.

The platform is built using a **microservices architecture**, allowing independent deployment and scaling of services, and follows modern DevOps and CI/CD practices.

---

## Key Features
- 🏠 **Maintenance Request Management** – Residents can create and track maintenance requests.
- 🔧 **Task Assignment & Scheduling** – Maintenance tasks are assigned to technicians based on availability.
- 📊 **Status Tracking** – Real-time updates on maintenance progress.
- 🔔 **Notifications** – Automated alerts for request updates and task completion.
- ⚙️ **Scalable Microservices Architecture** – Each service operates independently.
- 🔐 **Secure Communication** – Service-to-service communication with proper authentication.
- ☁️ **Cloud-Ready Deployment** – Designed for containerized and cloud environments.

---

## System Architecture
The platform follows a **microservices-based architecture**, where each core functionality is implemented as a separate service.

### Core Services
- **User Service** – Manages user registration, authentication, and roles.
- **Maintenance Service** – Handles maintenance requests, updates, and history.
- **Task Management Service** – Assigns and schedules maintenance tasks.
- **Notification Service** – Sends alerts and updates to users.
- **API Gateway** – Acts as a single entry point for client requests.
- **Service Registry** – Enables service discovery and load balancing.

---

## Technology Stack
### Backend
- Java / Spring Boot
- RESTful APIs

### Database
- MySQL
- Database-per-service pattern

---

## Project Structure
```plaintext
smart-home-maintenance-platform/
│
├── api-gateway/
├── user-service/
├── maintenance-service/
├── task-service/
├── notification-service/
├── service-registry/
├── docker-compose.yml
└── README.md
```

---

## Installation & Setup
### Prerequisites
- Java 17
- Maven
- Git

### Steps
```bash
git clone https://github.com/your-repo/smart-home-maintenance-platform.git
cd smart-home-maintenance-platform
mvn clean install
docker-compose up
```

---

## Usage
- Access the system through the API Gateway
- Create users and maintenance requests
- Track request status and task progress
- Receive notifications

---

## Team Members (Sigma Team)
- Cian Farrell
- Taha Aflouk
- Mansoor Ahmed

---

## Future Enhancements
- Mobile application support
- Predictive maintenance
- IoT sensor integration
- Analytics dashboard
- Kubernetes auto-scaling

---

## License
Developed for TUS academic purposes.
