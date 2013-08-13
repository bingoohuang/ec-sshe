package org.n3r.sshe;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.n3r.sshe.ssh.Shell;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class SsheHost {
    private String host;
    private String user;
    private String pass;
    private Session session;
    private ChannelShell channelShell;
    private OutputStream outputStream;
    private InputStreamReader channelOutput;

    public SsheHost(String host, String user, String pass) {
        this.host = host;
        this.user = user;
        this.pass = pass;
    }

    public SsheHost(String host, SsheHost ssheHost) {
        this(host, ssheHost.getUser(), ssheHost.getPass());
    }

    public void connect() {
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

    public void disconnect() {
        if (channelShell != null) {
            channelShell.disconnect();
            channelShell = null;
        }

        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    public String getHostInfo() {
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

            String expect = Objects.firstNonNull(SsheConf.settings.get(SettingKey.expect), "$");
            Shell.waitUntilExpect(this, expect);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public void setChannelOutput(PipedInputStream channelOutput) {
        try {
            String charset = SsheConf.settings.get(SettingKey.charset);
            if (charset == null) charset = Charset.defaultCharset().name();

            this.channelOutput = new InputStreamReader(channelOutput, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
}
