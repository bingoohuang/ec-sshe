package org.n3r.sshe.collector;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheConf;

public class CommandAndResponse {
    private final String command;
    private final String response;

    public CommandAndResponse(String command, String response) {
        this.command = command;
        this.response = response;
    }

    public void display() {
        if (StringUtils.isNotEmpty(command))
            SsheConf.console.println(command);

        SsheConf.console.println(response);
    }
}
