package com.moranta.accountmanagement.service;

import com.moranta.accountmanagement.exception.InvalidInputException;
import com.moranta.accountmanagement.model.Account;
import com.moranta.accountmanagement.model.Client;
import com.moranta.accountmanagement.repository.AccountRepository;
import com.moranta.accountmanagement.repository.ClientRepository;
import com.moranta.accountmanagement.util.AccountRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final TransactionService transactionService;


    public void createNewAccount(AccountRequest accountRequest) {
        String customerId = accountRequest.getCustomerId();
        BigDecimal initialCredit = accountRequest.getInitialCredit();
        LocalDateTime date = LocalDateTime.now();
        Optional<Client> client = clientRepository.findByCustomerId(customerId);

        if(client.isEmpty()) {
            throw new InvalidInputException("Incorrect customerID");
        }

        Account createdAccount = saveNewAccount(initialCredit,client.get(),date);

        if(initialCredit.compareTo(BigDecimal.ZERO) > 0) {
            transactionService.createNewTransaction(initialCredit, date, createdAccount);
        }
    }

    public Account saveNewAccount(BigDecimal amount, Client owner, LocalDateTime creationDate) {
        Account createdAccount = new Account(amount, owner, creationDate);
        accountRepository.save(createdAccount);
        return createdAccount;
    }
}
