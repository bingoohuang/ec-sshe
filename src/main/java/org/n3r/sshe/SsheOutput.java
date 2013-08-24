package org.n3r.sshe;

public interface SsheOutput {
    void print(String x);

    void println(String x);

    void println();

    /**
     * wait user to confirm the current output.
     * @param maxWaitMillis max wait millis. if value <= 0, then wait forever
     */
    void waitConfirm(int maxWaitMillis);
}
