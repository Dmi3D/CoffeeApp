package com.example.consumer;

import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import java.util.List;

public interface OrderDao {
    @SqlUpdate("""
      INSERT INTO orders(
        id, customer_name, coffee_type, milk_type, num_shots, syrups,
        status, created_at, updated_at
      ) VALUES (
        :id, :customerName, :coffeeType, :milkType,
        :numShots, :syrups, 'PENDING', :createdAt, :createdAt
      )
    """)
    void insert(@BindBean Order order);

    @SqlQuery("""
      SELECT * FROM orders
      WHERE status = 'PENDING'
      ORDER BY created_at DESC
    """)
    @RegisterBeanMapper(Order.class)
    List<Order> listPending();
}
