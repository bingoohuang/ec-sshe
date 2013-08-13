package org.n3r.sshe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.operation.HostOperation;
import org.n3r.sshe.parser.HostsParser;
import org.n3r.sshe.parser.OperationsParser;
import org.n3r.sshe.parser.SectionParser;
import org.n3r.sshe.parser.SettingsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class SsheConf {
    private static final Logger logger = LoggerFactory.getLogger(SsheConf.class);

    public static List<SsheHost> ssheHosts = Lists.newArrayList();
    public static List<HostOperation> operations = Lists.newArrayList();
    public static Map<String, String> settings = Maps.newHashMap();

    public static void parseConf() throws IOException {
        File confFile = new File("sshe.conf");
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(confFile), "UTF-8"));

        SectionParser sectionParser = null;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            line = StringUtils.trim(line);

            // Inore blank lines and comment lines
            if (StringUtils.isEmpty(line) || line.startsWith("#")) continue;

            if (line.startsWith("*")) { // new section
                sectionParser = processSection(StringUtils.trim(line.substring(1)));
            } else if (sectionParser != null) {
                sectionParser.parse(line);
            }
        }
    }


    private static SectionParser processSection(String sectionName) {
        if ("hosts".equals(sectionName)) return new HostsParser();
        if ("operations".equals(sectionName)) return new OperationsParser();
        if ("settings".equals(sectionName)) return new SettingsParser();

        logger.warn("section {} was not recognized", sectionName);

        return null;
    }
}
