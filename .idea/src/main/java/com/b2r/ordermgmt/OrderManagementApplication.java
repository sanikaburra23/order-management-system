package com.b2r.ordermgmt;

import com.b2r.ordermgmt.core.Item;
import com.b2r.ordermgmt.core.Order;
import com.b2r.ordermgmt.core.OrderItem;
import com.b2r.ordermgmt.db.ItemDAO;
import com.b2r.ordermgmt.db.OrderDAO;
import com.b2r.ordermgmt.resources.ItemResource;
import com.b2r.ordermgmt.resources.OrderResource;
import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class OrderManagementApplication extends Application<OrderManagementConfiguration> {
    private final HibernateBundle<OrderManagementConfiguration> hibernateBundle =
            new HibernateBundle<OrderManagementConfiguration>(Item.class, Order.class, OrderItem.class) {
                @Override
                public PooledDataSourceFactory getDataSourceFactory(OrderManagementConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    public static void main(String[] args) throws Exception {
        new OrderManagementApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<OrderManagementConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(OrderManagementConfiguration configuration, Environment environment) {
        final OrderDAO orderDAO = new OrderDAO(hibernateBundle.getSessionFactory());
        final ItemDAO itemDAO = new ItemDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new OrderResource(orderDAO, itemDAO));

        environment.jersey().register(new ItemResource(itemDAO));
    }
}