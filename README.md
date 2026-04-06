# Smartphone Shop

Ứng dụng web bán điện thoại xây bằng Spring Boot + Thymeleaf, có đầy đủ luồng khách hàng và quản trị.

## Tính năng chính

- Đăng ký/đăng nhập, phân quyền `ROLE_USER` và `ROLE_ADMIN`
- Danh sách sản phẩm, lọc/tìm kiếm, trang chi tiết
- Giỏ hàng, checkout nhiều bước
- Quản lý phương thức thanh toán:
  - `Cash on Delivery`
  - `Bank Transfer`
  - `PayPay`
- Lưu địa chỉ mặc định trong profile và dùng lại khi shipping
- Theo dõi đơn hàng của khách
- Admin quản lý sản phẩm, đơn hàng, chat
- Chat realtime giữa khách và admin (SSE)

## Công nghệ

- Java 21
- Spring Boot 3.5.13
- Spring Security 6
- Spring Data JPA (Hibernate)
- Thymeleaf
- Maven
- H2 (dev/test), MySQL/MariaDB (prod)

## Cấu trúc dự án

```text
📂 smartphone-shop
├── 📂 src
│   ├── 📂 main
│   │   ├── 📂 java/io/github/ngtrphuc/smartphone_shop
│   │   │   ├── 📂 config
│   │   │   ├── 📂 controller
│   │   │   │   ├── 📂 admin
│   │   │   │   └── 📂 user
│   │   │   ├── 📂 model
│   │   │   ├── 📂 repository
│   │   │   ├── 📂 service
│   │   │   └── 📄 SmartphoneShopApplication.java
│   │   └── 📂 resources
│   │       ├── 📂 static
│   │       │   ├── 📂 admin/css
│   │       │   ├── 📂 customer/css
│   │       │   ├── 📂 customer/images
│   │       │   └── 📂 js
│   │       ├── 📂 templates
│   │       │   ├── 📂 admin
│   │       │   └── 📂 customer
│   │       ├── 📄 application.properties
│   │       ├── 📄 application-dev.properties
│   │       └── 📄 application-prod.properties
│   └── 📂 test
│       ├── 📂 java
│       └── 📂 resources
├── 📄 pom.xml
├── 📄 mvnw
└── 📄 mvnw.cmd
```

## Chạy nhanh (Dev profile mặc định)

Ứng dụng mặc định chạy profile `dev`, dùng H2 file database local nên không cần cài MySQL để chạy thử.

### 1) Run app

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

### 2) Truy cập

- Home: `http://localhost:8080/`
- H2 Console: `http://localhost:8080/h2-console`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`

## Tài khoản admin bootstrap

App tự bootstrap tài khoản admin bằng biến môi trường:

- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

Dev có giá trị mặc định trong `application-dev.properties`:

- `admin@smartphone.local`
- `Admin@123456`

Bạn có thể override trước khi chạy.

## Chạy Production (MySQL/MariaDB)

Dùng profile `prod` và cung cấp:

- `DATASOURCE_URL`
- `DATASOURCE_USER`
- `DATASOURCE_PASSWORD`
- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

Ví dụ:

```bash
SPRING_PROFILES_ACTIVE=prod \
DATASOURCE_URL=jdbc:mysql://localhost:3306/smartphone_shop \
DATASOURCE_USER=root \
DATASOURCE_PASSWORD=your_password \
ADMIN_EMAIL=admin@yourdomain.com \
ADMIN_PASSWORD=your_strong_password \
./mvnw spring-boot:run
```

## Chạy test

```bash
./mvnw test
```

Windows:

```bat
mvnw.cmd test
```

## Ghi chú bảo mật

- Cookie session `HttpOnly`, `SameSite=Lax`
- Session fixation protection + giới hạn concurrent session
- Security headers: CSP, frame deny, referrer policy, permissions policy
- CSRF bật mặc định cho form actions

