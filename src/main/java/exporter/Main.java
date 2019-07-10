package exporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    private static String period;
    private static String logPath;
    private static String port;

    public static void main(String[] args) throws Exception {
        Properties property = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties")) {
            property.load(fileInputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        PromExporter promExporter = new PromExporter(Integer.valueOf(property.getProperty("server.period")));
        promExporter.startExporter(property.getProperty("server.logPath"),
                Integer.valueOf(property.getProperty("server.port")));
    }
}
