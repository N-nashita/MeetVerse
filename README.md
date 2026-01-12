# MeetVerse

A comprehensive meeting management application built with JavaFX that enables users to create, manage, and track meetings with an intuitive interface and robust admin controls.

## Features

### User Features

- **User Authentication**: Secure login and signup functionality with password hashing
- **Meeting Management**:
  - Create and schedule meetings with participants
  - View all scheduled meetings with real-time countdown timers
  - Cancel/delete meetings you created through Settings
  - Support for both Online and In-Person meeting types
  - Automatic copiable meeting link generation for online meetings

- **User Dashboard**:
  - Personalized dashboard displaying all meetings
  - Meeting status indicators (Pending, Approved, Rejected)
  - Real-time countdown to approved meetings
  - Empty state messaging when no meetings are scheduled
- **Meeting History**:
  - Track and review completed meetings
  - View rejected meetings with proper status labels
  - Automatic archiving of past and rejected meetings
- **Profile Settings**:
  - Cancel meeting functionality
  - Logout option

### Admin Features

- **Admin Dashboard**:
  - Comprehensive overview of all meeting requests
  - Real-time countdown displays for approved meetings
  - Approve or reject pending meeting requests
  - Automatic history archiving for rejected meetings
- **User Management**:
  - View and manage all registered users
  - Add new users manually
  - Delete users (with automatic cleanup of related data)
  - Transfer admin role to other users
- **System Settings**: Configure application-wide settings
- **Meeting Oversight**:
  - Monitor all meetings in the system
  - Approve or reject meeting requests
  - View detailed meeting information with participants list

## Technology Stack

- **Java 21**: Core programming language
- **JavaFX 21**: Modern desktop UI framework
- **Maven**: Build automation and dependency management
- **SQLite**: Lightweight embedded database for data persistence
- **FXML**: Declarative UI layout
- **CSS**: Custom styling for the user interface

## Project Structure

```
MeetVerse/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/meetverse/
│       │       ├── Main.java                    # Application entry point
│       │       ├── Launcher.java                # JavaFX launcher
│       │       ├── Controllers/                 # UI controllers
│       │       │   ├── LoginController.java
│       │       │   ├── SignupController.java
│       │       │   ├── HomeController.java
│       │       │   ├── UserDashboardController.java
│       │       │   ├── AdminDashboardController.java
│       │       │   ├── CreateMeetingController.java
│       │       │   ├── HistoryController.java
│       │       │   ├── UsersController.java
│       │       │   ├── UserSettingsController.java
│       │       │   └── AdminSettingsController.java
│       │       └── util/                        # Utility classes
│       │           ├── DatabaseManager.java     # Database operations
│       │           └── Navigation.java          # Screen navigation
│       └── resources/
│           └── com/example/meetverse/
│               ├── *.fxml                       # UI layouts
│               ├── Homepage.css                 # Stylesheets
│               ├── Databases/                   # SQLite databases
│               └── images/                      # Application images
├── pom.xml                                      # Maven configuration
└── README.md                                    # This file
```

## Prerequisites

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.6+** (or use included Maven wrapper)
- **JavaFX SDK 21** (automatically handled by Maven)

## Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd MeetVerse
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

## Running the Application

### Using Maven

```bash
mvn clean javafx:run
```

### Using Maven Wrapper (Windows)

```cmd
mvnw.cmd clean javafx:run
```

### Using Maven Wrapper (Linux/Mac)

```bash
./mvnw clean javafx:run
```

## Building for Production

To create a distributable package:

```bash
mvn clean package
```

## Development

### Running Tests

```bash
mvn test
```

### Clean Build

```bash
mvn clean compile
```

## Database

The application uses SQLite for data persistence. The database files are stored in:

```
src/main/resources/com/example/meetverse/Databases/
```

### Database Schema

The application uses the following main tables:

- **users**: Stores user information with role-based access (Admin/User)
- **meetings**: Stores meeting details with status tracking (Pending, Approved, Rejected)
- **meeting_participants**: Many-to-many relationship between meetings and users
- **is_history**: Flag to separate active meetings from historical records

### Database Features

- Automatic database initialization on first run
- Password hashing using SHA-256 for security
- Foreign key constraints for data integrity
- Automatic cleanup of related records on deletion
- First registered user automatically becomes Admin

### Database Regeneration

If the `.db` file is deleted, the application will:

- Automatically create a new database file
- Recreate all tables with proper schema
- All previous data will be lost (fresh start required)

## Configuration

Application settings and configurations can be managed through:

- **User Settings**: Cancel meetings and logout
- **Admin Settings**: System-wide configurations and user management

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please open an issue in the repository.

## Acknowledgments

- JavaFX team for the excellent UI framework
- SQLite for the reliable embedded database
- Maven community for build automation tools
