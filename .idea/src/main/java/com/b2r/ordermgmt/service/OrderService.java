package com.b2r.ordermgmt.service;

import com.b2r.ordermgmt.core.Order;
import com.b2r.ordermgmt.core.OrderItem;
import com.b2r.ordermgmt.core.Item;
import com.b2r.ordermgmt.core.Payment;
import com.b2r.ordermgmt.db.OrderDAO;
import com.b2r.ordermgmt.db.ItemDAO;
import com.b2r.ordermgmt.db.PaymentDAO;
import com.b2r.ordermgmt.exceptions.ResourceNotFoundException;
import com.b2r.ordermgmt.exceptions.InvalidOrderStateException;
import com.b2r.ordermgmt.exceptions.InsufficientStockException;

import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;
    private final ItemDAO itemDAO;
    private final PaymentDAO paymentDAO;

    public OrderService(OrderDAO orderDAO, ItemDAO itemDAO, PaymentDAO paymentDAO) {
        this.orderDAO = orderDAO;
        this.itemDAO = itemDAO;
        this.paymentDAO = paymentDAO;
    }
    public Order createOrder(Order order) {
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }

        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.setOrder(order);
                Long itemId = orderItem.getItem().getId();
                Item realItem = itemDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + itemId + " not found."));
                if (realItem.getStockQuantity() < orderItem.getQuantity()) {
                    throw new InsufficientStockException("Insufficient stock for item: " + realItem.getName()
                            + ". Available: " + realItem.getStockQuantity() + ", Requested: " + orderItem.getQuantity());
                }
                realItem.setStockQuantity(realItem.getStockQuantity() - orderItem.getQuantity());
                itemDAO.save(realItem); // Update the catalog item state
                orderItem.setItem(realItem);
            }
        }

        return orderDAO.create(order);
    }
    public Order getOrderById(Long id) {
        Order order = orderDAO.findById(id);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID " + id + " does not exist.");
        }
        return order;
    }
    public List<Order> getOrdersByAccountId(String accountId) {
        List<Order> orders = orderDAO.findByAccountId(accountId);
        if (orders == null || orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found associated with account ID: " + accountId);
        }
        return orders;
    }
    public Order updateOrder(Long id, String newStatus) {
        Order existingOrder = getOrderById(id); // Reuses the validation logic above
        String currentStatus = existingOrder.getStatus();
        if ("DELIVERED".equalsIgnoreCase(currentStatus) || "CANCELLED".equalsIgnoreCase(currentStatus)) {
            throw new InvalidOrderStateException("Cannot update an order that is currently marked as " + currentStatus);
        }

        existingOrder.setStatus(newStatus);
        return orderDAO.create(existingOrder); // Persist handles updates on tracked instances
    }
    public void deleteOrder(Long id) {
        Order existingOrder = getOrderById(id);
        if (!"PENDING".equalsIgnoreCase(existingOrder.getStatus())) {
            throw new InvalidOrderStateException("Only PENDING orders can be deleted. Current order state is: "
                    + existingOrder.getStatus());
        }

        orderDAO.delete(existingOrder);
    }
}