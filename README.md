# Smartphone Shop

A smartphone e-commerce web application built with Spring Boot + Thymeleaf, including both customer and admin flows.

## Key Features

- Sign up/sign in with `ROLE_USER` and `ROLE_ADMIN` authorization
- Product listing with filtering/search and product detail pages
- Shopping cart and multi-step checkout
- Payment method management:
  - `Cash on Delivery`
  - `Bank Transfer`
  - `PayPay`
- Save default shipping address in profile and reuse it during checkout
- Customer order tracking
- Admin management for products, orders, and chat
- Real-time chat between customer and admin (SSE)

## Tech Stack

- Java 21
- Spring Boot 3.5.13
- Spring Security 6
- Spring Data JPA (Hibernate)
- Thymeleaf
- Maven
- H2 (dev/test), MySQL/MariaDB (prod)

## Project Structure

```text
📂 smartphone-shop
├── 📂 .data/
│   ├── 📄 smartphone_shop_dev.lock.db
│   ├── 📄 smartphone_shop_dev.mv.db
│   └── 📄 smartphone_shop_dev.trace.db
├── 📂 .mvn/
│   └── 📂 wrapper/
│       └── 📄 maven-wrapper.properties
├── 📂 .vscode/
│   ├── 📄 launch.json
│   └── 📄 settings.json
├── 📂 backend
│   └── 📂 src
│       ├── 📂 main
│       │   ├── 📂 java/io/github/ngtrphuc/smartphone_shop
│       │   │   ├── 📂 config
│       │   │   ├── 📂 controller
│       │   │   │   ├── 📂 admin
│       │   │   │   └── 📂 user
│       │   │   ├── 📂 model
│       │   │   ├── 📂 repository
│       │   │   ├── 📂 service
│       │   │   └── 📄 SmartphoneShopApplication.java
│       │   └── 📂 resources
│       │       ├── 📄 application.properties
│       │       ├── 📄 application-dev.properties
│       │       └── 📄 application-prod.properties
│       └── 📂 test
│           ├── 📂 java/io/github/ngtrphuc/smartphone_shop
│           │   ├── 📂 controller/user
│           │   ├── 📂 service
│           │   └── 📄 SmartphoneShopApplicationTests.java
│           └── 📂 resources
├── 📂 frontend
│   ├── 📂 static
│   │   ├── 📂 admin/css
│   │   ├── 📂 customer/css
│   │   ├── 📂 customer/images
│   │   ├── 📂 svg
│   │   └── 📂 js
│   └── 📂 templates
│       ├── 📂 admin
│       └── 📂 customer
├── 📂 scripts
│   └── 📄 remove_product_backgrounds.py
├── 📂 tmp
├── 📄 pom.xml
├── 📄 mvnw
└── 📄 mvnw.cmd
```

- `backend/src/main/java/.../config`: System configuration, security, Thymeleaf, web setup, and bootstrap data
- `backend/src/main/java/.../controller`: Request handlers for user/admin
- `backend/src/main/java/.../model`: Main entities/models
- `backend/src/main/java/.../repository`: Data access layer (Spring Data JPA)
- `backend/src/main/java/.../service`: Business logic
- `frontend/static`: Static assets (CSS, JS, images, SVG)
- `frontend/templates`: Thymeleaf views for admin/customer
- `backend/src/test`: Unit tests and application configuration tests
- `scripts`: Auxiliary scripts outside the core application
- `.data`: Local H2 database files for development

## Quick Start (Default Dev Profile)

By default, the app runs with profile `dev` and uses a local file-based H2 database, so MySQL setup is not required for local testing.

### 1) Run the app

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

### 2) Access URLs

- Home: `http://localhost:8080/`
- H2 Console: `http://localhost:8080/h2-console`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`

## Bootstrap Admin Account

The app bootstraps an admin account via environment variables:

- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

Default dev values in `application-dev.properties`:

- `admin@smartphone.local`
- `Admin@123456`

You can override them before running the app.

## Run in Production (MySQL/MariaDB)

Use profile `prod` and provide:

- `DATASOURCE_URL`
- `DATASOURCE_USER`
- `DATASOURCE_PASSWORD`
- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

Example:

```bash
SPRING_PROFILES_ACTIVE=prod \
DATASOURCE_URL=jdbc:mysql://localhost:3306/smartphone_shop \
DATASOURCE_USER=root \
DATASOURCE_PASSWORD=your_password \
ADMIN_EMAIL=admin@yourdomain.com \
ADMIN_PASSWORD=your_strong_password \
./mvnw spring-boot:run
```

## Run Tests

```bash
./mvnw test
```

Windows:

```bat
mvnw.cmd test
```

## Security Notes

- Session cookie uses `HttpOnly`, `SameSite=Lax`
- Session fixation protection and concurrent session limits
- Security headers: CSP, frame deny, referrer policy, permissions policy
- CSRF is enabled by default for form actions
