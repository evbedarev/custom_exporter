package exporter;

import exporter.elk_monitoring.PrometheusGaugeElk;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

public class PromExporter {
    private List<StartMonitoring> listPrometheusGauges = new ArrayList<>();

    public void startExporter() throws Exception {
        final Vertx vertx = Vertx.vertx();
        final Router router = Router.router(vertx);
        router.route("/metrics").handler(new MetricsHandler());
        vertx.createHttpServer().requestHandler(router::accept).listen(Integer.valueOf(Main.appProps.get("port")));
        generatePrometheusGauge();
//        GaugeOne gaugeOne = new GaugeOne("gauge_reuters_available", Pattern.compile(pattern), logPath, period);
//        StartMonitoring prometheusGauge = new PrometheusGaugeElk(Main.appProps.get("gaugeName"));//new PrometheusGaugeLog(gaugeOne);
        try {
            while (true) {
                for (StartMonitoring prometheusGauge : listPrometheusGauges) {
                    prometheusGauge.startMonitoring();
                }

//                prometheusGauge.startMonitoring();
                Thread.sleep(30000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            for (StartMonitoring prometheusGauge : listPrometheusGauges) {
                prometheusGauge.stopMonitoring();
            }
//            prometheusGauge.stopMonitoring();
        }
    }

    public void generatePrometheusGauge() {
        int countGauges = Integer.valueOf(Main.appProps.get("countGauge"));
//        List<StartMonitoring> listPrometheusGauges = new ArrayList<>();
        for (int i = 0; i < countGauges; i++) {
            String cGauge = Integer.toString((i + 1));
            String gaugeName = Main.appProps.get("gaugeName" + (i + 1));
            System.out.println(cGauge + " " + gaugeName);
            listPrometheusGauges.add(new PrometheusGaugeElk(gaugeName,cGauge));
        }
    }
}
