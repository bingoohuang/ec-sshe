package org.n3r.sshe.ssh;

import com.jcraft.jsch.ChannelShell;
import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SettingKey;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.SsheHost;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.regex.Pattern;

public class Shell {
    static boolean excludeLinePatternParsed = false;
    static Pattern excludeLinePattern = null;

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

            filterResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void filterResponse(StringBuilder response) {
        if (!excludeLinePatternParsed) {
            excludeLinePatternParsed = true;
            String excludeLinePattern = SsheConf.settings.get(SettingKey.excludeLinePattern);
            if (StringUtils.isNotEmpty(excludeLinePattern))
                Shell.excludeLinePattern = Pattern.compile(excludeLinePattern);
        }

        // Response text will add some \r and some other characters unexpected.
        int start = 0;
        int linePos = response.indexOf("\n");
        while (linePos > 0) {
            String line = response.substring(start, linePos);
            // Response text will add some \r and some other characters unexpected.

            line = line.replaceAll("\r", "").replaceAll("(\\[\\w.)+$", "");

            if (excludeLinePattern == null || !excludeLinePattern.matcher(line).find())
                SsheConf.console.println(line);

            start = linePos + 1;
            if (start >= response.length()) break;
            linePos = response.indexOf("\n", start);
        }

        SsheConf.console.print(response.substring(start));
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
