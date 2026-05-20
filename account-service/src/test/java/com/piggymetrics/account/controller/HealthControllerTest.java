package com.piggymetrics.account.controller;

import com.piggymetrics.account.AccountApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = AccountApplication.class)
@AutoConfigureMockMvc
public class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void health() throws Exception {
        mockMvc.perform(get("/api/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"UP\",\"service\":\"account-service\"}"));
    }
}