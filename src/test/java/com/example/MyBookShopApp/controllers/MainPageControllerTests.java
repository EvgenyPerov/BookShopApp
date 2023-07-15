package com.example.MyBookShopApp.controllers;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class MainPageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Доступ к главной странице")
    public void mainPageAccessTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }



    @Test
    @DisplayName("Поиск по слову 'Born'")
    public void searchTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/search/Born"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers
                        .xpath("/html/body/div/div/main/div[2]/div/div[1]/div[2]/strong/a")
                        .string("Born Romantic"));

    }



}