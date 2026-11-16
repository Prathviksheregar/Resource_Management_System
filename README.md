# Resource Management System

A desktop-based Resource Management System developed using **Java Swing, JDBC, and MySQL**. This application helps organisations manage company resources, handle employee resource requests, track resource activities, and generate administrative reports.

## Project Overview

The Resource Management System provides two types of users:

- **Admin:** Manages resources, approves or rejects employee requests, views resource history, generates reports, and exports data.
- **Employee:** Views available resources, submits resource requests, and tracks request status.

The project follows a layered architecture using Model, DAO, Service, and UI packages to keep the application organised and maintainable.

## Features

### 1. User Authentication
- User registration and login.
- Password hashing using BCrypt.
- Role-based access for Admin and Employee.

### 2. Resource Management
- Add new resources.
- View all resources.
- Update resource details.
- Delete resources.
- Manage resource quantity, category, timeline, and cost.

### 3. Employee Resource Management
- View available resources.
- Submit resource requests.
- View personal request history.
- Track request status.

### 4. Request Management
- Admin can view employee requests.
- Approve or reject pending requests.
- Resource quantity is reduced only after approval.
- Database transactions help maintain consistency during request approval.

### 5. Resource History
- View resource request approval and rejection activities.
- Track the related resource, user, quantity, action, and timestamp.

### 6. Admin Reports
- Total number of resources.
- Total available quantity.
- Pending requests.
- Approved requests.
- Rejected requests.
- Total inventory value.
- Category-wise resource reports.

### 7. CSV Export
- Export resource information to CSV.
- Export employee request information to CSV.
- Open exported reports using Microsoft Excel or other spreadsheet applications.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Core programming language |
| Java Swing | Desktop graphical user interface |
| MySQL | Database management |
| JDBC | Database connectivity |
| Maven | Dependency management |
| BCrypt | Password hashing |
| IntelliJ IDEA | Development environment |

## Project Architecture

The application follows a layered architecture:

```text
User Interface (UI)
        |
        v
Service Layer
        |
        v
DAO Layer
        |
        v
MySQL Database
```

### Package Structure

```text
src/main/java/company/
│
├── Main.java
├── AdminSetup.java
│
├── config/
│   └── DatabaseConnection.java
│
├── model/
│   ├── User.java
│   ├── Resource.java
│   ├── ResourceRequest.java
│   ├── ResourceHistory.java
│   ├── ReportSummary.java
│   └── CategoryReport.java
│
├── dao/
│   ├── UserDAO.java
│   ├── ResourceDAO.java
│   ├── RequestDAO.java
│   ├── ResourceHistoryDAO.java
│   ├── ReportDAO.java
│   └── ReportExportDAO.java
│
├── service/
│   ├── AuthService.java
│   ├── ResourceService.java
│   ├── RequestService.java
│   ├── ResourceHistoryService.java
│   ├── ReportService.java
│   └── ReportExportService.java
│
└── ui/
    ├── LoginFrame.java
    ├── RegisterFrame.java
    ├── AdminDashboardFrame.java
    ├── EmployeeDashboardFrame.java
    ├── ResourceManagementFrame.java
    ├── EmployeeResourceFrame.java
    ├── EmployeeRequestFrame.java
    ├── AdminRequestFrame.java
    ├── MyRequestsFrame.java
    ├── ResourceHistoryFrame.java
    └── AdminReportsFrame.java
```

## Database Configuration

The application uses MySQL.

Create a database:

```sql
CREATE DATABASE resource_management;
```

Select the database:

```sql
USE resource_management;
```

Create the following tables with the columns used by the application:

### Users Table

```sql
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Resource Table

```sql
CREATE TABLE resource (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    resource_name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    timeline VARCHAR(100),
    quantity INT NOT NULL,
    cost DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Access Requests Table

```sql
CREATE TABLE access_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    resource_id INT NOT NULL,
    requested_quantity INT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (resource_id) REFERENCES resource(resource_id)
);
```

### Resource History Table

```sql
CREATE TABLE resource_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    resource_id INT NOT NULL,
    user_id INT NOT NULL,
    quantity INT NOT NULL,
    action VARCHAR(30),
    action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES resource(resource_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

**Note:** These are reference table definitions. If you already have tables created, do not run the CREATE statements again. Check your existing schema first. Use InnoDB for transaction and row-locking support.

## Installation and Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/ResourceManagementSystem.git
```

### Step 2: Open the Project

- Open IntelliJ IDEA.
- Select Open Project.
- Choose the cloned project folder.
- Allow Maven to download dependencies.

### Step 3: Configure MySQL

- Start your MySQL server.
- Create the `resource_management` database.
- Create the required tables.
- Configure the database connection.

### Step 4: Configure Environment Variables

Set these environment variables in your IntelliJ Run Configuration:

```text
DB_USER=root
DB_PASSWORD=your_mysql_password
```

Replace the example password with your own local MySQL password.

**Security note:** Never upload database passwords or other secrets to GitHub.

### Step 5: Run the Application

Run the main class:

```text
company.Main
```

The login screen should appear.

Use the project's secure admin setup process to create an administrator account.

## Application Workflow

```text
Application Start
       |
       v
   Login Screen
       |
       v
 Authentication
       |
       +-------------------+
       |                   |
       v                   v
 Admin Dashboard     Employee Dashboard
       |                   |
       v                   v
 Manage Resources    View Resources
 Manage Requests     Submit Requests
 View History        My Requests
 View Reports
 Export CSV
```

## Security and Data Consistency

- Passwords are stored as BCrypt hashes rather than plain text.
- Service classes validate user roles before protected operations.
- Prepared statements are used for database queries.
- Request approval uses database transactions and row locking to help prevent incorrect stock updates.
- Database credentials are configured through environment variables.

## Future Enhancements

- Email notifications for request status changes.
- Advanced search and filtering.
- Complete resource audit history.
- Native Excel export (`.xlsx`).
- Improved dashboard visualisations.
- Web-based version of the application.
- Stronger server-side authentication and authorisation.

## Learning Outcomes

Through this project, I gained practical experience in:

- Java Object-Oriented Programming.
- Java Swing GUI development.
- JDBC and MySQL connectivity.
- SQL queries and database relationships.
- Layered application architecture.
- Authentication and role-based access.
- Password hashing using BCrypt.
- Database transactions and row locking.
- Generating reports and exporting CSV files.



