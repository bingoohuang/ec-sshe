package org.n3r.sshe.parser;

import org.n3r.sshe.SsheConf;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;

public class SettingsParser implements SectionParser {
    private static Pattern blankPattern = Pattern.compile("[:=]");

    private Map<String, String> settings;

    public SettingsParser() {
        this.settings = SsheConf.settings;
        this.settings.clear();
    }

    @Override
    public void parse(String line) {
        Matcher matcher = blankPattern.matcher(line);
        if (matcher.find()) {
            String key = trim(line.substring(0, matcher.start()));
            String value = trim(substring(line, matcher.end()));
            settings.put(key, value);
        } else {
            settings.put(line, "");
        }
    }
}
