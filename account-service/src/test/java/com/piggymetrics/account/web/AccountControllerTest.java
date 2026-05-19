package com.piggymetrics.account.web;

import com.piggymetrics.account.domain.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnBadRequest_whenAmountIsNegative() throws Exception {
        Item item = new Item();
        item.setTitle("Test item");
        item.setAmount(new BigDecimal(-10));
        // Set other necessary fields here

        mockMvc.perform(post("/account/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("must be greater than or equal to 0"));
    }

    @Test
    public void shouldReturnBadRequest_whenAmountIsZero() throws Exception {
        Item item = new Item();
        item.setTitle("Test item");
        item.setAmount(new BigDecimal(0));
        // Set other necessary fields here

        mockMvc.perform(post("/account/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("must be greater than or equal to 0"));
    }

    @Test
    public void shouldCreateItem_whenAmountIsPositive() throws Exception {
        Item item = new Item();
        item.setTitle("Test item");
        item.setAmount(new BigDecimal(10));
        // Set other necessary fields here

        mockMvc.perform(post("/account/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(item)))
                .andExpect(status().isCreated());
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}