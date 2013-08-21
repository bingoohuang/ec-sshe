package org.n3r.sshe;

import org.apache.commons.cli.*;
import org.n3r.sshe.gui.SsheForm;
import org.n3r.sshe.util.Util;

import java.io.File;
import java.io.IOException;

public class SsheMain {
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = parseCommandLine(args);

        File configFile = new File(commandLine.getOptionValue('f', "sshe.conf"));
        if (!configFile.exists()) {
            System.err.println("config file does not exists or unspecified1");
            System.exit(-1);
        }

        if (commandLine.hasOption('g')) {
            SsheForm.runGUI(configFile);
            return;
        }

        runAscommandLine(configFile);
    }

    private static void runAscommandLine(File configFile) throws IOException {
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
        parseConf(configFile);
        executeAndPrintCost(start);
    }

    private static void executeAndPrintCost(long start) {
        try {
            executeOperations();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

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
        for (SsheHost ssheHost : SsheConf.ssheHosts)
            ssheHost.executeOperations();
    }

}
