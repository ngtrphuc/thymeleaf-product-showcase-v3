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

## Detailed Project Structure

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
├── 📂 backend/
│   └── 📂 src/
│       ├── 📂 main/
│       │   ├── 📂 java/io/github/ngtrphuc/smartphone_shop/
│       │   │   ├── 📂 config/
│       │   │   │   ├── 📄 AdminAccountInitializer.java
│       │   │   │   ├── 📄 DataInitializer.java
│       │   │   │   ├── 📄 GlobalModelAttributes.java
│       │   │   │   ├── 📄 LoginSuccessHandler.java
│       │   │   │   ├── 📄 SecurityConfig.java
│       │   │   │   ├── 📄 ThymeleafConfig.java
│       │   │   │   └── 📄 WebConfig.java
│       │   │   ├── 📂 controller/
│       │   │   │   ├── 📂 admin/
│       │   │   │   │   ├── 📄 AdminController.java
│       │   │   │   │   └── 📄 ChatAdminController.java
│       │   │   │   └── 📂 user/
│       │   │   │       ├── 📄 AuthController.java
│       │   │   │       ├── 📄 CartController.java
│       │   │   │       ├── 📄 ChatUserController.java
│       │   │   │       ├── 📄 MainController.java
│       │   │   │       ├── 📄 OrderController.java
│       │   │   │       ├── 📄 PaymentMethodController.java
│       │   │   │       ├── 📄 ProfileController.java
│       │   │   │       └── 📄 WishlistController.java
│       │   │   ├── 📂 model/
│       │   │   │   ├── 📄 CartItem.java
│       │   │   │   ├── 📄 CartItemEntity.java
│       │   │   │   ├── 📄 ChatMessage.java
│       │   │   │   ├── 📄 Order.java
│       │   │   │   ├── 📄 OrderItem.java
│       │   │   │   ├── 📄 PaymentMethod.java
│       │   │   │   ├── 📄 Product.java
│       │   │   │   ├── 📄 User.java
│       │   │   │   ├── 📄 WishlistItem.java
│       │   │   │   └── 📄 WishlistItemEntity.java
│       │   │   ├── 📂 repository/
│       │   │   │   ├── 📄 CartItemRepository.java
│       │   │   │   ├── 📄 ChatMessageRepository.java
│       │   │   │   ├── 📄 OrderRepository.java
│       │   │   │   ├── 📄 PaymentMethodRepository.java
│       │   │   │   ├── 📄 ProductRepository.java
│       │   │   │   ├── 📄 UserRepository.java
│       │   │   │   └── 📄 WishlistItemRepository.java
│       │   │   ├── 📂 service/
│       │   │   │   ├── 📄 AuthService.java
│       │   │   │   ├── 📄 CartService.java
│       │   │   │   ├── 📄 ChatService.java
│       │   │   │   ├── 📄 CustomUserDetailsService.java
│       │   │   │   ├── 📄 OrderService.java
│       │   │   │   ├── 📄 OrderValidationException.java
│       │   │   │   ├── 📄 PaymentMethodService.java
│       │   │   │   └── 📄 WishlistService.java
│       │   │   └── 📄 SmartphoneShopApplication.java
│       │   └── 📂 resources/
│       │       ├── 📄 application-dev.properties
│       │       ├── 📄 application-prod.properties
│       │       └── 📄 application.properties
│       └── 📂 test/
│           ├── 📂 java/io/github/ngtrphuc/smartphone_shop/
│           │   ├── 📂 controller/user/
│           │   │   └── 📄 MainControllerTest.java
│           │   ├── 📂 service/
│           │   │   ├── 📄 AuthServiceTest.java
│           │   │   ├── 📄 CartServiceTest.java
│           │   │   ├── 📄 OrderServiceTest.java
│           │   │   ├── 📄 PaymentMethodServiceTest.java
│           │   │   └── 📄 WishlistServiceTest.java
│           │   └── 📄 SmartphoneShopApplicationTests.java
│           └── 📂 resources/
│               └── 📄 application-test.properties
├── 📂 frontend/
│   ├── 📂 static/
│   │   ├── 📂 admin/
│   │   │   └── 📂 css/
│   │   │       └── 📄 style.css
│   │   ├── 📂 customer/
│   │   │   ├── 📂 css/
│   │   │   │   └── 📄 style.css
│   │   │   └── 📂 images/
│   │   │       ├── 📄 findn5.png
│   │   │       ├── 📄 findx9pro.png
│   │   │       ├── 📄 galaxy_s25.png
│   │   │       ├── 📄 galaxy-s26.png
│   │   │       ├── 📄 galaxy-s26-plus.png
│   │   │       ├── 📄 galaxy-s26-ultra.png
│   │   │       ├── 📄 galaxy-z-fold7.png
│   │   │       ├── 📄 honor400pro.png
│   │   │       ├── 📄 honor-magic-v5.png
│   │   │       ├── 📄 huawei-mate-x7.png
│   │   │       ├── 📄 iphone16.png
│   │   │       ├── 📄 iphone16plus.png
│   │   │       ├── 📄 iphone16pro.png
│   │   │       ├── 📄 iphone16promax.png
│   │   │       ├── 📄 iphone17.png
│   │   │       ├── 📄 iphone17e.png
│   │   │       ├── 📄 iphone17pro.png
│   │   │       ├── 📄 iphone17promax.png
│   │   │       ├── 📄 iphoneair.png
│   │   │       ├── 📄 oppo-find-n6.png
│   │   │       ├── 📄 oppo-find-x8-ultra.png
│   │   │       ├── 📄 paypay.png
│   │   │       ├── 📄 paypay-icon.svg
│   │   │       ├── 📄 pixel10proxl.png
│   │   │       ├── 📄 pixel9.png
│   │   │       ├── 📄 pura70ultra.png
│   │   │       ├── 📄 redmagic_11_pro_xam_3eac852136.jpg
│   │   │       ├── 📄 redmagic10.png
│   │   │       ├── 📄 redmagic11pro.png
│   │   │       ├── 📄 rog9.png
│   │   │       ├── 📄 vivo-x200-ultra.png
│   │   │       ├── 📄 xiaomi15ultra.png
│   │   │       ├── 📄 xiaomi17ultra.png
│   │   │       ├── 📄 xiaomi-mix-flip2.png
│   │   │       ├── 📄 xperia1vi.png
│   │   │       ├── 📄 xperia1vii.png
│   │   │       ├── 📄 z70ultra.png
│   │   │       └── 📄 zflip7.png
│   │   ├── 📂 js/
│   │   │   ├── 📄 admin-shell.js
│   │   │   ├── 📄 auth-password-toggle.js
│   │   │   └── 📄 order-success.js
│   │   └── 📂 svg/
│   │       └── 📂 griddy/
│   │           ├── 📄 README.md
│   │           ├── 📄 admin.svg
│   │           ├── 📄 alert.svg
│   │           ├── 📄 arrow-left.svg
│   │           ├── 📄 arrow-right.svg
│   │           ├── 📄 ban.svg
│   │           ├── 📄 box.svg
│   │           ├── 📄 cart.svg
│   │           ├── 📄 chat.svg
│   │           ├── 📄 check.svg
│   │           ├── 📄 clipboard.svg
│   │           ├── 📄 close-circle.svg
│   │           ├── 📄 credit-card.svg
│   │           ├── 📄 dashboard.svg
│   │           ├── 📄 eye.svg
│   │           ├── 📄 eye-off.svg
│   │           ├── 📄 heart-filled.svg
│   │           ├── 📄 heart-outline.svg
│   │           ├── 📄 home.svg
│   │           ├── 📄 location-pin.svg
│   │           ├── 📄 login.svg
│   │           ├── 📄 logout.svg
│   │           ├── 📄 orders.svg
│   │           ├── 📄 package.svg
│   │           ├── 📄 phone.svg
│   │           ├── 📄 profile.svg
│   │           ├── 📄 spark.svg
│   │           ├── 📄 trash.svg
│   │           ├── 📄 user.svg
│   │           └── 📄 wishlist.svg
│   └── 📂 templates/
│       ├── 📂 admin/
│       │   ├── 📂 error/
│       │   │   └── 📄 access-denied-admin.html
│       │   ├── 📄 chat.html
│       │   ├── 📄 dashboard.html
│       │   ├── 📄 orders.html
│       │   ├── 📄 product-form.html
│       │   └── 📄 products.html
│       └── 📂 customer/
│           ├── 📂 auth/
│           │   ├── 📄 login.html
│           │   └── 📄 register.html
│           ├── 📂 fragments/
│           │   └── 📄 chat-widget.html
│           ├── 📄 cart.html
│           ├── 📄 checkout.html
│           ├── 📄 detail.html
│           ├── 📄 index.html
│           ├── 📄 my-orders.html
│           ├── 📄 payment-select.html
│           ├── 📄 profile.html
│           ├── 📄 shipping.html
│           ├── 📄 success.html
│           └── 📄 wishlist.html
├── 📂 scripts/
│   └── 📄 remove_product_backgrounds.py
├── 📂 target/
├── 📂 tmp/
├── 📄 .editorconfig
├── 📄 .gitattributes
├── 📄 .gitignore
├── 📄 HELP.md
├── 📄 pom.xml
├── 📄 README.md
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
