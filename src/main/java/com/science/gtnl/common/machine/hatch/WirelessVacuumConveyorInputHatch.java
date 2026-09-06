package com.science.gtnl.common.machine.hatch;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.MutablePair;

import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtil;
import gregtech.common.tileentities.machines.multi.nanochip.factory.VacuumFactoryElement;
import gregtech.common.tileentities.machines.multi.nanochip.factory.VacuumFactoryGrid;
import gregtech.common.tileentities.machines.multi.nanochip.hatches.MTEHatchVacuumConveyorInput;
import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitComponent;
import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitComponentPacket;

public class WirelessVacuumConveyorInputHatch extends MTEHatchVacuumConveyorInput {

    public Map<CircuitComponent, List<MutablePair<String, Long>>> persistedContents = Map.of();
    private boolean persistedContentsInitialized;

    public WirelessVacuumConveyorInputHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public WirelessVacuumConveyorInputHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new WirelessVacuumConveyorInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[] { translateToLocal("Tooltip_WirelessVacuumConveyorInputHatch_00"),
            translateToLocal("Tooltip_WirelessVacuumConveyorInputHatch_01"),
            translateToLocal("Tooltip_WirelessVacuumConveyorInputHatch_02") };
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && aTick % 20 == VACUUM_MOVE_TICK) {
            if (clearEmptyContents() || hasContentsChanged()) {
                markDirty();
            }
            aBaseMetaTileEntity.setActive(contents != null);
        }
    }

    @Override
    public void unifyPacket(CircuitComponentPacket packet) {
        super.unifyPacket(packet);
        markDirty();
    }

    @Override
    public int tryConsume(ItemStack stack, boolean withName) {
        int consumed = super.tryConsume(stack, withName);
        if (consumed > 0 || clearEmptyContents()) {
            markDirty();
        }
        return consumed;
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

    private boolean hasContentsChanged() {
        Map<CircuitComponent, List<MutablePair<String, Long>>> currentContents = contents == null ? Map.of()
            : contents.getComponents();
        if (!persistedContentsInitialized) {
            persistedContents = new HashMap<>(currentContents);
            persistedContentsInitialized = true;
            return false;
        }
        if (persistedContents.equals(currentContents)) return false;

        persistedContents = new HashMap<>(currentContents);
        return true;
    }

    private boolean clearEmptyContents() {
        if (contents == null || !contents.getComponents()
            .isEmpty()) return false;

        contents = null;
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aPlayer instanceof EntityPlayerMP && tryLinkDataStick(aBaseMetaTileEntity, aPlayer)) return true;
        openGui(aPlayer);
        return true;
    }

    private boolean tryLinkDataStick(IGregTechTileEntity inputBase, EntityPlayer player) {
        ItemStack dataStick = player.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true) || dataStick.stackTagCompound == null) {
            return false;
        }
        NBTTagCompound tag = dataStick.stackTagCompound;
        if (!WirelessVacuumConveyorOutputHatch.DATA_STICK_TYPE.equals(tag.getString("type"))) return false;

        World world = inputBase.getWorld();
        if (tag.hasKey(WirelessVacuumConveyorOutputHatch.LEGACY_NBT_DIMENSION)
            && tag.getInteger(WirelessVacuumConveyorOutputHatch.LEGACY_NBT_DIMENSION) != world.provider.dimensionId) {
            player.addChatMessage(new ChatComponentTranslation("Chat_WirelessVacuumConveyor_01"));
            return true;
        }

        TileEntity tile = GTUtil
            .getTileEntity(world, tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), false);
        if (!(tile instanceof IGregTechTileEntity gregTechTile) || gregTechTile.isDead()
            || !(gregTechTile.getMetaTileEntity() instanceof WirelessVacuumConveyorOutputHatch output)) {
            player.addChatMessage(new ChatComponentTranslation("Chat_WirelessVacuumConveyor_01"));
            return true;
        }

        boolean linked = output.trySetTarget(inputBase.getXCoord(), inputBase.getYCoord(), inputBase.getZCoord());
        player.addChatMessage(
            new ChatComponentTranslation(linked ? "Chat_WirelessVacuumConveyor_00" : "Chat_WirelessVacuumConveyor_01"));
        return true;
    }
}
