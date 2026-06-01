package com.b2r.ordermgmt.db;

import com.b2r.ordermgmt.core.Order;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.util.List;

public class OrderDAO extends AbstractDAO<Order> {

    public OrderDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    public Order findById(Long id) {
        return get(id);
    }
    public Order create(Order order) {
        return persist(order);  // saving in db
    }
    public List<Order> findByAccountId(String accountId) {
        return list(currentSession()
                .createQuery("FROM Order WHERE userId = :accountId", Order.class)
                .setParameter("accountId", accountId));
    }
    public List<Order> findAll() {
        return list(currentSession().createQuery("FROM Order", Order.class));
    }
    public void delete(Order order) {
        currentSession().delete(order);
    }
}