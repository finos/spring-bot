# Sample Kotlin Project

This is a sample Kotlin Spring Boot application that demonstrates various Kotlin features and best practices.

## Features Demonstrated

### 1. **Data Classes**

- `Task` - Represents a task with immutable properties
- `ApiResponse<T>` - Generic response wrapper
- `CreateTaskRequest` and `UpdateTaskRequest` - Request DTOs

### 2. **Extension Functions**

- `String.generateId()` - Extension function for generating IDs
- `List<Task>.getCompletedTasks()` - Extension function for filtering completed tasks
- `List<Task>.getPendingTasks()` - Extension function for filtering pending tasks
- `Task.isOverdue()` - Extension function for checking if a task is overdue

### 3. **Coroutines**

- Async task creation and updates using `suspend` functions
- `withContext(Dispatchers.IO)` for background processing
- Simulated async operations with `delay()`

### 4. **Spring Boot Integration**

- RESTful API endpoints
- Dependency injection with `@Autowired`
- Service layer with `@Service` annotation
- Controller with `@RestController`

### 5. **Kotlin Language Features**

- Null safety with `?` operator
- Smart casts
- String templates
- Property access syntax
- Default parameter values
- Named parameters

### 6. **Testing**

- JUnit 5 tests with Kotlin
- MockMvc for testing REST endpoints
- Extension function testing
- Backtick notation for test names

## Project Structure

```
src/main/kotlin/org/finos/springbot/tool/llm/sample/
├── SampleKotlinApp.kt          # Main application class
└── TaskController.kt           # REST controller
└── TaskService.kt              # Service layer
└── TaskUtils.kt                # Utility functions

src/test/kotlin/org/finos/springbot/tool/llm/sample/
└── SampleKotlinAppTest.kt     # Test cases
```

## API Endpoints

- `POST /api/tasks` - Create a new task
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/completed` - Get completed tasks
- `GET /api/tasks/pending` - Get pending tasks
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build the project

```bash
mvn clean compile
```

### Run the application

```bash
mvn spring-boot:run
```

### Run tests

```bash
mvn test
```

## Example Usage

### Create a task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Kotlin",
    "description": "Study Kotlin language features"
  }'
```

### Get all tasks

```bash
curl http://localhost:8080/api/tasks
```

### Get completed tasks

```bash
curl http://localhost:8080/api/tasks/completed
```

## Key Kotlin Features Used

1. **Data Classes**: Automatic `equals()`, `hashCode()`, `toString()`, and `copy()` methods
2. **Extension Functions**: Adding functionality to existing classes
3. **Coroutines**: Asynchronous programming with structured concurrency
4. **Null Safety**: Compile-time null safety checks
5. **Smart Casts**: Automatic type casting in safe contexts
6. **String Templates**: String interpolation with `$` and `${}`
7. **Default Arguments**: Optional parameters with default values
8. **Named Parameters**: Explicit parameter naming for clarity

## Configuration

The project uses Maven with the following key configurations:

- **JVM Target**: 17 (for compatibility with Spring Boot 3.x)
- **Kotlin Version**: 1.9.23
- **Spring Boot**: Latest version from parent POM
- **Testing**: JUnit 5 with MockMvc

## Dependencies

- Spring Boot Web Starter
- Spring Boot Test Starter
- Kotlin Standard Library
- Jackson for JSON processing
- Logback for logging
- Apache Commons Lang3

This sample demonstrates how to build a modern Kotlin application with Spring Boot, showcasing the power and expressiveness of the Kotlin language while maintaining compatibility with the Java ecosystem.
