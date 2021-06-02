import exporter.rest_monitoring.RestRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Test {
    public static final Map<String, String> appProps = new HashMap<>();
    public static void main(String[] args) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(".").toAbsolutePath().normalize().toString() +
                "/application.properties")) {
            fillProperties(fileInputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        System.out.println(appProps.get("url"));
        RestRequest restRequest = new RestRequest(appProps);
        System.out.println(restRequest.getResponseStatus());
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
