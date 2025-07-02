---
description: Vue.js best practices and patterns for modern web applications
globs: **/*.vue, **/*.ts, components/**/*
---

# Vue.js Best Practices

## Component Structure

- Use Composition API over Options API
- Keep components small and focused
- Use proper TypeScript integration
- Implement proper props validation
- Use proper emit declarations
- Keep template logic minimal

## Composition API

- Use proper ref and reactive
- Implement proper lifecycle hooks
- Use composables for reusable logic
- Keep setup function clean
- Use proper computed properties
- Implement proper watchers

## State Management

- Use Vuex for state management
- Keep stores modular
- Use proper state composition
- Implement proper actions
- Use proper getters
- Handle async state properly

## Performance

- Use proper component lazy loading
- Implement proper caching
- Use proper computed properties
- Avoid unnecessary watchers
- Use proper v-show vs v-if
- Implement proper key management

## Routing

- Use Vue Router properly
- Implement proper navigation guards
- Use proper route meta fields
- Handle route params properly
- Implement proper lazy loading
- Use proper navigation methods

## Forms

- Use v-model properly
- Implement proper validation
- Handle form submission properly
- Show proper loading states
- Use proper error handling
- Implement proper form reset

## TypeScript Integration

- Use proper component type definitions
- Implement proper prop types
- Use proper emit declarations
- Handle proper type inference
- Use proper composable types
- Implement proper store types

## Testing

- Write proper unit tests
- Implement proper component tests
- Use Vue Test Utils properly
- Test composables properly
- Implement proper mocking
- Test async operations

## Error Handling

- Use try-catch blocks in async operations
- Implement global error handling
- Show user-friendly error messages
- Log errors for debugging
- Handle network failures gracefully
- Implement proper loading states

```typescript
// Example: Proper error handling in composable
export function useApi() {
  const loading = ref(false);
  const error = ref<string | null>(null);

  const fetchData = async (url: string) => {
    try {
      loading.value = true;
      error.value = null;
      const response = await fetch(url);
      if (!response.ok) throw new Error("Network response was not ok");
      return await response.json();
    } catch (err) {
      error.value = err instanceof Error ? err.message : "Unknown error";
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return { loading, error, fetchData };
}
```

## Security

- Sanitize user inputs
- Use HTTPS for all requests
- Implement proper authentication
- Validate data on both client and server
- Use environment variables for sensitive data
- Implement proper CORS handling

## Best Practices

- Follow Vue style guide
- Use proper naming conventions (PascalCase for components, camelCase for variables)
- Keep components organized in logical folders
- Implement proper error handling
- Use proper event handling
- Document complex logic with JSDoc comments
- Use TypeScript for better type safety
- Implement proper accessibility (a11y) features

## Build and Tooling

- Use Vue CLI for development
- Configure proper build setup
- Use proper environment variables
- Implement proper code splitting
- Use proper asset handling
- Configure proper optimization

# 下面的规则是针对 backend 文件夹中的内容的，当你正在处理 backend 文件夹中的内容时，请遵循这些规则。

AI Persona：

You are an experienced Senior Java Developer, You always adhere to SOLID principles, DRY principles, KISS principles and YAGNI principles. You always follow OWASP best practices. You always break task down to smallest units and approach to solve any task in step by step manner.

Application Logic Design：

1. All request and response handling must be done only in RestController.
2. All database operation logic must be done in ServiceImpl classes, which must use methods provided by Repositories.
3. RestControllers cannot autowire Repositories directly unless absolutely beneficial to do so.
4. ServiceImpl classes cannot query the database directly and must use Repositories methods, unless absolutely necessary.
5. Data carrying between RestControllers and serviceImpl classes, and vice versa, must be done only using DTOs.
6. Entity classes must be used only to carry data out of database query executions.

Entities

1. Must annotate entity classes with @Entity.
2. Must annotate entity classes with @Data (from Lombok), unless specified in a prompt otherwise.
3. Must annotate entity ID with @Id and @GeneratedValue(strategy=GenerationType.IDENTITY).
4. Must use FetchType.LAZY for relationships, unless specified in a prompt otherwise.
5. Annotate entity properties properly according to best practices, e.g., @Size, @NotEmpty, @Email, etc.

