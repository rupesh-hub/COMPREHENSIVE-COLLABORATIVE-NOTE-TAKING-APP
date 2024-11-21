# Comprehensive Collaborative Note-Taking Application

## Features

### Core Features
1. **Authentication and Authorization**
   - OAuth 2.0 / OpenID Connect for login.
   - Social logins (Google, Microsoft, GitHub).
   - Email/password login with multi-factor authentication (MFA).
   - Password reset and account recovery.

2. **User Management**
   - User profile (view/update).
   - Manage avatars (upload, crop).
   - Account settings (language, theme).
   - Subscription plans (free, premium).

3. **Collaborators Management**
   - Invite collaborators by email or shareable links.
   - Role-based permissions (viewer, editor, admin).
   - Real-time collaboration on notes.

4. **Real-Time Note Editing**
   - Shared editing powered by **WebSockets**.
   - Markdown support.
   - Rich text formatting (bold, italics, bullet points).
   - Add media (images, videos, file attachments).

5. **Search and Tagging**
   - Full-text search across notes.
   - Tag notes for better organization.
   - Filter notes by collaborators, tags, or date.

6. **Notifications**
   - Email/SMS notifications for updates.
   - In-app push notifications for activity.
   - Reminders for tasks within notes.

7. **Version Control**
   - Keep track of note versions.
   - Allow users to restore previous versions.

8. **Offline Mode**
   - View and edit notes offline.
   - Sync changes once back online.

9. **Integration with Other Tools**
   - Export/import notes to/from Google Docs, OneNote, or Evernote.
   - API for third-party integrations.

10. **Analytics**
    - Dashboard for user activity.
    - Insights into collaborator contributions.

### Additional Commercial Features
1. **Monetization Options**
   - Subscription plans with tiered features (basic, pro, enterprise).
   - Ad integration for free users.
   - Custom branding for enterprise clients.

2. **Team Management**
   - Dedicated workspace for teams.
   - Bulk user invitation and role assignment.

3. **Advanced Collaboration Tools**
   - Commenting and mentions (@mentions).
   - Task assignment and tracking within notes.
   - Video or voice collaboration during editing.

4. **Custom Templates**
   - Note templates for meeting minutes, brainstorming, etc.
   - Allow users to save and share templates.

5. **Security and Compliance**
   - End-to-end encryption for sensitive notes.
   - Compliance with GDPR, HIPAA (for enterprise users).

6. **AI-Driven Features**
   - Summarize notes using NLP.
   - Suggest tags based on content.
   - Autocomplete for frequently used phrases.

---

src/
├── app/
│   ├── core/                         # Core modules, services, guards, etc.
│   │   ├── services/                 # Common/shared services (e.g., API, authentication)
│   │   ├── guards/                   # Route guards
│   │   ├── interceptors/             # HTTP interceptors (e.g., auth token)
│   │   ├── models/                   # Data models (e.g., Note, User)
│   │   └── utils/                    # Utility classes or helpers
│   ├── features/                     # Feature-specific modules
│   │   ├── note/                     # Note module
│   │   │   ├── components/           # Components related to notes (e.g., note list, note detail)
│   │   │   ├── services/             # Services related to notes (e.g., note API service)
│   │   │   ├── note.module.ts        # Note module definition
│   │   │   └── note-routing.module.ts # Note routing module
│   ├── shared/                       # Shared components, directives, pipes
│   │   ├── components/               # Shared UI components (e.g., buttons, modals)
│   │   ├── directives/               # Shared directives (e.g., validation)
│   │   ├── pipes/                    # Shared pipes (e.g., date format)
│   │   └── shared.module.ts          # Shared module definition
│   ├── layout/                       # Layout components (e.g., header, footer, sidebar)
│   │   ├── components/               # Layout-related components
│   │   ├── layout.module.ts          # Layout module definition
│   ├── authentication/               # Authentication-related modules (login, register)
│   │   ├── components/               # Auth components (e.g., login, register)
│   │   ├── services/                 # Auth services (e.g., login API)
│   │   ├── authentication.module.ts  # Auth module definition
│   ├── app.component.ts              # Main app component
│   ├── app.module.ts                 # Root module
│   ├── app-routing.module.ts         # App-wide routing module
├── assets/                           # Static assets (images, styles, etc.)
│   ├── images/
│   ├── styles/                       # Global styles
├── environments/                     # Environment-specific settings (e.g., dev, prod)
│   ├── environment.ts                # Default environment
│   ├── environment.prod.ts           # Production environment
└── index.html                        # Entry HTML file


