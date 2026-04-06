# Smartphone Shop - Spring Boot Web Application

A smartphone e-commerce web application built with Spring Boot, Thymeleaf, Spring Security, and JPA.

## Key Features

- Authentication and authorization with separate `ADMIN` and `USER` roles
- Product browsing, filtering, and detail pages
- Shopping cart and checkout flow
- User profile and order history
- Admin dashboard for products, orders, and chat
- Customer/admin chat support
- Multi-profile configuration (`dev`, `prod`, `test`)

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA / Hibernate
- Thymeleaf
- Maven

## Project Structure

```text
📂 smartphone-shop
├── 📂 src
│   ├── 📂 main
│   │   ├── 📂 java
│   │   │   └── 📂 io/github/ngtrphuc/smartphone_shop
│   │   │       ├── 📂 config
│   │   │       │   ├── 📄 AdminAccountInitializer.java
│   │   │       │   ├── 📄 DataInitializer.java
│   │   │       │   ├── 📄 GlobalModelAttributes.java
│   │   │       │   ├── 📄 LoginSuccessHandler.java
│   │   │       │   ├── 📄 SecurityConfig.java
│   │   │       │   ├── 📄 ThymeleafConfig.java
│   │   │       │   └── 📄 WebConfig.java
│   │   │       ├── 📂 controller
│   │   │       │   ├── 📂 admin
│   │   │       │   │   ├── 📄 AdminController.java
│   │   │       │   │   └── 📄 ChatAdminController.java
│   │   │       │   └── 📂 user
│   │   │       │       ├── 📄 AuthController.java
│   │   │       │       ├── 📄 CartController.java
│   │   │       │       ├── 📄 ChatUserController.java
│   │   │       │       ├── 📄 MainController.java
│   │   │       │       ├── 📄 OrderController.java
│   │   │       │       └── 📄 ProfileController.java
│   │   │       ├── 📂 model
│   │   │       │   ├── 📄 CartItem.java
│   │   │       │   ├── 📄 CartItemEntity.java
│   │   │       │   ├── 📄 ChatMessage.java
│   │   │       │   ├── 📄 Order.java
│   │   │       │   ├── 📄 OrderItem.java
│   │   │       │   ├── 📄 Product.java
│   │   │       │   └── 📄 User.java
│   │   │       ├── 📂 repository
│   │   │       │   ├── 📄 CartItemRepository.java
│   │   │       │   ├── 📄 ChatMessageRepository.java
│   │   │       │   ├── 📄 OrderRepository.java
│   │   │       │   ├── 📄 ProductRepository.java
│   │   │       │   └── 📄 UserRepository.java
│   │   │       ├── 📂 service
│   │   │       │   ├── 📄 AuthService.java
│   │   │       │   ├── 📄 CartService.java
│   │   │       │   ├── 📄 ChatService.java
│   │   │       │   ├── 📄 CustomUserDetailsService.java
│   │   │       │   ├── 📄 OrderService.java
│   │   │       │   └── 📄 OrderValidationException.java
│   │   │       └── 📄 SmartphoneShopApplication.java
│   │   └── 📂 resources
│   │       ├── 📂 static
│   │       │   ├── 📂 admin
│   │       │   │   └── 📂 css
│   │       │   │       └── 📄 style.css
│   │       │   ├── 📂 customer
│   │       │   │   ├── 📂 css
│   │       │   │   │   └── 📄 style.css
│   │       │   │   └── 📂 images
│   │       │   └── 📂 js
│   │       │       ├── 📄 admin-shell.js
│   │       │       └── 📄 order-success.js
│   │       ├── 📂 templates
│   │       │   ├── 📂 admin
│   │       │   │   ├── 📂 error
│   │       │   │   │   └── 📄 access-denied-admin.html
│   │       │   │   ├── 📄 chat.html
│   │       │   │   ├── 📄 dashboard.html
│   │       │   │   ├── 📄 orders.html
│   │       │   │   ├── 📄 product-form.html
│   │       │   │   └── 📄 products.html
│   │       │   └── 📂 customer
│   │       │       ├── 📂 auth
│   │       │       │   ├── 📄 login.html
│   │       │       │   └── 📄 register.html
│   │       │       ├── 📂 fragments
│   │       │       │   └── 📄 chat-widget.html
│   │       │       ├── 📄 cart.html
│   │       │       ├── 📄 checkout.html
│   │       │       ├── 📄 detail.html
│   │       │       ├── 📄 index.html
│   │       │       ├── 📄 my-orders.html
│   │       │       ├── 📄 profile.html
│   │       │       ├── 📄 shipping.html
│   │       │       └── 📄 success.html
│   │       ├── 📄 application.properties
│   │       ├── 📄 application-dev.properties
│   │       └── 📄 application-prod.properties
│   └── 📂 test
│       ├── 📂 java
│       │   └── 📂 io/github/ngtrphuc/smartphone_shop
│       │       ├── 📂 controller
│       │       │   └── 📂 user
│       │       │       └── 📄 MainControllerTest.java
│       │       ├── 📂 service
│       │       │   ├── 📄 AuthServiceTest.java
│       │       │   ├── 📄 CartServiceTest.java
│       │       │   └── 📄 OrderServiceTest.java
│       │       └── 📄 SmartphoneShopApplicationTests.java
│       └── 📂 resources
│           └── 📄 application-test.properties
├── 📂 scripts
│   └── 📄 remove_product_backgrounds.py
├── 📄 .gitignore
├── 📄 HELP.md
├── 📄 mvnw
├── 📄 mvnw.cmd
├── 📄 pom.xml
└── 📄 README.md
```

## Run

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```
