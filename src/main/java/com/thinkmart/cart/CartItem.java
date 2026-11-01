package com.thinkmart.cart;

import com.thinkmart.catalog.Product;

import java.math.BigDecimal;

public record CartItem(Product product, int quantity) {
    public BigDecimal lineTotal() {
        return product.price().multiply(BigDecimal.valueOf(quantity));
    }
}