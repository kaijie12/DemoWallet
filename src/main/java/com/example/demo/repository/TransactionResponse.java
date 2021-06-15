package com.example.demo.repository;

import java.util.List;

public class TransactionResponse {


    List<Integer> coinsPaid;
    List<Integer> coinsLeft;
    Integer change;

    public TransactionResponse() {
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "coinsPaid=" + coinsPaid +
                ", coinsLeft=" + coinsLeft +
                ", change=" + change +
                '}';
    }

    public TransactionResponse(List<Integer> coinsPaid, List<Integer> coinsLeft, Integer change) {
        this.coinsPaid = coinsPaid;
        this.coinsLeft = coinsLeft;
        this.change = change;
    }

    public List<Integer> getCoinsPaid() {
        return coinsPaid;
    }

    public List<Integer> getCoinsLeft() {
        return coinsLeft;
    }

    public void setCoinsLeft(List<Integer> coinsLeft) {
        this.coinsLeft = coinsLeft;
    }

    public void setCoinsPaid(List<Integer> coinsPaid) {
        this.coinsPaid = coinsPaid;
    }

    public Integer getChange() {
        return change;
    }

    public void setChange(Integer change) {
        this.change = change;
    }

    public static class TransactionResponseBuilder
    {
        List<Integer> coinsPaid;
        List<Integer> coinsLeft;
        Integer change;

        public TransactionResponseBuilder() {
        }

        public TransactionResponseBuilder setCoinsLeft(List<Integer> coinsLeft) {
            this.coinsLeft = coinsLeft;
            return this;
        }

        public TransactionResponseBuilder setCoinsPaid(List<Integer> coinsPaid) {
            this.coinsPaid = coinsPaid;
            return this;
        }

        public TransactionResponseBuilder setChange(Integer change) {
            this.change = change;
            return this;
        }
        public TransactionResponse build() {
            return new TransactionResponse(coinsPaid, coinsLeft,change);
        }
    }

    public static TransactionResponseBuilder newBuilder(){
        return new TransactionResponseBuilder();
    }
}
