package org.n3r.sshe.operation;

import com.google.common.base.Throwables;
import org.apache.commons.io.IOUtils;
import org.n3r.sshe.SsheHost;
import org.n3r.sshe.ssh.Shell;

public class ExecOperation extends HostOperation {
    private final String commandLine;

    public ExecOperation(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public void execute(SsheHost ssheHost, HostOperation lastOperation) {
        ssheHost.tryCreateChannelShell();

        try {
            if (lastOperation != null && !(lastOperation instanceof ExecOperation)) {
                IOUtils.write("\n", ssheHost.getOutputStream());
                Shell.waitUntilExpect(ssheHost, "$");
            }

            IOUtils.write(commandLine + "\n", ssheHost.getOutputStream());
            Shell.waitUntilExpect(ssheHost, "$");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


}
