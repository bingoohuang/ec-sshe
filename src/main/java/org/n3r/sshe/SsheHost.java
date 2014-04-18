package org.n3r.sshe;

import com.google.common.base.Throwables;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.n3r.sshe.collector.OperationCollector;
import org.n3r.sshe.operation.HostOperation;
import org.n3r.sshe.security.AESEncrypter;
import org.n3r.sshe.ssh.Shell;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.substring;

public class SsheHost {
    private int hostIndex;
    private String host;
    private String user;
    private String pass;
    private Session session;
    private ChannelShell channelShell;
    private OutputStream outputStream;
    private InputStreamReader channelOutput;
    private OperationCollector operationCollector;

    public SsheHost(int hostIndex, String host, String user, String pass) {
        this.hostIndex = hostIndex;
        this.host = host;
        this.user = user;
        this.pass = parsePassword(pass);
    }

    private String parsePassword(String pass) {
        if (!startsWith(pass, "{AES}")) return pass;

        return new AESEncrypter(SsheConf.key).decrypt(substring(pass, 5));
    }

    public SsheHost(int hostIndex, String host, SsheHost ssheHost) {
        this(hostIndex, host, ssheHost.getUser(), ssheHost.getPass());
    }

    public void connect() {
        if (session != null) return;

        String hostInfo = getHostInfo();
        SsheConf.console.println("\r\n\r\n== " + hostInfo + " ==\r\n");
        operationCollector = new OperationCollector(hostInfo);

        JSch jSch = new JSch();

        try {
            session = jSch.getSession(user, host, 22);
            session.setPassword(pass);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10 * 1000);   // making a connection with timeout.
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    private void disconnect() {
        try {
            if (channelShell != null) {
                channelShell.disconnect();
                channelShell = null;
            }

            if (session != null) {
                session.disconnect();
                session = null;
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    private String getHostInfo() {
        return user + "@" + host;
    }

    public void setChannelShell(ChannelShell channelShell) {
        this.channelShell = channelShell;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void tryCreateChannelShell() {
        if (channelShell != null) return;

        try {
            Shell.createShell(this);

            Shell.waitUntilExpect(this);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public void setChannelOutput(PipedInputStream channelOutput) {
        try {
            this.channelOutput = new InputStreamReader(channelOutput, SsheConf.getCharset());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public HostOperation executeOperations(List<OperationCollector> operationCollectors, boolean lastHost) {
        try {
            HostOperation lastOperation = null;
            for (int i = 0, ii = SsheConf.operations.size(); i < ii; ++i) {
                HostOperation operation = SsheConf.operations.get(i);
                lastOperation = operation.execute(this, lastOperation, lastHost && i + 1 == ii);
            }

            if (operationCollector != null && operationCollector.isNotEmpty())
                operationCollectors.add(operationCollector);

            return lastOperation;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            disconnect();
        }
        return null;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public Session getSession() {
        return session;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getHost() {
        return host;
    }

    public InputStreamReader getChannleOutput() {
        return channelOutput;
    }

    public OperationCollector getOperationCollector() {
        return operationCollector;
    }
}
