package org.n3r.sshe;

import com.google.common.collect.Lists;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.n3r.sshe.cmd.CmdOutput;
import org.n3r.sshe.collector.OperationCollector;
import org.n3r.sshe.gui.SsheForm;
import org.n3r.sshe.operation.HostOperation;
import org.n3r.sshe.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SsheMain {
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = parseCommandLine(args);

        File configFile = new File(commandLine.getOptionValue('f', "sshe.conf"));
        if (!configFile.exists()) {
            FileUtils.writeStringToFile(configFile, "*settings\r\n\r\n*hosts\r\n\r\n*operations\r\n\r\n");
        }

        if (commandLine.hasOption('g')) {
            SsheForm.runGUI(configFile);
            return;
        }

        runAscommandLine(configFile);
    }

    private static void runAscommandLine(File configFile) throws IOException {
        long start = System.currentTimeMillis();

        SsheConf.console = new CmdOutput();
        parseConf(configFile);
        executeAndPrintCost(start);
    }

    private static void executeAndPrintCost(long start) {
        SsheConf.console.print("= Operations result =");

        executeOperations();

        long costMillis = System.currentTimeMillis() - start;
        String costStr = Util.humanReadableDuration(costMillis);
        SsheConf.console.println("\r\n\r\n== Over cost " + costStr + " ==");
    }

    public static void runGUI(String configurationContent, SsheOutput ssheOutput) throws IOException {
        long start = System.currentTimeMillis();
        SsheConf.console = ssheOutput;
        parseConf(configurationContent);
        executeAndPrintCost(start);
    }

    private static void parseConf(String configurationContent) throws IOException {
        SsheConf.parseConf(configurationContent);
    }

    private static void parseConf(File configFile) throws IOException {
        SsheConf.parseConf(configFile);
    }

    private static CommandLine parseCommandLine(String[] args) throws ParseException {
        Options options = new Options();
        Option option = new Option("f", "conf-file", true, "specify a conf file");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("h", "help", false, "display help text");
        options.addOption(option);

        option = new Option("g", "gui", false, "display GUI");
        options.addOption(option);

        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption('h')) {
            new HelpFormatter().printHelp("ec-sshe", options, true);
            System.exit(0);
        }

        return commandLine;
    }

    private static void executeOperations() {
        if (SsheConf.ssheHosts.size() == 0)
            SsheConf.console.print("no hosts defined to run operations!");

        List<OperationCollector> operationCollectors = Lists.newArrayList();
        for (int i = 0, ii = SsheConf.ssheHosts.size(); i < ii; ++i ) {
            SsheHost ssheHost = SsheConf.ssheHosts.get(i);
            HostOperation lastOperation = ssheHost.executeOperations(operationCollectors, i == ii - 1);
            if (lastOperation != null && i < ii - 1) SsheConf.confirmByHost();
        }

        if (operationCollectors.size() > 0)
            SsheConf.console.println("\r\n\r\n= Collectors result =");

        for (OperationCollector collector : operationCollectors)
            collector.displayCollectorResult();
    }

}
