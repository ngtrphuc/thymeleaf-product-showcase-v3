# 📱 Smartphone Shop - Spring Boot Web Application

A smartphone e-commerce web application built with Spring Boot, Thymeleaf, Spring Security, and MySQL. The project includes customer-facing shopping flows, admin management pages, chat support, and multi-profile configuration for development and production.

## 🚀 Key Features

- Authentication and authorization with separate `ADMIN` and `USER` roles
- Custom login success handling based on role
- Product browsing, filtering, and detail pages
- Persistent shopping cart and checkout flow
- User profile management
- Customer order history and order cancellation
- Admin dashboard for products, orders, and chat management
- Customer/admin chat support flow
- Profile-based configuration for development and production

## 🛠 Tech Stack

- Backend: Java 21, Spring Boot 3.5.x
- Security: Spring Security with form-based authentication and role-based access control
- Frontend: Thymeleaf, HTML5, CSS3
- Database: MySQL in both development and production profiles
- Persistence: Spring Data JPA / Hibernate
- API Docs: springdoc-openapi
- Build Tool: Maven

## 📂 Project Structure

```
📂 SMARTPHONE SHOP
├── 📂 src
│   ├── 📂 main
│   │   ├── 📂 java
│   │   │   └── 📂 io.github.ngtrphuc.smartphone_shop
│   │   │       ├── 📂 config
│   │   │       │   ├── 📄 DataInitializer.java
│   │   │       │   ├── 📄 GlobalModelAttributes.java
│   │   │       │   ├── 📄 LoginSuccessHandler.java
│   │   │       │   ├── 📄 SecurityConfig.java
│   │   │       │   ├── 📄 ThymeleafConfig.java
│   │   │       │   └── 📄 WebConfig.java
│   │   │       │
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
│   │   │       │
│   │   │       ├── 📂 model
│   │   │       │   ├── 📄 CartItem.java
│   │   │       │   ├── 📄 CartItemEntity.java
│   │   │       │   ├── 📄 ChatMessage.java
│   │   │       │   ├── 📄 Order.java
│   │   │       │   ├── 📄 OrderItem.java
│   │   │       │   ├── 📄 Product.java
│   │   │       │   └── 📄 User.java
│   │   │       │
│   │   │       ├── 📂 repository
│   │   │       │   ├── 📄 CartItemRepository.java
│   │   │       │   ├── 📄 ChatMessageRepository.java
│   │   │       │   ├── 📄 OrderRepository.java
│   │   │       │   ├── 📄 ProductRepository.java
│   │   │       │   └── 📄 UserRepository.java
│   │   │       │
│   │   │       ├── 📂 service
│   │   │       │   ├── 📄 AuthService.java
│   │   │       │   ├── 📄 CartService.java
│   │   │       │   ├── 📄 ChatService.java
│   │   │       │   ├── 📄 CustomUserDetailsService.java
│   │   │       │   └── 📄 OrderService.java
│   │   │       │
│   │   │       └── 📄 SmartphoneShopApplication.java
│   │   │
│   │   └── 📂 resources
│   │       ├── 📂 static
│   │       │   ├── 📂 admin
│   │       │   │   └── 📂 css
│   │       │   │       └── 📄 style.css
│   │       │   └── 📂 customer
│   │       │       ├── 📂 css
│   │       │       │   └── 📄 style.css
│   │       │       ├── 📂 fonts
│   │       │       └── 📂 images
│   │       │
│   │       ├── 📂 templates
│   │       │   ├── 📂 admin
│   │       │   │   ├── 📂 error
│   │       │   │   │   └── 📄 access-denied-admin.html
│   │       │   │   ├── 📄 chat.html
│   │       │   │   ├── 📄 dashboard.html
│   │       │   │   ├── 📄 orders.html
│   │       │   │   ├── 📄 product-form.html
│   │       │   │   └── 📄 products.html
│   │       │   │
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
│   │       │
│   │       ├── 📄 application.properties
│   │       ├── 📄 application-dev.properties
│   │       └── 📄 application-prod.properties
│   │
│   └── 📂 test
│       └── 📂 java
│           └── 📂 io.github.ngtrphuc.smartphone_shop
│               └── 📄 SmartphoneShopApplicationTests.java
│
├── 📄 pom.xml
├── 📄 mvnw
├── 📄 mvnw.cmd
├── 📄 .gitignore
└── 📄 README.md
```

## ⚙️ Setup and Run

### 1. Clone the repository

```bash
git clone https://github.com/ngtrphuc/thymeleaf-product-showcase-v3.git
cd thymeleaf-product-showcase-v3
```

### 2. Configure the database

The app uses the `dev` profile by default:

```properties
spring.profiles.active=dev
```

Update your local MySQL settings in:

- `src/main/resources/application-dev.properties` for local development
- `src/main/resources/application-prod.properties` for production

### 3. Run the application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

### 4. Access the app

Open:

```text
http://localhost:8080
```

## 📝 Notes

- Development currently uses MySQL, not H2
- Static assets are organized under `static/customer` and `static/admin`
- Templates are organized under `templates/customer` and `templates/admin`
