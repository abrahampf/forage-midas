package com.jpmc.midascore.repository;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

    @Autowired
    private RestTemplate restTemplate;

    public Incentive fetchIncentive(Transaction transaction) {
        return restTemplate.postForObject("${incentive-api-url}", transaction, Incentive.class);
    }
}