package com.nhnacademy.jdbc.bank.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    Account account;
    @BeforeEach
    void setUp() {
        account = new Account(1l,"nhn아카데미",10_0000l);
    }

    @Test
    void getAccountNumber() {
        Assertions.assertEquals(account.getAccountNumber(),1l);
    }

    @Test
    void getName() {
        Assertions.assertEquals(account.getName(),"nhn아카데미");
    }

    @Test
    void getBalance() {
        Assertions.assertEquals(account.getBalance(),10_0000l);
    }

    @Test
    void setAccountNumber() {
        account.setAccountNumber(2l);
        Assertions.assertEquals(account.getAccountNumber(),2l);
    }

    @Test
    void isWithdraw() {
        //출금가능 여부 테스트
        Assertions.assertAll(
                ()->Assertions.assertTrue(account.isWithdraw(1_000l)),
                ()->Assertions.assertFalse(account.isWithdraw(20_0000l))
        );
    }

}