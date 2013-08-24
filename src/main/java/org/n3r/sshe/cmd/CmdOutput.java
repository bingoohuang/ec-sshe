package org.n3r.sshe.cmd;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheOutput;
import org.n3r.sshe.util.Util;

import java.io.IOException;

public class CmdOutput implements SsheOutput {
    @Override
    public void print(String x) {
        System.out.print(x);
    }

    @Override
    public void println(String x) {
        System.out.println(x);
    }

    @Override
    public void println() {
        System.out.println();
    }

    @Override
    public void waitConfirm(int maxWaitMillis) {
        String tips = "[Please press ENTER key to continue.]";

        System.out.print(tips);
        try {
            long start = System.currentTimeMillis();
            boolean timeout = false;
            while (System.in.available() == 0) {
                if (maxWaitMillis > 0 && System.currentTimeMillis() - start >= maxWaitMillis) {
                    timeout = true;
                    break;
                }
                Util.sleepMillis(100);
            }

            if (timeout) {
                System.out.print(StringUtils.repeat('\b', tips.length())); // move cursor back
                System.out.print(StringUtils.repeat(' ', tips.length()));  // override with space
                System.out.print(StringUtils.repeat('\b', tips.length())); // move cursor back again
            }
            else while (System.in.available() > 0) System.in.read(); // flush the buffer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
