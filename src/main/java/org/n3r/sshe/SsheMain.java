package org.n3r.sshe;

import org.apache.commons.cli.*;
import org.n3r.sshe.util.Util;

import java.io.File;
import java.io.IOException;

public class SsheMain {
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = parseCommandLine(args);

        long start = System.currentTimeMillis();

        parseConf(commandLine);
        executeOperations();

        long costMillis = System.currentTimeMillis() - start;
        System.out.println("\r\n\r\n==Over cost " + Util.humanReadableDuration(costMillis) + "==");
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
