# MeetVerse

A comprehensive meeting management application built with JavaFX that enables users to create, manage, and track meetings with an intuitive interface and robust admin controls.

## Features

### User Features

- **User Authentication**: Secure login and signup functionality
- **Meeting Management**: Create and schedule meetings with ease
- **User Dashboard**: Personalized dashboard for managing your meetings
- **Meeting History**: Track and review past meetings
- **Profile Settings**: Customize your user profile and preferences

### Admin Features

- **Admin Dashboard**: Comprehensive overview of all system activities
- **User Management**: View and manage all registered users
- **System Settings**: Configure application-wide settings
- **Meeting Oversight**: Monitor and manage all meetings in the system

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

## Configuration

Application settings and configurations can be managed through:

- User Settings (for individual users)
- Admin Settings (for system-wide configurations)

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
