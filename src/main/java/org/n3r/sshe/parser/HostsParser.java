package org.n3r.sshe.parser;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.SsheHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HostsParser implements SectionParser {
    private Logger logger = LoggerFactory.getLogger(HostsParser.class);
    private List<SsheHost> ssheHosts;

    public HostsParser() {
        this.ssheHosts = SsheConf.ssheHosts;
        this.ssheHosts.clear();
    }

    @Override
    public void parse(String line) {
        String[] fields = StringUtils.split(line);
        SsheHost ssheHost = null;
        if (fields.length == 3) {
            ssheHost = new SsheHost(ssheHosts.size(), fields[0], fields[1], fields[2]);
        } else if (fields.length == 1 && ssheHosts.size() > 0) {
            ssheHost = new SsheHost(ssheHosts.size(), fields[0], ssheHosts.get(ssheHosts.size() - 1));
        } else {
            logger.warn("host config {} is not valid", line);
        }

        if (ssheHost != null) ssheHosts.add(ssheHost);
    }
}
