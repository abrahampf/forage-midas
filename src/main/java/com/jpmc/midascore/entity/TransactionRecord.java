package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    // Many transaction records can be linked to one recipient user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @JoinColumn(nullable = false)
    private float amount;

    @JoinColumn
    private float incentive;

    public TransactionRecord (UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = 0;
    }

    public UserRecord getSender() {
        return this.sender;
    }

    public UserRecord getRecipient() {
        return this.recipient;
    }

    public void setIncentive(float incentive) {
        this.incentive = incentive;
    }

    public float getAmount() {
        return this.amount;
    }
}
