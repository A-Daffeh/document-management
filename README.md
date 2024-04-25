## Document Management System

## Introduction
The Secure Document Management System is a robust and user-friendly application designed to securely store, update, upload, and delete documents. This application addresses the need for a centralized and secure repository for sensitive information, offering a seamless and efficient solution for document management.
The Secure Document Management System offers a comprehensive solution for organizations to securely store, manage, and collaborate on documents. With robust security measures and a user-friendly interface, it addresses the needs of businesses requiring a reliable and secure document management platform.

## Key Features

### User Authentication and Authorization
Users must authenticate themselves to access the system, ensuring secure access.

### Multi-Factor Authentication (MFA)
Users can enable MFA for an additional layer of security during login.

### Access Control
Role-based access control allows to define user privileges based on their roles and permissions, maintaining data integrity.
Authorized users can perform actions (update, delete, etc) on documents.

### Document Upload
Users can easily upload documents through a user-friendly interface.
Supported document formats include DOC, DOCX, XLS, PDFs, etc.

### Document Search
A powerful search feature enables users to quickly locate specific documents based on document metadata (name, file extension).
Users can search, filter, and go through different pages of documents.
Advanced filtering options enhance document retrieval efficiency.

### Audit Trail
The system logs all user activities, providing a detailed audit trail.
Audit logs help administrators monitor and review user actions for security and compliance purposes.

# Functional Requirements

## User Account

**New Account**
1. The application should allow users to create a new account using basic information, email(all emails are unique), and password.
2. The application should disabled all newly created accounts until verified.
3. The application should send an email with a link to confirm new user account.
4. Only after verifying a new account should a user be able to log into the application.

**Log In**
1. The application should allow users to enter an email and password to log in.
2. If MFA is set up, the application should ask for a QR code after entering correct email and password.
3. After 6 failed login attempts, user account should be locked for 15 minutes (mitigate brute force attack).
4. After 90 days, user password should expire therefore can't log in until password is updated (password rotation).

**Reset Password**
1. The application should allow users to reset their password.
2. The application should send a link to users' email to reset their password (link to be invalid after being clicked on).
3. The application should present a screen with a form to reset password when the link is clicked.
4. If a password is reset successfully, user should be able to log in using the new password.
5. The application should allow users to reset their password as many times as they need.

**MFA (Multi-Factor Authentication)**  
1. The application should allow users to set up Multi-Factor Authentication to help secure their account.
2. Multi-Factor Authentication should use a QR code on users' mobile phone.
3. The application should allow users to scan a QR code using an authenticator application on their phone to set up Multi-Factor Authentication.
4. The application should ask users to enter the QR code from their mobile phone authenticator application in order to log in successfully.

**Profile**
1. The application should allow users to update their basic information while logged in.
2. The application should allow users to update their password while logged in.
3. The application should allow users to update their account settings while logged in.
4. The application should allow users to update their profile picture while logged in.

## Document Management

**Document List**
1. The application should show a list of all the documents uploaded in the homepage.
2. The application should show some details (name, size, owner, type, etc) about each document in the list. 
3. The application should allow logged in users to upload new documents.
4. The application should have pagination for the document list.
5. The application should allow to set how many documents to display per page.
6. The application should allow to search documents by name (result should also include pagination).
7. The application should allow to click on a document to see more details.

**Document Details**
1. The application should show details of a document when clicked on.
2. The document details should include document owner.
3. The application should allow to update the name and description of a document (in detail page).
4. The application should allow to download a document (in detail page).
5. The application should allow to delete the document (in detail page).

## Access Control
**User Role**
1. The application should give roles to users.
2. The application roles should contain specific permissions (authorities).
3. The application roles should grant different access levels.
4. The application should allow only users with proper roles to be able to perform certain actions.
5. The application should only allow non-user role users to update account settings.
6. The application should only allow non-user role users to update account roles.
7. The application should only allow users with "delete" document permission to delete documents.
8. The application should only allow non-user role users to view other users in the system. 

## Audit Trail
1. The application should keep track of who created an entity (user, document, etc).
2. The application should keep track of when an entity (user, document, etc) was created.
3. The application should keep track of who updated an entity (user, document, etc). 
4. The application should keep track of when an entity (user, document, etc) was updated.

