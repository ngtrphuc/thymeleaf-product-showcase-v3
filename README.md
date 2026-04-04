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
├── 📁 backend/
│   ├── ⚡ SmartphoneShopApplication.java
│
│   ├── ⚙️ config/
│   │   ├── DataInitializer.java
│   │   ├── GlobalModelAttributes.java
│   │   ├── LoginSuccessHandler.java
│   │   ├── SecurityConfig.java
│   │   ├── ThymeleafConfig.java          
│   │   └── WebConfig.java                
│   │
│   ├── 🎮 controller/
│   │   ├── 📁 admin/
│   │   │   ├── AdminController.java
│   │   │   └── ChatAdminController.java  
│   │   │
│   │   └── 📁 user/
│   │       ├── AuthController.java
│   │       ├── CartController.java
│   │       ├── ChatUserController.java   
│   │       ├── MainController.java
│   │       ├── OrderController.java
│   │       └── ProfileController.java
│   │
│   ├── 🛠️ service/
│   │   ├── AuthService.java
│   │   ├── CartService.java
│   │   ├── ChatService.java
│   │   ├── CustomUserDetailsService.java
│   │   └── OrderService.java
│   │
│   ├── 🗄️ repository/
│   │   ├── CartItemRepository.java
│   │   ├── ChatMessageRepository.java
│   │   ├── OrderRepository.java
│   │   ├── ProductRepository.java
│   │   └── UserRepository.java
│   │
│   └── 📦 model/
│       ├── CartItem.java
│       ├── CartItemEntity.java
│       ├── ChatMessage.java
│       ├── Order.java
│       ├── OrderItem.java
│       ├── Product.java
│       └── User.java
│
├── 📁 frontend/
│   ├── 👤 customer/
│   │   ├── 🧾 templates/
│   │   │   ├── 🏠 index.html
│   │   │   ├── 🔍 detail.html
│   │   │   ├── 🛒 cart.html
│   │   │   ├── 🏷️ checkout.html
│   │   │   ├── 🚚 shipping.html
│   │   │   ├── ✅ success.html
│   │   │   ├── 📦 my-orders.html
│   │   │   ├── 🆔 profile.html
│   │   │   │
│   │   │   ├── 👤 auth/
│   │   │   │   ├── 🔑 login.html
│   │   │   │   └── 🔒 register.html
│   │   │   │
│   │   │   └── 🧩 fragments/
│   │   │       └── chat-widget.html
│   │   │
│   │   └── 📄 static/
│   │       ├── 🎨 css/style.css
│   │       ├── 🔤 fonts/                 
│   │       └── 🖼️ images/
│   │
│   └── 🛡️ admin/
│       ├── 🧾 templates/
│       │   ├── dashboard.html
│       │   ├── orders.html
│       │   ├── products.html
│       │   ├── product-form.html
│       │   ├── chat.html                
│       │   └── 📁 error/
│       │       └── access-denied-admin.html
│       │
│       └── 📄 static/
│           └── 🎨 css/style.css
│
├── 🕹️ config/
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prod.properties
│
├── 🧪 test/
│   └── SmartphoneShopApplicationTests.java
│
├── 🪶 pom.xml
├── 📄 mvnw
├── 📄 mvnw.cmd
└── 📄 README.md

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
