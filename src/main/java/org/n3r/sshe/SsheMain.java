package org.n3r.sshe;

import org.apache.commons.cli.*;
import org.n3r.sshe.gui.SsheForm;
import org.n3r.sshe.util.Util;

import java.io.File;
import java.io.IOException;

public class SsheMain {
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = parseCommandLine(args);

        if (commandLine.hasOption('g')) {
            SsheForm.runGUI();
            return;
        }

        runAscommandLine(commandLine);
    }

    private static void runAscommandLine(CommandLine commandLine) throws IOException {
        long start = System.currentTimeMillis();

        SsheConf.console = new SsheOutput() {
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
        };
        parseConf(commandLine);
        executeAndPrintCost(start);
    }

    private static void executeAndPrintCost(long start) {
        executeOperations();

        long costMillis = System.currentTimeMillis() - start;
        SsheConf.console.println("\r\n\r\n==Over cost " + Util.humanReadableDuration(costMillis) + "==");
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

    private static void parseConf(CommandLine commandLine) throws IOException {
        SsheConf.parseConf(new File(commandLine.getOptionValue('f', "sshe.conf")));
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
        for (SsheHost ssheHost : SsheConf.ssheHosts)
            ssheHost.executeOperations();
    }

}
