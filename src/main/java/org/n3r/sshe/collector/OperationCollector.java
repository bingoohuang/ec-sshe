package org.n3r.sshe.collector;

import com.google.common.collect.Lists;
import org.n3r.sshe.SsheConf;

import java.util.List;

public class OperationCollector {
    private final String hostInfo;
    private List<CommandAndResponse> commandAndResponses = Lists.newArrayList();

    public OperationCollector(String hostInfo) {
        this.hostInfo = hostInfo;
    }

    public void add(String command, String response) {
         commandAndResponses.add(new CommandAndResponse(command, response));
    }

    public boolean isNotEmpty() {
        return !commandAndResponses.isEmpty();
    }

    public void displayCollectorResult() {
        SsheConf.console.println("\r\n== " + hostInfo + " ==");
        for (CommandAndResponse commandAndResponse : commandAndResponses) {
            commandAndResponse.display();
        }
    }
}
