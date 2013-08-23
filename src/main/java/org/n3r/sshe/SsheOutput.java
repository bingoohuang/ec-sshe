package org.n3r.sshe;

public interface SsheOutput {
    void print(String x);

    void println(String x);

    void println();

    /**
     * wait user to confirm the current output.
     */
    void waitConfirm();
}
