package com.nhnacademy.jdbc.bank.exception;

public class BalanceNotEnoughException extends RuntimeException {
    public BalanceNotEnoughException(long account_number) {
        super("balance not enough accountNumber : " + account_number );
    }
}
