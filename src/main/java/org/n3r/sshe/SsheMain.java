package org.n3r.sshe;

import org.apache.commons.cli.*;
import org.n3r.sshe.operation.HostOperation;
import org.n3r.sshe.util.Util;

import java.io.File;

public class SsheMain {
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = parseCommandLine(args);
        File configurationFile = new File(commandLine.getOptionValue('f', "sshe.conf"));

        long start = System.currentTimeMillis();
        SsheConf.parseConf(configurationFile);
        batchRun();
        System.out.println("\r\n");
        long costMillis = System.currentTimeMillis() - start;
        System.out.println("==Over cost " + Util.humanReadableDuration(costMillis) + "==");
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

    private static void batchRun() {
        for (SsheHost ssheHost : SsheConf.ssheHosts) {
            ssheHost.connect();
            executeOperations(ssheHost);
            ssheHost.disconnect();
        }
    }

    private static void executeOperations(SsheHost ssheHost) {
        System.out.println("\r\n");
        System.out.println("==" + ssheHost.getHostInfo() + "==");
        System.out.println("\r\n");
        HostOperation lastOperation = null;

        for (HostOperation operation : SsheConf.operations) {
            if (operation.matchSpecHost(ssheHost)) {
                operation.execute(ssheHost, lastOperation);
                lastOperation = operation;
            }
        }
    }

}
