# laa-spring-boot-library-tutorial
[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/laa-spring-boot-library-tutorial/badge)](https://github-community.service.justice.gov.uk/repository-standards/laa-spring-boot-library-tutorial)

## Overview

Companion service for the LAA Java community tutorials. The first checkpoint preserves the
template-generated item API while establishing the Java 25, Gradle, package, module, and
PostgreSQL foundations used by later tutorials.

See [TEMPLATE_PROVENANCE.md](TEMPLATE_PROVENANCE.md) for the source template revision and
reproducible initialiser choices.

The project uses the `laa-spring-boot-gradle-plugin` Gradle plugin which provides
sensible defaults for the following plugins:

- [Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
- [Dependency Management](https://plugins.gradle.org/plugin/io.spring.dependency-management)
- [Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [Java](https://docs.gradle.org/current/userguide/java_plugin.html)
- [Maven Publish](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Spring Boot](https://plugins.gradle.org/plugin/org.springframework.boot)
- [Test Logger](https://github.com/radarsh/gradle-test-logger-plugin)
- [Versions](https://github.com/ben-manes/gradle-versions-plugin)

The plugin is provided by [laa-spring-boot-common](https://github.com/ministryofjustice/laa-spring-boot-common)
and is publicly available from Maven Central. The common repository contains more information about its usage.

### Project Structure
Includes the following subprojects:

- `laa-spring-boot-library-tutorial-api` - example OpenAPI specification used for generating API stub interfaces and documentation.
- `laa-spring-boot-library-tutorial-service` - example REST API service with CRUD operations interfacing a JPA repository with a PostgreSQL database.

## Initialising Your Service from This Template

This repository ships with `initialise-service.py` - a Python 3 script that renames every
placeholder value across all source files, renames subproject directories, restructures package
names, and cleans up the README in one guided run. Optionally it can also switch the database
from H2 to PostgreSQL (RDS-ready).

The script is a thin launcher; its implementation lives in `.initialise-service-tools/template_tools/`.
Fragment files used by the PostgreSQL option are in `.initialise-service-fragments/`.

### Prerequisites

- **Python 3.9 or later** (no third-party packages required - all imports are from the standard library).
- Run the script from the **root of the repository**.

### Running the Script

```bash
# Fully interactive - the script will prompt you for every value
python3 initialise-service.py

# Pass the service name up-front to skip the first prompt
python3 initialise-service.py laa-my-new-service

# Preview every change without touching any files
python3 initialise-service.py laa-my-new-service --dry-run

# Override individual values directly (skips those prompts)
python3 initialise-service.py laa-my-new-service \
    --package-suffix my.new.service \
    --class-prefix MyNewService
```

### What the Script Prompts You For

| # | Prompt | Behaviour | Example |
|---|--------|-----------|---------|
| 1 | **New service name** *(required)* | Lowercase kebab-case. Drives all other defaults. | `laa-crime-applications` |
| 2 | **Java package suffix** | Defaults to a dot-notation form of the service name. Override if needed. | `crime.applications` |
| 3 | **Application class prefix** | Defaults to PascalCase form of the service name. | `CrimeApplications` |
| 4 | **Application version** | Defaults to `1.0.0` (kept as-is in `gradle.properties`). | `1.0.0` |
| 5 | **Server port** | Defaults to `8081`. Updates `application.yml`, `Dockerfile`, `docker-compose.yml`, and README example URLs. | `8080` |
| 6 | **Management (actuator) port** | Defaults to `8181`. | `9090` |
| 7 | **Delete script and fragments when done?** | Defaults to `no`. If `yes`, removes `initialise-service.py`, `.initialise-service-tools/`, and `.initialise-service-fragments/` after a successful run. | `y` / `n` |
| 8 | **Use PostgreSQL instead of H2?** | Defaults to `no`. If `yes`, applies the PostgreSQL configuration step - see below. | `y` / `n` |

Before any changes are applied, the script displays a **confirmation summary** showing every value
it is about to use. You must explicitly confirm before it writes anything.

### What Gets Changed

The script performs the following operations **in order**:

1. **File content replacements** - walks every non-binary, non-generated file in the repository
   and replaces the template placeholders with your values. The replacements are applied
   most-specific-first to avoid partial collisions:
   - `laa-spring-boot-library-tutorial` → your root project name
   - `laa-spring-boot-library-tutorial` → `laa-{your-service}`
   - `laa-spring-boot-library-tutorial` → your service name (module paths, Gradle paths)
   - `uk.gov.justice.laa.library.tutorial` → your Java package
   - `uk.gov.laa.library.tutorial` → your Gradle/Dependabot package group
   - `LibraryTutorial` → your class prefix (class names, Jacoco exclusions)
   - `LAA Spring Boot Library Tutorial` → your display name (`application.yml`, `info.app.*`)

2. **Java source file renames** - renames `LibraryTutorialApplication.java` and
   `LibraryTutorialApplicationTests.java` to match your class prefix.

3. **Java package directory renames** - moves the `uk/gov/justice/laa/springboot/microservice`
   directory tree under every `src/.../java` root to your new package path.

4. **Subproject directory renames** - renames `laa-spring-boot-library-tutorial-api` and
   `laa-spring-boot-library-tutorial-service` to `{your-service}-api` and `{your-service}-service`.

5. **Port number updates** *(only if you changed the defaults)* - targeted replacements in
   `application.yml`, `Dockerfile`, `docker-compose.yml`, and `README.md`.

6. **README cleanup** - removes the ⚠️ WORK IN PROGRESS banner and replaces the
   *Setup Instructions* section with a TODO placeholder for you to fill in.

7. **PostgreSQL configuration** *(only if selected at prompt 8)* - replaces H2 with PostgreSQL +
   Flyway in `build.gradle` and `application.yml`, adds a `postgres` service to `docker-compose.yml`,
   writes Flyway migrations, and adds `TestcontainersConfig.java` for integration tests.

### Files and Directories That Are Never Modified

The following are intentionally skipped:

| Skipped | Reason |
|---------|--------|
| `.git/`, `.gradle/`, `build/`, `bin/`, `generated/` | Generated / VCS internals |
| `.initialise-service-tools/`, `.initialise-service-fragments/` | Script implementation and fragment files - not part of your service |
| `gradlew`, `gradlew.bat`, `gradle-wrapper.jar` | Gradle wrapper - must not be altered |
| `initialise-service.py` | The script itself |
| `*.class`, `*.jar`, `*.exe`, binary assets | Binary files that cannot be text-replaced |

Any new source files you add in the future will be processed automatically - no changes to the
script are needed.

### After Running the Script

The script prints a checklist of remaining manual steps:

1. `git diff` - review every change before committing.
2. Rewrite the TODO section in `README.md` to describe your service.
3. Replace `open-api-specification.yml` with your own API design.
4. Set `sentry.dsn` and `sentry.environment` in `application.yml`.
5. Set your team as code owner in `.github/CODEOWNERS`.
6. Delete the example `Item*` classes, `schema.sql` / `data.sql` (H2) or Flyway migrations (PostgreSQL) if not needed.

If you chose PostgreSQL, two additional steps apply:

7. Run `helm dependency update .helm/{service-name}` and configure the image registry and repository in each values file.
8. Docker must be running locally for integration tests (Testcontainers starts a real PostgreSQL container).

---

## TODO: Update this README

Replace this section with clear documentation for your service. Include what it does, how to run it locally, environment variables, and any other details relevant to developers.

### Database scripts
The *.sql scripts in  `src/main/resources` have been included to provide an example database for demonstration purposes only and should be removed for your application.

## Build And Run Application

### Build application
`./gradlew clean build`

### Run integration tests
`./gradlew integrationTest`

### Run application
`./gradlew bootRun`

### Run application via Docker
`docker compose up`

### Debug application running via Docker

#### Configuration

* Go to Run > Edit Configurations
* Click + (Add New Configuration)
* Select Remote JVM Debug
* Configure:
* Name: Docker Debug
* Debugger mode: Attach to remote JVM
* Host: localhost
* Port: 5005
* Use module classpath: Select (laa-spring-boot-library-tutorial)

#### Debugging
* run `docker compose up`
* run > Debug 'Docker Debug'

#### Local Development Logging

When running with the `local` profile, structured logging is disabled, for console output:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Logging Configuration

This application uses **ECS (Elastic Common Schema) structured logging** for production environments and console logging for local development.

#### Structured Logging (Default/Production)

By default, the application outputs logs in ECS JSON format with distributed tracing support:
```json
{
  "@timestamp": "2026-03-06T16:25:18.992904Z",
  "ecs": {
    "version": "8.11"
  },
  "log": {
    "level": "INFO",
    "logger": "uk.gov.justice.laa.library.tutorial.LibraryTutorialApplication"
  },
  "message": "Getting all items",
  "process": {
    "pid": 49402,
    "thread": {
      "name": "http-nio-8080-exec-2"
    }
  },
  "service": {
    "environment": "local",
    "name": "laa-spring-boot-library-tutorial",
    "node": {
      "name": "unknown"
    },
    "version": "1.0.0"
  },
  "spanId": "fe4586c5fd5f7021",
  "traceId": "69aaffee8d19869cfe4586c5fd5f7021"
}
```
#### logback-spring.xml Conflicts

Adding `logback-spring.xml` will:
- Override the profile-based logging configuration in `application.yml`

## Application Endpoints

### API Documentation

#### Swagger UI
- http://localhost:8081/swagger-ui/index.html

#### API docs (JSON)
- http://localhost:8081/v3/api-docs

### Actuator Endpoints
The following actuator endpoints have been configured:
- http://localhost:8081/actuator
- http://localhost:8081/actuator/health

## Application Configuration

### Sentry
In order to integrate with Sentry, the following properties need to be configured in the `application.yml`:

```
sentry:
  dsn: <configure sentry dsn url here>
  environment: <configure environment name here>
```

## Libraries Used
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html) - used to provide various endpoints to help monitor the application, such as view application health and information.
- [Spring Boot Web](https://docs.spring.io/spring-boot/reference/web/index.html) - used to provide features for building the REST API implementation.
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa.html) - used to simplify database access and interaction, by providing an abstraction over persistence technologies, to help reduce boilerplate code.
- [Springdoc OpenAPI](https://springdoc.org/) - used to generate OpenAPI documentation. It automatically generates Swagger UI, JSON documentation based on your Spring REST APIs.
- used to capture application exception events at runtime, which can be monitored via the Sentry UI.
- [Lombok](https://projectlombok.org/) - used to help to reduce boilerplate Java code by automatically generating common
  methods like getters, setters, constructors etc. at compile-time using annotations.
- [MapStruct](https://mapstruct.org/) - used for object mapping, specifically for converting between different Java object types, such as Data Transfer Objects (DTOs)
  and Entity objects. It generates mapping code at compile code.
- [PostgreSQL](https://www.postgresql.org/) - used to provide a local/example database.
- [Sentry for Java SDK](https://docs.sentry.io/platforms/java/) - used to capture application exception events at runtime, which can be monitored via the Sentry UI.

## ⚠️ Temporary Dependency Overrides

The following declarations intentionally override dependency versions managed by the
`laa-spring-boot-gradle-plugin` from `laa-spring-boot-common` (and its imported Spring Boot dependency management).
They are **temporary** and should be removed when `laa-spring-boot-common` supplies the required versions.
Add any future dependency override to this table in the same change that introduces it to the Gradle build.

| Spring Boot BOM property     | Version managed by `laa-spring-boot-common` | Override used by this service | Affected dependency family | Date Added |
|------------------------------|---------------------------------------------|-------------------------------|----------------------------|------------|
| `jackson-2-bom.version`      | `2.21.2`                                    | `2.21.5`                      | Jackson 2                  | 2026-07-22 |
| `jackson-bom.version`        | `3.1.2`                                     | `3.1.5`                       | Jackson 3                  | 2026-07-22 |
| `logback.version`            | `1.5.32`                                    | `1.5.36`                      | Logback                    | 2026-07-22 |
| `micrometer.version`         | `1.16.5`                                    | `1.16.6`                      | Micrometer                 | 2026-07-22 |
| `spring-data-bom.version`    | `2025.1.5`                                  | `2025.1.6`                    | Spring Data 4.0.6          | 2026-07-22 |
| `spring-framework.version`   | `7.0.7`                                     | `7.0.8`                       | Spring Framework           | 2026-07-22 |
| `tomcat.version`             | `11.0.21`                                   | `11.0.23`                     | Embedded Tomcat            | 2026-07-22 |

Other dependencies with explicit versions in the Gradle build are direct dependency declarations, not overrides of
versions managed by `laa-spring-boot-common`.
