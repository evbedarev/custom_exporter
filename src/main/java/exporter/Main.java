package exporter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableAutoConfiguration
public class Main {
    @Value("${server.port}")
    String port;

    @Value("${server.logPath}")
    String logPath;

    @Value("${server.period}")
    String period;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void init() throws Exception {
        PromExporter promExporter = new PromExporter(Integer.valueOf(period));
        promExporter.startExporter(logPath, Integer.valueOf(port));
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
