package com.science.gtnl.common.machine.multiMachineBase;

import net.minecraft.entity.player.EntityPlayer;

import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;

import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.WirelessComputationPacket;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public abstract class TTMultiMachineBase extends TTMultiblockBase {

    public TTMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TTMultiMachineBase(String aName) {
        super(aName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    public void switchWirelessComputation(EntityPlayer player, boolean enabled) {
        if (enabled) {
            GTUtility.sendChatTrans(player, "gtnl.machine.message.wireless_mode.enabled");
            WirelessComputationPacket.enableWirelessNetWork(getBaseMetaTileEntity());
        } else {
            GTUtility.sendChatTrans(player, "gtnl.machine.message.wireless_mode.disabled");
            WirelessComputationPacket.disableWirelessNetWork(getBaseMetaTileEntity());
        }
    }
}
