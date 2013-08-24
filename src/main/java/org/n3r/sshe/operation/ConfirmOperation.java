package org.n3r.sshe.operation;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.SsheHost;

public class ConfirmOperation extends HostOperation {
    private int maxWaitMilis;

    public ConfirmOperation(String commandLine) {
        this.maxWaitMilis = StringUtils.isNumeric(commandLine)
                ? Integer.parseInt(commandLine)
                : SsheConf.parseMaxWaitMilis();
    }

    @Override
    protected void executeImpl(SsheHost ssheHost, HostOperation lastOperation, boolean isLastHostOperation) {
        if (lastOperation != null && !isLastHostOperation) SsheConf.confirm(maxWaitMilis);
    }

    @Override
    protected boolean requireConnect() {
        return false;
    }
}
