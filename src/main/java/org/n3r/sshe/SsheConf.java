package org.n3r.sshe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.collector.CollectorMatcher;
import org.n3r.sshe.collector.OperationCollector;
import org.n3r.sshe.operation.HostOperation;
import org.n3r.sshe.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class SsheConf {
    private static final Logger logger = LoggerFactory.getLogger(SsheConf.class);

    public static List<SsheHost> ssheHosts = Lists.newArrayList();
    public static List<CollectorMatcher> collectors = Lists.newArrayList();
    public static List<HostOperation> operations = Lists.newArrayList();
    public static Map<String, String> settings = Maps.newHashMap();
    public static SsheOutput console;
    public static String key = "UpHJVmxF2GbXz3uMkGgmHw==";

    public static void parseConf(String configurationContent) throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(configurationContent));
        parseConfLines(br);
    }
    public static void parseConf(File configurationFile) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(configurationFile), "UTF-8"));

        parseConfLines(br);
    }

    private static void parseConfLines(BufferedReader br) throws IOException {
        SectionParser sectionParser = null;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            line = StringUtils.trim(line);

            // Inore blank lines and comment lines
            if (StringUtils.isEmpty(line) || line.startsWith("#")) continue;

            if (line.startsWith("*")) { // new section
                sectionParser = processSection(StringUtils.trim(line.substring(1)));
            } else if (sectionParser != null) {
                sectionParser.parse(line);
            } else {
                logger.warn("line {} is not recognized", line);
            }
        }

        br.close();
    }

    private static SectionParser processSection(String sectionName) {
        if ("hosts".equals(sectionName)) return new HostsParser();
        if ("operations".equals(sectionName)) return new OperationsParser();
        if ("settings".equals(sectionName)) return new SettingsParser();
        if ("collectors".equals(sectionName)) return new CollectorsParser();

        logger.warn("section {} was not recognized", sectionName);

        return null;
    }

    public static String getCharset() {
        String charset = settings.get(SettingKey.charset);
        if (charset == null) charset = Charset.defaultCharset().name();

        return charset;
    }

    public static void collect(OperationCollector operationCollector, StringBuilder fullResponse, String command) {
        for (CollectorMatcher collectorMatcher : collectors) {
            String response = fullResponse.toString();
            if (collectorMatcher.match(response)) {
                operationCollector.add(command, response);
            }
        }
    }
}
