package com.b2r.ordermgmt.resources;

import com.b2r.ordermgmt.core.Order;
import com.b2r.ordermgmt.core.Payment;
import com.b2r.ordermgmt.db.OrderDAO;
import com.b2r.ordermgmt.db.PaymentDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private final PaymentDAO paymentDAO;
    private final OrderDAO orderDAO;

    public PaymentResource(PaymentDAO paymentDAO, OrderDAO orderDAO) {
        this.paymentDAO = paymentDAO;
        this.orderDAO = orderDAO;
    }

    @POST
    @UnitOfWork
    public Response processPayment(Payment incomingPayment) {
        Order order = orderDAO.findById(incomingPayment.getOrderId());
        if (order == null) {
            throw new WebApplicationException("Order not found!", Response.Status.NOT_FOUND);
        }

        incomingPayment.setPaymentStatus("SUCCESS");

        order.setStatus("PAID");
        orderDAO.create(order);

        Payment savedPayment = paymentDAO.save(incomingPayment);

        return Response.ok(savedPayment).build();
    }
}