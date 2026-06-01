package com.b2r.ordermgmt.db;
import com.b2r.ordermgmt.core.Payment;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.util.List;
import java.util.Optional;
public class PaymentDAO extends AbstractDAO<Payment> {

    public PaymentDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    public Payment save(Payment payment) {
        return persist(payment);
    }
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(get(id));
    }
    public List<Payment> findByOrderId(Long orderId) {
        return list(namedTypedQuery("com.b2r.ordermgmt.core.Payment.findByOrderId")
                .setParameter("orderId", orderId));
    }

}