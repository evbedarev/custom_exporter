package exporter;

import exporter.elk_monitoring.PrometheusGaugeElk;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class PromExporter {

    public void startExporter() throws Exception {
        final Vertx vertx = Vertx.vertx();
        final Router router = Router.router(vertx);
        router.route("/metrics").handler(new MetricsHandler());
        vertx.createHttpServer().requestHandler(router::accept).listen(Integer.valueOf(Main.appProps.get("port")));
//        GaugeOne gaugeOne = new GaugeOne("gauge_reuters_available", Pattern.compile(pattern), logPath, period);
        StartMonitoring prometheusGauge = new PrometheusGaugeElk(Main.appProps.get("gaugeName"));//new PrometheusGaugeLog(gaugeOne);
        try {
            while (true) {
                prometheusGauge.startMonitoring();
                Thread.sleep(30000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            prometheusGauge.stopMonitoring();
        }
    }
}
