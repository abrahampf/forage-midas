package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.IncentiveService;
import com.jpmc.midascore.repository.TransactionService;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransactionKafkaListener {

    @Autowired
    public final TransactionService transactionService;

    @Autowired
    public final UserRepository userRepository;

    @Autowired
    public final IncentiveService incentiveService;

    public TransactionKafkaListener(TransactionService transactionService, UserRepository userRepository, IncentiveService incentiveService) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.incentiveService = incentiveService;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        Optional<UserRecord> sender = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipient = userRepository.findById(transaction.getRecipientId());
        float amount = transaction.getAmount();
        // makes sure both are found in table, ignores if either not found
        if (sender.isEmpty() || recipient.isEmpty() || amount < 0.0) {
            return;
        }

        TransactionRecord transactionRecord = new TransactionRecord(sender.get(), recipient.get(), amount);
        try {
            if (transactionService.isValidTransaction(transactionRecord)) {
                float incentive_amt = incentiveService.fetchIncentive(transaction).getAmount();
                transactionRecord.setIncentive(incentive_amt);
                UserRecord s = sender.get();
                s.setBalance(s.getBalance() - amount);
                UserRecord r = recipient.get();
                r.setBalance(r.getBalance() + amount + incentive_amt);
                userRepository.save(s);
                userRepository.save(r);
                transactionService.saveTransaction(transactionRecord);
            } else {
                // Discard invalid transaction, optionally log warning
                System.out.println("Invalid transaction discarded: Sender did not have enough money" + transaction);
            }
        } catch (Exception e) {
            // Handle failure: rollback transaction so message is retried
            System.out.println("Error processing transaction: {}" + transaction + e);
            throw e; // rethrow to trigger rollback
        }
    }
}
