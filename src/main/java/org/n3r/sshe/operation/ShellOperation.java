package org.n3r.sshe.operation;

import com.google.common.base.Throwables;
import org.apache.commons.io.IOUtils;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.SsheHost;
import org.n3r.sshe.ssh.Shell;

public class ShellOperation extends HostOperation {
    private final String commandLine;

    public ShellOperation(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    protected void executeImpl(SsheHost ssheHost, HostOperation lastOperation, boolean isLastHostOperation) {
        ssheHost.tryCreateChannelShell();

        try {
            if (lastOperation != null && !(lastOperation instanceof ShellOperation)) {
                IOUtils.write("\n", ssheHost.getOutputStream());
                Shell.waitUntilExpect(ssheHost);
            }

            String realCommand = filterCommandLime(ssheHost, commandLine);
            String data = realCommand + "\n";
            byte[] bytes = data.getBytes(SsheConf.getCharset());
            IOUtils.write(bytes, ssheHost.getOutputStream());

            Shell.waitUntilExpect(ssheHost);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected boolean requireConnect() {
        return true;
    }
}
