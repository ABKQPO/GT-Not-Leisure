package com.science.gtnl.common.machine.hatch;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.materials.MaterialPool;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.util.GTUtility;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaEnergyHatch extends MTEHatchEnergy implements IAddUIWidgets {

    private static final int MANA_POOL_RADIUS = 5;
    private static final int EU_TO_MANA_RATE = 250;
    private static final FluidStack fluidMana = MaterialPool.FluidMana.getFluidOrGas(1);
    private static int mAmp;

    public ManaEnergyHatch(int aID, String aName, String aNameRegional, int aTier, int aAmp) {
        super(aID, aName, aNameRegional, aTier);
        mAmp = aAmp;
    }

    public ManaEnergyHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures, int aAmp) {
        super(aName, aTier, aDescription, aTextures);
        mAmp = aAmp;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ManaEnergyHatch(mName, mTier, mDescriptionArray, mTextures, mAmp);
    }

    @Override
    public String[] getDescription() {

        ArrayList<String> desc = new ArrayList<>();

        desc.add(StatCollector.translateToLocal("Tooltip_ManaEnergyHatch_00"));
        desc.add(StatCollector.translateToLocal("Tooltip_ManaEnergyHatch_01"));
        desc.add(StatCollector.translateToLocal("Tooltip_ManaEnergyHatch_02"));
        desc.add(StatCollector.translateToLocal("Tooltip_ManaEnergyHatch_03") + getCapacity() + "L");

        return desc.toArray(new String[] {});
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isClientSide()) return;

        if (aBaseMetaTileEntity.isAllowedToWork() && aTick % 20 == 0) {
            convertEUToMana(aBaseMetaTileEntity);
            List<TilePool> pools = findManaPoolsInRange(aBaseMetaTileEntity);

            if (!pools.isEmpty()) {
                transferFluidToPools(pools);
            }

        }
    }

    private void convertEUToMana(IGregTechTileEntity aBaseMetaTileEntity) {
        long storedEU = aBaseMetaTileEntity.getStoredEU();
        if (storedEU <= 0) return;

        int currentMana = getFluidAmount();
        int capacity = getCapacity();
        int availableSpace = capacity - currentMana;

        if (availableSpace <= 0) return;

        long euToConvert = Math.min(storedEU, availableSpace / EU_TO_MANA_RATE);
        if (euToConvert <= 0) return;

        int manaToAdd = (int) (euToConvert * EU_TO_MANA_RATE);

        aBaseMetaTileEntity.increaseStoredEnergyUnits(euToConvert, true);
        fill(createFluidStack(manaToAdd), true);
    }

    private FluidStack createFluidStack(int amount) {
        FluidStack stack = fluidMana.copy();
        stack.amount = amount;
        return stack;
    }

    private void transferFluidToPools(List<TilePool> pools) {
        int fluidAmount = getFluidAmount();

        for (TilePool pool : pools) {
            if (isDropMetaValid(pool)) continue;

            int availableSpace = pool.getAvailableSpaceForMana();
            if (availableSpace <= 0) continue;

            int fluidToTransfer = Math.min(fluidAmount, availableSpace);
            if (fluidToTransfer <= 0) continue;

            FluidStack drainedStack = drain(fluidToTransfer, true);
            if (drainedStack != null && drainedStack.amount > 0) {
                pool.recieveMana(drainedStack.amount);
            }
        }
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return true;
    }

    @Override
    public int getCapacity() {
        return 1000000 * this.mTier;
    }

    private boolean isDropMetaValid(TilePool pool) {
        int meta = pool.getBlockMetadata();
        return meta == 1;
    }

    private List<TilePool> findManaPoolsInRange(IGregTechTileEntity aBaseMetaTileEntity) {
        World world = aBaseMetaTileEntity.getWorld();
        int x = aBaseMetaTileEntity.getXCoord();
        int y = aBaseMetaTileEntity.getYCoord();
        int z = aBaseMetaTileEntity.getZCoord();

        List<TilePool> pools = new ArrayList<>();

        for (int dx = -MANA_POOL_RADIUS; dx <= MANA_POOL_RADIUS; dx++) {
            for (int dy = -MANA_POOL_RADIUS; dy <= MANA_POOL_RADIUS; dy++) {
                for (int dz = -MANA_POOL_RADIUS; dz <= MANA_POOL_RADIUS; dz++) {
                    TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
                    if (te instanceof TilePool pool) {
                        pools.add(pool);
                    }
                }
            }
        }
        return pools;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        FluidStack currentManaStack = getFillableStack();
        if (currentManaStack != null && currentManaStack.amount > 0) {
            tag.setInteger("currentMana", currentManaStack.amount);
            tag.setInteger("capacity", getCapacity());
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("currentMana")) {
            int currentMana = tag.getInteger("currentMana");
            int capacity = tag.getInteger("capacity");
            currentTip.add(
                EnumChatFormatting.BLUE + StatCollector.translateToLocal("Info_ManaEnergyHatch_00")
                    + EnumChatFormatting.RESET
                    + EnumChatFormatting.GREEN
                    + GTUtility.formatNumbers(currentMana)
                    + EnumChatFormatting.RESET
                    + " / "
                    + EnumChatFormatting.YELLOW
                    + GTUtility.formatNumbers(capacity));
        }
    }

    @Override
    public String[] getInfoData() {
        FluidStack currentManaStack = getFillableStack();
        int currentMana = currentManaStack != null ? currentManaStack.amount : 0;
        int capacity = getCapacity();

        if (currentMana != 0) {
            return new String[] { EnumChatFormatting.BLUE + StatCollector.translateToLocal("Info_ManaEnergyHatch_00")
                + EnumChatFormatting.RESET
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(currentMana)
                + EnumChatFormatting.RESET
                + " / "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(capacity) };
        }

        return new String[] {};
    }

    @Override
    public long maxAmperesIn() {
        return mAmp;
    }
}