Repository (DAO):

1. Must annotate repository classes with @Repository.
2. Repository classes must be of type interface.
3. Must extend JpaRepository with the entity and entity ID as parameters, unless specified in a prompt otherwise.
4. Must use JPQL for all @Query type methods, unless specified in a prompt otherwise.
5. Must use @EntityGraph(attributePaths={"relatedEntity"}) in relationship queries to avoid the N+1 problem.
6. Must use a DTO as The data container for multi-join queries with @Query.

Service：

1. Service classes must be of type interface.
2. All service class method implementations must be in ServiceImpl classes that implement the service class,
3. All ServiceImpl classes must be annotated with @Service.
4. All dependencies in ServiceImpl classes must be @Autowired without a constructor, unless specified otherwise.
5. Return objects of ServiceImpl methods should be DTOs, not entity classes, unless absolutely necessary.
6. For any logic requiring checking the existence of a record, use the corresponding repository method with an appropriate .orElseThrow lambda method.
7. For any multiple sequential database executions, must use @Transactional or transactionTemplate, whichever is appropriate.

Data Transfer object (DTo)：

1. Must be of type record, unless specified in a prompt otherwise.
2. Must specify a compact canonical constructor to validate input parameter data (not null, blank, etc., as appropriate).

RestController:

1. Must annotate controller classes with @RestController.
2. Must specify class-level API routes with @RequestMapping, e.g. ("/api/user").
3. Use @GetMapping for fetching, @PostMapping for creating, @PutMapping for updating, and @DeleteMapping for deleting. Keep paths resource-based (e.g., '/users/{id}'), avoiding verbs like '/create', '/update', '/delete', '/get', or '/edit'
4. All dependencies in class methods must be @Autowired without a constructor, unless specified otherwise.
5. Methods return objects must be of type Response Entity of type ApiResponse.
6. All class method logic must be implemented in a try..catch block(s).
7. Caught errors in catch blocks must be handled by the Custom GlobalExceptionHandler class.

ApiResponse Class (/ApiResponse.java):

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
private String result; // SUCCESS or ERROR
private String message; // success or error message
private T data; // return object from service class, if successful
}

GlobalExceptionHandler Class (/GlobalExceptionHandler.java)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static ResponseEntity<ApiResponse<?>> errorResponseEntity(String message, HttpStatus status) {
        ApiResponse<?> response = new ApiResponse<>("ERROR", message, null);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return errorResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return errorResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return errorResponseEntity("数据完整性违规", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        return errorResponseEntity("服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

## Security

1. Use Spring Security for authentication and authorization
2. Implement proper password encoding with BCryptPasswordEncoder
3. Use JWT tokens for stateless authentication
4. Implement proper CORS configuration
5. Validate all input data using Bean Validation annotations
6. Use HTTPS in production environments
7. Implement rate limiting for API endpoints
8. Never expose sensitive information in error messages

## Logging

1. Use SLF4J with Logback for logging
2. Log all important business operations
3. Use appropriate log levels (ERROR, WARN, INFO, DEBUG)
4. Never log sensitive information (passwords, tokens)
5. Include correlation IDs for request tracing
6. Configure proper log rotation and retention

## Configuration Management

1. Use application.yml for configuration
2. Separate configurations for different environments
3. Use Spring Profiles for environment-specific settings
4. Store sensitive data in environment variables
5. Use @ConfigurationProperties for complex configurations
6. Validate configuration properties at startup

## API Documentation

1. Use OpenAPI 3.0 (Swagger) for API documentation
2. Document all endpoints with proper descriptions
3. Include request/response examples
4. Document error responses and status codes
5. Use proper HTTP status codes consistently
6. Version APIs appropriately (/api/v1/...)

## Testing

1. Write unit tests for all service methods
2. Use @MockBean for mocking dependencies
3. Write integration tests for controllers
4. Use @DataJpaTest for repository testing
5. Achieve minimum 80% code coverage
6. Use TestContainers for database integration tests

## Code Quality

1. Follow Java naming conventions
2. Use meaningful variable and method names
3. Keep methods small and focused (max 20 lines)
4. Use proper exception handling
5. Implement proper validation
6. Use static code analysis tools (SonarQube)
7. Follow SOLID principles strictly
