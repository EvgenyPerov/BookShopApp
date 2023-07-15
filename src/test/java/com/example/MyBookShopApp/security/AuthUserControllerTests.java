package com.example.MyBookShopApp.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class AuthUserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Доступ к 'my' только для регист users")
    public void accessOnliAuthorizedPageFailTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/my"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/signin"));
    }

    @Test
    @DisplayName("Корректный вход по Email авториз user")
    public void correctLoginTest() throws Exception{
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin("/signin")
                        .user("My@mail.ru").password("1234567"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    @DisplayName("Доступ к странице 'profile' только для регист users")
    @WithUserDetails("My@mail.ru")
    public void testAuthentificatedAccessToProfilePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/profile"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(MockMvcResultMatchers
                        .xpath("/html/body/header/div[1]/div/div/div[3]/div/a[4]/span[1]")
                        .string("Samanta"));

    }

    @Test
    @DisplayName("регистрация нового пользователя")
    void regNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .xpath("/html/body/div/div[2]/main/form/div/div/div[4]/button")
                        .string("Зарегистрироваться"));
    }

    @Test
    @DisplayName("вход по Email")
    void loginByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signin"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .xpath("/html/body/div/div[2]/main/form/div/div[3]/div[1]/label")
                        .string("Введите пароль для входа или пройдите регистрацию:"))   ;
    }

    @Test
    @DisplayName("выход из личного кабинета")
    void logout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/signin")) ;
    }


}