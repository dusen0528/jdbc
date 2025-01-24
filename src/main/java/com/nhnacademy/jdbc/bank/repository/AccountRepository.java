package com.nhnacademy.jdbc.bank.repository;

import com.nhnacademy.jdbc.bank.domain.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public interface AccountRepository {
    //계좌조회
    Optional<Account> findByAccountNumber(Connection connection, long accountNumber);
    //계좌등록
    int save(Connection connection, Account account);
    //계좌 count -> 존재하면 1 return
    int countByAccountNumber(Connection connection, long accountNumber);
    //입금
    int deposit(Connection connection, long accountNumber, long amount);
    //출금
    int withdraw(Connection connection, long accountNumber, long amount);

    //계좌삭제
    int deleteByAccountNumber(Connection connection, long accountNumber);

}
