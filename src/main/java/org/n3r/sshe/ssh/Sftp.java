package org.n3r.sshe.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.util.Util;

public class Sftp {
    public static void sftp(Session session, String cmd, String p1, String p2) throws Exception {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        if (Util.anyOf(cmd, "get", "get-resume", "get-append", "put", "put-resume", "put-append")) {
            SftpProgressMonitor monitor = new SsheProgressMonitor();
            if (cmd.startsWith("get")) {
                int mode = ChannelSftp.OVERWRITE;
                if (cmd.equals("get-resume")) {
                    mode = ChannelSftp.RESUME;
                } else if (cmd.equals("get-append")) {
                    mode = ChannelSftp.APPEND;
                }
                channel.get(p1, p2, monitor, mode);
            } else {
                int mode = ChannelSftp.OVERWRITE;
                if (cmd.equals("put-resume")) {
                    mode = ChannelSftp.RESUME;
                } else if (cmd.equals("put-append")) {
                    mode = ChannelSftp.APPEND;
                }
                channel.put(p1, p2, monitor, mode);
            }
        }

        channel.disconnect();
    }


    public static class SsheProgressMonitor implements SftpProgressMonitor {
        long count = 0;
        long max = 0;
        long sentSize;
        int msgLength;
        int maxMsgLength;


        public void init(int op, String src, String dest, long max) {
            this.max = max;
            count = 0;
            System.out.print("SFTP processed ");
        }

        public boolean count(long count) {
            this.count += count;
            if (this.count > max / 100) reportProcess();

            return true;
        }

        public void end() {
            if (this.count > 0) reportProcess();

            System.out.println();
        }

        private void reportProcess() {
            while (msgLength-- > 0) System.out.print('\b');

            sentSize += this.count;
            this.count = 0;

            String msg = String.format("%s percent %.2f%%",
                    Util.humanReadableByteCount(sentSize, false), sentSize * 100. / max);
            msgLength = msg.length();
            if (maxMsgLength < msgLength) maxMsgLength = msgLength;
            if (msgLength < maxMsgLength) msg = msg + StringUtils.repeat(' ', maxMsgLength - msgLength);
            msgLength = maxMsgLength;

            System.out.print(msg);
        }
    }
}
