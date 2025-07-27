package com.example.consumer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {
    private final OpenSearchService os;

    public SearchResource(OpenSearchService os) {
        this.os = os;
    }

    @GET
    @Path("/time-to-serve")
    public List<WeeklyAverage> timeToServe() throws Exception {
        return os.getWeeklyTimeToServe();
    }
}