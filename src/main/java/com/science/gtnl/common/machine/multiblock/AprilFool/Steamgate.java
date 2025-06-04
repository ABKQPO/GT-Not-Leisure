package com.science.gtnl.common.machine.multiblock.AprilFool;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.loader.BlockLoader;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;

public class Steamgate extends MTEEnhancedMultiBlockBase<Steamgate> implements ISurvivalConstructable {

    public static final String TEXTURE_OVERLAY_STEAMGATE_CONTROLLER = RESOURCE_ROOT_ID + ":"
        + "iconsets/OVERLAY_STEAMGATE_CONTROLLER";
    public static final String TEXTURE_STEAMGATE_CASING = RESOURCE_ROOT_ID + ":" + "MetaCasing/22";
    public static Textures.BlockIcons.CustomIcon OVERLAY_STEAMGATE_CONTROLLER = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_STEAMGATE_CONTROLLER);
    public static Textures.BlockIcons.CustomIcon STEAMGATE_CASING = new Textures.BlockIcons.CustomIcon(
        TEXTURE_STEAMGATE_CASING);
    private static IStructureDefinition<Steamgate> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steamgate";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SG_STRUCTURE_FILE_PATH);

    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 0;

    public Steamgate(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public Steamgate(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new Steamgate(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstoneLevel) {
        ITexture[] rTexture;
        if (side == facing) {
            rTexture = new ITexture[] { TextureFactory.builder()
                .addIcon(STEAMGATE_CASING)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_STEAMGATE_CONTROLLER)
                    .extFacing()
                    .glow()
                    .build() };

        } else {
            rTexture = new ITexture[] { TextureFactory.builder()
                .addIcon(STEAMGATE_CASING)
                .build() };
        }
        return rTexture;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public IStructureDefinition<Steamgate> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<Steamgate>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BlockLoader.MetaCasing, 21))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 22))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addInfo(StatCollector.translateToLocal("Tooltip_Steamgate_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Steamgate_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Steamgate_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Steamgate_03"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(9, 9, 1, true)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 0;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    // YOINKED all this code n im NOT giving it back

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!(aPlayer instanceof EntityPlayerMP)) return;
        if (!mMachine) return;

        ItemStack device = aPlayer.inventory.getCurrentItem();
        if (GTNLItemList.SteamgateDialingDevice.isStackEqual(device, false, true)) {

            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("type", "SteamgateLinker");
            tag.setInteger("x", aBaseMetaTileEntity.getXCoord());
            tag.setInteger("y", aBaseMetaTileEntity.getYCoord());
            tag.setInteger("z", aBaseMetaTileEntity.getZCoord());

            device.stackTagCompound = tag;
            aPlayer
                .addChatMessage(new ChatComponentTranslation(StatCollector.translateToLocal("Tooltip_Steamgate_04")));
        }
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!mMachine) return false;
        if (linkedGate == null) tryLinkDataStick(aPlayer);
        else {
            BaseMetaTileEntity gate = (BaseMetaTileEntity) linkedGate.getBaseMetaTileEntity();
            aPlayer.setPositionAndUpdate(gate.getXCoord(), gate.getYCoord() + 2, gate.getZCoord());
        }
        return true;
    }

    private void tryLinkDataStick(EntityPlayer aPlayer) {
        // Make sure the held item is a DIALING DEVICE
        ItemStack device = aPlayer.inventory.getCurrentItem();
        if (!GTNLItemList.SteamgateDialingDevice.isStackEqual(device, false, true)) {
            return;
        }

        // Make sure this DIALING DEVICE is a proper STEAMGATE LETS GO link DIALING DEVICE.
        if (!device.hasTagCompound() || !device.stackTagCompound.getString("type")
            .equals("SteamgateLinker")) {
            return;
        }

        // Now read link coordinates from the DIALING DEVICE.
        NBTTagCompound nbt = device.stackTagCompound;
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");

        // Try to link, and report the result back to the player.
        boolean result = trySetControllerFromCoord(x, y, z);
        if (result) {
            aPlayer.addChatMessage(new ChatComponentTranslation("Tooltip_Steamgate_05"));
        } else {
            aPlayer.addChatMessage(new ChatComponentTranslation("Tooltip_Steamgate_06"));
        }

    }

    Steamgate linkedGate;

    private boolean trySetControllerFromCoord(int x, int y, int z) {
        // Find the block at the requested coordinated and check if it is a STEAMGATE WOOOOOOOOO.
        var tileEntity = getBaseMetaTileEntity().getWorld()
            .getTileEntity(x, y, z);
        if (tileEntity == null) return false;
        if (!(tileEntity instanceof IGregTechTileEntity gtTileEntity)) return false;
        var metaTileEntity = gtTileEntity.getMetaTileEntity();
        if (!(metaTileEntity instanceof Steamgate)) return false;
        if (metaTileEntity == this) return false;

        // Before linking, unlink from current STEAMGATE YEAAAAAAAA
        if (linkedGate != null) {
            linkedGate.linkedGate = null;
        }
        linkedGate = (Steamgate) metaTileEntity;
        linkedGate.linkedGate = this;
        return true;
    }
}
