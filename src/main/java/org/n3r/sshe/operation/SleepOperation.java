package org.n3r.sshe.operation;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheHost;
import org.n3r.sshe.util.Util;

public class SleepOperation extends HostOperation {
    private final String commandLine;
    private final long sleepMillis;

    public SleepOperation(String commandLine) {
        this.commandLine = commandLine;
        String[] fields = StringUtils.split(commandLine);
        if (fields.length != 1) throw new RuntimeException(commandLine + " is invalid.");

        this.sleepMillis = Long.parseLong(fields[0]);
    }

    @Override
    protected void executeImpl(SsheHost ssheHost, HostOperation lastOperation) {
        System.out.println("[sleep] " + commandLine);
        Util.sleepMillis(sleepMillis);
    }
}
