# Minimal Unit Testing Guide - 5 Core Test Cases

## Overview
This guide covers 5 essential unit tests implemented for **ActivityService** (3 tests) and **UserService** (2 tests) in your microservices.

---

## Test Files Created

### 1. ActivityServiceTest
**Location:** `activityservice/src/test/java/com/fitness/activityservice/service/ActivityServiceTest.java`

**Tests included:**
- ✅ **testTrackActivityWithValidUser** - Validates activity tracking with valid user, repository save, and RabbitMQ publish
- ✅ **testTrackActivityWithInvalidUser** - Ensures exception thrown when user validation fails, no side effects
- ✅ **testGetActivityByIdFound** - Retrieves activity when ID exists
- ✅ **testGetActivityByIdNotFound** - Throws exception when activity doesn't exist

### 2. UserServiceTest
**Location:** `userservice/src/test/java/com/fitness/userservice/service/UserServiceTest.java`

**Tests included:**
- ✅ **testRegisterNewUser** - New user registration with password hashing
- ✅ **testRegisterExistingUser** - Existing user returns without re-saving
- ✅ **testGetUserProfileFound** - User profile retrieval
- ✅ **testGetUserProfileNotFound** - Exception for missing user
- ✅ **testExistByUserId** - User existence check by KeyCloak ID

---

## Running the Tests

### Run All Tests (Both Services)
```bash
cd /Users/olawaletijani/Documents/Personal/AIFitnessAppMicroservice

# Run all tests in the project
mvn test

# Run with detailed output
mvn test -X
```

### Run Activity Service Tests Only
```bash
cd /Users/olawaletijani/Documents/Personal/AIFitnessAppMicroservice/activityservice

# Run all tests in this module
mvn test

# Run a specific test class
mvn test -Dtest=ActivityServiceTest

# Run a specific test method
mvn test -Dtest=ActivityServiceTest#testTrackActivityWithValidUser
```

### Run User Service Tests Only
```bash
cd /Users/olawaletijani/Documents/Personal/AIFitnessAppMicroservice/userservice

# Run all tests in this module
mvn test

# Run a specific test class
mvn test -Dtest=UserServiceTest

# Run a specific test method
mvn test -Dtest=UserServiceTest#testRegisterNewUser
```

### Run from IDE
- **IntelliJ IDEA**: Right-click on test class → "Run" or "Run with Coverage"
- **VS Code**: Use Maven plugin or command palette

---

## Test Coverage Analysis

### ActivityService (3 critical paths covered)
| Test Case | What It Tests | Mocks Used |
|-----------|--------------|-----------|
| **trackActivity (valid user)** | Happy path - save + publish | UserValidationService, ActivityRepository, RabbitTemplate |
| **trackActivity (invalid user)** | Exception handling | UserValidationService |
| **getActivityById (found)** | Successful retrieval | ActivityRepository |
| **getActivityById (not found)** | Exception on missing data | ActivityRepository |

### UserService (4 critical paths covered)
| Test Case | What It Tests | Mocks Used |
|-----------|--------------|-----------|
| **register (new user)** | Password hashing + save | PasswordEncoder, UserRepository |
| **register (existing user)** | Duplicate email handling | UserRepository |
| **getUserProfile (found)** | Profile retrieval | UserRepository |
| **getUserProfile (not found)** | Exception on missing user | UserRepository |
| **existByUserId** | User existence check | UserRepository |

---

## Key Testing Concepts Used

### 1. **Mocking External Dependencies**
```java
@Mock
private ActivityRepository activityRepository;

@Mock
private UserValidationService userValidationService;

@InjectMocks
private ActivityService activityService;
```
- `@Mock` creates fake objects that return predefined values
- `@InjectMocks` injects mocks into the service under test
- Isolates business logic from database/external calls

### 2. **Arrange-Act-Assert (AAA) Pattern**
```java
@Test
void testExample() {
    // ARRANGE - Set up test data and mock behavior
    when(repository.findById(id)).thenReturn(Optional.of(mockData));
    
    // ACT - Call the method under test
    Result result = service.getData(id);
    
    // ASSERT - Verify the result
    assertEquals(expected, result);
}
```

