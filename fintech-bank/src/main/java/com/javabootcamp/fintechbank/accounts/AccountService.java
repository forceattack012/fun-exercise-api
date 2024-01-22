package com.javabootcamp.fintechbank.accounts;

import com.javabootcamp.fintechbank.exceptions.BadRequestException;
import com.javabootcamp.fintechbank.exceptions.InternalServerException;
import com.javabootcamp.fintechbank.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponse> getAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(acc -> new AccountResponse(acc.getNo(), acc.getType(), acc.getName(), acc.getBalance()))
                .toList();
    }

    public AccountResponse depositAccount(Integer accountNo, DepositRequest depositRequest) {
        Optional<Account> optionalAccount = accountRepository.findById(accountNo);
        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Account account = optionalAccount.get();
        Double newBalance = account.getBalance() + depositRequest.amount();
        account.setBalance(newBalance);

        try {
            accountRepository.save(account);
        } catch (Exception e) {
            throw new InternalServerException("Failed to deposit");
        }
        return new AccountResponse(account.getNo(), account.getType(), account.getName(), account.getBalance());
    }

    @Transactional
    public AccountResponse withdrawAccount(Integer accountNo, WithdrawRequest withdrawRequest){
        Optional<Account> optionalAccount = accountRepository.findById(accountNo);
        if(optionalAccount.isEmpty()){
            throw new NotFoundException("Account not found");
        }

        Account account = optionalAccount.get();
        if(account.getBalance() < withdrawRequest.amount()){
            throw new BadRequestException("account balance is not enough to withdraw");
        }

        Double newBalance = account.getBalance() - withdrawRequest.amount();
        account.setBalance(newBalance);

        try {
            accountRepository.save(account);
        }
        catch (Exception ex){
            throw new InternalServerException("Failed to withdraw");
        }

        return new AccountResponse(account.getNo(), account.getType(), account.getName(), account.getBalance());
    }
}
