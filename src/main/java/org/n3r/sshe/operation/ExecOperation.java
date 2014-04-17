package org.n3r.sshe.operation;

import com.google.common.base.Throwables;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.SsheHost;
import org.n3r.sshe.ssh.Exec;

public class ExecOperation extends HostOperation {
    private final String commandLine;

    public ExecOperation(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    protected void executeImpl(SsheHost ssheHost, HostOperation lastOperation, boolean isLastHostOperation) {
        String realCommandLine = filterCommandLime(ssheHost, commandLine);
        SsheConf.console.println("[exec] " + realCommandLine);
        try {
            Exec.exec(ssheHost, realCommandLine);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected boolean requireConnect() {
        return true;
    }
}
