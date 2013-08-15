package org.n3r.sshe.operation;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheHost;

public abstract class HostOperation {
    private String specHost;

    protected abstract void executeImpl(SsheHost ssheHost, HostOperation lastOperation);

    public HostOperation execute(SsheHost ssheHost, HostOperation lastOperation) {
        if (!matchSpecHost(ssheHost)) return lastOperation;

        executeImpl(ssheHost, lastOperation);
        return this;
    }

    public void setSpecHost(String specHost) {
        this.specHost = specHost;
    }

    private boolean matchSpecHost(SsheHost ssheHost) {
        return StringUtils.isEmpty(specHost)
                || StringUtils.contains(ssheHost.getHost(), specHost)
                || FilenameUtils.wildcardMatch(specHost, ssheHost.getHost());
    }
}
