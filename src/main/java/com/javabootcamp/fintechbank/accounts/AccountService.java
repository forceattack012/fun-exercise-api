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
    public AccountResponse transfer(Integer accountNo, Integer targetAccountNo, TransferRequest transferRequest) {
        Optional<Account> optionalAccount = accountRepository.findById(accountNo);
        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Optional<Account> optionalTargetAccount = accountRepository.findById(targetAccountNo);
        if (optionalTargetAccount.isEmpty()) {
            throw new NotFoundException("Target account not found");
        }

        Account account = optionalAccount.get();
        if (account.getBalance() < transferRequest.amount()) {
            throw new BadRequestException("account balance is not enough to transfer");
        }

        Account target = optionalTargetAccount.get();
        try {
            double newAccountBalance = account.getBalance() - transferRequest.amount();
            account.setBalance(newAccountBalance);

            double newTargetBalance = target.getBalance() + transferRequest.amount();
            target.setBalance(newTargetBalance);

            accountRepository.save(account);
            accountRepository.save(target);
        } catch (Exception ex) {
            throw new InternalServerException("Failed to transfer");
        }
        return new AccountResponse(account.getNo(), account.getType(), account.getName(), account.getBalance());
    }

    public AccountResponse withdrawAccount(Integer accountNo, WithdrawRequest withdrawRequest) {
        Optional<Account> optionalAccount = accountRepository.findById(accountNo);
        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Account account = optionalAccount.get();
        if (account.getBalance() < withdrawRequest.amount()) {
            throw new BadRequestException("account balance is not enough to withdraw");
        }

        Double newBalance = account.getBalance() - withdrawRequest.amount();
        account.setBalance(newBalance);

        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            throw new InternalServerException("Failed to withdraw");
        }

        return new AccountResponse(
                account.getNo(),
                account.getType(),
                account.getName(),
                account.getBalance()
        );
    }
    public AccountResponse createAccount(AccountRequest accountRequest){
        Account account = new Account();
        account.setName(accountRequest.name());
        account.setBalance(accountRequest.balance());
        account.setType(accountRequest.type());

        account = accountRepository.save(account);

        return new AccountResponse(
                account.getNo(),
                account.getType(),
                account.getName(),
                account.getBalance()
        );
    }

    public AccountResponse getAccountByAccountNo(Integer accountNo){
        return accountRepository.findById(accountNo)
                .map(account -> new AccountResponse(account.getNo(), account.getType(), account.getName(), account.getBalance()))
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }
}
