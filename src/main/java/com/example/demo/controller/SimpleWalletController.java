package com.example.demo.controller;


import com.example.demo.entity.Transaction;
import com.example.demo.exception.NegativeAmountException;
import com.example.demo.repository.TransactionResponse;
import com.example.demo.repository.TransactionType;
import com.example.demo.service.SimpleWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/wallet")
public class SimpleWalletController {

    @Autowired
    private SimpleWalletService walletService;

    @GetMapping(value = "/pay")
    public String pay(@RequestParam() Integer amount) {


        if (amount <= 0) {
            return "Please enter a positive amount";
        }

        Transaction newTransaction = new Transaction(TransactionType.PAY, amount, walletService.getLocalDate());
        try {
            TransactionResponse r = walletService.createTransaction(newTransaction);
            String res="Successfully paid "+r.getCoinsPaid().size()+"\n" +
                    "My current coins are "+r.getCoinsLeft().toString();
            return res;
        }catch(Exception e){
            return e.getMessage();
        }
    }

    @GetMapping(value = "/balance")
    public String getBalance() throws Exception {
        return "My current coins are "+walletService.getBalance().get();
    }

//    @GetMapping(value = "/add/{amount}")
//    public Transaction deposit(@PathVariable("amount") Integer amount) {
//
//
//        Transaction newTransaction = new Transaction("Deposit", amount, walletService.getLocalDate());
//        walletService.createTransaction(newTransaction);
//
//        return newTransaction;
//    }

}


