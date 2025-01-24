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

@Slf4j

public class BankServiceImpl implements BankService {

    private final AccountRepository accountRepository;

    public BankServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /*
    getAccount: 계좌번호로 계좌 정보 조회, 없으면 AccountNotFoundException 발생
     */
    @Override
    public Account getAccount(Connection connection, long accountNumber){
        Optional<Account> account = accountRepository.findByAccountNumber(connection, accountNumber);

        return account.orElseThrow(()-> new AccountNotFoundException(accountNumber));
    }

    /*
    createAccount: 신규 계좌 생성, 이미 존재하면 AccountAreadyExistException 발생
     */
    @Override
    public void createAccount(Connection connection, Account account){
        //todo#12 계좌-등록
       if(isExistAccount(connection, account.getAccountNumber())){
           throw new AccountAreadyExistException(account.getAccountNumber());
       }
       accountRepository.save(connection,account);
    }

    /*

     depositAccount: 입금 처리
     - isExistAccount: 계좌 존재 여부 확인
     - accountRepository.deposit: 실제 입금 수행
     */

    @Override
    public boolean depositAccount(Connection connection, long accountNumber, long amount){
        //todo#13 예금, 계좌가 존재하는지 체크 -> 예금실행 -> 성공 true, 실패 false;
        if(!isExistAccount(connection, accountNumber)){
            throw new AccountNotFoundException(accountNumber);
        }

        if(amount<=0){
            return  false;
        }

        return accountRepository.deposit(connection, accountNumber, amount) > 0;
    }


    /*
    withdrawAccount: 출금 처리
    - isExistAccount: 계좌 존재 여부 확인
    - getAccount().getBalance(): 현재 잔액 확인
    - accountRepository.withdraw: 실제 출금 수행
     */
    @Override
    public boolean withdrawAccount(Connection connection, long accountNumber, long amount){
        //todo#14 출금, 계좌가 존재하는지 체크 ->  출금가능여부 체크 -> 출금실행, 성공 true, 실폐 false 반환
        if(!isExistAccount(connection, accountNumber)){
            throw new AccountNotFoundException(accountNumber);
        }

        if(amount <= 0 || getAccount(connection, accountNumber).getBalance() < amount){
            throw new BalanceNotEnoughException(accountNumber);
        }

        return accountRepository.withdraw(connection, accountNumber, amount) > 0;
    }

    /**
     * 계좌 이체를 수행하는 메서드
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
    public void transferAmount(Connection connection, long accountNumberFrom, long accountNumberTo, long amount){
        //todo 계좌 이체 accountNumberFrom -> accountNumberTo 으로 amount만큼 이체

        //계좌체크
        if(!isExistAccount(connection,accountNumberFrom)){
            throw new AccountNotFoundException(accountNumberFrom);
        }
        if(!isExistAccount(connection,accountNumberTo)){
            throw new AccountNotFoundException(accountNumberTo);
        }

        Optional<Account> accountFromOptional = accountRepository.findByAccountNumber(connection,accountNumberFrom);
        if(accountFromOptional.isEmpty()){
            throw new AccountNotFoundException(accountNumberFrom);
        }

        Optional<Account> accountToOptional = accountRepository.findByAccountNumber(connection,accountNumberTo);
        if(accountToOptional.isEmpty()){
            throw new AccountNotFoundException(accountNumberTo);
        }

        Account accountFrom = accountFromOptional.get();

        if(!accountFrom.isWithdraw(amount)){
            throw new BalanceNotEnoughException(accountNumberFrom);
        }

        int result1 = accountRepository.withdraw(connection,accountNumberFrom,amount);

        if(result1<1){
            throw new RuntimeException("fail - withdraw :" + accountNumberFrom );
        }

        int result2 = accountRepository.deposit(connection,accountNumberTo,amount);

        if(result2 <1){
            throw new RuntimeException("fail - deposit : " + accountNumberTo);
        }

    }
    /*
    isExistAccount: 계좌 존재 여부 확인
    - accountRepository.countByAccountNumber: 계좌 수 조회
     */
    @Override
    public boolean isExistAccount(Connection connection, long accountNumber){
        //todo#16 Account가 존재하면 true , 존재하지 않다면 false
        return accountRepository.countByAccountNumber(connection, accountNumber)>0;

    }

    /*
    dropAccount: 계좌 삭제
    - isExistAccount: 삭제 전 존재 여부 확인
    - accountRepository.deleteByAccountNumber: 실제 삭제 수행
     */
    @Override
    public void dropAccount(Connection connection, long accountNumber) {
        //todo#17 account 삭제
        if(!isExistAccount(connection, accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        if(accountRepository.deleteByAccountNumber(connection, accountNumber) == 0) {
            throw new RuntimeException("Failed to delete account");
        }
    }

}