### 3. **Verification of Mock Interactions**
```java
verify(userValidationService, times(1)).validateUser(testUserId);
verify(rabbitTemplate, times(1)).convertAndSend(...);
verify(passwordEncoder, never()).encode(...);  // Ensure it was NOT called
```

### 4. **Exception Testing**
```java
RuntimeException exception = assertThrows(
    RuntimeException.class,
    () -> activityService.trackActivity(invalidRequest)
);
assertTrue(exception.getMessage().contains("expected text"));
```

---

## What Each Test Validates

### ActivityServiceTest

**Test 1: testTrackActivityWithValidUser**
- ✅ User validation is called
- ✅ Activity is saved to repository
- ✅ Message is published to RabbitMQ exchange
- ✅ Response contains correct activity details
- ✅ Side effects are verified (save, publish)

**Test 2: testTrackActivityWithInvalidUser**
- ✅ Exception is thrown for invalid user
- ✅ Repository save is NOT called
- ✅ RabbitMQ publish is NOT called
- ✅ Exception message is clear

**Test 3 & 4: getActivityById**
- ✅ Found: returns mapped ActivityResponse
- ✅ Not found: throws RuntimeException with clear message

### UserServiceTest

**Test 1: testRegisterNewUser**
- ✅ Password is encoded using PasswordEncoder
- ✅ User is saved to repository
- ✅ Response contains all user details
- ✅ Save happens exactly once

**Test 2: testRegisterExistingUser**
- ✅ Existing email is detected
- ✅ User is found and returned
- ✅ Password is NOT re-encoded
- ✅ User is NOT re-saved (no duplicate)

**Test 3-5: getUserProfile & existByUserId**
- ✅ User found: returns profile response
- ✅ User not found: throws RuntimeException
- ✅ Existence check returns correct boolean

---

## Expected Output

When you run tests successfully, you should see:
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.fitness.activityservice.service.ActivityServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.xxx s - OK
[INFO] Running com.fitness.userservice.service.UserServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.xxx s - OK
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
```

---

## Troubleshooting

### If Tests Fail

1. **"Missing mock bean"** - Ensure all dependencies are annotated with `@Mock`
2. **"Mockito initialization error"** - Check `@ExtendWith(MockitoExtension.class)` is present
3. **"Assertion failed"** - Verify expected values match actual values or adjust assertions
4. **"Import errors"** - Ensure test dependencies exist in `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-test</artifactId>
       <scope>test</scope>
   </dependency>
   ```

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Tests don't run | Run `mvn clean install` first to compile |
| "Cannot find symbol" | Check imports match your package names |
| Mock not working | Ensure `@ExtendWith(MockitoExtension.class)` is on class |
| ReflectionTestUtils error | Used only in ActivityServiceTest for private fields (exchangeName, routingKey) |

---

## Next Steps to Expand Testing

### Option 1: Add Controller Tests
```bash
# Test REST endpoints with @WebMvcTest
create: ActivityControllerTest.java
create: UserControllerTest.java
```

### Option 2: Add Repository Tests
```bash
# Test database queries with @DataMongoTest or @DataJpaTest
create: ActivityRepositoryTest.java
create: UserRepositoryTest.java
```

### Option 3: Add Integration Tests
```bash
# Test full flow with real database/RabbitMQ
create: ActivityServiceIntegrationTest.java
```

### Option 4: Add More Edge Cases
- Null parameter handling
- Boundary value testing (empty strings, negative durations, etc.)
- Concurrent request testing

---

## Summary

**Total Tests Created: 9**
- ActivityService: 4 tests
- UserService: 5 tests

**Key Patterns:**
- ✅ Mock external dependencies (Repository, Queue, Encoder)
- ✅ Test both happy path (success) and sad path (exceptions)
- ✅ Verify side effects (save, publish)
- ✅ Assert correct response DTOs
- ✅ Use Arrange-Act-Assert structure

**Run any time with:**
```bash
mvn test  # From project root or service directory
```

This provides **minimal but meaningful test coverage** for core business logic without excessive overhead.

