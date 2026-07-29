package com.science.gtnl.common.machine.basicMachine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.modularui.StellarIrisGui;
import com.science.gtnl.utils.enums.BlockIcons;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.render.TextureFactory;

public class StellarIrisController extends MTEBasicTank {

    public StellarIrisController(int id, String name, String regionalName, int tier, ITexture... textures) {
        super(
            id,
            name,
            regionalName,
            tier,
            1,
            new String[] { StatCollector.translateToLocal("Tooltip_StellarIrisController_00") },
            textures);
    }

    public StellarIrisController(String name, int tier, int inventorySlots, String[] description,
        ITexture[][][] textures) {
        super(name, tier, inventorySlots, description, textures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new StellarIrisController(mName, mTier, mInventory.length, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isValidSlot(int index) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity tileEntity, int index, ForgeDirection side, ItemStack stack) {
        return false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity tileEntity, int index, ForgeDirection side, ItemStack stack) {
        return false;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstoneLevel) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.builder()
                .addIcon(BlockIcons.OVERLAY_ENERGY_MONITOR)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[1][0] };
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] textures) {
        return new ITexture[0][0][0];
    }

    @Override
    public boolean doesFillContainers() {
        return false;
    }

    @Override
    public boolean doesEmptyContainers() {
        return false;
    }

    @Override
    public boolean canTankBeFilled() {
        return false;
    }

    @Override
    public boolean canTankBeEmptied() {
        return false;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new StellarIrisGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity tileEntity, EntityPlayer player) {
        if (tileEntity.isClientSide()) {
            return true;
        }
        openGui(player);
        return true;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }
}
