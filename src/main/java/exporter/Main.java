package exporter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Properties;

public class Main {
    public static final String PATH_TO_LOG = Paths.get(".").toAbsolutePath().normalize().toString() + "/debug.log";
    public static void main(String[] args) throws Exception {
        Properties property = new Properties();
        writeToLog(" Search propfile " + Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties");
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties")) {
            property.load(fileInputStream);
            writeToLog("Load port " + property.getProperty("server.port"));
            writeToLog("Load logPath " + property.getProperty("server.logPath"));
            writeToLog("Load period " + property.getProperty("server.port"));
            writeToLog("Load pattern " + property.getProperty("log.pattern"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        PromExporter promExporter = new PromExporter(Integer.valueOf(property.getProperty("server.period")));
        promExporter.startExporter(property.getProperty("server.logPath"),
                Integer.valueOf(property.getProperty("server.port")),
                property.getProperty("log.pattern"));
    }

    public static void writeToLog(String text) throws IOException {
        try (FileWriter fw=new FileWriter(PATH_TO_LOG, true)) {
            fw.write(LocalDateTime.now() + " " + text + "\n");
        }
    }
}