## Dependencies

### Backend (Spring Boot)
1. **Core Framework**
   - `spring-boot-starter-web`: For building REST APIs.
   - `spring-boot-starter-data-jpa`: For database interaction.
   - `spring-boot-starter-security`: For authentication/authorization.

2. **Real-Time Collaboration**
   - `spring-websocket`: For real-time communication.
   - `spring-boot-starter-redis`: For caching and Pub/Sub messaging.

3. **Authentication**
   - `spring-security-oauth2-client`: For OAuth 2.0 login.
   - `spring-security-oauth2-resource-server`: For token validation.

4. **Database**
   - `spring-boot-starter-data-jpa` with PostgreSQL.
   - Hibernate for ORM.

5. **File Storage**
   - Azure Blob Storage SDK (`com.azure:azure-storage-blob`).

6. **Notification**
   - Kafka (`spring-kafka`) for event-driven notifications.

7. **Utilities**
   - `springdoc-openapi-ui`: For generating API documentation.
   - `modelmapper`: For DTO conversion.

---

### Frontend (Angular 18)
1. **Core Libraries**
   - `@angular/material`: For UI components.
   - `@angular/forms`: For handling forms.
   - `@angular/router`: For routing.

2. **Real-Time Communication**
   - `@ngx-socket-io`: For WebSocket integration.

3. **Rich Text Editing**
   - `ngx-quill`: For a WYSIWYG editor.
   - `@ngx-markdown`: For Markdown support.

4. **State Management**
   - `@ngrx/store`: For centralized state management.

5. **Styling**
   - `bootstrap` or `tailwindcss`: For responsive design.

6. **Notifications**
   - `ngx-toastr`: For toast notifications.
   - `angular-push-notifications`: For web push notifications.

---

## Tools for Project Management

1. **Version Control**
   - **Git** with GitHub or Azure DevOps.

2. **Project Management**
   - **Jira**: For sprint planning and issue tracking.
   - **Trello** or **ClickUp**: For task tracking.

3. **Collaboration**
   - **Slack** or **Microsoft Teams**: For team communication.
   - **Figma**: For designing UI prototypes.

4. **Testing**
   - **Postman**: For API testing.
   - **Selenium** or **Cypress**: For frontend end-to-end testing.

5. **CI/CD**
   - **Azure DevOps Pipelines** or **GitHub Actions**.

---

## Microsoft Azure Integration

1. **Hosting**
   - Azure App Service: Host Spring Boot microservices.
   - Azure Static Web Apps: Host Angular frontend.

2. **Database**
   - Azure SQL Database or Azure Cosmos DB for scalability.

3. **Storage**
   - Azure Blob Storage: For storing media files.
   - Azure Cache for Redis: For caching.

4. **Real-Time Communication**
   - Azure SignalR Service: For WebSocket communication.

5. **Notifications**
   - Azure Notification Hubs: For push notifications.

6. **Monitoring**
   - Azure Monitor: For logging and application insights.

---

# **Collaborative Note-Taking Application Database Design**

## **1. Users Table**
Holds user account details.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `username`       | VARCHAR(50)    | UNIQUE, NOT NULL        |
| `email`          | VARCHAR(100)   | UNIQUE, NOT NULL        |
| `password`       | VARCHAR(255)   | NOT NULL                |
| `avatar_url`     | TEXT           | NULLABLE                |
| `created_at`     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |
| `updated_at`     | TIMESTAMP      | ON UPDATE CURRENT_TIMESTAMP |

---

## **2. Roles Table**
Defines roles for users (e.g., Admin, Editor, Viewer).

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `name`           | VARCHAR(50)    | UNIQUE, NOT NULL        |
| `description`    | TEXT           | NULLABLE                |

---

## **3. User Roles Table**
Maps users to roles (many-to-many relationship).

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `user_id`        | UUID           | PK, FK → `Users(id)`    |
| `role_id`        | UUID           | PK, FK → `Roles(id)`    |

---

## **4. Authority Table**
Defines granular permissions (e.g., create_note, delete_note).

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `name`           | VARCHAR(50)    | UNIQUE, NOT NULL        |
| `description`    | TEXT           | NULLABLE                |

