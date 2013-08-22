package org.n3r.sshe.parser;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.collector.CollectorMatcher;
import org.n3r.sshe.collector.ContainsMatcher;
import org.n3r.sshe.collector.MatchesMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectorsParser implements SectionParser {
    private Logger logger = LoggerFactory.getLogger(CollectorsParser.class);

    // [has] some commands
    private final Pattern collectorTypePattern = Pattern.compile("^\\[(.+)\\]\\s*(.+)");
    private final List<CollectorMatcher> collectors;

    public CollectorsParser() {
        this.collectors = SsheConf.collectors;
    }

    @Override
    public void parse(String line) {
        Matcher matcher = collectorTypePattern.matcher(line);
        String collectorType, parameter;
        if (matcher.matches()) {
            collectorType = matcher.group(1);
            parameter = matcher.group(2);
        }
        else {
            collectorType = "contains";
            parameter = line;
        }


        CollectorMatcher collector = createCollector(collectorType, parameter);
        if (collector != null) collectors.add(collector);
    }

    private CollectorMatcher createCollector(String collectorType, String parameter) {
        boolean not = false;
        if (collectorType.indexOf("not ") == 0) {
            not = true;
            parameter = StringUtils.trim(StringUtils.substring(collectorType, 4));
        }

        if ("contains".equals(collectorType)) return new ContainsMatcher(parameter, not);
        if ("matches".equals(collectorType)) return new MatchesMatcher(parameter, not);

        logger.warn("unkown command type {}", collectorType);

        return null;
    }
}
