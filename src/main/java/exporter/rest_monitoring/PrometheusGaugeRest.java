package exporter.rest_monitoring;

import exporter.Main;
import exporter.StartMonitoring;
import io.prometheus.client.Gauge;
import java.security.KeyException;
import java.util.Map;

public class PrometheusGaugeRest implements StartMonitoring {
    private Gauge g;
    private Map<String, String> props;
    private RestRequest restRequest;

    public PrometheusGaugeRest(String gaugeName) {
        this.props = Main.appProps;
        g = Gauge.build().name(gaugeName).help(gaugeName).register();
        restRequest = new RestRequest(props);
    }

    @Override
    public void startMonitoring() throws Exception {
        if (verifyMap()) {
            int resp = restRequest.getResponseStatus();
            if (resp == 200) {
                g.set(1);
            } else {
                g.set(0);
            }

        } else {
            throw new KeyException("Not found keys in Map props");

        }
    }

    @Override
    public void stopMonitoring() throws Exception {
        System.out.println("Stopping");
    }

    private boolean verifyMap() {
        return props.containsKey("passphrase") &&
                props.containsKey("keypass") &&
                props.containsKey("pathToJks") &&
                props.containsKey("pathClient") &&
                props.containsKey("url");
    }
}
