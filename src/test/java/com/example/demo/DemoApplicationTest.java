package com.example.demo;


import com.example.demo.controller.SimpleWalletController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(SimpleWalletController.class)
@AutoConfigureMockMvc
public class DemoApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHelloWithName() throws Exception {
        mockMvc.perform(get("/wallet/balance"))
                .andExpect(content().string("Hello, Dan"));
    }
}
