package io.github.ngtrphuc.smartphone_shop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                System.out.println(">> Database already initialized, skipping.");
                return;
            }

            saveProduct(repository, "iPhone 16 Pro Max", 249800.0, "/images/iphone16.jpg", "iOS 18", "8 GB", "Apple A18 Pro", "4.04 GHz", "1 TB", "6.9 inch", "2868 x 1320", "4676 mAh", "30W", "Advanced titanium design with A18 Pro chip.");
            saveProduct(repository, "Samsung Galaxy S25 Ultra", 253800.0, "/images/galaxy_s25.jpg", "Android 15", "12 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB", "6.9 inch", "3120 x 1440", "5000 mAh", "45W", "6.9-inch Dynamic AMOLED 2X display with S Pen.");
            saveProduct(repository, "Google Pixel 9 Pro XL", 212900.0, "/images/pixel9.jpg", "Android 15", "16 GB", "Tensor G4", "3.10 GHz", "1 TB", "6.8 inch", "2992 x 1344", "5060 mAh", "45W", "Advanced AI camera and long-term support.");
            // ... (Các sản phẩm khác tương tự)

            System.out.println(">> Database initialized successfully!");
        };
    }

    private void saveProduct(ProductRepository repo, String name, Double price, String img,
            String os, String ram, String cpu, String speed, String storage,
            String size, String res, String battery, String charging, String desc) {
        Product p = new Product();
        p.setName(name); p.setPrice(price); p.setStock(10); p.setImageUrl(img);
        p.setOs(os); p.setRam(ram); p.setChipset(cpu); p.setSpeed(speed);
        p.setStorage(storage); p.setSize(size); p.setResolution(res);
        p.setBattery(battery); p.setCharging(charging); p.setDescription(desc);
        repo.save(p);
    }
}