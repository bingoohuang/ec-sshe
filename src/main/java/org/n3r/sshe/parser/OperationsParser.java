package org.n3r.sshe.parser;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.operation.ExecOperation;
import org.n3r.sshe.operation.HostOperation;
import org.n3r.sshe.operation.ScpOperation;
import org.n3r.sshe.operation.SftpOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationsParser implements SectionParser {
    // [exec] some commands
    private final Pattern commandTypePattern = Pattern.compile("^\\[(.+)\\]\\s(.+)");
    private final List<HostOperation> operations;
    private Logger logger = LoggerFactory.getLogger(OperationsParser.class);

    public OperationsParser() {
        this.operations = SsheConf.operations;
    }

    @Override
    public void parse(String line) {
        String specHost = null;
        if (line.startsWith("@")) {
            int blankIndex = line.indexOf(' ');
            if (blankIndex > 0) {
                specHost = line.substring(1, blankIndex);
                line = line.substring(blankIndex);
                line = StringUtils.trim(line);
            }
        }

        Matcher matcher = commandTypePattern.matcher(line);
        String commandType;
        String commandLine;
        if (matcher.matches()) {
            commandType = matcher.group(1);
            commandLine = matcher.group(2);
        } else {
            commandType = "exec";
            commandLine = line;
        }

        HostOperation operation = parseOperationCommand(commandType, commandLine);
        if (operation != null) {
            operation.setSpecHost(specHost);
            operations.add(operation);
        }
    }

    private HostOperation parseOperationCommand(String commandType, String commandLine) {
        if ("exec".equals(commandType)) {
            return new ExecOperation(commandLine);
        }
        if ("sftp".equals(commandType)) {
            return new SftpOperation(commandLine);
        }
        if ("scp".equals(commandType)) {
            return new ScpOperation(commandLine);
        }

        logger.warn("unkown command type {}", commandType);

        return null;
    }

}
