package com.science.gtnl.common.machine.hatch;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtil;
import gregtech.common.tileentities.machines.multi.nanochip.factory.VacuumFactoryElement;
import gregtech.common.tileentities.machines.multi.nanochip.factory.VacuumFactoryGrid;
import gregtech.common.tileentities.machines.multi.nanochip.hatches.MTEHatchVacuumConveyorOutput;
import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitComponentPacket;

public class WirelessVacuumConveyorOutputHatch extends MTEHatchVacuumConveyorOutput {

    public static final String DATA_STICK_TYPE = "gtnlWirelessVacuumConveyorOutput";
    public static final String LEGACY_NBT_DIMENSION = "dimension";
    private static final String NBT_LINK = "wirelessVacuumConveyorLink";
    private static final String NBT_X = "x";
    private static final String NBT_Y = "y";
    private static final String NBT_Z = "z";

    private int targetX;
    private int targetY;
    private int targetZ;
    private boolean targetSet;

    public WirelessVacuumConveyorOutputHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public WirelessVacuumConveyorOutputHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new WirelessVacuumConveyorOutputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[] { translateToLocal("Tooltip_WirelessVacuumConveyorOutputHatch_00"),
            translateToLocal("Tooltip_WirelessVacuumConveyorOutputHatch_01"),
            translateToLocal("Tooltip_WirelessVacuumConveyorOutputHatch_02") };
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!(aPlayer instanceof EntityPlayerMP)) return;

        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) return;

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", DATA_STICK_TYPE);
        tag.setInteger(NBT_X, aBaseMetaTileEntity.getXCoord());
        tag.setInteger(NBT_Y, aBaseMetaTileEntity.getYCoord());
        tag.setInteger(NBT_Z, aBaseMetaTileEntity.getZCoord());
        dataStick.stackTagCompound = tag;
        dataStick.setStackDisplayName(StatCollector.translateToLocal("DataStick_WirelessVacuumConveyor"));
        aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.saved"));
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (!aBaseMetaTileEntity.isServerSide() || aTick % 20 != VACUUM_MOVE_TICK) return;

        WirelessVacuumConveyorInputHatch input = getTarget();
        if (contents == null || input == null) {
            aBaseMetaTileEntity.setActive(false);
            return;
        }

        CircuitComponentPacket packet = contents;
        contents = null;
        input.unifyPacket(packet);
        markDirty();
        aBaseMetaTileEntity.setActive(false);
    }

    @Override
    public void unifyPacket(CircuitComponentPacket packet) {
        super.unifyPacket(packet);
        markDirty();
    }

    @Override
    public boolean canConnectOnSide(ForgeDirection side) {
        return false;
    }

    @Override
    public void getNeighbours(Collection<VacuumFactoryElement> neighbours) {}

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        VacuumFactoryGrid.INSTANCE.removeElement(this);
    }

    @Override
    public void onColorChangeServer(byte aColor) {
        super.onColorChangeServer(aColor);
        VacuumFactoryGrid.INSTANCE.removeElement(this);
    }

    @Override
    public void onFacingChange() {
        super.onFacingChange();
        VacuumFactoryGrid.INSTANCE.removeElement(this);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (!targetSet) return;

        NBTTagCompound link = new NBTTagCompound();
        link.setInteger(NBT_X, targetX);
        link.setInteger(NBT_Y, targetY);
        link.setInteger(NBT_Z, targetZ);
        aNBT.setTag(NBT_LINK, link);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        targetSet = false;
        if (!aNBT.hasKey(NBT_LINK)) return;

        NBTTagCompound link = aNBT.getCompoundTag(NBT_LINK);
        if (link.hasKey(LEGACY_NBT_DIMENSION)
            && link.getInteger(LEGACY_NBT_DIMENSION) != getBaseMetaTileEntity().getWorld().provider.dimensionId) {
            return;
        }
        targetX = link.getInteger(NBT_X);
        targetY = link.getInteger(NBT_Y);
        targetZ = link.getInteger(NBT_Z);
        targetSet = true;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (targetSet) {
            info.add(
                StatCollector
                    .translateToLocalFormatted("Info_WirelessVacuumConveyorOutputHatch_00", targetX, targetY, targetZ));
        }
        return info.toArray(new String[0]);
    }

    public boolean trySetTarget(int x, int y, int z) {
        World world = getBaseMetaTileEntity().getWorld();
        if (findTarget(world, x, y, z) == null) return false;

        targetX = x;
        targetY = y;
        targetZ = z;
        targetSet = true;
        markDirty();
        return true;
    }

    private WirelessVacuumConveyorInputHatch getTarget() {
        if (!targetSet) return null;
        return findTarget(getBaseMetaTileEntity().getWorld(), targetX, targetY, targetZ);
    }

    private WirelessVacuumConveyorInputHatch findTarget(World world, int x, int y, int z) {
        TileEntity tile = GTUtil.getTileEntity(world, x, y, z, false);
        if (!(tile instanceof IGregTechTileEntity gregTechTile) || gregTechTile.isDead()) return null;
        if (gregTechTile.getMetaTileEntity() instanceof WirelessVacuumConveyorInputHatch input) return input;
        return null;
    }
}
