# Product API

REST API for product management with CRUD operations.

## 🚀 Quick Start

### Development/Local (Default - Supabase)

```bash
# 1. Clone/download the project
cd product-api

# 2. Compile and run application (connects to Supabase by default)
mvn clean compile
mvn spring-boot:run
```

⚠️ **Note**: The application connects to **Supabase cloud database** by default. No Docker required for development.

## 🗄️ Database Setup

### Supabase (Default - Already Configured)
The application connects to **Supabase cloud database** automatically. No setup required for development.

### Docker PostgreSQL (Only for Integration Tests)
Docker Compose is **only needed for integration tests**, not for development:

```bash
# Only required when running integration tests
docker compose up -d
mvn integration-test
```

## 🧪 Tests

### Unit Tests (No Docker required)
```bash
mvn test
```

### Integration Tests (Requires Docker)
```bash
# 1. Start test database
docker compose up -d

# 2. Run integration tests
mvn integration-test

# 3. Run all tests
mvn verify
```

## 📋 **Summary**

| Component | Database | Docker Required |
|-----------|----------|----------------|
| **Development** | Supabase Cloud | ❌ No |
| **Unit Tests** | In-memory | ❌ No |
| **Integration Tests** | Local PostgreSQL | ✅ Yes |

## 🔍 Coverage: 91%
The coverage report will be available at: `/target/coverage-reports/aggregate/index.html`

![coverage.png](images/coverage.png)

## 📱 Access

- **REST API**: http://localhost:8080/api/v1/products


- **Documentation**: http://localhost:8080/swagger-ui.html
![swagger.png](images/swagger.png)




## 🛠️ Technologies

- Java 21 + Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL + Docker
- Maven + JUnit 5 + Mockito + jaCoCo
- Cucumber + gherkin


## 📋 Endpoints

| Method | URL | Description                      |
|--------|----|----------------------------------|
| `GET` | `/api/v1/products` | Product list               |
| `GET` | `/api/v1/products?page=0&size=10&sort=id,asc` | Product list with pagination |
| `GET` | `/api/v1/products/{id}` | Get product                 |
| `POST` | `/api/v1/products` | Create product                   |
| `PUT` | `/api/v1/products/{id}` | Update product              |
| `DELETE` | `/api/v1/products/{id}` | Delete product                |


### 🔄 **Future Improvements**
If I had more time I would implement:

1. **Security**
   - OAuth + JWT     

2. **Observability**
   - Metrics with Micrometer/Prometheus   

3. **Testing**
   - More use cases in integration tests 

4. **Code Quality**
   - I would implement SonarQube
   - Security test with OWASP ZAP

5. **Logging**
   - Add more log levels properly (INFO, WARN, ERROR, DEBUG)
   
## 👨‍💻 Technical Decisions

### Hexagonal Architecture
I chose this architecture to:
- Decouple business logic from infrastructure
- Facilitate testing with mocks and stubs
- Allow technology changes without affecting the core
- Follow DDD and Clean Architecture principles

### Testing Strategy
- **Test Pyramid**: Wide base of unit tests, selective integration tests
- **BDD with Cucumber**: Executable documentation for stakeholders
- **Separation of Concerns**: Well-defined unit vs integration tests

### Database
- **Optimized PostgreSQL**: Efficient queries with strategic indexes

### Soft Delete
I implemented deactivation instead of physical deletion to:
- Maintain referential integrity


## 📝 Notes

This project demonstrates:
- **Testing Excellence**: 91% coverage with comprehensive strategy
- **Clean Architecture**: SOLID principles, DDD and Hexagonal Architecture
- **Optimized Performance**: Efficient queries with strategic indexes
- **Enterprise Quality**: Maintainable, documented and well-structured code
- **DevOps Ready**: Automated tests, coverage reports, containerization
- **Scalability**: Design prepared for growth and distribution