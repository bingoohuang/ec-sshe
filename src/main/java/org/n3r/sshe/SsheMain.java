package org.n3r.sshe;

import org.n3r.sshe.operation.HostOperation;

public class SsheMain {
    public static void main(String[] args) throws Exception {
        SsheConf.parseConf();
        batchRun();
        System.out.println();
        System.out.println();
        System.out.println("==Over==");
    }

    private static void batchRun() {
        for (SsheHost ssheHost : SsheConf.ssheHosts) {
            ssheHost.connect();
            executeOperations(ssheHost);
            ssheHost.disconnect();
        }
    }

    private static void executeOperations(SsheHost ssheHost) {
        System.out.println();
        System.out.println();
        System.out.println("==" + ssheHost.getHostInfo() + "==");
        System.out.println();
        System.out.println();

        for (HostOperation operation : SsheConf.operations) {
            if (operation.matchSpecHost(ssheHost)) {
                operation.execute(ssheHost);
            }
        }
    }

}
