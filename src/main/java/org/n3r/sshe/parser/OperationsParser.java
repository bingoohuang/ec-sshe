package org.n3r.sshe.parser;

import org.apache.commons.lang3.StringUtils;
import org.n3r.sshe.SsheConf;
import org.n3r.sshe.operation.*;
import org.n3r.sshe.util.Substituters;
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
        this.operations.clear();
    }

    @Override
    public void parse(String line) {
        String parsedLine = Substituters.parse(line, SsheConf.settings);
        String specHost = null;
        if (parsedLine.startsWith("@")) {
            int blankIndex = parsedLine.indexOf(' ');
            if (blankIndex > 0) {
                specHost = parsedLine.substring(1, blankIndex);
                parsedLine = parsedLine.substring(blankIndex);
                parsedLine = StringUtils.trim(parsedLine);
            }
        }

        Matcher matcher = commandTypePattern.matcher(parsedLine);
        String commandType;
        String commandLine;
        if (matcher.matches()) {
            commandType = matcher.group(1);
            commandLine = matcher.group(2);
        } else {
            commandType = "shell";
            commandLine = parsedLine;
        }

        HostOperation operation = parseOperationCommand(commandType, commandLine);
        if (operation != null) {
            operation.setSpecHost(specHost);
            operations.add(operation);
        }
    }

    private HostOperation parseOperationCommand(String commandType, String commandLine) {
        if ("exec".equals(commandType)) return new ExecOperation(commandLine);
        if ("shell".equals(commandType)) return new ShellOperation(commandLine);
        if ("sftp".equals(commandType)) return new SftpOperation(commandLine);
        if ("scp".equals(commandType)) return new ScpOperation(commandLine);
        if ("sleep".equals(commandType)) return new SleepOperation(commandLine);

        logger.warn("unkown command type {}", commandType);

        return null;
    }

}
