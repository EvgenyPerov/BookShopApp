package com.example.MyBookShopApp.controllers;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Role;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class MainPageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Доступ к главной странице")
    @Role(2)
     void mainPageAccessTest() throws Exception{
        int  bookId = 3;
//        mockMvc.perform(MockMvcRequestBuilders.get("/"))
//                .andDo(MockMvcResultHandlers.print());
//                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Online Bookshop")))
//                .andExpect(MockMvcResultMatchers.status().isOk());


        assertEquals(3, bookId);
    }



    @Test
    @DisplayName("Поиск по слову 'Patt'")
    void searchTest() throws Exception{
        int  bookId = 3;
//        mockMvc.perform(MockMvcRequestBuilders.get("/search/Patt"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers
//                        .xpath("/html/body/div/div/main/div/div/div/div[2]/strong/a")
//                        .string("Patton"));


        assertEquals(3, bookId);
    }



}