package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamFlightModule extends SteamElevatorModule {

    public static final int POTION_FLIGHT_ID = Mods.BloodMagic.isModLoaded() ? getPotionFlightId() : Potion.jump.id;

    public SteamFlightModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 1);
    }

    public SteamFlightModule(String aName) {
        super(aName, 1);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamFlightModule(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamFlightModuleRecipeType");
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return null;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamFlightModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFlightModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFlightModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFlightModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFlightModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamFlightModule_04"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        this.lEUt = mTier * GTValues.V[5] * Math.max(recipeOcCount, 1);
        this.mEfficiency = 10000;
        this.mMaxProgresstime = 800;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (mProgresstime == 1) {
            final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
            if (tileEntity == null) {
                return super.onRunningTick(stack);
            }
            int x = tileEntity.getXCoord();
            int y = tileEntity.getYCoord();
            int z = tileEntity.getZCoord();
            World world = tileEntity.getWorld();

            List<EntityPlayer> playersInRange = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox(
                    x - getMachineEffectRange(),
                    y - getMachineEffectRange(),
                    z - getMachineEffectRange(),
                    x + getMachineEffectRange(),
                    y + getMachineEffectRange(),
                    z + getMachineEffectRange()));
            for (EntityPlayer player : playersInRange) {
                double distance = player.getDistance(x, y, z);
                if (distance <= getMachineEffectRange()) {
                    player.addPotionEffect(new PotionEffect(POTION_FLIGHT_ID, 1000, 1));
                }
            }
        }
        return super.onRunningTick(stack);
    }

    @Override
    public int getMachineEffectRange() {
        return 64 * Math.max(recipeOcCount, 1);
    }

    @Optional.Method(modid = "AWWayofTime")
    public static int getPotionFlightId() {
        return AlchemicalWizardry.customPotionFlightID;
    }
}
