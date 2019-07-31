package exporter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static final Map<String, String> appProps = new HashMap<>();
    public static int countGauge;
    private static final String PATH_TO_LOG = Paths.get(".").toAbsolutePath().normalize().toString() + "/debug.log";

    public static void main(String[] args) throws Exception {
        writeToLog(" Search propfile " + Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties");
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties")) {
            fillProperties(fileInputStream);
            for (Map.Entry<String, String> entry: appProps.entrySet()) {
                writeToLog("Load " + entry.getKey() + " " + entry.getValue());
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
//        System.out.println(appProps.get("ipElk"));
        PromExporter promExporter = new PromExporter();
        promExporter.startExporter();
    }

    public static void writeToLog(String text) throws IOException {
        try (FileWriter fw=new FileWriter(PATH_TO_LOG, true)) {
            fw.write(LocalDateTime.now() + " " + text + "\n");
        }
    }

    private static void fillProperties(FileInputStream fileInputStream) throws IOException {
        Properties property = new Properties();
        property.load(fileInputStream);
        countGauge= Integer.valueOf(property.getProperty("count_gauge"));
        appProps.put("port" , property.getProperty("server.port"));
        appProps.put("period", property.getProperty("server.period"));
        appProps.put("portElk", property.getProperty("elk.Port"));
        appProps.put("ipElk", property.getProperty("elk.ipElk"));
        appProps.put("countGauge", property.getProperty("count_gauge"));
        for (int i = 0; i < countGauge; i++) {
            appProps.put("service" + (i + 1), property.getProperty("elk" + (i + 1) +".service"));
            appProps.put("level" + (i + 1), property.getProperty("elk" + (i + 1) + ".level"));
            appProps.put("message" + (i + 1), property.getProperty("elk" + (i + 1) + ".message"));
            appProps.put("gaugeName" + (i + 1), property.getProperty("gauge" + (i + 1) + ".name"));
        }
    }
}