## Domain Model Class Diagram
```mermaid
classDiagram
    class User {
        +id: SERIAL
        +user_id: VARCHAR(255)
        +first_name: VARCHAR(50)
        +last_name: VARCHAR(50)
        +email: VARCHAR(100)
        +phone: VARCHAR(30)
        +bio: VARCHAR(255)
        +reference_id: VARCHAR(255)
        +qr_code_secret: VARCHAR(255)
        +qr_code_image_uri: TEXT
        +image_url: VARCHAR(255) = 'https://cdn-icons-png.flaticon.com/512/149/149071.png'
        +last_login: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
        +login_attempts: INTEGER = 0
        +mfa: BOOLEAN = FALSE
        +enabled: BOOLEAN = FALSE
        +account_non_expired: BOOLEAN = FALSE
        +account_non_locked: BOOLEAN = FALSE
        +created_by: BIGINT
        +updated_by: BIGINT
        +created_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
        +updated_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
    }

    class Confirmation {
        +id: SERIAL
        +key: VARCHAR(255)
        +user_id: BIGINT
        +reference_id: VARCHAR(255)
        +created_by: BIGINT
        +updated_by: BIGINT
        +created_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
        +updated_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
    }

    class Credential {
        +id: SERIAL
        +password: VARCHAR(255)
        +reference_id: VARCHAR(255)
        +user_id: BIGINT
        +created_by: BIGINT
        +updated_by: BIGINT
        +created_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
        +updated_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
    }

    class Document {
        +id: SERIAL
        +document_id: VARCHAR(255)
        +reference_id: VARCHAR(255)
        +extension: VARCHAR(10)
        +formatted_size: VARCHAR(20)
        +icon: VARCHAR(255)
        +name: VARCHAR(50)
        +size: BIGINT
        +uri: VARCHAR(255)
        +description: VARCHAR(255)
        +created_by: BIGINT
        +updated_by: BIGINT
        +created_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
        +updated_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
    }

    class Role {
        +id: SERIAL
        +authorities: VARCHAR(255)
        +name: VARCHAR(255)
        +reference_id: VARCHAR(255)
        +created_by: BIGINT
        +updated_by: BIGINT
        +created_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
        +updated_at: TIMESTAMP(6) WITH TIME ZONE = CURRENT_TIMESTAMP
    }

    class UserRole {
        +id: SERIAL
        +user_id: BIGINT
        +role_id: BIGINT
    }

    User --|> User : created_by
    User --|> User : updated_by
    Confirmation --|> User : user_id
    Confirmation --|> User : created_by
    Confirmation --|> User : updated_by
    Credential --|> User : user_id
    Credential --|> User : created_by
    Credential --|> User : updated_by
    Document --|> User : created_by
    Document --|> User : updated_by
    Role --|> User : created_by
    Role --|> User : updated_by
    UserRole --|> User : user_id
    UserRole --|> Role : role_id
```

## Database Design ER-Diagram
```mermaid
erDiagram
    %% Table Definitions
    USERS {
        int id "Primary Key"
        varchar user_id "Unique"
        varchar first_name
        varchar last_name
        varchar email "Unique"
        varchar phone
        varchar bio
        varchar reference_id
        varchar qr_code_secret
        text qr_code_image_uri
        varchar image_url
        timestamp last_login
        int login_attempts
        bool mfa
        bool enabled
        bool account_non_expired
        bool account_non_locked
        bigint created_by "Foreign Key"
        bigint updated_by "Foreign Key"
        timestamp created_at
        timestamp updated_at
    }

    CONFIRMATIONS {
        int id "Primary Key"
        varchar key "Unique"
        bigint user_id "Foreign Key"
        varchar reference_id
        bigint created_by "Foreign Key"
        bigint updated_by "Foreign Key"
        timestamp created_at
        timestamp updated_at
    }

    CREDENTIALS {
        int id "Primary Key"
        varchar password
        varchar reference_id
        bigint user_id "Foreign Key"
        bigint created_by "Foreign Key"
        bigint updated_by "Foreign Key"
        timestamp created_at
        timestamp updated_at
    }

    DOCUMENTS {
        int id "Primary Key"
        varchar document_id "Unique"
        varchar reference_id
        varchar extension
        varchar formatted_size
        varchar icon
        varchar name
        bigint size
        varchar uri
        varchar description
        bigint created_by "Foreign Key"
        bigint updated_by "Foreign Key"
        timestamp created_at
        timestamp updated_at
    }

    ROLES {
        int id "Primary Key"
        varchar authorities
        varchar name
        varchar reference_id
        bigint created_by "Foreign Key"
        bigint updated_by "Foreign Key"
        timestamp created_at
        timestamp updated_at
    }

    USER_ROLES {
        int id "Primary Key"
        bigint user_id "Foreign Key"
        bigint role_id "Foreign Key"
    }

    %% Relationships
    USERS ||--|{ USER_ROLES : "Has many user roles"
    USER_ROLES ||--|| USERS : "Belongs to a user"
    USER_ROLES ||--|| ROLES : "Belongs to a role"
    
    USERS ||--|{ CONFIRMATIONS : "Has many confirmations"
    CONFIRMATIONS ||--|| USERS : "Belongs to a user"

    USERS ||--|{ CREDENTIALS : "Has many credentials"
    CREDENTIALS ||--|| USERS : "Belongs to a user"
    
    USERS ||--|{ DOCUMENTS : "Has many documents"
    DOCUMENTS ||--|| USERS : "Created by a user"

    USERS ||--|{ ROLES : "Created by or updated by a user"
    ROLES ||--|| USERS : "Created by or updated by a user"
```

