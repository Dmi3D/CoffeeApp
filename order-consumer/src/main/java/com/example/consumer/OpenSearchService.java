// src/main/java/com/example/consumer/OpenSearchService.java
package com.example.consumer;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opensearch.action.index.IndexRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.common.xcontent.XContentType;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.opensearch.search.aggregations.bucket.histogram.Histogram;
import org.opensearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.opensearch.search.aggregations.metrics.ParsedAvg;
import org.opensearch.search.builder.SearchSourceBuilder;

public class OpenSearchService {
    private final RestHighLevelClient client;
    private final String index;

    public OpenSearchService(RestHighLevelClient client, String index) {
        this.client = client;
        this.index  = index;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public void indexOrder(Order order) throws IOException {
        byte[] data = mapper.writeValueAsBytes(order);
        IndexRequest req = new IndexRequest(index)
                .id(order.getId().toString())
                .source(data, XContentType.JSON);
        client.index(req, RequestOptions.DEFAULT);
    }

    public List<WeeklyAverage> getWeeklyTimeToServe() throws IOException {
        var source = new SearchSourceBuilder()
                .size(0)
                .query(QueryBuilders.termQuery("status", "completed"))
                .aggregation(
                        AggregationBuilders
                                .dateHistogram("by_week")
                                .field("createdAt")
                                .calendarInterval(DateHistogramInterval.WEEK)
                                .subAggregation(
                                        AggregationBuilders
                                                .avg("avg_time_ms")
                                                .script(new Script(
                                                        ScriptType.INLINE,
                                                        "painless",
                                                        // same script you have
                                                        "doc['completedAt'].value.toInstant().toEpochMilli() "
                                                                + "- doc['createdAt'].value.toInstant().toEpochMilli()",
                                                        Map.of()
                                                ))
                                )
                );

        SearchRequest request = new SearchRequest(index);
        request.source(source);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        ParsedDateHistogram hist =
                response.getAggregations().get("by_week");
        List<WeeklyAverage> out = new ArrayList<>();

        for (Histogram.Bucket bucket : hist.getBuckets()) {
            ParsedAvg avgAgg = bucket.getAggregations().get("avg_time_ms");
            String weekKey = bucket.getKeyAsString();
            long weekStartMs = Instant.parse(weekKey).toEpochMilli();
            double avgMs     = avgAgg.getValue();
            out.add(new WeeklyAverage(weekStartMs, avgMs));
        }
        return out;
    }
}