package com.thinkmart.cart;

import com.thinkmart.catalog.Product;
import com.thinkmart.catalog.ProductCatalog;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {
    private static final String ITEMS_KEY = "thinkmart.cart";
    private static final String COUPON_KEY = "thinkmart.coupon";
    private static final BigDecimal COUPON_RATE = new BigDecimal("0.08");
    private final ProductCatalog catalog;

    public CartService(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    public void add(HttpSession session, long productId) {
        catalog.findById(productId).ifPresent(product -> {
            Map<Long, Integer> items = items(session);
            items.merge(productId, 1, Integer::sum);
        });
    }

    public void changeQuantity(HttpSession session, long productId, int change) {
        Map<Long, Integer> items = items(session);
        int quantity = items.getOrDefault(productId, 0) + change;
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            items.put(productId, quantity);
        }
    }

    public void remove(HttpSession session, long productId) {
        items(session).remove(productId);
    }

    public List<CartItem> getItems(HttpSession session) {
        List<CartItem> result = new ArrayList<>();
        items(session).forEach((id, quantity) -> catalog.findById(id)
                .ifPresent(product -> result.add(new CartItem(product, quantity))));
        return result;
    }

    public int count(HttpSession session) {
        return items(session).values().stream().mapToInt(Integer::intValue).sum();
    }

    public BigDecimal subtotal(HttpSession session) {
        return getItems(session).stream().map(CartItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal discount(HttpSession session) {
        return isCouponApplied(session) ? subtotal(session).multiply(COUPON_RATE) : BigDecimal.ZERO;
    }

    public BigDecimal total(HttpSession session) {
        return subtotal(session).subtract(discount(session));
    }

    public boolean applyCoupon(HttpSession session, String code) {
        if ("THINK8".equalsIgnoreCase(code == null ? "" : code.trim()) && count(session) > 0) {
            session.setAttribute(COUPON_KEY, true);
            return true;
        }
        return false;
    }

    public boolean isCouponApplied(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(COUPON_KEY));
    }

    public void clear(HttpSession session) {
        session.removeAttribute(ITEMS_KEY);
        session.removeAttribute(COUPON_KEY);
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> items(HttpSession session) {
        Object existing = session.getAttribute(ITEMS_KEY);
        if (existing instanceof Map<?, ?>) {
            return (Map<Long, Integer>) existing;
        }
        Map<Long, Integer> items = new LinkedHashMap<>();
        session.setAttribute(ITEMS_KEY, items);
        return items;
    }
}