---

## **5. Role Authorities Table**
Maps roles to authorities (many-to-many relationship).

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `role_id`        | UUID           | PK, FK → `Roles(id)`    |
| `authority_id`   | UUID           | PK, FK → `Authority(id)`|

---

## **6. Collaborators Table**
Manages collaborations on projects and notes.

| Column Name      | Data Type       | Constraints                  |
|-------------------|----------------|-------------------------------|
| `id`             | UUID           | PK                           |
| `user_id`        | UUID           | FK → `Users(id)`             |
| `project_id`     | UUID           | FK → `Projects(id)`          |
| `role`           | ENUM('editor', 'viewer', 'admin') | NOT NULL |

---

## **7. Projects Table**
Tracks high-level projects that contain notes.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `name`           | VARCHAR(100)   | NOT NULL                |
| `description`    | TEXT           | NULLABLE                |
| `owner_id`       | UUID           | FK → `Users(id)`        |
| `created_at`     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |
| `updated_at`     | TIMESTAMP      | ON UPDATE CURRENT_TIMESTAMP |

---

## **8. Notes Table**
Stores individual notes within a project.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `project_id`     | UUID           | FK → `Projects(id)`     |
| `title`          | VARCHAR(255)   | NOT NULL                |
| `content`        | TEXT           | NULLABLE                |
| `created_by`     | UUID           | FK → `Users(id)`        |
| `created_at`     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |
| `updated_at`     | TIMESTAMP      | ON UPDATE CURRENT_TIMESTAMP |

---

## **9. Drafts Table**
Manages versions of notes for version control.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `note_id`        | UUID           | FK → `Notes(id)`        |
| `content`        | TEXT           | NULLABLE                |
| `created_at`     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |

---

## **10. Media Table**
Stores media files (images, videos) associated with notes.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `note_id`        | UUID           | FK → `Notes(id)`        |
| `media_url`      | TEXT           | NOT NULL                |
| `media_type`     | VARCHAR(50)    | e.g., 'image', 'video'  |
| `uploaded_at`    | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |

---

## **11. Activity Table**
Tracks user activities within the system.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `user_id`        | UUID           | FK → `Users(id)`        |
| `activity_type`  | VARCHAR(100)   | e.g., 'note_created'    |
| `details`        | JSON           | NULLABLE                |
| `timestamp`      | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |

---

## **12. Notifications Table**
Stores notifications sent to users.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `user_id`        | UUID           | FK → `Users(id)`        |
| `type`           | VARCHAR(50)    | e.g., 'email', 'push'   |
| `message`        | TEXT           | NOT NULL                |
| `status`         | ENUM('sent', 'pending') | DEFAULT 'pending' |
| `created_at`     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |

---

## **13. Logs Table**
Stores system and application logs.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `id`             | UUID           | PK                      |
| `level`          | VARCHAR(50)    | e.g., 'INFO', 'ERROR'   |
| `message`        | TEXT           | NOT NULL                |
| `timestamp`      | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP |

---

## **14. System Configurations Table**
Manages application-wide configurations.

| Column Name      | Data Type       | Constraints             |
|-------------------|----------------|--------------------------|
| `key`            | VARCHAR(100)   | PK                      |
| `value`          | TEXT           | NULLABLE                |
| `updated_at`     | TIMESTAMP      | ON UPDATE CURRENT_TIMESTAMP |

---

## **Relationships Summary**
1. **Users ↔ Roles**: Many-to-Many via `User Roles`.
2. **Roles ↔ Authorities**: Many-to-Many via `Role Authorities`.
3. **Projects ↔ Collaborators**: Many-to-Many via `Collaborators`.
4. **Projects ↔ Notes**: One-to-Many.
5. **Notes ↔ Drafts**: One-to-Many.
6. **Notes ↔ Media**: One-to-Many.
7. **Users ↔ Activity**: One-to-Many.
8. **Users ↔ Notifications**: One-to-Many.



# **Collaborative Note-Taking Application Documentation**

---

## **Global Response Format**
A unified response structure for all API endpoints.

