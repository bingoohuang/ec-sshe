package org.n3r.sshe.ssh;

import com.google.common.io.Closeables;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import org.n3r.sshe.util.Util;

import java.io.*;

public class Scp {

    public static void scp(Session session, String lfile, String rfile) throws Exception {
        // Preserves modification times, access times, and modes from the original file.
        boolean ptimestamp = true;

        // exec 'scp -t rfile' remotely
        String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
        System.out.println(Util.currentTime() + "> " + command);
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        checkAck(in);

        if (ptimestamp) commandTimestamp(lfile, out, in);
        commandC0644(lfile, out, in);


        commandFileContent(lfile, out, in);
        out.close();

        channel.disconnect();
    }

    private static void commandFileContent(String lfile, OutputStream out, InputStream in) throws IOException {
        sendFileContent(lfile, out);
        sendZero(out);

        out.flush();

        checkAck(in);
    }

    private static void commandTimestamp(String lfile, OutputStream out, InputStream in) throws IOException {
        File _lfile = new File(lfile);
        String command = "T " + (_lfile.lastModified() / 1000) + " 0";
        // The access time should be sent here,
        // but it is not accessible with JavaAPI ;-<
        command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
        out.write(command.getBytes());
        out.flush();

        checkAck(in);
    }

    private static void commandC0644(String lfile, OutputStream out, InputStream in) throws IOException {
        String command;// send "C0644 filesize filename", where filename should not include '/'
        long filesize = new File(lfile).length();
        command = "C0644 " + filesize + " ";
        if (lfile.lastIndexOf('/') > 0) {
            command += lfile.substring(lfile.lastIndexOf('/') + 1);
        } else {
            command += lfile;
        }

        command += "\n";
        out.write(command.getBytes());
        out.flush();

        checkAck(in);
    }

    private static void sendZero(OutputStream out) throws IOException {
        byte[] buf = new byte[1];
        buf[0] = 0; // send '\0'
        out.write(buf, 0, 1);
    }

    private static void sendFileContent(String lfile, OutputStream out) throws IOException {
        long filesize = new File(lfile).length();
        long sentSize = 0;
        long batchSize = 0;
        FileInputStream fis = new FileInputStream(lfile);
        byte[] buf = new byte[1024];
        try {
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) break;
                out.write(buf, 0, len); //out.flush();
                batchSize += len;
                if (batchSize > 1024 * 1024) {
                    sentSize += batchSize;
                    batchSize = 0;
                    System.out.println(String.format("sent:%s percent: %.2f%%",
                            Util.humanReadableByteCount(sentSize, false), sentSize * 100. / filesize));
                }
            }
            if (batchSize > 0) {
                sentSize += batchSize;
                System.out.println(String.format("sent:%s percent: %.2f%%",
                        Util.humanReadableByteCount(sentSize, false), sentSize * 100. / filesize));
            }
        } finally {
            Closeables.closeQuietly(fis);
        }
    }


    static void checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return;
        if (b == -1) return;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');

            if (b == 1) { // error
                System.out.print(sb.toString());
                System.exit(0);
            }

            if (b == 2) { // fatal error
                System.out.print(sb.toString());
                System.exit(0);
            }
        }
    }
}
