package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BalanceController {

    @Autowired
    public final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private long getUserBalance(long userId) {
        Optional<UserRecord> user = userRepository.findById(userId);
        return user.map(userRecord -> (long) userRecord.getBalance()).orElse(0L);
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam long userId) {
        long amount = getUserBalance(userId);
        return new Balance(amount);
    }
}