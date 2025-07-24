package com.example.consumer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class ConsumerResource {
    private final OrderDao orderDao;

    public ConsumerResource(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @GET
    public List<Order> getPending() {
        return orderDao.listPending();
    }
}