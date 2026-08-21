package com.science.gtnl.common.machine.basicMachine;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.gui.modularui.GTNLBasicMachineGui;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class SmallEssentiaSmeltery extends MTEBasicMachine {

    private static final String OUTPUT_ASPECTS_KEY = "OutputAspects";
    private static final String PENDING_ASPECTS_KEY = "PendingAspects";
    private static final int BASE_DURATION_PER_ESSENTIA = 32;
    private static final int BASE_ESSENTIA_BUFFER_CAPACITY = 64;

    private static final IIconContainer FRONT_ACTIVE = Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE;
    private static final IIconContainer FRONT_ACTIVE_GLOW = Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW;
    private static final IIconContainer FRONT_INACTIVE = Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE;
    private static final IIconContainer FRONT_INACTIVE_GLOW = Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW;

    private final AspectList outputAspects = new AspectList();
    private final AspectList pendingAspects = new AspectList();

    public SmallEssentiaSmeltery(int id, String name, String nameRegional, int tier) {
        super(
            id,
            name,
            nameRegional,
            tier,
            1,
            new String[] { StatCollector.translateToLocal("Tooltip_SmallEssentiaSmeltery_00"),
                StatCollector.translateToLocal("Tooltip_SmallEssentiaSmeltery_01"),
                StatCollector.translateToLocal("Tooltip_SmallEssentiaSmeltery_02"),
                StatCollector
                    .translateToLocalFormatted("Tooltip_SmallEssentiaSmeltery_03", getEssentiaBufferCapacity(tier)) },
            1,
            0,
            null,
            null,
            createFrontTexture(FRONT_ACTIVE, FRONT_ACTIVE_GLOW),
            createFrontTexture(FRONT_INACTIVE, FRONT_INACTIVE_GLOW),
            null,
            null,
            null,
            null);
    }

    public SmallEssentiaSmeltery(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, 1, description, textures, 1, 0);
    }

    private static ITexture createFrontTexture(IIconContainer icon, IIconContainer glow) {
        return TextureFactory.of(
            TextureFactory.of(icon),
            TextureFactory.builder()
                .addIcon(glow)
                .glow()
                .build());
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SmallEssentiaSmeltery(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int checkRecipe() {
        ItemStack input = getInputAt(0);
        if (!GTUtility.isStackValid(input) || input.stackSize <= 0) return DID_NOT_FIND_RECIPE;

        AspectList aspects = getEssentia(input);
        int bufferedEssentia = outputAspects.visSize() + pendingAspects.visSize();
        if (bufferedEssentia > 0 && bufferedEssentia + aspects.visSize() > getEssentiaBufferCapacity(mTier)) {
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }

        pendingAspects.add(aspects);
        input.stackSize--;

        calculateOverclockedNess((int) TierEU.RECIPE_LV, Math.max(1, aspects.visSize() * BASE_DURATION_PER_ESSENTIA));
        return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
    }

    private AspectList getEssentia(ItemStack stack) {
        AspectList result = new AspectList();
        AspectList aspects = ThaumcraftCraftingManager.getObjectTags(stack);
        aspects = ThaumcraftCraftingManager.getBonusTags(stack, aspects);

        if (aspects != null && aspects.size() > 0 && aspects.getAspects()[0] != null) {
            result.add(aspects);
        } else {
            result.add(Aspect.ENTROPY, 1);
        }
        return result;
    }

    private static int getEssentiaBufferCapacity(int tier) {
        return BASE_ESSENTIA_BUFFER_CAPACITY << Math.max(0, tier - 1);
    }

    @Override
    public void endProcess() {
        outputAspects.add(pendingAspects);
        pendingAspects.aspects.clear();
        getBaseMetaTileEntity().markDirty();
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        super.onPostTick(baseMetaTileEntity, tick);
        if (baseMetaTileEntity.isServerSide() && outputAspects.size() > 0 && outputEssentia(baseMetaTileEntity)) {
            baseMetaTileEntity.markDirty();
            baseMetaTileEntity.markInventoryBeenModified();
        }
    }

    private boolean outputEssentia(IGregTechTileEntity baseMetaTileEntity) {
        ForgeDirection outputSide = baseMetaTileEntity.getFrontFacing();
        TileEntity tileEntity = baseMetaTileEntity.getWorld()
            .getTileEntity(
                baseMetaTileEntity.getOffsetX(outputSide, 1),
                baseMetaTileEntity.getOffsetY(outputSide, 1),
                baseMetaTileEntity.getOffsetZ(outputSide, 1));
        if (!(tileEntity instanceof IEssentiaTransport transport)) return false;

        ForgeDirection targetSide = outputSide.getOpposite();
        boolean forceMultiJarInput = tileEntity instanceof TileEntityMultiEssentiaJar
            && targetSide != ForgeDirection.UP;
        if (!forceMultiJarInput) {
            if (!transport.isConnectable(targetSide) || !transport.canInputFrom(targetSide)) return false;
            if (transport.getSuctionAmount(targetSide) <= 0) return false;
        }

        Aspect aspect = transport.getSuctionType(targetSide);
        if (aspect == null) {
            Aspect[] aspects = outputAspects.getAspectsSortedAmount();
            if (aspects.length == 0) return false;
            aspect = aspects[0];
        }

        int available = outputAspects.getAmount(aspect);
        if (available <= 0) return false;

        int accepted = forceMultiJarInput
            ? ((TileEntityMultiEssentiaJar) tileEntity).addEssentiaFromSmeltery(aspect, available, targetSide)
            : transport.addEssentia(aspect, available, targetSide);
        if (accepted <= 0) return false;
        outputAspects.remove(aspect, Math.min(accepted, available));
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        NBTTagCompound outputTag = new NBTTagCompound();
        outputAspects.writeToNBT(outputTag);
        nbt.setTag(OUTPUT_ASPECTS_KEY, outputTag);

        NBTTagCompound pendingTag = new NBTTagCompound();
        pendingAspects.writeToNBT(pendingTag);
        nbt.setTag(PENDING_ASPECTS_KEY, pendingTag);
        super.saveNBTData(nbt);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        outputAspects.aspects.clear();
        pendingAspects.aspects.clear();
        if (nbt.hasKey(OUTPUT_ASPECTS_KEY)) outputAspects.readFromNBT(nbt.getCompoundTag(OUTPUT_ASPECTS_KEY));
        if (nbt.hasKey(PENDING_ASPECTS_KEY)) pendingAspects.readFromNBT(nbt.getCompoundTag(PENDING_ASPECTS_KEY));
        sanitizeAspects(outputAspects);
        sanitizeAspects(pendingAspects);
        if (mMaxProgresstime <= 0 && pendingAspects.size() > 0) {
            outputAspects.add(pendingAspects);
            pendingAspects.aspects.clear();
        }
    }

    private void sanitizeAspects(AspectList aspects) {
        AspectList validAspects = new AspectList();
        for (Map.Entry<Aspect, Integer> entry : aspects.aspects.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0) {
                validAspects.add(entry.getKey(), entry.getValue());
            }
        }
        aspects.aspects.clear();
        aspects.add(validAspects);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new GTNLBasicMachineGui<SmallEssentiaSmeltery>(this, getUIProperties()) {

            @Override
            protected boolean supportsBottomLeftCornerFlow() {
                return false;
            }
        }.build(data, syncManager, uiSettings);
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    @Deprecated
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }
}
