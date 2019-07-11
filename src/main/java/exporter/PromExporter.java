package exporter;

import io.prometheus.client.Gauge;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import java.util.regex.Pattern;

public class PromExporter {
    private int period;

    public PromExporter(int period) {
        this.period = period;
    }

    public void startExporter(String logPath, int port, String pattern) throws Exception {
        final Vertx vertx = Vertx.vertx();
        final Router router = Router.router(vertx);
        router.route("/metrics").handler(new MetricsHandler());
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        GaugeOne gaugeOne = new GaugeOne("gauge_reuters_available", Pattern.compile(pattern), logPath, period);
        PrometheusGauge prometheusGauge = new PrometheusGauge(gaugeOne);
        while (true) {
            prometheusGauge.startMonitoring();
            Thread.sleep(30000);
        }
    }
}
