
package com.b2r.ordermgmt.resources;

import com.b2r.ordermgmt.core.Item;
import com.b2r.ordermgmt.core.Order;
import com.b2r.ordermgmt.core.OrderItem;
import com.b2r.ordermgmt.db.ItemDAO;
import com.b2r.ordermgmt.db.OrderDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderDAO orderDAO;
    private final ItemDAO itemDAO;

    public OrderResource(OrderDAO orderDAO, ItemDAO itemDAO) {
        this.orderDAO = orderDAO;
        this.itemDAO = itemDAO;
    }
    @POST
    @UnitOfWork
    public Order createOrder(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new WebApplicationException("An order must contain at least one item.", 400);
        }
        for (OrderItem orderItem : order.getOrderItems()) {
            Item item = itemDAO.findById(orderItem.getItem().getId()).orElse(null);

            if (item == null) {
                throw new WebApplicationException("Item not found with ID: " + orderItem.getItem().getId(), 404);
            }
            if (item.getStockQuantity() < orderItem.getQuantity()) {
                throw new WebApplicationException(
                        "Insufficient stock for " + item.getName() +
                                ". Available: " + item.getStockQuantity() +
                                ", Requested: " + orderItem.getQuantity(), 400);
            }
            int updatedStock = item.getStockQuantity() - orderItem.getQuantity();
            item.setStockQuantity(updatedStock);
            orderItem.setOrder(order);
        }
        return orderDAO.create(order);
    }
    @GET
    @Path("/{id}")
    @UnitOfWork
    public Order getOrderById(@PathParam("id") Long id) {
        Order order = orderDAO.findById(id);
        if (order == null) {
            throw new WebApplicationException("Order with ID " + id + " does not exist.", 404);
        }
        return order;
    }
    @GET
    @Path("/account/{accountId}")
    @UnitOfWork
    public List<Order> getOrdersByAccountId(@PathParam("accountId") String accountId) {
        List<Order> orders = orderDAO.findByAccountId(accountId);
        if (orders == null || orders.isEmpty()) {
            throw new WebApplicationException("No orders found associated with account ID: " + accountId, 404);
        }
        return orders;
    }
    @PUT
    @Path("/{id}/status")
    @UnitOfWork
    public Order updateOrder(@PathParam("id") Long id, @QueryParam("status") String newStatus) {
        Order existingOrder = orderDAO.findById(id);
        if (existingOrder == null) {
            throw new WebApplicationException("Order with ID " + id + " does not exist.", 404);
        }

        String currentStatus = existingOrder.getStatus();
        if ("DELIVERED".equalsIgnoreCase(currentStatus) || "CANCELLED".equalsIgnoreCase(currentStatus)) {
            throw new WebApplicationException("Cannot update an order that is currently marked as " + currentStatus, 400);
        }

        existingOrder.setStatus(newStatus);
        return orderDAO.create(existingOrder);
    }
    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response deleteOrder(@PathParam("id") Long id) {
        Order existingOrder = orderDAO.findById(id);
        if (existingOrder == null) {
            throw new WebApplicationException("Order with ID " + id + " does not exist.", 404);
        }

        if (!"PENDING".equalsIgnoreCase(existingOrder.getStatus())) {
            throw new WebApplicationException("Only PENDING orders can be deleted. Current order state is: "
                    + existingOrder.getStatus(), 400);
        }

        orderDAO.delete(existingOrder);
        return Response.noContent().build(); 
    }
}