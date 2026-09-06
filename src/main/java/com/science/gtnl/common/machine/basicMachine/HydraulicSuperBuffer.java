package com.science.gtnl.common.machine.basicMachine;

import static gregtech.api.enums.Textures.BlockIcons.ARROW_DOWN;
import static gregtech.api.enums.Textures.BlockIcons.ARROW_DOWN_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.ARROW_LEFT;
import static gregtech.api.enums.Textures.BlockIcons.ARROW_LEFT_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.ARROW_RIGHT;
import static gregtech.api.enums.Textures.BlockIcons.ARROW_RIGHT_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.ARROW_UP;
import static gregtech.api.enums.Textures.BlockIcons.ARROW_UP_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.AUTOMATION_SUPERBUFFER;
import static gregtech.api.enums.Textures.BlockIcons.AUTOMATION_SUPERBUFFER_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_BRONZE_BOTTOM;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_BRONZE_SIDE;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_BRONZE_TOP;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_PIPE_OUT;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;

import gregtech.api.enums.Dyes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBuffer;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.render.TextureFactory;
import gregtech.common.gui.modularui.singleblock.base.MTEBufferBaseGui;

public class HydraulicSuperBuffer extends MTEBuffer {

    public HydraulicSuperBuffer(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            257,
            new String[] { StatCollector.translateToLocal("Tooltip_HydraulicSuperBuffer_00"),
                StatCollector.translateToLocal("Tooltip_HydraulicSuperBuffer_01"),
                StatCollector.translateToLocal("Tooltip_HydraulicSuperBuffer_02"),
                StatCollector.translateToLocal("Tooltip_HydraulicSuperBuffer_03"),
                StatCollector.translateToLocal("Tooltip_HydraulicSuperBuffer_04"),
                StatCollector.translateToLocal("Tooltip_HydraulicSuperBuffer_05"),
                StatCollector.translateToLocal("Tooltip_HydraulicSuperBuffer_06") });
    }

    public HydraulicSuperBuffer(String aName, int aTier, int aInvSlotCount, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HydraulicSuperBuffer(
            this.mName,
            this.mTier,
            this.mInventory.length,
            this.mDescriptionArray,
            this.mTextures);
    }

    @Override
    public ITexture getOverlayIcon() {
        return TextureFactory.of(
            TextureFactory.of(AUTOMATION_SUPERBUFFER),
            TextureFactory.builder()
                .addIcon(AUTOMATION_SUPERBUFFER_GLOW)
                .glow()
                .build());
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[ForgeDirection.VALID_DIRECTIONS.length][17][];
        ITexture tIcon = getOverlayIcon();
        ITexture tOut = TextureFactory.of(OVERLAY_PIPE_OUT);
        ITexture tUp = TextureFactory.of(
            TextureFactory.of(ARROW_UP),
            TextureFactory.builder()
                .addIcon(ARROW_UP_GLOW)
                .glow()
                .build());
        ITexture tDown = TextureFactory.of(
            TextureFactory.of(ARROW_DOWN),
            TextureFactory.builder()
                .addIcon(ARROW_DOWN_GLOW)
                .glow()
                .build());
        ITexture tLeft = TextureFactory.of(
            TextureFactory.of(ARROW_LEFT),
            TextureFactory.builder()
                .addIcon(ARROW_LEFT_GLOW)
                .glow()
                .build());
        ITexture tRight = TextureFactory.of(
            TextureFactory.of(ARROW_RIGHT),
            TextureFactory.builder()
                .addIcon(ARROW_RIGHT_GLOW)
                .glow()
                .build());
        for (int i = 0; i < rTextures[0].length; i++) {
            ITexture tCasing = i == 0
                ? TextureFactory
                    .of(MACHINE_BRONZE_BOTTOM, MACHINE_BRONZE_TOP, MACHINE_BRONZE_SIDE, Dyes.MACHINE_METAL.getRGBA())
                : TextureFactory.of(
                    MACHINE_BRONZE_BOTTOM,
                    MACHINE_BRONZE_TOP,
                    MACHINE_BRONZE_SIDE,
                    Dyes.get(i - 1)
                        .getRGBA());
            rTextures[0][i] = new ITexture[] { tCasing, tOut };
            rTextures[1][i] = new ITexture[] { tCasing, tRight, tIcon };
            rTextures[2][i] = new ITexture[] { tCasing, tDown, tIcon };
            rTextures[3][i] = new ITexture[] { tCasing, tLeft, tIcon };
            rTextures[4][i] = new ITexture[] { tCasing, tUp, tIcon };
            rTextures[5][i] = new ITexture[] { tCasing, tIcon };
        }
        return rTextures;
    }

    @Override
    protected void moveItems(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        moveItems(aBaseMetaTileEntity, aTimer, 16);
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return new MTEBufferBaseGui<>(this) {

            @Override
            protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
                return super.createContentSection(panel, syncManager).child(
                    GTGuiTextures.PICTURE_SUPER_BUFFER.asWidget()
                        .size(54)
                        .horizontalCenter());
            }

            @Override
            protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
                return super.createBottomLeftCornerFlow(panel, syncManager).child(
                    GTGuiTextures.PICTURE_ARROW_22_RED.asWidget()
                        .size(50, 22)
                        .marginLeft(11));
            }

            @Override
            protected int getBasePanelHeight() {
                return super.getBasePanelHeight() + 4;
            }

            @Override
            protected boolean supportsEmitEnergy() {
                return false;
            }
        }.build(guiData, syncManager, uiSettings);
    }

    @Override
    public boolean isEnetInput() {
        return false;
    }

    @Override
    public boolean isEnetOutput() {
        return false;
    }

    @Override
    public long getMinimumStoredEU() {
        return 0;
    }

    @Override
    public long maxEUStore() {
        return 0;
    }

    @Override
    public long maxEUInput() {
        return 0;
    }

    @Override
    public long maxEUOutput() {
        return 0;
    }

    @Override
    public long maxAmperesIn() {
        return 0;
    }

    @Override
    public long maxAmperesOut() {
        return 0;
    }
}
