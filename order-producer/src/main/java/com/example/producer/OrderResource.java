package com.example.producer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    private final RabbitMqService mq;
    private final ObjectMapper mapper = new ObjectMapper();

    public static class OrderRequest {
        public String customerName;
        public String coffeeType;
        public String milkType;
        public int numShots;
        public List<String> syrups;
    }

    public OrderResource(RabbitMqService mq) {
        this.mq = mq;
    }

    @POST
    public Response create(OrderRequest req) {
        try {
            UUID id = UUID.randomUUID();

            Map<String,Object> coffeeOrderEvent = Map.of(
                    "id",           id.toString(),
                    "customerName", req.customerName,
                    "coffeeType",   req.coffeeType,
                    "numShots",     req.numShots,
                    "milkType",     req.milkType,
                    "syrups",       req.syrups == null ? List.of() : req.syrups,
                    "createdAt",    Instant.now().toString()
            );

            byte[] payload = mapper.writeValueAsBytes(coffeeOrderEvent);
            mq.publish(payload);

            // 5) Return 202 Accepted with the orderId
            return Response.accepted(Map.of("orderId", id)).build();
        } catch (Exception e) {
            // on failure, return 500 with an error message
            return Response.serverError()
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
