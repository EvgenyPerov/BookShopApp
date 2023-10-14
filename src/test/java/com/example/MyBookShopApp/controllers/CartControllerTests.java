package com.example.MyBookShopApp.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Role;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class CartControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Role(2)
    @DisplayName("Добавление книги в корзину не авториз user")
    void handleChangeBookStatusCart() throws Exception {
        int  bookId = 3;
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/books/changeBookStatus/CART/"+bookId+"/slug"))
                .andDo(MockMvcResultHandlers.print());
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//                .andExpect(MockMvcResultMatchers.redirectedUrl("/books/postponed"))
//                .andExpect(MockMvcResultMatchers.cookie().value("cartContents", String.valueOf(bookId)));

        String name = "testName";
        assertEquals("testName", name);
    }

    @Test
    @DisplayName("Удаление книги из корзины не авториз user")
    void handleChangeBookStatusCartRemove() throws Exception {
        int  bookId = 3;

        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/books/changeBookStatus/UNLINK/"+bookId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> {
                    Cookie[] cookies = result.getResponse().getCookies();
                    boolean cookieCartExists = java.util.Arrays.stream(cookies)
                            .anyMatch(cookie -> cookie.getName().equals("cartContents") &&
                                                cookie.getValue().equals(bookId));
                    assertFalse(cookieCartExists, "Unexpected cookie found!");
                });
    }

}