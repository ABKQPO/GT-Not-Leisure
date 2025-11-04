package com.science.gtnl.common.packet;

import com.science.gtnl.api.mixinHelper.LinkedEyeOfHarmonyUnit;
import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.HighPerformanceComputationArray;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientSyncLinkedUnitsHandler {

    public static void apply(int x, int y, int z, List<LinkedEyeOfHarmonyUnit> units) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world != null) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof IGregTechTileEntity gtTE
                && gtTE.getMetaTileEntity() instanceof EyeOfHarmonyInjector injector) {
                injector.mLinkedUnits = units;
            }
        }
    }
}