## High Level Architecture Diagram
```mermaid
flowchart TD
    %% Frontend
    subgraph "Frontend (React)"
        subgraph "UI Components"
            RC[React Components]
        end
        
        subgraph "State Management"
            RX[Redux Store]
            RAC[Redux Actions]
            RRD[Redux Reducers]
        end
        
        subgraph "API Communication"
            RG[Axios Requests]
        end
        
        subgraph "Routing"
            RR[React Router]
        end
        
        subgraph "Styling"
            RBS[React Bootstrap]
        end
    end
    
    %% Connections in Frontend
    RC --> RX
    RC --> RG
    RC --> RR
    RC --> RBS
    
    RX --> RAC
    RX --> RRD
    
    RAC --> RG
    
    %% Backend
    subgraph "Backend (Spring Boot)"
        subgraph "Web Layer"
            BC[Controllers]
            BSEC[Security Configuration]
        end
        
        subgraph "Service Layer"
            BS[Services]
            BDM[Document Management Service]
        end
        
        subgraph "Data Access Layer"
            BDR[Repositories]
            BDMR[Document Management Repositories]
        end
    end
    
    %% Connections in Backend
    BC --> BS
    BC --> BSEC
    
    BS --> BDR
    BS --> BDM
    
    BDR --> DB[PostgreSQL]
    BDM --> BDMR
    
    %% Database
    subgraph "Database"
        DB[PostgreSQL]
    end
    
    %% UserController
    subgraph "UserController"
        RPOST[POST /user/register]
        GGET[GET /user/verify/account]
        PGET[GET /user/profile]
        PUPATCH[PATCH /user/update]
        PUROLEPATCH[PATCH /user/updaterole]
        PTAEPATCH[PATCH /user/toggleaccountexpired]
        PTALPATCH[PATCH /user/toggleaccountlocked]
        PTAEPATCH2[PATCH /user/toggleaccountenabled]
        PTCRPATCH[PATCH /user/togglecredentialsexpired]
        PSUMFAPATCH[PATCH /user/mfa/setup]
        PCMFPATCH[PATCH /user/mfa/cancel]
        VERIFYPWGET[GET /user/verify/password]
        UPWDPATCH[PATCH /user/updatepassword]
        RESETPOST[POST /user/resetpassword]
        DORESETPWPOST[POST /user/resetpassword/reset]
        LGET[GET /user/list]
        UPPATCH[PATCH /user/photo]
        LOUTPOST[POST /user/logout]

        RPOST --> GGET
        GGET --> PGET
        PGET --> PUPATCH
        PUPATCH --> PUROLEPATCH
        PUROLEPATCH --> PTAEPATCH
        PTAEPATCH --> PTALPATCH
        PTALPATCH --> PTAEPATCH2
        PTAEPATCH2 --> PTCRPATCH
        PTCRPATCH --> PSUMFAPATCH
        PSUMFAPATCH --> PCMFPATCH
        PCMFPATCH --> VERIFYPWGET
        VERIFYPWGET --> UPWDPATCH
        UPWDPATCH --> RESETPOST
        RESETPOST --> DORESETPWPOST
        DORESETPWPOST --> LGET
        LGET --> UPPATCH
        UPPATCH --> LOUTPOST
    end
    
    %% DocumentController
    subgraph "DocumentController"
        UPOST[POST /documents/upload]
        GGET[GET /documents]
        SGET[GET /documents/search]
        GIDGET[GET /documents/documentId]
        UPATCH[PATCH /documents/documentId]
        DGET[GET /documents/download/documentName]

        UPOST --> GGET
        GGET --> SGET
        SGET --> GIDGET
        GIDGET --> UPATCH
        UPATCH --> DGET
    end
    
    %% JWT Authentication
    subgraph "JWT Authentication"
        BC -->|JWT Tokens| BSEC
        RG -->|JWT Tokens| BC
    end
```
