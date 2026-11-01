package com.thinkmart.catalog;

import java.math.BigDecimal;

public record Product(
        long id,
        String name,
        String category,
        BigDecimal price,
        BigDecimal previousPrice,
        double rating,
        String imageUrl,
        String badge,
        String description) {
}