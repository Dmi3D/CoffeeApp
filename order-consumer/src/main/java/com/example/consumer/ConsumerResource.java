package com.example.consumer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    @POST
    @Path("/orders/{id}/complete")
    public Response complete(@PathParam("id") UUID id) {
        Instant now = Instant.now();
        orderDao.updateStatus(id, "completed", now, null, now);
        return Response.accepted().build();
    }

    @POST
    @Path("/orders/{id}/cancel")
    public Response cancel(@PathParam("id") UUID id) {
        Instant now = Instant.now();
        orderDao.updateStatus(id, "cancelled", null, now, now);
        return Response.accepted().build();
    }
}