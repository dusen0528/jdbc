package com.nhnacademy.jdbc.bank.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(long accountNumber) {
        super("Account Not Found accountNumber : " + accountNumber );
    }
}
