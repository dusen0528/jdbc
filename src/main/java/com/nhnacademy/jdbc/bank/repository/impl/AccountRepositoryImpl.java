package com.nhnacademy.jdbc.bank.repository.impl;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class AccountRepositoryImpl implements AccountRepository {

    public Optional<Account> findByAccountNumber(Connection connection, long accountNumber){
        //todo#1 계좌-조회
        String sql = "SELECT * FROM jdbc_account WHERE account_Number = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setLong(1, accountNumber);;
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    Account account = new Account(
                            rs.getLong("account_number"),
                            rs.getString("name"),
                            rs.getLong("balance")
                    );
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public int save(Connection connection, Account account) {
        //todo#2 계좌-등록, executeUpdate() 결과를 반환 합니다.
        String sql = "INSERT INTO jdbc_account (account_number, name, balance) VALUES (?,?,?)";
        try(
                PreparedStatement statement = connection.prepareStatement(sql);
                ) {
            statement.setLong(1, account.getAccountNumber());
            statement.setString(2, account.getName());
            statement.setLong(3, account.getBalance());

            int result = statement.executeUpdate();
            log.debug("save:{}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countByAccountNumber(Connection connection, long accountNumber){
        int count=0;
        //todo#3 select count(*)를 이용해서 계좌의 개수를 count해서 반환
        String sql = "SELECT count(*) from jdbc_account WHERE account_Number=?";
        try(
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ){
            preparedStatement.setLong(1, accountNumber);
            try(ResultSet rs = preparedStatement.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int deposit(Connection connection, long accountNumber, long amount){
        //todo#4 입금, executeUpdate() 결과를 반환 합니다.
        String sql = "UPDATE jdbc_account SET balance = balance + ? WHERE account_Number=?";
        try(
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setLong(1, amount);
            statement.setLong(2, accountNumber);

            int result = statement.executeUpdate();
            log.debug("deposit : {}", result);
            return  result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int withdraw(Connection connection, long accountNumber, long amount){
        //todo#5 출금, executeUpdate() 결과를 반환 합니다.
        String sql = "UPDATE jdbc_account SET balance = balance-? WHERE account_Number=?";

        try(
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setLong(1, amount);
            statement.setLong(2, accountNumber);

            int result = statement.executeUpdate();
            log.debug("withdraw:{}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteByAccountNumber(Connection connection, long accountNumber) {
        //todo#6 계좌 삭제, executeUpdate() 결과를 반환 합니다.
        String sql = "DELETE FROM jdbc_account WHERE account_Number=?";
        try(
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ){
            preparedStatement.setLong(1, accountNumber);

            int result = preparedStatement.executeUpdate();
            log.debug("delete:{}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
