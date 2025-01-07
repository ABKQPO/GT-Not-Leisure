package com.science.gtnl.mixin.MultiBlockStructure.PrimitiveBlastFurnace;

import gregtech.GTMod;
import gregtech.api.interfaces.ISecondaryDescribable;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEBrickedBlastFurnace;
import gregtech.common.tileentities.machines.multi.MTEPrimitiveBlastFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MTEBrickedBlastFurnace.class, remap = false)
public abstract class MTEBrickedBlastFurnaceMixin extends MTEPrimitiveBlastFurnace implements ISecondaryDescribable {

    public MTEBrickedBlastFurnaceMixin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Shadow
    private MultiblockTooltipBuilder tooltipBuilder;

    /**
     * @author GT-NOT-Leisure
     * @reason 修改土高炉机器描述
     */
    @Overwrite
    protected MultiblockTooltipBuilder getTooltip(){
        if (tooltipBuilder == null) {
            tooltipBuilder = new MultiblockTooltipBuilder();
            tooltipBuilder.addMachineType("Blast Furnace")
                .addInfo("Usable for Steel and general Pyrometallurgy")
                .addInfo("Has a useful interface, unlike other gregtech multis")
                .addPollutionAmount(GTMod.gregtechproxy.mPollutionPrimitveBlastFurnacePerSecond)
                .beginStructureBlock(3, 4, 3, true)
                .addController("Front center")
                .addOtherStructurePart("Firebricks", "Everything except the controller")
                .addStructureInfo("The top block is also empty")
                .addStructureInfo("You can share the walls of GT multis, so")
                .addStructureInfo("each additional one costs less, up to 4")
                .toolTipFinisher();
        }
        return tooltipBuilder;
    }
}
