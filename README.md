# Notes App

A modern, production-ready Android notes application built with Kotlin, Jetpack Compose, and a robust Clean Architecture approach. This project is designed for scalability, maintainability, and high code quality, leveraging the latest Android development best practices and tooling.

---

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Key Libraries & Tools](#key-libraries--tools)
- [Code Quality](#code-quality)
- [How to Build & Run](#how-to-build--run)
- [How to Run Code Quality Tools](#how-to-run-code-quality-tools)
- [Documentation](#documentation)
- [License](#license)

---

## Project Overview
Notes App is a simple yet powerful note-taking application. It demonstrates:
- **MVVM (Model-View-ViewModel) pattern** for clear separation of concerns
- **Clean Architecture** for modular, testable, and maintainable code
- **Jetpack Compose** for modern, declarative UI
- **Room** for local data persistence
- **Hilt** for dependency injection
- **Navigation Component** for seamless navigation

---

## Architecture
This project follows the principles of **Clean Architecture** and **MVVM**:

- **Presentation Layer**: UI (Jetpack Compose) and ViewModels
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories, Room database, and data sources

This separation ensures testability, scalability, and maintainability.

---

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/sitharaj/notes/
│   │   │   ├── di/                # Dependency injection modules
│   │   │   ├── data/              # Data layer (Room, repositories, data sources)
│   │   │   ├── domain/            # Domain layer (use cases, models)
│   │   │   ├── presentation/      # UI layer (ViewModels, Compose screens)
│   │   │   └── NotesApp.kt        # Application class
│   │   └── res/                   # Resources (layouts, strings, etc.)
│   └── test/                      # Unit and integration tests
├── build.gradle.kts               # Module-level Gradle config
└── ...
```

---

## Key Libraries & Tools
- **Kotlin**: Modern, expressive programming language
- **Jetpack Compose**: Declarative UI toolkit
- **Room**: Local database
- **Hilt**: Dependency injection
- **Navigation Component**: Navigation between screens
- **Coroutines**: Asynchronous programming
- **JUnit, MockK, Robolectric**: Testing

---

## Code Quality
This project uses several tools to ensure code quality and maintainability:

### 1. JaCoCo
- **Purpose**: Code coverage reports for unit tests
- **How to run**:
  ```sh
  ./gradlew jacocoTestReport
  # Reports: app/build/reports/jacoco
  ```

### 2. Dokka
- **Purpose**: Generates HTML documentation from Kotlin source and KDoc comments
- **How to run**:
  ```sh
  ./gradlew dokkaHtml
  # Docs: app/build/dokka/html/index.html
  ```

### 3. Detekt
- **Purpose**: Static code analysis for Kotlin (style, complexity, best practices)
- **How to run**:
  ```sh
  ./gradlew detekt
  # Reports: app/build/reports/detekt
  ```

---

## How to Build & Run
1. **Clone the repository**
   ```sh
   git clone <repo-url>
   cd Notes
   ```
2. **Open in Android Studio**
3. **Build the project** (Build > Make Project)
4. **Run on an emulator or device**

---

## How to Run Code Quality Tools
- **Run all checks**:
  ```sh
  ./gradlew clean build jacocoTestReport dokkaHtml detekt
  ```
- **View reports**:
  - JaCoCo: `app/build/reports/jacoco`
  - Dokka: `app/build/dokka/html/index.html`
  - Detekt: `app/build/reports/detekt`

---

## Documentation
- **Dokka** generates API documentation from your KDoc comments.
- After running `./gradlew dokkaHtml`, open `app/build/dokka/html/index.html` in your browser.

---

## License
This project is licensed under the [Apache 2.0 License](LICENSE).

---

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.