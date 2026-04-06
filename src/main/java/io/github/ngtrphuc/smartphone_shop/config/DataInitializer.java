package io.github.ngtrphuc.smartphone_shop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@Configuration
@Profile("dev")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            int inserted = 0;
            int updated = 0;

            Result[] results = {
                upsertProduct(repository, "iPhone 17e", 134800.0, "/images/iphone17e.png",
                        "iOS 26", "8 GB", "Apple A19", "3.78 GHz", "512 GB", "6.1 inch",
                        "2532 x 1170", "4005 mAh", "20W",
                        "Compact iPhone with A19 chip, 48MP camera and durable Ceramic Shield 2 design."),
                upsertProduct(repository, "iPhone 17", 164800.0, "/images/iphone17.png",
                        "iOS 26", "8 GB", "Apple A19", "3.78 GHz", "512 GB", "6.3 inch",
                        "2622 x 1206", "3692 mAh", "40W",
                        "Balanced flagship iPhone featuring ProMotion display and advanced dual-camera system."),
                upsertProduct(repository, "iPhone Air", 229800.0, "/images/iphoneair.png",
                        "iOS 26", "12 GB", "Apple A19 Pro", "4.05 GHz", "1 TB", "6.5 inch",
                        "2736 x 1260", "3149 mAh", "40W",
                        "Ultra-thin premium iPhone delivering flagship performance in a sleek lightweight design."),
                upsertProduct(repository, "iPhone 17 Pro", 249800.0, "/images/iphone17pro.png",
                        "iOS 26", "12 GB", "Apple A19 Pro", "4.05 GHz", "1 TB", "6.3 inch",
                        "2622 x 1206", "4200 mAh", "40W",
                        "Professional-grade iPhone with triple 48MP cameras and powerful A19 Pro performance."),
                upsertProduct(repository, "iPhone 17 Pro Max", 329800.0, "/images/iphone17promax.png",
                        "iOS 26", "12 GB", "Apple A19 Pro", "4.05 GHz", "2 TB", "6.9 inch",
                        "2868 x 1320", "4832 mAh", "45W",
                        "Apple's most powerful flagship with a large 6.9-inch display and advanced triple-camera system."),
                upsertProduct(repository, "Samsung Galaxy S26 Ultra", 299200.0, "/images/galaxy-s26-ultra.png",
                        "Android 16 (One UI 8)", "16 GB", "Snapdragon 8 Elite Gen 5", "4.47 GHz", "1 TB",
                        "6.9 inch",
                        "3120 x 1440", "5000 mAh", "60W",
                        "Samsung's most advanced flagship featuring a 200MP camera, S Pen support and a premium 6.9-inch display."),
                upsertProduct(repository, "Samsung Galaxy S26+", 196900.0, "/images/galaxy-s26-plus.png",
                        "Android 16 (One UI 8)", "12 GB", "Snapdragon 8 Elite Gen 5", "4.47 GHz", "512 GB",
                        "6.7 inch",
                        "3120 x 1440", "4900 mAh", "45W",
                        "Large-screen Samsung flagship delivering powerful performance and an immersive AMOLED display."),
                upsertProduct(repository, "Samsung Galaxy S26", 163900.0, "/images/galaxy-s26.png",
                        "Android 16 (One UI 8)", "12 GB", "Snapdragon 8 Elite Gen 5", "4.47 GHz", "512 GB",
                        "6.3 inch",
                        "2340 x 1080", "4300 mAh", "45W",
                        "Compact Samsung flagship with top-tier performance and a high-quality AMOLED display."),
                upsertProduct(repository, "Samsung Galaxy Z Fold7", 329320.0, "/images/galaxy-z-fold7.png",
                        "Android 16 (One UI 8)", "16 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB", "8.0 inch",
                        "2184 x 1968", "4400 mAh", "25W",
                        "Premium foldable smartphone with a tablet-sized display and cutting-edge multitasking capabilities."),
                upsertProduct(repository, "Xiaomi 17 Ultra", 219800.0, "/images/xiaomi17ultra.png",
                        "Android 16 (HyperOS 3)", "16 GB", "Snapdragon 8 Elite Gen 2", "4.47 GHz", "1 TB",
                        "6.73 inch",
                        "3200 x 1440", "5600 mAh", "120W",
                        "Xiaomi's next-generation Ultra flagship featuring Leica optics, extreme performance and ultra-fast charging."),
                upsertProduct(repository, "Xiaomi MIX Flip 2", 185000.0, "/images/xiaomi-mix-flip2.png",
                        "Android 15 (HyperOS 2)", "16 GB", "Snapdragon 8 Elite", "4.32 GHz", "1 TB",
                        "6.86 inch",
                        "2912 x 1224", "5165 mAh", "67W",
                        "Stylish foldable flip smartphone combining flagship hardware with a compact design."),
                upsertProduct(repository, "OPPO Find X9 Pro", 175000.0, "/images/findx9pro.png",
                        "Android 16 (ColorOS 16)", "16 GB", "Dimensity 9500", "4.21 GHz", "512 GB", "6.8 inch",
                        "2772 x 1272", "7500 mAh", "80W",
                        "Powerful OPPO flagship designed for photography and high-performance mobile computing."),
                upsertProduct(repository, "OPPO Find N5", 304690.0, "/images/findn5.png",
                        "Android 15 (ColorOS)", "16 GB", "Snapdragon 8 Elite", "4.32 GHz", "512 GB", "8.12 inch",
                        "2480 x 2248", "5600 mAh", "80W",
                        "Premium foldable smartphone offering a large immersive display and flagship performance."),
                upsertProduct(repository, "Sony Xperia 1 VII", 234300.0, "/images/xperia1vii.png",
                        "Android 15", "16 GB", "Snapdragon 8 Elite", "4.32 GHz", "512 GB", "6.5 inch",
                        "2340 x 1080", "5000 mAh", "30W",
                        "Sony's flagship smartphone built for creators with professional camera and display technology."),
                upsertProduct(repository, "Sony Xperia 1 VI", 218900.0, "/images/xperia1vi.png",
                        "Android 14", "12 GB", "Snapdragon 8 Gen 3", "3.30 GHz", "512 GB", "6.5 inch",
                        "2340 x 1080", "5000 mAh", "30W",
                        "Premium Sony flagship combining cinematic display quality with advanced camera features."),
                upsertProduct(repository, "Huawei Pura 70 Ultra", 189800.0, "/images/pura70ultra.png",
                        "HarmonyOS", "16 GB", "Kirin 9010", "2.30 GHz", "512 GB", "6.8 inch",
                        "2844 x 1260", "5200 mAh", "100W",
                        "Huawei's camera-focused flagship delivering cutting-edge photography performance."),
            };

            for (Result r : results) {
                inserted += r.inserted() ? 1 : 0;
                updated += r.updated() ? 1 : 0;
            }

            if (inserted > 0 || updated > 0) {
                log.info("Product catalog synced. Inserted: {}, Updated: {}", inserted, updated);
            } else {
                log.info("Product catalog already up to date.");
            }
        };
    }

    private Result upsertProduct(ProductRepository repo, String name, Double price, String img,
            String os, String ram, String cpu, String speed, String storage,
            String size, String res, String battery, String charging, String desc) {
        Product p = repo.findFirstByNameIgnoreCase(name)
                .orElseGet(Product::new);
        boolean isNew = p.getId() == null;

        p.setName(name);
        p.setPrice(price);
        if (isNew || p.getStock() == null) {
            p.setStock(10);
        }
        p.setImageUrl(img);
        p.setOs(os);
        p.setRam(ram);
        p.setChipset(cpu);
        p.setSpeed(speed);
        p.setStorage(storage);
        p.setSize(size);
        p.setResolution(res);
        p.setBattery(battery);
        p.setCharging(charging);
        p.setDescription(desc);
        repo.save(p);

        if (isNew) {
            return Result.insertedResult();
        }
        return Result.updatedResult();
    }

    private record Result(boolean inserted, boolean updated) {

        static Result insertedResult() {
            return new Result(true, false);
        }

        static Result updatedResult() {
            return new Result(false, true);
        }
    }
}
