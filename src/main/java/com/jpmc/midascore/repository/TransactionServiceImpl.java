package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public TransactionRecord saveTransaction(TransactionRecord transaction) {
        return transactionRepository.save(transaction);
    }

    public Boolean isValidTransaction(TransactionRecord transaction) {
        UserRecord sender = transaction.getSender();
        float sender_balance = sender.getBalance();
        float amount = transaction.getAmount();
        // sender doesn't have enough money
        if ( (sender_balance - amount) < 0.0) {
            return false;
        }
        // sender has at least amount of money in their account
        return true;
    }
}
