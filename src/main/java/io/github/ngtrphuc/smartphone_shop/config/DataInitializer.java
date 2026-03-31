package io.github.ngtrphuc.smartphone_shop.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.model.User;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import io.github.ngtrphuc.smartphone_shop.repository.UserRepository;
@Configuration
public class DataInitializer {
    private final ProductRepository repository;
    private final UserRepository userRepository;
    public DataInitializer(ProductRepository repository,
                           UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Bean
    public CommandLineRunner initDatabase(PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.count() > 0) {
                System.out.println(">> Database already initialized, skipping.");
                if (!userRepository.existsByEmail("admin@shop.com")) {
                    User admin = new User();
                    admin.setEmail("admin@shop.com");
                    admin.setFullName("Admin");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setRole("ROLE_ADMIN");
                    userRepository.save(admin);
                    System.out.println(">> Admin account created: admin@shop.com / admin123");
                }
                return;
            }

            saveProduct(repository, "iPhone 16 Pro Max", 249800.0, "/images/iphone16.jpg",
                    "iOS 18", "8 GB", "Apple A18 Pro", "4.04 GHz", "1 TB",
                    "6.9 inch", "2868 x 1320", "4676 mAh", "30W",
                    "高級なチタン設計とA18 Proチップを搭載したiPhone 16 Pro Maxです。");
            saveProduct(repository, "Samsung Galaxy S25 Ultra", 253800.0, "/images/galaxy_s25.jpg",
                    "Android 15", "12 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB",
                    "6.9 inch", "3120 x 1440", "5000 mAh", "45W",
                    "6.9インチのDynamic AMOLED 2XディスプレイとSペンを搭載。");
            saveProduct(repository, "Google Pixel 9 Pro XL", 212900.0, "/images/pixel9.jpg",
                    "Android 15", "16 GB", "Tensor G4", "3.10 GHz", "1 TB",
                    "6.8 inch", "2992 x 1344", "5060 mAh", "45W",
                    "先進的なAIカメラと長期間のサポートが特徴。");
            saveProduct(repository, "Oppo Find X8 Ultra", 165000.0, "/images/oppo_x8.jpg",
                    "Android 15", "16 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB",
                    "6.82 inch", "3168 x 1440", "6100 mAh", "100W",
                    "超ズームカメラシステムと薄型デザイン。");
            saveProduct(repository, "Vivo X200 Ultra", 152200.0, "/images/vivo_x200.jpg",
                    "Android 15", "16 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB",
                    "6.82 inch", "3168 x 1440", "6000 mAh", "90W",
                    "LTPO 2Kディスプレイとジンバル手ぶれ補正カメラ。");
            saveProduct(repository, "Xiaomi 15 Ultra", 199800.0, "/images/xiaomi15.jpg",
                    "Android 15", "16 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB",
                    "6.73 inch", "3200 x 1440", "5410 mAh", "90W",
                    "1インチセンサーを搭載したカメラ特化モデル。");
            saveProduct(repository, "ASUS ROG Phone 9 Pro", 239800.0, "/images/rog9.jpg",
                    "Android 15", "24 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB",
                    "6.78 inch", "2400 x 1080", "5800 mAh", "65W",
                    "ゲーマー向けの強化冷却システム搭載。");
            saveProduct(repository, "ZTE Nubia RedMagic 10 Pro", 189800.0, "/images/redmagic10.jpg",
                    "Android 15", "24 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB",
                    "6.85 inch", "2400 x 1080", "7050 mAh", "80W",
                    "144Hzディスプレイと内蔵ファン搭載。");

            System.out.println(">> Database initialized successfully!");

            if (!userRepository.existsByEmail("admin@shop.com")) {
                User admin = new User();
                admin.setEmail("admin@shop.com");
                admin.setFullName("Admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println(">> Admin account created: admin@shop.com / admin123");
            }
        };
    }

    private void saveProduct(ProductRepository repo, String name, Double price, String img,
            String os, String ram, String cpu, String speed, String storage,
            String size, String res, String battery, String charging, String desc) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStock(10);
        p.setImageUrl(img);
        p.setOs(os);
        p.setRam(ram);
        p.setCpu(cpu);
        p.setSpeed(speed);
        p.setStorage(storage);
        p.setSize(size);
        p.setResolution(res);
        p.setBattery(battery);
        p.setCharging(charging);
        p.setDescription(desc);
        repo.save(p);
    }
}