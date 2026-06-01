package com.b2r.ordermgmt.db;

import com.b2r.ordermgmt.core.Item;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.util.List;
import java.util.Optional;

public class ItemDAO extends AbstractDAO<Item> {

    public ItemDAO(SessionFactory factory) {
        super(factory);
    }
    public List<Item> getAllItems() {
        return list(currentSession().createQuery("FROM Item", Item.class));
    }
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(get(id));
    }
    public Item save(Item item) {
        return persist(item);
    }
    public void delete(Item item) {
        currentSession().delete(item);
    }
}