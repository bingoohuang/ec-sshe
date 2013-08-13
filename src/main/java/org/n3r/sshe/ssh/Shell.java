package org.n3r.sshe.ssh;

import com.jcraft.jsch.ChannelShell;
import org.n3r.sshe.SsheHost;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class Shell {
    public static void waitUntilExpect(SsheHost ssheHost, String expect) {
        InputStreamReader is = ssheHost.getChannleOutput();
        char[] buff = new char[1024];
        try {
            int read;
            StringBuilder response = new StringBuilder();
            while ((read = is.read(buff)) != -1) {
                response.append(buff, 0, read);
                if (response.indexOf(expect) >= 0) break;
            }


            while (is.ready()) {
                if ((read = is.read(buff)) != -1)
                    response.append(buff, 0, read);
            }

            System.out.print(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static void createShell(SsheHost ssheHost) throws Exception {
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);

        ChannelShell channel = (ChannelShell) ssheHost.getSession().openChannel("shell");


        channel.setInputStream(pis, true);

        PipedOutputStream poos = new PipedOutputStream();
        PipedInputStream pois = new PipedInputStream(poos);
        channel.setOutputStream(poos, true);

        channel.connect(10 * 1000);

        ssheHost.setChannelShell(channel);
        ssheHost.setOutputStream(pos);
        ssheHost.setChannelOutput(pois);
    }


}
