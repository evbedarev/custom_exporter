package exporter;

import java.util.regex.Pattern;

public class GaugeOne {
    private String name;
    private Pattern pattern;
    private String logPath;
    private int period;

    public GaugeOne(String name, Pattern pattern, String logPath, int period) {
        this.name = name;
        this.pattern = pattern;
        this.logPath = logPath;
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
