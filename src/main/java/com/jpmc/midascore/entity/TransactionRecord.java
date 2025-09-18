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

    @JoinColumn(name = "recipient_id", nullable = false)
    private float amount;

    public TransactionRecord (UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public UserRecord getSender() {
        return this.sender;
    }

    public UserRecord getRecipient() {
        return this.recipient;
    }

    public float getAmount() {
        return this.amount;
    }
}
