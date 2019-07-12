package exporter.elk_monitoring;

import exporter.Main;
import exporter.StartMonitoring;
import io.prometheus.client.Gauge;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;

import java.security.KeyException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PrometheusGaugeElk implements StartMonitoring {
    private Gauge g;
    private String gaugeName;
    private Map<String, String> props;

    public PrometheusGaugeElk(String gaugeName) {
        this.gaugeName = gaugeName;
        this.props = Main.appProps;
        g = Gauge.build().name(gaugeName).help(gaugeName).register();
    }

    @Override
    public void startMonitoring() throws Exception {
        if (verifyMap()) {
            RestHighLevelClient esClient = new RestHighLevelClient(RestClient.builder(new HttpHost(props.get("ipElk"),
                    Integer.valueOf(props.get("portElk")), "http")));
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.timeout(new TimeValue(600, TimeUnit.SECONDS));
            sourceBuilder.from(0);
            sourceBuilder.size(10); // Size of result hits in scroll
            sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
            sourceBuilder.query(createBuilder());
            SearchRequest searchRequest = new SearchRequest("fxp-log*");
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse;
            searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            System.out.println(hits.totalHits);
            if (hits.totalHits > 0) {
                g.set(1);
            } else {
                g.set(0);
            }
            esClient.close();
        } else {
            throw new KeyException("Not found keys in Map props");
        }
    }

    private BoolQueryBuilder createBuilder() {
        DateTime dateTime = new DateTime();
        return QueryBuilders.boolQuery()
                .must(new MatchQueryBuilder("service", props.get("service"))) //"storm"
                .must(new MatchQueryBuilder("level", props.get("level"))) //"ERROR"
                .must(new MatchQueryBuilder("message", props.get("message"))) //"Wakeup error while waiting*"
                .must(QueryBuilders.rangeQuery("@timestamp")
                        .gt(dateTime.minusMinutes(Integer.valueOf(props.get("period")))))
                .must(QueryBuilders.rangeQuery("@timestamp")
                        .lt(dateTime.minusMinutes(0)));
    }

    private boolean verifyMap() {
        return props.containsKey("service") &&
                props.containsKey("level") &&
                props.containsKey("message") &&
                props.containsKey("ipElk") &&
                props.containsKey("portElk");
    }
}
