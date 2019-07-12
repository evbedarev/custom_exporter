package exporter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static final Map<String, String> appProps = new HashMap<>();
    public static final String PATH_TO_LOG = Paths.get(".").toAbsolutePath().normalize().toString() + "/debug.log";

    public static void main(String[] args) throws Exception {
        writeToLog(" Search propfile " + Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties");
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties")) {
            fillProperties(fileInputStream);
            writeToLog("Load port " + appProps.get("portElk"));
            writeToLog("Load logPath " + appProps.get("logPath"));
            writeToLog("Load period " + appProps.get("period"));
            writeToLog("Load pattern " + appProps.get("pattern"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        PromExporter promExporter = new PromExporter(Integer.valueOf(appProps.get("period")));
        promExporter.startExporter(appProps.get("logPath"),
                Integer.valueOf(appProps.get("port")),
                appProps.get("pattern"));
    }

    public static void writeToLog(String text) throws IOException {
        try (FileWriter fw=new FileWriter(PATH_TO_LOG, true)) {
            fw.write(LocalDateTime.now() + " " + text + "\n");
        }
    }

    private static void fillProperties(FileInputStream fileInputStream) throws IOException {
        Properties property = new Properties();
        property.load(fileInputStream);
        appProps.put("port", property.getProperty("server.port"));
        appProps.put("service", property.getProperty("elk.service"));
        appProps.put("level", property.getProperty("elk.level"));
        appProps.put("message", property.getProperty("elk.message"));
        appProps.put("ipElk", property.getProperty("elk.ipElk"));
        appProps.put("logPath", property.getProperty("server.logPath"));
        appProps.put("pattern", property.getProperty("log.pattern"));
        appProps.put("period", property.getProperty("server.period"));
        appProps.put("portElk", property.getProperty("elk.Port"));
    }
}
