# Smartphone Shop

Smartphone e-commerce web application built with Spring Boot + Thymeleaf.
The project contains both customer-facing flows and admin management flows.

## Key Features

- Sign up / sign in with `ROLE_USER` and `ROLE_ADMIN`
- Product listing, search/filter, product detail
- Compare products (persisted compare list)
- Wishlist and shopping cart
- Multi-step checkout and order placement
- Payment method selection and management:
  - `Cash on Delivery`
  - `Bank Transfer`
  - `PayPay`
- Customer profile and default shipping address reuse
- Customer order history/tracking
- Admin product/order/chat management
- Real-time customer-admin chat (SSE)

## Tech Stack

- Java 21
- Spring Boot 3.5.13
- Spring MVC + Thymeleaf
- Spring Security 6
- Spring Data JPA (Hibernate)
- Spring Validation
- Spring Actuator
- springdoc OpenAPI UI
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- H2 (dev/test), MySQL/MariaDB (prod)

## Project Layout Notes

This repository uses a custom Maven source/resource layout (configured in `pom.xml`):

- Java source: `backend/src/main/java`
- Test source: `backend/src/test/java`
- Runtime resources: `backend/src/main/resources`
- Frontend templates/static assets are packaged from `frontend/**`

## Detailed Project Structure

```text
smartphone-shop/
|-- backend/
|   |-- src/
|   |   |-- main/
|   |   |   |-- java/io/github/ngtrphuc/smartphone_shop/
|   |   |   |   |-- config/
|   |   |   |   |   |-- AdminAccountInitializer.java
|   |   |   |   |   |-- DataInitializer.java
|   |   |   |   |   |-- GlobalModelAttributes.java
|   |   |   |   |   |-- LoginSuccessHandler.java
|   |   |   |   |   |-- PaymentMethodSchemaInitializer.java
|   |   |   |   |   |-- SecurityConfig.java
|   |   |   |   |   |-- ThymeleafConfig.java
|   |   |   |   |   `-- WebConfig.java
|   |   |   |   |-- controller/
|   |   |   |   |   |-- admin/
|   |   |   |   |   |   |-- AdminController.java
|   |   |   |   |   |   `-- ChatAdminController.java
|   |   |   |   |   `-- user/
|   |   |   |   |       |-- AuthController.java
|   |   |   |   |       |-- CartController.java
|   |   |   |   |       |-- ChatUserController.java
|   |   |   |   |       |-- CompareController.java
|   |   |   |   |       |-- MainController.java
|   |   |   |   |       |-- OrderController.java
|   |   |   |   |       |-- PaymentMethodController.java
|   |   |   |   |       |-- ProfileController.java
|   |   |   |   |       `-- WishlistController.java
|   |   |   |   |-- model/
|   |   |   |   |   |-- CartItem.java
|   |   |   |   |   |-- CartItemEntity.java
|   |   |   |   |   |-- ChatMessage.java
|   |   |   |   |   |-- CompareItemEntity.java
|   |   |   |   |   |-- Order.java
|   |   |   |   |   |-- OrderItem.java
|   |   |   |   |   |-- PaymentMethod.java
|   |   |   |   |   |-- Product.java
|   |   |   |   |   |-- User.java
|   |   |   |   |   |-- WishlistItem.java
|   |   |   |   |   `-- WishlistItemEntity.java
|   |   |   |   |-- repository/
|   |   |   |   |   |-- CartItemRepository.java
|   |   |   |   |   |-- ChatMessageRepository.java
|   |   |   |   |   |-- CompareItemRepository.java
|   |   |   |   |   |-- OrderRepository.java
|   |   |   |   |   |-- PaymentMethodRepository.java
|   |   |   |   |   |-- ProductRepository.java
|   |   |   |   |   |-- UserRepository.java
|   |   |   |   |   `-- WishlistItemRepository.java
|   |   |   |   |-- service/
|   |   |   |   |   |-- AuthService.java
|   |   |   |   |   |-- CartService.java
|   |   |   |   |   |-- ChatService.java
|   |   |   |   |   |-- CompareService.java
|   |   |   |   |   |-- CustomUserDetailsService.java
|   |   |   |   |   |-- OrderService.java
|   |   |   |   |   |-- OrderValidationException.java
|   |   |   |   |   |-- PaymentMethodService.java
|   |   |   |   |   `-- WishlistService.java
|   |   |   |   |-- support/
|   |   |   |   |   `-- StorefrontSupport.java
|   |   |   |   |-- Port8080Guard.java
|   |   |   |   `-- SmartphoneShopApplication.java
|   |   |   `-- resources/
|   |   |       |-- application.properties
|   |   |       |-- application-dev.properties
|   |   |       `-- application-prod.properties
|   |   `-- test/
|   |       |-- java/io/github/ngtrphuc/smartphone_shop/
|   |       |   |-- config/
|   |       |   |   `-- PaymentMethodSchemaInitializerTest.java
|   |       |   |-- controller/user/
|   |       |   |   |-- CartControllerTest.java
|   |       |   |   |-- CompareControllerTest.java
|   |       |   |   `-- MainControllerTest.java
|   |       |   |-- model/
|   |       |   |   `-- PaymentMethodTest.java
|   |       |   |-- service/
|   |       |   |   |-- AuthServiceTest.java
|   |       |   |   |-- CartServiceTest.java
|   |       |   |   |-- MockitoNullSafety.java
|   |       |   |   |-- OrderServiceTest.java
|   |       |   |   |-- PaymentMethodServiceTest.java
|   |       |   |   `-- WishlistServiceTest.java
|   |       |   |-- Port8080GuardTest.java
|   |       |   `-- SmartphoneShopApplicationTests.java
|   |       `-- resources/
|   |           `-- application-test.properties
|-- frontend/
|   |-- templates/
|   |   |-- admin/
|   |   |   |-- dashboard.html
|   |   |   |-- products.html
|   |   |   |-- product-form.html
|   |   |   |-- orders.html
|   |   |   |-- chat.html
|   |   |   `-- error/access-denied-admin.html
|   |   `-- customer/
|   |       |-- auth/login.html
|   |       |-- auth/register.html
|   |       |-- fragments/chat-widget.html
|   |       |-- fragments/compare-bar.html
|   |       |-- fragments/footer.html
|   |       |-- index.html
|   |       |-- detail.html
|   |       |-- compare.html
|   |       |-- wishlist.html
|   |       |-- cart.html
|   |       |-- checkout.html
|   |       |-- shipping.html
|   |       |-- payment-select.html
|   |       |-- profile.html
|   |       |-- my-orders.html
|   |       `-- success.html
|   `-- static/
|       |-- admin/css/style.css
|       |-- customer/css/style.css
|       |-- customer/images/...
|       |-- js/
|       |   |-- admin-shell.js
|       |   |-- auth-password-toggle.js
|       |   `-- order-success.js
|       `-- svg/griddy/*.svg
|-- scripts/
|   `-- remove_product_backgrounds.py
|-- .data/                       # Local H2 database files in dev
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
`-- README.md
```

## Structure Updates Reflected In This README

- Added compare domain persistence layer in structure docs:
  - `model/CompareItemEntity.java`
  - `repository/CompareItemRepository.java`
  - `service/CompareService.java`
- Added `CartControllerTest.java` in controller test section.
- Clarified custom Maven layout (`backend` + `frontend`) so onboarding is easier.

## Quick Start (Default Dev Profile)

By default, profile `dev` is active (`spring.profiles.default=dev`) and uses local file-based H2 DB.

### Run app

Windows:

```bat
mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

### Useful URLs

- Home: `http://localhost:8080/`
- H2 Console: `http://localhost:8080/h2-console`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`

## Bootstrap Admin Account

Admin account is initialized via env vars:

- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

Default dev fallback values (`application-dev.properties`):

- `admin@smartphone.local`
- `Admin@123456`

## Production Profile (MySQL/MariaDB)

Set profile `prod` and required env vars:

- `DATASOURCE_URL`
- `DATASOURCE_USER`
- `DATASOURCE_PASSWORD`
- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

PowerShell example:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod"
$env:DATASOURCE_URL = "jdbc:mysql://localhost:3306/smartphone_shop"
$env:DATASOURCE_USER = "root"
$env:DATASOURCE_PASSWORD = "your_password"
$env:ADMIN_EMAIL = "admin@yourdomain.com"
$env:ADMIN_PASSWORD = "your_strong_password"
./mvnw spring-boot:run
```

## Run Tests

Windows:

```bat
mvnw.cmd test
```

macOS/Linux:

```bash
./mvnw test
```

## Security Notes

- Session cookie uses `HttpOnly` and `SameSite=Lax`
- Session fixation protection and concurrent session controls
- Security headers configured in `SecurityConfig`
- CSRF enabled for form actions
