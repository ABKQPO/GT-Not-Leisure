package com.science.gtnl;

import static com.gtnltest.ContractAssertions.finish;

import com.science.gtnl.common.wireless.WirelessAutoConnectContracts;
import com.science.gtnl.common.wireless.WirelessCardViewContracts;
import com.science.gtnl.common.wireless.WirelessFrequencyContracts;
import com.science.gtnl.common.wireless.WirelessMatterManipulatorContracts;

public final class WirelessContractTest {

    public static void main(String[] args) throws Exception {
        WirelessMatterManipulatorContracts.run();
        WirelessAutoConnectContracts.run();
        WirelessFrequencyContracts.run();
        WirelessCardViewContracts.run();
        finish("Wireless");
    }
}
