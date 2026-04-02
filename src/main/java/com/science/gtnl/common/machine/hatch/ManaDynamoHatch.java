package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.GTValues.V;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.material.GTNLMaterials;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.util.GTUtility;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import vazkii.botania.api.subtile.ManaHelper;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.block.tile.TileSpecialFlower;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaDynamoHatch extends MTEHatchDynamo implements IAddUIWidgets {

    private boolean isLiquidizerMode = true;
    private static final int MANA_POOL_RADIUS = 2;
    private static final int MANA_FLOWER_RADIUS = 6;
    private static final int MANA_TO_EU_RATE = 200;
    private static final FluidStack fluidMana = GTNLMaterials.FluidMana.getFluidOrGas(1);
    private static int mAmp;

    public ManaDynamoHatch(int aID, String aName, String aNameRegional, int aTier, int aAmp) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            new String[] { StatCollector.translateToLocal("Tooltip_ManaDynamoHatch_00"),
                StatCollector.translateToLocal("Tooltip_ManaDynamoHatch_01"),
                StatCollector.translateToLocal("Tooltip_ManaDynamoHatch_02"), "",
                StatCollector.translateToLocal("Tooltip_ManaDynamoHatch_04") });
        mDescriptionArray[3] = StatCollector.translateToLocal("Tooltip_ManaDynamoHatch_03") + getCapacity() + "L";
        mAmp = aAmp;
    }

    public ManaDynamoHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures, int aAmp) {
        super(aName, aTier, aDescription, aTextures);
        mDescriptionArray[3] = StatCollector.translateToLocal("Tooltip_ManaDynamoHatch_03") + getCapacity() + "L";
        mAmp = aAmp;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ManaDynamoHatch(mName, mTier, mDescriptionArray, mTextures, mAmp);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("isLiquidizerMode", isLiquidizerMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        isLiquidizerMode = aNBT.getBoolean("isLiquidizerMode");
    }

    @Override
    public int getCapacity() {
        return 1000000 * this.mTier;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isClientSide()) return;

        if (aBaseMetaTileEntity.isAllowedToWork() && aTick % 20 == 0) {
            List<TilePool> pools = findManaPoolsInRange(aBaseMetaTileEntity);
            List<SubTileGenerating> flowers = findGeneratingFlowersInRange(aBaseMetaTileEntity);
            if (!flowers.isEmpty()) {
                processFlowerManaTransfer(flowers);
            }

            if (!pools.isEmpty()) {
                if (isLiquidizerMode) {
                    for (TilePool pool : pools) {
                        if (isDropMetaValid(pool)) {
                            fillChamberToMax();
                        }
                    }
                    transferManaToChamber(pools);
                } else {
                    transferFluidToPools(pools);
                }
            }

            FluidStack currentManaStack = getFillableStack();
            if (currentManaStack != null && currentManaStack.amount > 0) {
                long currentMana = currentManaStack.amount;

                long maxEUStore = maxEUStore();
                long currentEU = aBaseMetaTileEntity.getUniversalEnergyStored();
                long availableSpace = maxEUStore - currentEU;

                if (availableSpace > 0) {
                    long euToGenerate = Math.min(currentMana * MANA_TO_EU_RATE, availableSpace);
                    long manaToConsume = euToGenerate / MANA_TO_EU_RATE;
                    drain((int) manaToConsume, true);
                    aBaseMetaTileEntity.increaseStoredEnergyUnits(euToGenerate, true);
                }
            }
        }
    }

    private void fillChamberToMax() {
        int targetMana = getCapacity();
        FluidStack currentMana = getFillableStack();
        int currentAmount = currentMana != null ? currentMana.amount : 0;

        if (currentAmount < targetMana) {
            int manaToAdd = targetMana - currentAmount;
            FluidStack manaStack = new FluidStack(fluidMana, manaToAdd);
            fill(manaStack, true);
        }
    }

    private void transferManaToChamber(List<TilePool> pools) {
        FluidStack currentMana = getFillableStack();
        int currentAmount = currentMana != null ? currentMana.amount : 0;
        int capacity = getCapacity();

        for (TilePool pool : pools) {
            if (isDropMetaValid(pool)) continue;

            int poolMana = pool.getCurrentMana();
            if (poolMana <= 0) continue;

            int manaToTransfer = Math.min(poolMana, capacity - currentAmount);
            if (manaToTransfer <= 0) continue;

            FluidStack manaStack = new FluidStack(fluidMana, manaToTransfer);
            fill(manaStack, true);
            pool.recieveMana(-manaToTransfer);
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

    private void transferFluidToPools(List<TilePool> pools) {
        FluidStack currentMana = getFillableStack();
        int currentAmount = currentMana != null ? currentMana.amount : 0;

        for (TilePool pool : pools) {
            if (isDropMetaValid(pool)) continue;

            int availableSpace = pool.getAvailableSpaceForMana();

            if (availableSpace <= 0) continue;

            int manaToTransfer = Math.min(currentAmount, availableSpace);
            if (manaToTransfer <= 0) continue;

            drain(manaToTransfer, true);
            pool.recieveMana(manaToTransfer);
        }
    }

    private void processFlowerManaTransfer(List<SubTileGenerating> flowers) {
        FluidStack currentMana = getFillableStack();
        int currentAmount = currentMana != null ? currentMana.amount : 0;
        int capacity = getCapacity();

        for (SubTileGenerating flower : flowers) {
            int flowerMana = ManaHelper.getMana(flower);
            if (flowerMana <= 0) continue;

            int transferAmount = Math.min(flowerMana, capacity - currentAmount);
            if (transferAmount <= 0) continue;

            ManaHelper.setMana(flower, flowerMana - transferAmount);

            FluidStack manaStack = new FluidStack(fluidMana, transferAmount);
            fill(manaStack, true);
            currentAmount += transferAmount;

            TileEntity supertile = ManaHelper.getSupertile(flower);
            if (supertile != null) {
                flower.sync();
            }

            if (currentAmount >= capacity) break;
        }
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

    private List<SubTileGenerating> findGeneratingFlowersInRange(IGregTechTileEntity aBaseMetaTileEntity) {
        World world = aBaseMetaTileEntity.getWorld();
        int x = aBaseMetaTileEntity.getXCoord();
        int y = aBaseMetaTileEntity.getYCoord();
        int z = aBaseMetaTileEntity.getZCoord();

        List<SubTileGenerating> flowers = new ArrayList<>();

        for (int dx = -MANA_FLOWER_RADIUS; dx <= MANA_FLOWER_RADIUS; dx++) {
            for (int dy = -MANA_FLOWER_RADIUS; dy <= MANA_FLOWER_RADIUS; dy++) {
                for (int dz = -MANA_FLOWER_RADIUS; dz <= MANA_FLOWER_RADIUS; dz++) {
                    TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
                    if (te instanceof TileEntity) {
                        if (te instanceof TileSpecialFlower flowerTile) {
                            SubTileEntity subTile = flowerTile.getSubTile();
                            if (subTile instanceof SubTileGenerating) {
                                flowers.add((SubTileGenerating) subTile);
                            }
                        }
                    }
                }
            }
        }
        return flowers;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        isLiquidizerMode = !isLiquidizerMode;
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal("Mode_ManaDynamoHatch_0" + (isLiquidizerMode ? 1 : 0)));
    }

    @Override
    public String[] getInfoData() {
        FluidStack currentManaStack = getFillableStack();
        int currentMana = currentManaStack != null ? currentManaStack.amount : 0;
        int capacity = getCapacity();

        if (currentMana != 0) {
            return new String[] { EnumChatFormatting.BLUE + StatCollector.translateToLocal("Info_ManaDynamoHatch_00")
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
                EnumChatFormatting.BLUE + StatCollector.translateToLocal("Info_ManaDynamoHatch_00")
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
    public long maxEUStore() {
        return 512L + V[mTier + 1] * 16L;
    }

    @Override
    public long maxAmperesOut() {
        return mAmp;
    }
}
