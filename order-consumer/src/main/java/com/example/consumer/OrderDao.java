package com.example.consumer;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderDao {
    @SqlUpdate("""
      INSERT INTO orders(
        id, customer_name, coffee_type, milk_type, num_shots, syrups,
        status, created_at, updated_at
      ) VALUES (
        :id, :customerName, :coffeeType, :milkType,
        :numShots, :syrups, 'pending', :createdAt, :createdAt
      )
    """)
    void insert(@BindBean Order order);

    @SqlQuery("""
      SELECT * FROM orders
      WHERE status = 'pending'
      ORDER BY created_at DESC
    """)
    @RegisterBeanMapper(Order.class)
    List<Order> listPending();

    @SqlUpdate("""
      UPDATE orders
      SET status       = :status,
          completed_at = :completedAt,
          cancelled_at = :cancelledAt,
          updated_at   = :now
      WHERE id = :id
    """)
    void updateStatus(
            @Bind("id")           UUID id,
            @Bind("status")       String  status,
            @Bind("completedAt")  Instant completedAt,
            @Bind("cancelledAt")  Instant cancelledAt,
            @Bind("now")          Instant now
    );

}