### **Response Format**
```json
{
  "status": "SUCCESS/FAILURE",
  "message": "Descriptive message about the response",
  "data": "Payload returned in case of success",
  "error": "Error details (only present in case of failure)"
}


Authentication APIs
1. User Registration
Endpoint: /api/auth/register
Method: POST
Description: Registers a new user.
Request
{
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "securepassword"
}

Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "User registered successfully",
  "data": {
    "userId": "UUID"
  }
}
2. User Login
Endpoint: /api/auth/login
Method: POST
Description: Authenticates a user and generates a JWT token.
Request
json
Copy code
{
  "email": "john.doe@example.com",
  "password": "securepassword"
}
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Login successful",
  "data": {
    "token": "JWT_TOKEN",
    "userId": "UUID"
  }
}
User Management APIs
1. Fetch User Profile
Endpoint: /api/users/{userId}
Method: GET
Description: Retrieves user profile details by ID.
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "User profile retrieved successfully",
  "data": {
    "id": "UUID",
    "username": "john_doe",
    "email": "john.doe@example.com",
    "avatar_url": "https://example.com/avatar.png"
  }
}
2. Update User Profile
Endpoint: /api/users/{userId}
Method: PUT
Description: Updates the user profile information.
Request
json
Copy code
{
  "username": "new_username",
  "avatar_url": "https://example.com/new_avatar.png"
}
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "User profile updated successfully",
  "data": null
}
Project Management APIs
1. Create Project
Endpoint: /api/projects
Method: POST
Description: Creates a new project.
Request
json
Copy code
{
  "name": "New Project",
  "description": "Project description"
}
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Project created successfully",
  "data": {
    "projectId": "UUID"
  }
}
2. Fetch All Projects
Endpoint: /api/projects
Method: GET
Description: Retrieves all projects for the authenticated user.
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Projects retrieved successfully",
  "data": [
    {
      "id": "UUID",
      "name": "Project 1",
      "description": "Description of project 1"
    },
    {
      "id": "UUID",
      "name": "Project 2",
      "description": "Description of project 2"
    }
  ]
}
Note Management APIs
1. Create Note
Endpoint: /api/notes
Method: POST
Description: Creates a new note within a project.
Request
json
Copy code
{
  "projectId": "UUID",
  "title": "New Note",
  "content": "Note content here..."
}
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Note created successfully",
  "data": {
    "noteId": "UUID"
  }
}
2. Update Note
Endpoint: /api/notes/{noteId}
Method: PUT
Description: Updates an existing note.
Request
json
Copy code
{
  "title": "Updated Note Title",
  "content": "Updated note content..."
}
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Note updated successfully",
  "data": null
}
3. Fetch All Notes in a Project
Endpoint: /api/projects/{projectId}/notes
Method: GET
Description: Retrieves all notes within a specified project.
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Notes retrieved successfully",
  "data": [
    {
      "id": "UUID",
      "title": "Note 1",
      "content": "Content of note 1"
    },
    {
      "id": "UUID",
      "title": "Note 2",
      "content": "Content of note 2"
    }
  ]
}
Collaborators APIs
1. Add Collaborator
Endpoint: /api/projects/{projectId}/collaborators
Method: POST
Description: Adds a collaborator to a project.
Request
json
Copy code
{
  "userId": "UUID",
  "role": "editor"
}
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Collaborator added successfully",
  "data": null
}
2. Fetch Collaborators
Endpoint: /api/projects/{projectId}/collaborators
Method: GET
Description: Retrieves all collaborators for a specific project.
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Collaborators retrieved successfully",
  "data": [
    {
      "id": "UUID",
      "username": "john_doe",
      "role": "editor"
    },
    {
      "id": "UUID",
      "username": "jane_smith",
      "role": "viewer"
    }
  ]
}
Activity Logging APIs
1. Fetch Activity Logs
Endpoint: /api/activity
Method: GET
Description: Retrieves activity logs for auditing purposes.
Response
json
Copy code
{
  "status": "SUCCESS",
  "message": "Activity logs retrieved successfully",
  "data": [
    {
      "id": "UUID",
      "userId": "UUID",
      "activityType": "note_created",
      "details": {
        "noteId": "UUID",
        "title": "Sample Note"
      },
      "timestamp": "2024-11-18T10:30:00Z"
    }
  ]
}
Error Response Example
For validation or server-side errors:

json
Copy code
{
  "status": "FAILURE",
  "message": "Validation error",
  "error": {
    "field": "email",
    "message": "Email is already in use"
  }
}
