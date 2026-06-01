package com.b2r.ordermgmt.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "order_id")
    private Long orderId; // Acts as Primary Key

    @OneToOne
    @MapsId // Syncs the ID with the associated Order's primary key
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "transaction_id")
    private String transactionId;

    public Payment() {}

    public Payment(Order order, double amount, String paymentStatus, String transactionId) {
        this.order = order;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}