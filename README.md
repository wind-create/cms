# 📦 WindLabs CMS Backend

Backend API untuk Content Management System (CMS) berbasis **Spring Boot**, dirancang modular, scalable, dan siap digunakan untuk berbagai produk di ekosistem **WindLabs**.

---

## 🚀 Tech Stack

- ☕ Java 25  
- ⚡ Spring Boot 4.x  
- 🗄️ PostgreSQL  
- 🧩 Spring Data JPA  
- 🔄 Flyway  
- 📄 SpringDoc OpenAPI (Swagger UI)  
- 🐳 Docker Compose support  
- 🪶 Lombok  

---

## 📂 Project Structure

```
src/
 └── main/
     ├── java/com/windlabs/cms/
     │    ├── controller/
     │    ├── service/
     │    ├── repository/
     │    ├── entity/
     │    └── config/
     └── resources/
          ├── application.yml
          └── db/migration/
```

---

## ⚙️ Features

- RESTful API (Spring Web MVC)  
- Database migration with Flyway  
- ORM dengan JPA & Hibernate  
- Auto documentation via Swagger UI  
- Hot reload (DevTools)  
- Docker Compose ready  
- Clean architecture (Controller → Service → Repository)

---

## 🛠️ Setup & Installation

### 1. Clone Repository
```
git clone https://github.com/your-username/windlabs-cms.git
cd windlabs-cms
```

### 2. Configure Database

Edit `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cms_db
    username: postgres
    password: yourpassword

  jpa:
    hibernate:
      ddl-auto: validate

  flyway:
    enabled: true
```

---

### 3. Run dengan Docker (Opsional)

```
docker-compose up -d
```

---

### 4. Run Application

```
./mvnw spring-boot:run
```

atau:

```
mvn clean install
java -jar target/cms-0.0.1-SNAPSHOT.jar
```

---

## 📑 API Documentation

Swagger UI tersedia di:

```
http://localhost:8080/swagger-ui.html
```

---

## 🗄️ Database Migration

Migration file ada di:

```
src/main/resources/db/migration
```

Contoh:

```
V1__init.sql
V2__create_users.sql
```

Flyway akan otomatis menjalankan migration saat aplikasi start.

---

## 🧪 Testing

```
mvn test
```

---

## 🔥 Development Tips

- Gunakan Lombok untuk mengurangi boilerplate  
- Gunakan DTO untuk API contract  
- Pisahkan logic di service, jangan di controller  
- Gunakan versioning API (`/api/v1/...`)  

---

## 📌 Roadmap

- Authentication (JWT / OAuth2)  
- Role & Permission (RBAC)  
- CMS Module (Post, Page, Media)  
- Multi-tenant support  
- Caching (Redis)

---

## 🧑‍💻 Author

**WindLabs**  
Building scalable SaaS products 🚀
