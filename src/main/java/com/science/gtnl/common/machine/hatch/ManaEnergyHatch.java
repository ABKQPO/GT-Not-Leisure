package com.science.gtnl.common.machine.hatch;

import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.common.material.GTNLMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import vazkii.botania.common.block.tile.mana.TilePool;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class ManaEnergyHatch extends MTEHatchEnergy implements IAddUIWidgets {

    private static final int MANA_POOL_RADIUS = 5;
    private static final int EU_TO_MANA_RATE = 25;
    private static final FluidStack fluidMana = GTNLMaterials.FluidMana.getFluidOrGas(1);
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
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.mana_energy." + GTValues.VN[mTier].toLowerCase(Locale.ROOT) + ".name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ManaEnergyHatch(mName, mTier, mDescriptionArray, mTextures, mAmp);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isClientSide()) return;

        if (aBaseMetaTileEntity.isAllowedToWork() && aTick % 20 == 0) {
            convertEUToMana(aBaseMetaTileEntity);
            List<TilePool> pools = ManaScan.pools(aBaseMetaTileEntity, MANA_POOL_RADIUS);

            if (!pools.isEmpty()) {
                transferFluidToPools(pools);
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
        return 2000000 * this.mTier;
    }

    @Override
    public long maxAmperesIn() {
        return mAmp;
    }

    @Override
    public String[] getDescription() {
        ObjectArrayList<String> desc = new ObjectArrayList<>();
        desc.add(StatCollector.translateToLocal("gtnl.hatch.mana_energy.tooltip.0"));
        desc.add(StatCollector.translateToLocal("gtnl.hatch.mana_energy.tooltip.1"));
        desc.add(StatCollector.translateToLocalFormatted("gtnl.hatch.mana_energy.tooltip.2", EU_TO_MANA_RATE));
        desc.add(StatCollector.translateToLocalFormatted("gtnl.hatch.mana_energy.tooltip.3", getCapacity()));
        return desc.toArray(new String[] {});
    }

    @Override
    public String[] getInfoData() {
        FluidStack currentManaStack = getFillableStack();
        int currentMana = currentManaStack != null ? currentManaStack.amount : 0;
        int capacity = getCapacity();

        if (currentMana == 0) return new String[] {};
        return new String[] { IGregTechDeviceInformation.encode(
            "gtnl.hatch.mana_energy.info.stored_mana",
            EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(currentMana)
                + EnumChatFormatting.RESET
                + " / "
                + EnumChatFormatting.YELLOW
                + NumberFormatUtil.formatNumber(capacity)) };
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
                EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted(
                    "gtnl.hatch.mana_energy.info.stored_mana",
                    EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(currentMana)
                        + EnumChatFormatting.RESET
                        + " / "
                        + EnumChatFormatting.YELLOW
                        + NumberFormatUtil.formatNumber(capacity)));
        }
    }

    private void convertEUToMana(IGregTechTileEntity aBaseMetaTileEntity) {
        long storedEU = aBaseMetaTileEntity.getStoredEU();
        if (storedEU <= 0) return;

        int currentMana = getFluidAmount();
        int capacity = getCapacity();
        int availableSpace = capacity - currentMana;

        if (availableSpace <= 0) return;

        long manaToAdd = Math.min(storedEU / EU_TO_MANA_RATE, availableSpace);
        if (manaToAdd <= 0) return;

        long euToConvert = manaToAdd * EU_TO_MANA_RATE;

        aBaseMetaTileEntity.increaseStoredEnergyUnits(-euToConvert, true);
        fill(createFluidStack((int) manaToAdd), true);
    }

    private void transferFluidToPools(List<TilePool> pools) {
        int fluidAmount = getFluidAmount();

        for (TilePool pool : pools) {
            if (ManaScan.isDropMetaValid(pool)) continue;

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

    private FluidStack createFluidStack(int amount) {
        FluidStack stack = fluidMana.copy();
        stack.amount = amount;
        return stack;
    }
}
