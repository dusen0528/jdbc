package com.nhnacademy.jdbc.bank.domain;


import java.util.Objects;
public class Account {

    //계좌번호, 편의를 위해서 1,2,3,4.... 형식으로 사용합니다.
    private long accountNumber;
    //이름
    private String name;
    //잔고
    private long balance;

    public Account(long accountNumber, String name, long balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public long getBalance() {
        return balance;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isWithdraw(long amount){
        return balance-amount >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber == account.accountNumber && balance == account.balance && Objects.equals(name, account.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, name, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
