package com.thinkmart.web;

import com.thinkmart.cart.CartService;
import com.thinkmart.catalog.ProductCatalog;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StoreController {
    private final ProductCatalog catalog;
    private final CartService cart;

    public StoreController(ProductCatalog catalog, CartService cart) {
        this.catalog = catalog;
        this.cart = cart;
    }

    @GetMapping("/")
    public String start() {
        return "redirect:/signup";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        model.addAttribute("featured", catalog.featured());
        model.addAttribute("categories", catalog.categories());
        model.addAttribute("cartCount", cart.count(session));
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        model.addAttribute("cartCount", cart.count(session));
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        RedirectAttributes redirect) {
        if (email.isBlank() || password.isBlank()) {
            redirect.addFlashAttribute("error", "Enter your email and password to continue.");
            return "redirect:/login";
        }
        redirect.addFlashAttribute("notice", "You're signed in for this demo.");
        return "redirect:/home";
    }

    @GetMapping("/signup")
    public String signupPage(Model model, HttpSession session) {
        model.addAttribute("cartCount", cart.count(session));
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String name, @RequestParam String email,
                         @RequestParam String password, RedirectAttributes redirect) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            redirect.addFlashAttribute("error", "Complete each field to continue.");
            return "redirect:/signup";
        }
        redirect.addFlashAttribute("notice", "Account created. Sign in to continue, " + name.trim() + ".");
        return "redirect:/login";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String category,
                           @RequestParam(defaultValue = "featured") String sort,
                           Model model, HttpSession session) {
        model.addAttribute("products", catalog.findAll(category, sort));
        model.addAttribute("categories", catalog.categories());
        model.addAttribute("selectedCategory", category == null ? "" : category);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("cartCount", cart.count(session));
        return "products";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        model.addAttribute("items", cart.getItems(session));
        model.addAttribute("cartCount", cart.count(session));
        model.addAttribute("subtotal", cart.subtotal(session));
        model.addAttribute("discount", cart.discount(session));
        model.addAttribute("total", cart.total(session));
        model.addAttribute("couponApplied", cart.isCouponApplied(session));
        return "cart";
    }

    @PostMapping("/cart/items/{id}")
    public String addToCart(@PathVariable long id, HttpSession session) {
        cart.add(session, id);
        return "redirect:/products";
    }

    @PostMapping("/cart/items/{id}/quantity")
    public String changeQuantity(@PathVariable long id, @RequestParam int change, HttpSession session) {
        if (change == 1 || change == -1) {
            cart.changeQuantity(session, id, change);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/items/{id}/remove")
    public String removeItem(@PathVariable long id, HttpSession session) {
        cart.remove(session, id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/coupon")
    public String applyCoupon(@RequestParam String code, HttpSession session, RedirectAttributes redirect) {
        if (cart.applyCoupon(session, code)) {
            redirect.addFlashAttribute("notice", "THINK8 applied. Your 8% discount is ready.");
        } else {
            redirect.addFlashAttribute("error", "That code is not valid for this cart.");
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirect) {
        if (cart.count(session) == 0) {
            redirect.addFlashAttribute("error", "Your cart is empty. Add something you love first.");
            return "redirect:/cart";
        }
        cart.clear(session);
        redirect.addFlashAttribute("notice", "Your order has been placed successfully. Thanks for shopping with ThinkMart.");
        return "redirect:/home";
    }

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        model.addAttribute("cartCount", cart.count(session));
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model, HttpSession session) {
        model.addAttribute("cartCount", cart.count(session));
        return "contact";
    }

    @PostMapping("/contact")
    public String sendContactMessage(RedirectAttributes redirect) {
        redirect.addFlashAttribute("notice", "Message received. Our team will be in touch soon.");
        return "redirect:/contact";
    }
}