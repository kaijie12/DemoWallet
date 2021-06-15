package com.example.demo;


import com.example.demo.controller.SimpleWalletController;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.Wallet;
import com.example.demo.repository.SimpleWalletRepository;
import com.example.demo.repository.TransactionResponse;
import com.example.demo.repository.TransactionType;
import com.example.demo.service.SimpleWalletService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private SimpleWalletRepository walletRepository;
    @Autowired
    @InjectMocks
    private SimpleWalletService simpleWalletService;
    private Wallet wallet1;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        wallet1=new Wallet(1,"1,2,3");
    }


    @Test
    public void testGetBalance() throws Exception {
        when(walletRepository.findById((long)1)).thenReturn(java.util.Optional.ofNullable(wallet1));
        CompletableFuture<String> ans= simpleWalletService.getBalance();
        System.out.println("ans: " +ans.get());
    }

    @Test
    public void testNotEnoughCoinsException() throws Exception {
        Transaction newTransaction = new Transaction(TransactionType.PAY, 120, simpleWalletService.getLocalDate());
        try{
            TransactionResponse ans= simpleWalletService.createTransaction(newTransaction);
            System.out.println("ans: " +ans.toString());
        }catch(Exception e){
            assertEquals("Amount in Wallet: 105 is not enough to pay: 120",e.getMessage());
        }
    }

    @Test
    public void testWithoutChange() throws Exception {
        Transaction newTransaction = new Transaction(TransactionType.PAY, 2, simpleWalletService.getLocalDate());
        when(walletRepository.findById((long)1)).thenReturn(java.util.Optional.ofNullable(wallet1));
        TransactionResponse ans= simpleWalletService.createTransaction(newTransaction);
        assert(ans.getChange()==null);
        assert(ans.getCoinsPaid().toString().equals("[2]"));
        System.out.println("ans: " +ans.toString());
    }

    @Test
    public void testWithChange() throws Exception {
        Transaction newTransaction = new Transaction(TransactionType.PAY, 7, simpleWalletService.getLocalDate());
        when(walletRepository.findById((long)1)).thenReturn(java.util.Optional.ofNullable(wallet1));
        TransactionResponse ans= simpleWalletService.createTransaction(newTransaction);
        assert(ans.getChange()==98);
        System.out.println("ans: " +ans.toString());
    }

    @Test
    public void testControllerCalls() throws Exception {
        mockMvc.perform(get("/wallet/balance")).andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
        mockMvc.perform(get("/wallet/pay?amount=1")).andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }
}
