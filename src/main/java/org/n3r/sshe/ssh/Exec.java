package org.n3r.sshe.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.SsheHost;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Exec {
    public static void exec(SsheHost ssheHost, String cmd) throws Exception {
        ChannelExec channel = (ChannelExec) ssheHost.getSession().openChannel("exec");
        channel.setCommand(cmd);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        InputStream in = channel.getInputStream();
        InputStreamReader is = new InputStreamReader(in, SsheConf.getCharset());

        channel.connect();

        char[] buff = new char[1024];

        StringBuilder fullResponse = new StringBuilder();
        do {
            while (is.ready()) {
                int read = is.read(buff);
                String response = new String(buff, 0, read);
                fullResponse.append(response);
                SsheConf.console.print(response);
            }
        } while (!channel.isClosed());

        SsheConf.collect(ssheHost.getOperationCollector(), fullResponse.toString(), cmd);

        in.close();
        channel.disconnect();
    }
}
