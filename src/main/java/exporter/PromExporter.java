package exporter;

import io.prometheus.client.Gauge;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PromExporter {
    private static final Gauge g = Gauge.build().name("gauge_reuters_available").help("gauge_reuters_available").register();
    private static final Pattern ERROR_PATTERN = Pattern.compile(".*(ERROR).*");
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile(".*(\\d{4}-\\d{1,2}-\\d{1,2}) (\\d{2}:\\d{2}:\\d{2}).*");
    private int period;
    private Matcher matcher;

    public PromExporter(int period) {
        this.period = period;
    }

    public void startExporter(String logPath, int port) throws Exception {
        final Vertx vertx = Vertx.vertx();
        final Router router = Router.router(vertx);
        router.route("/metrics").handler(new MetricsHandler());
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        g.set(0);
        while (true) {
            if (parseFile(readLines(logPath))) {
                g.set(1);
            } else {
                g.set(0);
            }
            Thread.sleep(10000);
        }
    }

    private List<String> readLines(String confFilePath) throws IOException {
        List<String> strings = new ArrayList<>();
        Files.lines(Paths.get(confFilePath), StandardCharsets.UTF_8).forEach(p -> {
            if (checkEnterInInterval(p)) {
                strings.add(p);
            }
        });
        return strings;
    }

    private Boolean parseFile(List<String> fileStrings) {
        for (String str : fileStrings) {
            matcher = ERROR_PATTERN.matcher(str);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    boolean checkEnterInInterval(String fileString) {
        matcher = DATE_TIME_PATTERN.matcher(fileString);
        if (matcher.find()) {
//            System.out.println(matcher.group(1) + "T" + matcher.group(2));
            LocalDateTime ldtm = LocalDateTime.parse(matcher.group(1) + "T" + matcher.group(2));
            LocalDateTime ldtmNow = LocalDateTime.now();
//            System.out.println(ldtm);
            return ldtm.isAfter(ldtmNow.minusMinutes(period));
        }
        return false;
    }
}
