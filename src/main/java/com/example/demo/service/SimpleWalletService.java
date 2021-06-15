package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.Wallet;
import com.example.demo.exception.NegativeAmountException;
import com.example.demo.exception.NotEnoughCoinsException;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.SimpleWalletRepository;
import com.example.demo.repository.TransactionResponse;
import com.example.demo.repository.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;

@Service
public class SimpleWalletService {

    @Autowired
    private SimpleWalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionResponse createTransaction(Transaction transaction) {

        //current assumption that there is only 1 user
        Wallet wallet = walletRepository.findById((long) 1).get();
        TransactionResponse.TransactionResponseBuilder response= TransactionResponse.newBuilder();
        List<Integer> coinsPaid=new ArrayList<>();
        HashMap <Integer , Integer> walletMap = new HashMap<>();
        if(TransactionType.PAY.equals(transaction.getType())){
            //get coins
            List<String> walletStringList = Arrays.asList(wallet.getAccount_balance().replace(" ","").split(","));
            List<Integer> walletList=walletStringList.stream().map(Integer::parseInt).collect(Collectors.toList());
            System.out.println("walletList: "+ Arrays.asList(walletList).toString() );
            Integer sumOfWallet = walletList.stream().reduce(0, Integer::sum);
            int amountToPay=transaction.getAmount();
            //large to small
            List<Integer> walletListSorted=walletList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            //insert into hashmap
            walletListSorted.forEach(a->{
                ofNullable(walletMap.get(a)).ifPresentOrElse(key -> walletMap.put(key, walletMap.get(key) + 1), () -> {
                    walletMap.put(a, 1);
                });
            });
            System.out.println("walletMap: "+ walletMap.toString() );
            if(sumOfWallet<amountToPay)
                throw new NotEnoughCoinsException("Amount in Wallet: "+sumOfWallet+" is not enough to pay: "+amountToPay);

            //loop coins to find the exact if exist, else find if amount is bigger than coin, if amount is bigger then minus the coin & minus amount, if amount is smaller
            // then skip, check if amount is 0, once looped finish, if amount is not 0, then minus from largerValue set
            ofNullable(walletMap.get(amountToPay)).filter(a-> a>0).ifPresentOrElse(coin->{
                //remove from list
                System.out.println("exact coin found: "+ amountToPay );
                walletMap.put(amountToPay, walletMap.get(amountToPay) - 1);
                coinsPaid.add(amountToPay);
            },()->{
                List<Integer> smallerValuesList=walletListSorted.stream().filter(v->amountToPay>v).collect(Collectors.toList());
                List<Integer> largerValuesList=walletListSorted.stream().filter(v->amountToPay<v).collect(Collectors.toList());
                int tempAmount=amountToPay;
                for(int coin:smallerValuesList){
                    if(tempAmount>=coin){
                        walletMap.put(coin, walletMap.get(coin) - 1);
                        coinsPaid.add(coin);
                        tempAmount-=coin;
                    }
                    if(tempAmount==0) break;
                }
                //if still have, means there would be change
                if(tempAmount>0){
                    int smallestLargeElement=largerValuesList.get(largerValuesList.size() - 1);
                    int change=smallestLargeElement-tempAmount;
                    walletMap.put(smallestLargeElement, walletMap.get(smallestLargeElement) - 1);
                    //add change into coins list
                    ofNullable(walletMap.get(change)).ifPresentOrElse(c->walletMap.put(c, walletMap.get(c) - 1),()->walletMap.put(change, 1));
                    coinsPaid.add(smallestLargeElement);
                    response.setChange(change);
                }
            });
            System.out.println("walletMap after payment: "+ walletMap.toString() );
            //put walletmap into list for response
            List<Integer> coinsLeft=new ArrayList<>();
            walletMap.entrySet().forEach(coins->{
                System.out.println("inside walletMap loop key: "+ coins.getKey() );
                if(coins.getValue()>0)
                    IntStream.range(0,coins.getValue()).forEach(i -> coinsLeft.add(coins.getKey()));
            });
            wallet.setAccount_balance(coinsLeft.toString().replace("[", "").replace("]", ""));
            walletRepository.save(wallet);
            transactionRepository.save(transaction);
            return response.setCoinsLeft(coinsLeft).setCoinsPaid(coinsPaid).build();
        }else if(TransactionType.ADD.equals(transaction.getType())){

        }

        return new TransactionResponse();

    }

    public CompletableFuture<String> getBalance() {
        return CompletableFuture.completedFuture(walletRepository.findById((long) 1).get().getAccount_balance());
    }

    public CompletableFuture<List<Transaction>> get_last_N_Transactions(int N) throws InterruptedException {
        List<Transaction> allTransactions = new ArrayList<>();
        transactionRepository.findAll().forEach(allTransactions::add);

        List<Transaction> last_N_transactions = new ArrayList<>();

        for (int i = allTransactions.size() - 1; i >= 0; i--) {
            if (N <= 0) {
                break;
            }

            last_N_transactions.add(allTransactions.get(i));
            N--;
        }

        return CompletableFuture.completedFuture(last_N_transactions);
    }

    public String getLocalDate() {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return localDateFormat.format(new Date());
    }
}
