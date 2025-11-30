# Notes App

  ./gradlew dokkaGenerateHtml

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Code Quality](#code-quality)
- [How to Build & Run](#how-to-build--run)
- [How to Run Code Quality Tools](#how-to-run-code-quality-tools)
- [BDD (Behavior Driven Development)](#bdd-behavior-driven-development---cucumber-tests-)
- [Documentation](#documentation)
- [License](#license)
---

Notes App is a simple yet powerful note-taking application. It demonstrates:
- **MVVM (Model-View-ViewModel) pattern** for clear separation of concerns
  ./gradlew clean build jacocoTestReport dokkaGenerateHtml detekt
- **Jetpack Compose** for modern, declarative UI
- **Hilt** for dependency injection
- **Navigation Component** for seamless navigation

---
## Architecture
This project follows the principles of **Clean Architecture** and **MVVM**:
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
  ./gradlew dokkaGenerateHtml
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
  ./gradlew clean build jacocoTestReport dokkaGenerateHtml detekt
  ```
- **View reports**:
  - JaCoCo: `app/build/reports/jacoco`
  - Dokka: `app/build/dokka/html/index.html`
  - Detekt: `app/build/reports/detekt`
  - Cucumber (BDD) reports: `app/build/reports/cucumber/cucumber.json` and `app/build/reports/cucumber/cucumber.html`

---

## Documentation
- **Dokka** generates API documentation from your KDoc comments.
After running `./gradlew dokkaGenerateHtml`, open `app/build/dokka/html/index.html` in your browser.

---
## BDD (Behavior Driven Development) — Cucumber Tests 📋

This project includes BDD-style tests written using Cucumber (JUnit runner). Below are the commands and useful pointers to run the BDD tests locally and generate HTML/JSON reports.

### Where the tests are
- Feature files (Gherkin): `app/src/test/resources/features`
- Step definitions & runner: `app/src/test/java/com/sitharaj/notes/bdd` (see `RunCucumberTest.kt` and `NotesBddSteps.kt`)

### Quick commands
- Run the Cucumber JUnit runner (devDebug variant):
```bash
./gradlew :app:testDevDebugUnitTest --tests "*RunCucumberTest*"
```

- Run the runner for other variants (swap `DevDebug` for `ProdDebug`/`ProdRelease`):
```bash
./gradlew :app:testProdDebugUnitTest --tests "*RunCucumberTest*"
```

- Run all unit tests for the module:
```bash
./gradlew :app:testDevDebugUnitTest
```

### Cucumber report files
- JSON report: `app/build/reports/cucumber/cucumber.json` (machine readable)
- HTML (human readable): `app/build/reports/cucumber/cucumber.html` (open in a browser)
- JUnit HTML report for the runner: `app/build/reports/tests/testDevDebugUnitTest/classes/com.sitharaj.notes.bdd.RunCucumberTest.html`
- JUnit XML test results (if needed for CI): `app/build/test-results/testDevDebugUnitTest/TEST-com.sitharaj.notes.bdd.RunCucumberTest.xml`

### Open the report (Mac)
```bash
open app/build/reports/cucumber/cucumber.html
```

### Bundle reports (optional)
To zip the report files for sharing or CI: 
```bash
cd app/build/reports/cucumber
zip -r cucumber-report.zip cucumber.json cucumber.html
```

### CI Tips
- Add the Cucumber JSON & HTML files as artifacts to your CI job so they can be retained and reviewed: `app/build/reports/cucumber/*`.  
- Optionally, use the JSON (`cucumber.json`) in CI to generate additional reports (e.g., using `cucumber-reporting` maven plugin or a step to publish HTML). 

### Debugging
- If the runner fails, ensure the step definitions are under the `glue` package (`com.sitharaj.notes.bdd`) and the `CucumberOptions` have the `features` path pointing to `src/test/resources/features`.
---

---

## Offline-First Approach
- The Notes App is designed with an **offline-first** mindset. All notes are stored locally using Room database, ensuring your data is always available—even without an internet connection.
- When online, the app can be extended to sync with cloud services, but your core experience and data remain robust and accessible offline.

---

## License
This project is licensed under the [Apache 2.0 License](LICENSE).

---

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.