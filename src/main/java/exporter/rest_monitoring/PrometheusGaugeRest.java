package exporter.rest_monitoring;

import com.sun.security.ntlm.Client;
import exporter.Main;
import exporter.StartMonitoring;
import io.prometheus.client.Gauge;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.security.KeyException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PrometheusGaugeRest implements StartMonitoring {
    private Gauge g;
    private Map<String, String> props;
    private RestHighLevelClient esClient;

    public PrometheusGaugeRest(String gaugeName) {
        this.props = Main.appProps;
        g = Gauge.build().name(gaugeName).help(gaugeName).register();
    }

    @Override
    public void startMonitoring() throws Exception {
        if (verifyMap()) {

            if (1 > 0) {
                g.set(1);
            } else {
                g.set(0);
            }

        } else {
            esClient.close();
            throw new KeyException("Not found keys in Map props");

        }
    }

    @Override
    public void stopMonitoring() throws Exception {
        esClient.close();
    }


    private boolean verifyMap() {
        return props.containsKey("service") &&
                props.containsKey("level") &&
                props.containsKey("message") &&
                props.containsKey("ipElk") &&
                props.containsKey("portElk");
    }

    private HttpHost[] generateStackIpAddresses() {
        Pattern pattern = Pattern.compile(",");
        String[] hosts = pattern.split(props.get("ipElk"));
        HttpHost[] arrayHosts = new HttpHost[hosts.length];
        for (int i=0; i < hosts.length; i++) {
//            System.out.println(hosts[i]);
//            System.out.println(props.get("portElk"));
            arrayHosts[i] = new HttpHost(hosts[i].trim(),
                    Integer.valueOf(props.get("portElk")), "http");
        }
        return arrayHosts;
    }


}
