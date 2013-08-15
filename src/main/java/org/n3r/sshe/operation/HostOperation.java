package org.n3r.sshe.operation;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheHost;

public abstract class HostOperation {
    private String specHost;

    public abstract void execute(SsheHost ssheHost, HostOperation lastOperation);

    public void setSpecHost(String specHost) {
        this.specHost = specHost;
    }

    public boolean matchSpecHost(SsheHost ssheHost) {
        return StringUtils.isEmpty(specHost)
                || StringUtils.contains(ssheHost.getHost(), specHost)
                || FilenameUtils.wildcardMatch(specHost, ssheHost.getHost());
    }
}
