# Quick Start: Running Your Unit Tests

## 🚀 One-Liner Test Commands

### Run ALL Tests (Entire Project)
```bash
cd /Users/olawaletijani/Documents/Personal/AIFitnessAppMicroservice
mvn test
```

### Run Activity Service Tests Only
```bash
cd /Users/olawaletijani/Documents/Personal/AIFitnessAppMicroservice/activityservice
mvn test
```

### Run User Service Tests Only
```bash
cd /Users/olawaletijani/Documents/Personal/AIFitnessAppMicroservice/userservice
mvn test
```

---

## 📋 Specific Test Execution

### Run Single Test Class
```bash
# ActivityService Tests
mvn test -Dtest=ActivityServiceTest

# UserService Tests
mvn test -Dtest=UserServiceTest
```

### Run Single Test Method
```bash
# Test happy path
mvn test -Dtest=ActivityServiceTest#testTrackActivityWithValidUser

# Test exception handling
mvn test -Dtest=ActivityServiceTest#testTrackActivityWithInvalidUser

# Test new user registration
mvn test -Dtest=UserServiceTest#testRegisterNewUser
```

---

## 📊 Expected Output

### Successful Run
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running ActivityService Unit Tests
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.711 s -- OK
[INFO] 
[INFO] Results:
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

---

## 🧪 Test Matrix Overview

| Service | Test Class | Tests | Methods |
|---------|-----------|-------|---------|
| Activity | ActivityServiceTest | 4 | save, publish, retrieve, error handling |
| User | UserServiceTest | 5 | register, duplicate, profile, exist, hash |
| **TOTAL** | **2** | **9** | **✅ All Passing** |

---

## 🔍 What Each Test Does

### ActivityServiceTest

1. **testTrackActivityWithValidUser**
   - ✅ Validates user
   - ✅ Saves activity to database
   - ✅ Publishes to RabbitMQ
   - ✅ Returns mapped response

2. **testTrackActivityWithInvalidUser**
   - ❌ Rejects invalid user
   - ❌ No database save
   - ❌ No RabbitMQ publish

3. **testGetActivityByIdFound**
   - ✅ Finds activity by ID
   - ✅ Maps to response DTO

4. **testGetActivityByIdNotFound**
   - ❌ Throws exception
   - ❌ Handles missing data

### UserServiceTest

1. **testRegisterNewUser**
   - ✅ Creates new user
   - ✅ Hashes password
   - ✅ Saves to DB

2. **testRegisterExistingUser**
   - ✅ Detects duplicate email
   - ✅ Returns existing user
   - ✅ No duplicate save

3. **testGetUserProfileFound**
   - ✅ Retrieves user profile
   - ✅ Maps to response DTO

4. **testGetUserProfileNotFound**
   - ❌ Throws exception
   - ❌ Handles missing user

5. **testExistByUserId**
   - ✅ Checks user existence
   - ✅ Returns boolean

---

## 🛠️ Troubleshooting

### Tests Won't Compile
```bash
# Clean and rebuild
mvn clean compile
mvn test
```

### Dependency Issues
```bash
# Force update dependencies
mvn clean install -U
```

### Skip Tests (Build Only)
```bash
mvn clean install -DskipTests
```

### View Full Test Output
```bash
mvn test -X  # Extra verbose logging
```

---

## 📈 Coverage by Feature

### Activity Service Coverage
| Feature | Tested |
|---------|--------|
| User Validation | ✅ |
| Database Save | ✅ |
| RabbitMQ Publish | ✅ |
| Activity Retrieval | ✅ |
| Exception Handling | ✅ |
| Response Mapping | ✅ |

### User Service Coverage
| Feature | Tested |
|---------|--------|
| New User Registration | ✅ |
| Password Hashing | ✅ |
| Duplicate Detection | ✅ |
| Profile Retrieval | ✅ |
| Existence Checking | ✅ |
| Exception Handling | ✅ |

---

## 🎯 Integration with CI/CD

### GitHub Actions Example
```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run Tests
        run: mvn clean test
      - name: Upload Coverage
        uses: codecov/codecov-action@v2
```

---

## 💡 Pro Tips

### Run Tests in Parallel (Faster)
```bash
mvn -T 1C test  # 1 thread per core
```

### Generate Test Report
```bash
mvn surefire-report:report
# Report location: target/site/surefire-report.html
```

### Watch Mode (Re-run on Change)
```bash
# Using Maven plugin
mvn clean test -Drepeat-failed-tests
```

---

## 📚 Test Documentation

- **Full Guide:** `TEST_GUIDE.md`
- **Summary:** `TESTING_SUMMARY.md`
- **ActivityService Tests:** `activityservice/src/test/java/.../ActivityServiceTest.java`
- **UserService Tests:** `userservice/src/test/java/.../UserServiceTest.java`

---

## ✨ Status

✅ **9 Tests Created**
✅ **All Tests Passing**
✅ **Ready for Production**
✅ **CI/CD Ready**

---

**Last Updated:** April 4, 2026
**Test Framework:** JUnit 5 + Mockito
**Coverage:** Core business logic

