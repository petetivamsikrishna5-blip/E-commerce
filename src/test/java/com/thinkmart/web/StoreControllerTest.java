package com.thinkmart.web;

import com.thinkmart.cart.CartService;
import com.thinkmart.catalog.ProductCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class StoreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartService cart;

    @Autowired
    private ProductCatalog catalog;

    @Test
    void homeRendersTheNewStorefront() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"));
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("Better things.")))
                .andExpect(content().string(containsString("MacBook Pro 16-inch")));
    }

    @Test
    void categoryFilterReturnsMatchingProducts() throws Exception {
        mockMvc.perform(get("/products").param("category", "Headphones"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sony WH-1000XM5")))
                .andExpect(content().string(containsString("AirPods Max")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("MacBook Pro 16-inch"))));
    }

    @Test
        void signupLeadsToLoginAndLoginLeadsToHome() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));

        MvcResult signup = mockMvc.perform(post("/signup").param("name", "Taylor")
                        .param("email", "taylor@example.com").param("password", "secret1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andReturn();
        mockMvc.perform(get("/login").flashAttrs(signup.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Account created. Sign in to continue, Taylor.")));
        mockMvc.perform(post("/login").param("email", "taylor@example.com").param("password", "secret1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void cartSupportsAddsCouponAndCheckout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/cart/items/4").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
        mockMvc.perform(post("/cart/coupon").session(session).param("code", "THINK8"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(content().string(containsString("Sony WH-1000XM5")))
                .andExpect(content().string(containsString("$256.68")));

        MvcResult checkout = mockMvc.perform(post("/checkout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andReturn();
        mockMvc.perform(get("/home").session(session).flashAttrs(checkout.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your order has been placed successfully.")))
                .andExpect(content().string(containsString("showModal()")));
        org.junit.jupiter.api.Assertions.assertEquals(0, cart.count(session));
    }
}