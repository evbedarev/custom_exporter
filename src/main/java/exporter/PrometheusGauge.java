package exporter;

import io.prometheus.client.Gauge;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrometheusGauge {
    private Gauge g;
    private Matcher matcher;
    private GaugeOne gaugeOne;
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile(".*(\\d{4}-\\d{1,2}-\\d{1,2}) (\\d{2}:\\d{2}:\\d{2}).*");

    public PrometheusGauge(GaugeOne gaugeOne) {
        this.gaugeOne = gaugeOne;
        g = Gauge.build().name(gaugeOne.getName()).help(gaugeOne.getName()).register();
    }

    public void startMonitoring() throws IOException, InterruptedException {
        if (!new File(gaugeOne.getLogPath()).exists()) {
            g.set(2);
            Main.writeToLog("Log file " + gaugeOne.getLogPath() + " not found!!!");
            Thread.sleep(60000);
            return;
        }
        if (parseFile(readLines(gaugeOne.getLogPath()))) {
            g.set(1);
        } else {
            g.set(0);
        }
    }

    private List<String> readLines(String logPath) throws IOException {
        List<String> strings = new ArrayList<>();
        Files.lines(Paths.get(logPath), StandardCharsets.UTF_8).forEach(p -> {
            if (checkEnterInInterval(p)) {
                strings.add(p);
            }
        });
        return strings;
    }

    private Boolean parseFile(List<String> fileStrings) {
        for (String str : fileStrings) {
            matcher = gaugeOne.getPattern().matcher(str);
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
            return ldtm.isAfter(ldtmNow.minusMinutes(gaugeOne.getPeriod()));
        }
        return false;
    }
}
