package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import java.util.List;

public interface TransactionService {
    TransactionRecord saveTransaction(TransactionRecord transaction);
    public Boolean isValidTransaction(TransactionRecord transaction);


}
