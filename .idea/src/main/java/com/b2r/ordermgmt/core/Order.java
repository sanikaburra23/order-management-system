package com.b2r.ordermgmt.core;

import com.fasterxml.jackson.annotation.JsonManagedReference; // ◄ 1. ADD THIS IMPORT
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "status", nullable = false)
    private String status;

    @JsonManagedReference // ◄ 2. ADD THIS TO PAIR WITH @JsonBackReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {}

    public Order(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public double getTotalOrderPrice() {
        return this.orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getQuantity() * orderItem.getItem().getPrice())
                .sum();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                item.setOrder(this); // This prevents the null constraint violation!
            }
        }
    }


}