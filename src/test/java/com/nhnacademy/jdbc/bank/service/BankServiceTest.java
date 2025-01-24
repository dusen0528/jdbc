package com.nhnacademy.jdbc.bank.service;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.exception.AccountAreadyExistException;
import com.nhnacademy.jdbc.bank.exception.AccountNotFoundException;
import com.nhnacademy.jdbc.bank.exception.BalanceNotEnoughException;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.impl.BankServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class BankServiceTest {
    //todo#18 BankServiceTest를 실행하고, Test Case가 통과할 수 있도록 BankServiceImpl 서비스를 수정합니다.

    @Mock
    AccountRepositoryImpl accountRepository;

    BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankServiceImpl(accountRepository);
    }

    @Test
    @Order(1)
    @DisplayName("계좌 조회")
    void findByAccountNumber() {
        long accountNumber=8000l;

        Mockito.when(accountRepository.findByAccountNumber(any(), anyLong())).thenReturn(Optional.of(new Account(8000l, "nhn아카데미-8000", 10_0000l)));
        Account account = bankService.getAccount(null,accountNumber);

        Assertions.assertAll(
                ()->Assertions.assertEquals(8000l, account.getAccountNumber()),
                ()->Assertions.assertEquals("nhn아카데미-8000", account.getName()),
                ()->Assertions.assertEquals(10_0000l, account.getBalance())
        );
    }

    @Test
    @Order(2)
    @DisplayName("계좌가 존재하지 않을 때")
    void findByAccountNumber_not_found() {
        long accountNumber = 8000l;
        Mockito.when(accountRepository.findByAccountNumber(any(), anyLong())).thenThrow(AccountNotFoundException.class);
        Assertions.assertThrows(RuntimeException.class, ()->{
            bankService.getAccount(null, accountNumber);
        });
    }


    @Test
    @Order(3)
    @DisplayName("계좌 등록")
    void saveAccount() {
        Account account = new Account(Long.MAX_VALUE,"nhn아카데미",Long.MAX_VALUE);
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(0);
        Mockito.when(accountRepository.save(null,account)).thenReturn(1);
        bankService.createAccount(null,account);
        Mockito.verify(accountRepository,Mockito.times(1)).save(any(),any());
    }

    @Test
    @Order(4)
    @DisplayName("계좌 등록 - 중복된 계좌번호")
    void saveAccount_duplicate_accountNumber() {
        Account account = new Account(Long.MAX_VALUE,"nhn아카데미",Long.MAX_VALUE);
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);
        Assertions.assertThrows(AccountAreadyExistException.class,()->bankService.createAccount(null,account));
    }

    @Test
    @Order(5)
    @DisplayName("계좌 입금")
    void deposit() {
        Mockito.when(accountRepository.deposit(any(),anyLong(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);

        boolean result = bankService.depositAccount(null,Long.MAX_VALUE,Long.MAX_VALUE);

        assertTrue(result);
        Mockito.verify(accountRepository,Mockito.times(1)).deposit(any(),anyLong(),anyLong());
    }

    @Test
    @Order(6)
    @DisplayName("계좌 입금 - 계좌가 존재하지 않을 때")
    void deposit_account_not_found() {
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(0);

        assertThrows(AccountNotFoundException.class,()-> bankService.depositAccount(null,Long.MAX_VALUE,Long.MAX_VALUE));
        Mockito.verify(accountRepository,Mockito.times(1)).countByAccountNumber(any(),anyLong());
    }

    @Test
    @Order(7)
    @DisplayName("계좌 출금")
    void withdraw() {
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.findByAccountNumber(any(),anyLong())).thenReturn(Optional.of(new Account(1l,"nhn아카데미",10_0000l)));
        Mockito.when(accountRepository.withdraw(any(),anyLong(),anyLong())).thenReturn(1);

        boolean result = bankService.withdrawAccount(null,Long.MAX_VALUE,1_0000);
        assertEquals(true,result);
    }

    @Test
    @Order(8)
    @DisplayName("계좌 출금 -  계좌가 존재하지 않을 떄")
    void withdraw_account_not_found(){
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(0);

        assertThrows(AccountNotFoundException.class,()->bankService.withdrawAccount(null,Long.MAX_VALUE,Long.MAX_VALUE));
        Mockito.verify(accountRepository,Mockito.times(1)).countByAccountNumber(any(),anyLong());
    }

    @Test
    @Order(9)
    @DisplayName("계좌 출금 - 잔액부족")
    void withdraw_balacne_not_enough(){
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.findByAccountNumber(any(),anyLong())).thenReturn(Optional.of(new Account(1l,"nhn아카데미",10_0000l)));

        Assertions.assertThrows(BalanceNotEnoughException.class,()-> bankService.withdrawAccount(null,1l, 20_0000l));
        Mockito.verify(accountRepository,Mockito.times(1)).findByAccountNumber(any(),anyLong());
    }

    @Test
    @Order(10)
    @DisplayName("계좌이체")
    void accountTransfer() {

        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.findByAccountNumber(any(),anyLong())).thenReturn(Optional.of(new Account(8000l, "nhn아카데미", 10_0000l)));
        Mockito.when(accountRepository.withdraw(any(),anyLong(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.deposit(any(),anyLong(),anyLong())).thenReturn(1);

        bankService.transferAmount(null,8000l,9000l,1_0000l);

        Mockito.verify(accountRepository,Mockito.times(2)).findByAccountNumber(any(),anyLong());
        Mockito.verify(accountRepository,Mockito.times(2)).countByAccountNumber(any(),anyLong());
        Mockito.verify(accountRepository,Mockito.times(1)).withdraw(any(),anyLong(),anyLong());
        Mockito.verify(accountRepository,Mockito.times(1)).deposit(any(),anyLong(),anyLong());
    }

    @Test
    @Order(11)
    @DisplayName("계좌이체 - 계좌가 존재하지 않느다면")
    void accountTransfer_account_not_found(){
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(0);
        Assertions.assertThrows(AccountNotFoundException.class, ()->bankService.transferAmount(null,8000l,9000l,1_0000l));
        Mockito.verify(accountRepository,Mockito.times(1)).countByAccountNumber(any(),anyLong());
    }

    @Test
    @Order(12)
    @DisplayName("계좌이체 - 잔고부족")
    void accountTransfer_balacne_not_enough(){
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.findByAccountNumber(any(),anyLong())).thenReturn(Optional.of(new Account(8000l, "nhn아카데미", 10_0000l)));

        Assertions.assertThrows(BalanceNotEnoughException.class,()->{
            bankService.transferAmount(null,8000l,9000l,20_0000l);
        });

        Mockito.verify(accountRepository,Mockito.times(2)).findByAccountNumber(any(),anyLong());
        Mockito.verify(accountRepository,Mockito.times(2)).countByAccountNumber(any(),anyLong());

    }

    @Test
    @Order(13)
    @DisplayName("Account 삭제")
    void dropAccount(){
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.deleteByAccountNumber(any(),anyLong())).thenReturn(1);

        bankService.dropAccount(null,Long.MAX_VALUE);

        Mockito.verify(accountRepository,Mockito.times(1)).countByAccountNumber(any(),anyLong());
        Mockito.verify(accountRepository,Mockito.times(1)).deleteByAccountNumber(any(),anyLong());

    }


    @Test
    @Order(14)
    @DisplayName("Account 삭제 - account not found")
    void dropAccount_account_not_found(){
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(0);
        Assertions.assertThrows(AccountNotFoundException.class,()->{
            bankService.dropAccount(null,Long.MAX_VALUE);
        });

        Mockito.verify(accountRepository,Mockito.times(1)).countByAccountNumber(any(),anyLong());

    }

    @Test
    @Order(15)
    @DisplayName("Account 삭제 - sql error")
    void dropAccount_query_error(){
        Mockito.when(accountRepository.countByAccountNumber(any(),anyLong())).thenReturn(1);
        Mockito.when(accountRepository.deleteByAccountNumber(any(),anyLong())).thenReturn(0);
        Assertions.assertThrows(RuntimeException.class,()->{
            bankService.dropAccount(null,Long.MAX_VALUE);
        });

        Mockito.verify(accountRepository,Mockito.times(1)).countByAccountNumber(any(),anyLong());
        Mockito.verify(accountRepository,Mockito.times(1)).deleteByAccountNumber(any(),anyLong());

    }

}