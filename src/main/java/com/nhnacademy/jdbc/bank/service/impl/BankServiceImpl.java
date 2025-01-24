package com.nhnacademy.jdbc.bank.service.impl;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.exception.AccountAreadyExistException;
import com.nhnacademy.jdbc.bank.exception.AccountNotFoundException;
import com.nhnacademy.jdbc.bank.exception.BalanceNotEnoughException;
import com.nhnacademy.jdbc.bank.repository.AccountRepository;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.BankService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 은행 서비스 구현 클래스
 * 계좌 관리, 입출금, 이체 기능을 제공합니다.
 */
@Slf4j
public class BankServiceImpl implements BankService {

    private final AccountRepository accountRepository;

    /**
     * 계좌 Repository를 주입받는 생성자
     * @param accountRepository 계좌 Repository 인터페이스 구현체
     */
    public BankServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 계좌번호로 계좌 정보를 조회합니다.
     *
     * @param connection DB 연결 객체
     * @param accountNumber 조회할 계좌번호
     * @return 조회된 계좌 정보
     * @throws AccountNotFoundException 계좌가 존재하지 않을 경우
     */
    @Override
    public Account getAccount(Connection connection, long accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(connection, accountNumber);
        return account.orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    /**
     * 신규 계좌를 생성합니다.
     *
     * @param connection DB 연결 객체
     * @param account 생성할 계좌 정보
     * @throws AccountAreadyExistException 이미 존재하는 계좌번호일 경우
     */
    @Override
    public void createAccount(Connection connection, Account account) {
        if(isExistAccount(connection, account.getAccountNumber())) {
            throw new AccountAreadyExistException(account.getAccountNumber());
        }
        accountRepository.save(connection, account);
    }

    /**
     * 계좌에 입금을 수행합니다.
     *
     * @param connection DB 연결 객체
     * @param accountNumber 입금할 계좌번호
     * @param amount 입금액
     * @return 입금 성공 여부
     * @throws AccountNotFoundException 계좌가 존재하지 않을 경우
     */
    @Override
    public boolean depositAccount(Connection connection, long accountNumber, long amount) {
        if(!isExistAccount(connection, accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        if(amount <= 0) {
            return false;
        }
        return accountRepository.deposit(connection, accountNumber, amount) > 0;
    }

    /**
     * 계좌에서 출금을 수행합니다.
     *
     * @param connection DB 연결 객체
     * @param accountNumber 출금할 계좌번호
     * @param amount 출금액
     * @return 출금 성공 여부
     * @throws AccountNotFoundException 계좌가 존재하지 않을 경우
     * @throws BalanceNotEnoughException 잔액이 부족할 경우
     */
    @Override
    public boolean withdrawAccount(Connection connection, long accountNumber, long amount) {
        if(!isExistAccount(connection, accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        if(amount <= 0 || getAccount(connection, accountNumber).getBalance() < amount) {
            throw new BalanceNotEnoughException(accountNumber);
        }
        return accountRepository.withdraw(connection, accountNumber, amount) > 0;
    }

    /**
     * 계좌 이체를 수행합니다.
     *
     * @param connection DB 연결 객체
     * @param accountNumberFrom 출금 계좌번호
     * @param accountNumberTo 입금 계좌번호
     * @param amount 이체 금액
     * @throws AccountNotFoundException 계좌가 존재하지 않을 경우
     * @throws BalanceNotEnoughException 잔액이 부족할 경우
     * @throws RuntimeException 이체 실패 시
     */
    @Override
    public void transferAmount(Connection connection, long accountNumberFrom, long accountNumberTo, long amount) {
        if(!isExistAccount(connection, accountNumberFrom)) {
            throw new AccountNotFoundException(accountNumberFrom);
        }
        if(!isExistAccount(connection, accountNumberTo)) {
            throw new AccountNotFoundException(accountNumberTo);
        }

        Optional<Account> accountFromOptional = accountRepository.findByAccountNumber(connection, accountNumberFrom);
        if(accountFromOptional.isEmpty()) {
            throw new AccountNotFoundException(accountNumberFrom);
        }

        Optional<Account> accountToOptional = accountRepository.findByAccountNumber(connection, accountNumberTo);
        if(accountToOptional.isEmpty()) {
            throw new AccountNotFoundException(accountNumberTo);
        }

        Account accountFrom = accountFromOptional.get();
        if(!accountFrom.isWithdraw(amount)) {
            throw new BalanceNotEnoughException(accountNumberFrom);
        }

        int result1 = accountRepository.withdraw(connection, accountNumberFrom, amount);
        if(result1 < 1) {
            throw new RuntimeException("fail - withdraw :" + accountNumberFrom);
        }

        int result2 = accountRepository.deposit(connection, accountNumberTo, amount);
        if(result2 < 1) {
            throw new RuntimeException("fail - deposit : " + accountNumberTo);
        }
    }

    /**
     * 계좌 존재 여부를 확인합니다.
     *
     * @param connection DB 연결 객체
     * @param accountNumber 확인할 계좌번호
     * @return 계좌 존재 여부
     */
    @Override
    public boolean isExistAccount(Connection connection, long accountNumber) {
        return accountRepository.countByAccountNumber(connection, accountNumber) > 0;
    }

    /**
     * 계좌를 삭제합니다.
     *
     * @param connection DB 연결 객체
     * @param accountNumber 삭제할 계좌번호
     * @throws AccountNotFoundException 계좌가 존재하지 않을 경우
     * @throws RuntimeException 삭제 실패 시
     */
    @Override
    public void dropAccount(Connection connection, long accountNumber) {
        if(!isExistAccount(connection, accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        if(accountRepository.deleteByAccountNumber(connection, accountNumber) == 0) {
            throw new RuntimeException("Failed to delete account");
        }
    }
}