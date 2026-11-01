package com.thinkmart.catalog;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCatalog {
    private final List<Product> products = List.of(
            product(1, "MacBook Pro 16-inch", "Laptops", "2499", "3299", 5.0,
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=900&q=85", "25% OFF", "M3 Max, 36GB memory, 1TB SSD"),
            product(2, "iPhone 15 Pro Max", "Smartphones", "1199", "1399", 4.8,
                    "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=900&q=85", "18% OFF", "256GB titanium edition"),
            product(3, "Dell XPS 15 OLED", "Laptops", "1899", "2199", 4.9,
                    "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?auto=format&fit=crop&w=900&q=85", "Bestseller", "Intel Core i9, 32GB memory, RTX 4070"),
            product(4, "Sony WH-1000XM5", "Headphones", "279", "399", 4.9,
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=85", "30% OFF", "Wireless noise cancelling headphones"),
            product(5, "Apple Watch Ultra 2", "Smartwatches", "749", "799", 4.9,
                    "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&w=900&q=85", "Just landed", "GPS + Cellular, 49mm titanium"),
            product(6, "Samsung Galaxy S24 Ultra", "Smartphones", "1249", "1469", 4.8,
                    "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?auto=format&fit=crop&w=900&q=85", "15% OFF", "512GB titanium gray"),
            product(7, "ASUS ROG Zephyrus G16", "Laptops", "2199", "2699", 4.9,
                    "https://images.unsplash.com/photo-1603302576837-37561b2e2302?auto=format&fit=crop&w=900&q=85", "20% OFF", "Ryzen 9, RTX 4080, 32GB memory"),
            product(8, "AirPods Max", "Headphones", "479", "549", 4.7,
                    "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=900&q=85", "Trending", "Wireless over-ear headphones"));

    public List<Product> findAll(String category, String sort) {
        Comparator<Product> comparator = switch (sort == null ? "featured" : sort) {
            case "price-asc" -> Comparator.comparing(Product::price);
            case "price-desc" -> Comparator.comparing(Product::price).reversed();
            case "rating" -> Comparator.comparingDouble(Product::rating).reversed();
            default -> Comparator.comparingLong(Product::id);
        };
        return products.stream()
                .filter(product -> category == null || category.isBlank()
                        || product.category().equalsIgnoreCase(category))
                .sorted(comparator)
                .toList();
    }

    public List<Product> featured() {
        return products.subList(0, 4);
    }

    public List<Product> categories() {
        return products.stream().map(Product::category).distinct().map(category ->
                products.stream().filter(product -> product.category().equals(category)).findFirst().orElseThrow())
                .toList();
    }

    public Optional<Product> findById(long id) {
        return products.stream().filter(product -> product.id() == id).findFirst();
    }

    private static Product product(long id, String name, String category, String price, String previousPrice,
                                   double rating, String imageUrl, String badge, String description) {
        return new Product(id, name, category, new BigDecimal(price), new BigDecimal(previousPrice),
                rating, imageUrl, badge, description);
    }
}