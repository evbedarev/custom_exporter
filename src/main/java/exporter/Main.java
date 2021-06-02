package exporter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static final Map<String, String> appProps = new HashMap<>();
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
        appProps.put("port", property.getProperty("server.port"));
        appProps.put("service", property.getProperty("elk.service"));
        appProps.put("level", property.getProperty("elk.level"));
        appProps.put("message", property.getProperty("elk.message"));
        appProps.put("ipElk", property.getProperty("elk.ipElk"));
        appProps.put("period", property.getProperty("server.period"));
        appProps.put("portElk", property.getProperty("elk.Port"));
        appProps.put("gaugeName", property.getProperty("gauge.name"));
        //Rest
        appProps.put("passphrase", property.getProperty("jks.passphrase"));
        appProps.put("keypass", property.getProperty("jks.keypass"));
        appProps.put("pathToJks", property.getProperty("jks.PathSrvCert"));
        appProps.put("pathClient", property.getProperty("jks.PathClientCert"));
        appProps.put("url", property.getProperty("rest.url"));
        appProps.put("restGaugeName", property.getProperty("rest.gauge"));

    }
}
