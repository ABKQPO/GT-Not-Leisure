package com.science.gtnl.common.machine.multiblock;

import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;

public class GodforgeModuleHooks {

    public static boolean connects(MTEBaseModule module, ForgeOfGodsData data) {
        if (module instanceof FOGAlloySmelterModule) return true;
        if (module instanceof FOGAlloyBlastSmelterModule || module instanceof FOGExtractorModule)
            return data.isUpgradeActive(ForgeOfGodsUpgrade.FDIM);
        return module instanceof FOGSolarMuonCatalystModule && data.isUpgradeActive(ForgeOfGodsUpgrade.QGPIU);
    }
}
