package com.science.gtnl;

import static com.gtnltest.ContractAssertions.finish;

import com.science.gtnl.config.ConfigMigrationContracts;

public final class ArchitectureContractTest {

    public static void main(String[] args) throws Exception {
        ArchitectureContracts.run();
        ApiSignatureContracts.run();
        PacketSideContracts.run();
        ConfigMigrationContracts.run();
        finish("Architecture");
    }
}
