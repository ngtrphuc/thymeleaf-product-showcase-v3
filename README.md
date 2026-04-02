📱 Smartphone Shop - Spring Boot Web Application 
A robust e-commerce platform for mobile devices, built with Java Spring Boot, Thymeleaf, and Spring Security, utilizing MySQL for persistent storage. This version focuses on a clean project architecture, optimized security logic, and professional multi-environment configuration.

🚀 Key Features
Authentication & Authorization: Secure registration and login system with clear role separation between Admin and User.

Smart Navigation: Implementation of a LoginSuccessHandler to automatically redirect users based on their roles after a successful login.

Persistent Shopping Cart: Full CRUD functionality for the cart, with data synchronized between sessions and the database.

User Profiles: Dedicated profile management allowing users to view and update their personal information.

Admin Dashboard: A centralized interface for managing products, inventory, and tracking customer orders.

Custom Error Handling: Professional "Access Denied" pages for unauthorized attempts to enter the administrative area.

🛠 Tech Stack
Backend: Java 17, Spring Boot 3.x

Security: Spring Security (Form-based authentication)

Frontend: Thymeleaf, HTML5, CSS3 (Internalized styling)

Database: MySQL (Production) & H2 (Development)

Build Tool: Maven

📂 Project Detailed Structure
This map represents the finalized architecture:
```
smartphone-shop/
├── 📁 src/
│   ├── 📂 main/
│   │   ├── 📁 java/io/github/ngtrphuc/smartphone_shop/
│   │   │   ├── ⚡ SmartphoneShopApplication.java
│   │   │   ├── ⚙️ config/
│   │   │   │   ├── DataInitializer.java
│   │   │   │   ├── LoginSuccessHandler.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── 🕹️ controller/
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CartController.java
│   │   │   │   ├── MainController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   └── ProfileController.java
│   │   │   ├── 📦 model/
│   │   │   │   ├── CartItem.java
│   │   │   │   ├── CartItemEntity.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Product.java
│   │   │   │   └── User.java
│   │   │   ├── 🗄️ repository/
│   │   │   │   ├── CartItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── 🛠️ service/
│   │   │       ├── AuthService.java
│   │   │       ├── CartService.java
│   │   │       ├── CustomUserDetailsService.java
│   │   │       └── OrderService.java
│   │   │
│   │   └── 📂 resources/
│   │       ├── 📄 static/
│   │       │   ├── 🎨 css/style.css
│   │       │   └── 🖼️ images/
│   │       ├── 📋 templates/
│   │       │   ├── 👤 auth/ (login.html, register.html)
│   │       │   ├── 🛡️ admin/ (dashboard.html, orders.html, product-form.html, products.html)
│   │       │   ├── ⚠️ error/ (access-denied-admin.html)
│   │       │   └── ... (cart, checkout, detail, index, my-orders, profile, shipping, success)
│   │       ├── ⚙️ application.properties
│   │       ├── ⚙️ application-dev.properties
│   │       └── ⚙️ application-prod.properties
│   │
│   └── 🧪 test/java/
│       └── SmartphoneShopApplicationTests.java
│
├── 🪶 pom.xml
└── 📄 mvnw
```

⚙️ Setup and Installation
Clone the repository:

Bash
git clone https://github.com/ngtrphuc/thymeleaf-product-showcase-v3.git
Database Configuration:
Configure your MySQL connection details in src/main/resources/application-prod.properties.

Run the Application:
Using the Maven Wrapper:

Bash
./mvnw spring-boot:run
Access the App:
Open your browser and navigate to http://localhost:8080.
