# 🎫 IT Helpdesk - Web Application

A full-featured IT Helpdesk ticketing system built with **Spring Boot 3.x** and **Thymeleaf**.

![Java](https://img.shields.io/badge/Java-17+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Docker-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple)

---

## 📋 Table of Contents

1. [Project Overview](#-project-overview)
2. [Technologies Used](#-technologies-used)
3. [Features](#-features)
4. [Project Structure](#-project-structure)
5. [Architecture](#-architecture)
6. [Database Schema](#-database-schema)
7. [Security & Roles](#-security--roles)
8. [API Endpoints](#-api-endpoints)
9. [How to Run](#-how-to-run)
10. [File Descriptions](#-file-descriptions)

---

## 🎯 Project Overview

This IT Helpdesk application allows organizations to manage support tickets efficiently. Users can create tickets, agents can claim and resolve them, and administrators have full control over the system.

### Key Capabilities:
- **Ticket Management**: Full CRUD operations (Create, Read, Update, Delete)
- **Role-Based Access Control**: Three distinct roles (USER, AGENT, ADMIN)
- **Real-time Stats**: Dashboard with AJAX-powered statistics refresh
- **Comments System**: Users can add comments to tickets
- **Search & Filter**: Find tickets by status, priority, or keyword
- **User Management**: Admins can manage users, roles, and statuses

---

## 🛠 Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Programming Language |
| Spring Boot | 3.x | Backend Framework |
| Spring MVC | 6.x | Web Layer |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Database Access |
| Thymeleaf | 3.1 | Template Engine |
| PostgreSQL | 15+ | Database (in Docker) |
| Bootstrap | 5.3 | CSS Framework |
| Font Awesome | 6.4 | Icons |
| Chart.js | 4.4 | Dashboard Charts |
| Docker | - | Database Container |
| Maven | 3.x | Build Tool |

---

## ✨ Features

### 🔐 Authentication & Authorization
- Custom login page (not Spring default)
- Logout functionality
- Role-based access control (ROLE_USER, ROLE_AGENT, ROLE_ADMIN)
- URL restrictions:
  - `/admin/**` → ADMIN only
  - `/app/**` → USER & ADMIN
- Custom Access Denied page

### 📝 Ticket Management (CRUD)
- **Create**: Submit new tickets with title, description, priority, category
- **Read**: View ticket details, comments, status history
- **Update**: Edit tickets, change status, assign to agents
- **Delete**: Remove tickets (with confirmation modal)

### 🔍 Search & Filter
- Filter by **Status** (Open, In Progress, Resolved, Closed)
- Filter by **Priority** (Low, Medium, High, Urgent)
- Search by **Keyword** (title or description)

### 💬 Comments System
- Add comments to tickets
- Delete own comments
- Admins can delete any comment

### 📊 Dashboard
- Statistics cards (Total, Open, In Progress, Resolved)
- Status distribution chart (Doughnut)
- Priority chart (Bar)
- **AJAX refresh** - Update stats without page reload
- Recent tickets list

### 👥 User Management (Admin)
- View all users with pagination
- Change user roles
- Change user status (Active/Inactive)
- Delete users (cascade deletes their tickets)

### 🎨 UI/UX Features
- Modern responsive design
- Toast notifications (auto-hide after 5 seconds)
- Bootstrap modals for confirmations
- Consistent color scheme
- Mobile-friendly sidebar

---

## 📁 Project Structure

```
helpdesk/
├── src/
│   ├── main/
│   │   ├── java/com/example/helpdesk/
│   │   │   ├── HelpdeskApplication.java      # Main application entry point
│   │   │   ├── config/                        # Configuration classes
│   │   │   │   ├── DataInitializer.java      # Seeds initial data
│   │   │   │   ├── MapperConfig.java         # MapStruct configuration
│   │   │   │   └── SecurityConfig.java       # Spring Security setup
│   │   │   ├── controller/                    # MVC Controllers
│   │   │   │   ├── AdminController.java      # Admin user management
│   │   │   │   ├── AuthController.java       # Login/Register/Logout
│   │   │   │   ├── DashboardController.java  # Dashboard views
│   │   │   │   ├── HomeController.java       # Home page
│   │   │   │   ├── ProfileController.java    # User profile
│   │   │   │   ├── TicketController.java     # Ticket CRUD
│   │   │   │   └── api/                      # REST Controllers
│   │   │   │       ├── StatsRestController.java   # Stats API
│   │   │   │       └── TicketRestController.java  # Ticket API
│   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   │   ├── DashboardStatsDto.java
│   │   │   │   ├── DepartmentDto.java
│   │   │   │   ├── TicketCommentDto.java
│   │   │   │   ├── TicketCreateDto.java
│   │   │   │   ├── TicketDto.java
│   │   │   │   ├── TicketUpdateDto.java
│   │   │   │   └── UserDto.java
│   │   │   ├── entity/                       # JPA Entities
│   │   │   │   ├── Department.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Ticket.java
│   │   │   │   ├── TicketAttachment.java
│   │   │   │   ├── TicketComment.java
│   │   │   │   └── User.java
│   │   │   ├── enums/                        # Enumerations
│   │   │   │   ├── TicketCategory.java
│   │   │   │   ├── TicketPriority.java
│   │   │   │   ├── TicketStatus.java
│   │   │   │   └── UserStatus.java
│   │   │   ├── mapper/                       # MapStruct Mappers
│   │   │   │   ├── DepartmentMapper.java
│   │   │   │   ├── TicketMapper.java
│   │   │   │   └── UserMapper.java
│   │   │   ├── repository/                   # Spring Data Repositories
│   │   │   │   ├── DepartmentRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   ├── TicketCommentRepository.java
│   │   │   │   ├── TicketRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                      # Business Logic
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   ├── TicketCommentService.java
│   │   │   │   ├── TicketService.java
│   │   │   │   └── UserService.java
│   │   │   └── validation/                   # Custom Validators
│   │   │       ├── UniqueEmail.java          # @UniqueEmail annotation
│   │   │       ├── UniqueEmailValidator.java
│   │   │       ├── ValidTicketStatus.java    # @ValidTicketStatus annotation
│   │   │       └── ValidTicketStatusValidator.java
│   │   └── resources/
│   │       ├── application.properties        # App configuration
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   ├── dashboard.css
│   │       │   │   └── style.css
│   │       │   ├── images/
│   │       │   │   └── logo.png
│   │       │   └── js/
│   │       │       ├── charts.js
│   │       │       └── main.js
│   │       └── templates/                    # Thymeleaf Templates
│   │           ├── home.html
│   │           ├── admin/
│   │           │   └── users.html
│   │           ├── auth/
│   │           │   ├── login.html
│   │           │   └── register.html
│   │           ├── dashboard/
│   │           │   ├── admin.html
│   │           │   └── index.html
│   │           ├── error/
│   │           │   └── access-denied.html
│   │           ├── profile/
│   │           │   └── view.html
│   │           ├── shared/                   # Thymeleaf Fragments
│   │           │   ├── footer.html
│   │           │   ├── header.html
│   │           │   ├── layout.html
│   │           │   ├── navbar.html
│   │           │   └── sidebar.html
│   │           └── ticket/
│   │               ├── create.html
│   │               ├── edit.html
│   │               ├── list.html
│   │               ├── search.html
│   │               └── view.html
│   └── test/
│       └── java/com/example/helpdesk/
│           └── HelpdeskApplicationTests.java
├── docker-compose.yaml                       # Docker setup for PostgreSQL
├── pom.xml                                   # Maven dependencies
├── mvnw                                      # Maven wrapper (Unix)
├── mvnw.cmd                                  # Maven wrapper (Windows)
└── README.md                                 # This file
```

---

## 🏗 Architecture

The application follows a **layered architecture**:

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Controllers │  │  Thymeleaf  │  │   REST Controllers  │  │
│  │   (MVC)     │  │  Templates  │  │       (API)         │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │TicketService│  │ UserService │  │  DashboardService   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     REPOSITORY LAYER                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │TicketRepo   │  │  UserRepo   │  │  DepartmentRepo     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                          │
│                    PostgreSQL (Docker)                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗄 Database Schema

### Entity Relationships

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│     User     │       │    Ticket    │       │   Comment    │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id           │───┐   │ id           │───┐   │ id           │
│ username     │   │   │ title        │   │   │ content      │
│ password     │   │   │ description  │   │   │ createdAt    │
│ email        │   └──▶│ createdBy    │   └──▶│ ticketId     │
│ firstName    │   ┌──▶│ assignedTo   │       │ userId       │
│ lastName     │   │   │ status       │       └──────────────┘
│ roles        │───┘   │ priority     │
│ department   │       │ category     │       ┌──────────────┐
│ status       │       │ department   │       │  Department  │
└──────────────┘       │ createdAt    │       ├──────────────┤
                       │ updatedAt    │       │ id           │
┌──────────────┐       │ resolvedAt   │       │ name         │
│     Role     │       └──────────────┘       │ description  │
├──────────────┤                              └──────────────┘
│ id           │
│ name         │
│ description  │
└──────────────┘
```

### Relationships:
- **User → Ticket**: OneToMany (User creates many tickets)
- **User → Ticket**: OneToMany (User assigned to many tickets)
- **User → Role**: ManyToMany (User has multiple roles)
- **Ticket → Comment**: OneToMany (Ticket has many comments)
- **User → Comment**: OneToMany (User writes many comments)
- **User → Department**: ManyToOne (User belongs to one department)
- **Ticket → Department**: ManyToOne (Ticket belongs to one department)

---

## 🔐 Security & Roles

### Three User Roles:

| Role | Permissions |
|------|-------------|
| **ROLE_USER** | Create tickets, view own tickets, edit/delete own tickets, add comments |
| **ROLE_AGENT** | View all tickets, claim unassigned tickets, change ticket status, add comments |
| **ROLE_ADMIN** | Full system access, manage users, edit/delete any ticket, assign to anyone |

### URL Security Configuration:

```java
// SecurityConfig.java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/tickets/**").hasAnyRole("USER", "AGENT", "ADMIN")
.requestMatchers("/dashboard/**").authenticated()
.requestMatchers("/", "/home", "/auth/**").permitAll()
```

### Default Users (Created by DataInitializer):

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| agent | agent123 | AGENT |
| user | user123 | USER |

---

## 🌐 API Endpoints

### REST API

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/stats` | Get dashboard statistics | Authenticated |
| GET | `/api/tickets` | Get all tickets | Authenticated |
| GET | `/api/tickets/{id}` | Get ticket by ID | Authenticated |

### MVC Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Home page |
| GET | `/auth/login` | Login page |
| POST | `/auth/login` | Process login |
| GET | `/auth/register` | Registration page |
| POST | `/auth/register` | Process registration |
| POST | `/auth/logout` | Logout |
| GET | `/dashboard` | Dashboard (role-based) |
| GET | `/tickets` | Ticket list (paginated) |
| GET | `/tickets/create` | Create ticket form |
| POST | `/tickets/create` | Submit new ticket |
| GET | `/tickets/{id}` | View ticket |
| GET | `/tickets/{id}/edit` | Edit ticket form |
| POST | `/tickets/{id}/update` | Update ticket |
| POST | `/tickets/{id}/delete` | Delete ticket |
| POST | `/tickets/{id}/claim` | Agent claims ticket |
| POST | `/tickets/{id}/status` | Change ticket status |
| GET | `/tickets/search` | Search tickets |
| POST | `/tickets/{id}/comments` | Add comment |
| POST | `/tickets/{id}/comments/{cid}/delete` | Delete comment |
| GET | `/profile` | View profile |
| POST | `/profile/update` | Update profile |
| POST | `/profile/change-password` | Change password |
| GET | `/admin/users` | User management (paginated) |
| POST | `/admin/users/{id}/role` | Change user role |
| POST | `/admin/users/{id}/status` | Change user status |
| POST | `/admin/users/{id}/delete` | Delete user |

---

## 🚀 How to Run

### Prerequisites:
- Java 17 or higher
- Docker & Docker Compose
- Maven 3.x (or use included wrapper)

### Steps:

1. **Clone the repository:**
   ```bash
   cd /path/to/IT_Helpdesk/helpdesk
   ```

2. **Start PostgreSQL with Docker:**
   ```bash
   docker-compose up -d
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application:**
   - URL: http://localhost:8080
   - Login with: `admin` / `admin123`

### Docker Compose Configuration:
```yaml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: helpdesk_db
      POSTGRES_USER: user
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
```

---

## 📄 File Descriptions

### Configuration Files

| File | Description |
|------|-------------|
| `SecurityConfig.java` | Configures Spring Security: login page, URL permissions, password encoding, CSRF |
| `DataInitializer.java` | Seeds initial data: roles (USER, AGENT, ADMIN), departments, default users, sample tickets |
| `MapperConfig.java` | Configures MapStruct for DTO mapping |
| `application.properties` | Database connection, JPA settings, Thymeleaf config, file upload limits |

### Controllers

| File | Description |
|------|-------------|
| `HomeController.java` | Handles home page (`/`) |
| `AuthController.java` | Login, registration, logout |
| `DashboardController.java` | Dashboard for different roles (USER vs ADMIN) |
| `TicketController.java` | Full CRUD for tickets, comments, status changes |
| `ProfileController.java` | User profile view and update |
| `AdminController.java` | User management (roles, status, delete) |
| `StatsRestController.java` | REST API for dashboard statistics (AJAX) |
| `TicketRestController.java` | REST API for ticket operations |

### Entities (JPA)

| File | Description |
|------|-------------|
| `User.java` | User entity with roles, tickets, comments |
| `Role.java` | Role entity (ROLE_USER, ROLE_AGENT, ROLE_ADMIN) |
| `Ticket.java` | Ticket entity with status, priority, category, comments |
| `TicketComment.java` | Comment entity linked to ticket and user |
| `Department.java` | Department entity |
| `TicketAttachment.java` | Attachment entity (for future use) |

### Services

| File | Description |
|------|-------------|
| `TicketService.java` | Business logic for tickets (CRUD, search, pagination) |
| `UserService.java` | User operations |
| `DashboardService.java` | Statistics calculations for dashboard |
| `TicketCommentService.java` | Comment operations |
| `CustomUserDetailsService.java` | Spring Security user loading |

### Custom Validators

| File | Description |
|------|-------------|
| `@UniqueEmail` | Validates email uniqueness during registration |
| `UniqueEmailValidator.java` | Implementation of @UniqueEmail |
| `@ValidTicketStatus` | Validates ticket status transitions |
| `ValidTicketStatusValidator.java` | Implementation of @ValidTicketStatus |

### Templates (Thymeleaf)

| File | Description |
|------|-------------|
| `shared/layout.html` | Base layout template |
| `shared/navbar.html` | Navigation bar fragment |
| `shared/sidebar.html` | Sidebar fragment with menu |
| `shared/footer.html` | Footer fragment |
| `auth/login.html` | Custom login page |
| `auth/register.html` | Registration page |
| `dashboard/index.html` | User/Agent dashboard with charts |
| `dashboard/admin.html` | Admin dashboard with additional stats |
| `ticket/list.html` | Ticket list with pagination |
| `ticket/create.html` | Create ticket form |
| `ticket/view.html` | Ticket details with comments |
| `ticket/edit.html` | Edit ticket form |
| `ticket/search.html` | Search/filter tickets |
| `admin/users.html` | User management with pagination |
| `profile/view.html` | User profile page |
| `error/access-denied.html` | 403 error page |

---

## 🎨 UI Components

### Toast Notifications
- **Success**: Green background, auto-hides after 5 seconds
- **Error**: Red background, auto-hides after 5 seconds
- Located in top-right corner

### Bootstrap Modals
- Delete ticket confirmation
- Delete comment confirmation
- Delete user confirmation (with warning about cascade delete)

### Pagination
- 10 items per page
- Previous/Next navigation
- Page numbers
- Used on: Ticket list, Admin users

### AJAX Features
- Dashboard stats refresh (without page reload)
- Charts update dynamically
- Spinning icon during loading

---

## 📊 Requirements Checklist

| Requirement | Status |
|-------------|--------|
| Spring Boot 3.x + Spring MVC | ✅ |
| Thymeleaf with Fragments & Layout | ✅ |
| Spring Security (Form Login) | ✅ |
| Custom Login Page | ✅ |
| Role-based Access (USER, ADMIN) | ✅ |
| URL Restrictions | ✅ |
| Custom Access Denied Page | ✅ |
| Spring Data JPA | ✅ |
| PostgreSQL in Docker | ✅ |
| Full CRUD Operations | ✅ |
| Search/Filter (2+ criteria) | ✅ |
| Form Validation + Error Display | ✅ |
| Database Relations (@OneToMany) | ✅ |
| Custom Validator (@UniqueEmail) | ✅ |
| REST Endpoint | ✅ |
| AJAX Call from Thymeleaf | ✅ |
| Bean Validation | ✅ |

---

## 👥 Authors

IT Helpdesk Team - Spring Boot Project

---

## 📝 License

This project is for educational purposes.

---

**🎉 Ready for Presentation!**
