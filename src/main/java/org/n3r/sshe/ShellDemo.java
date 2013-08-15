package org.n3r.sshe;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ShellDemo {
    public static void main(String[] arg) throws Exception {
        JSch jSch = new JSch();

        Session session = jSch.getSession("webapp", "10.142.194.155", 22);
        session.setPassword("inL58V6c");
        session.setConfig("StrictHostKeyChecking", "no");

        session.connect(30000);   // making a connection with timeout.

        Channel channel = session.openChannel("shell");


        channel.setInputStream(System.in);


        channel.setOutputStream(System.out);
        channel.connect();

    }

}
