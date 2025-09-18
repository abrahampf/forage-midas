package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionService;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransactionKafkaListener {

    public final TransactionService transactionService;

    public final UserRepository userRepository;

    public TransactionKafkaListener(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
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
                UserRecord s = sender.get();
                s.setBalance(s.getBalance() - amount);
                UserRecord r = recipient.get();
                r.setBalance(r.getBalance() + amount);
                userRepository.save(s);
                userRepository.save(r);
                transactionService.saveTransaction(transactionRecord);
            } else {
                // Discard invalid transaction, optionally log warning
                System.out.println("Invalid transaction discarded: Sender did not have enough money" + transaction);
            }
        } catch (Exception e) {
            // Handle failure: rollback transaction so message is retried
            System.out.println("Error processing transaction: {}" + transaction);
            throw e; // rethrow to trigger rollback
        }
    }
}
