package com.science.gtnl.common.machine.basicMachine;

import static gregtech.api.enums.Textures.BlockIcons.*;
import static tectech.recipe.TecTechRecipeMaps.researchStationFakeRecipes;
import static tectech.recipe.TecTechRecipeMaps.researchableALRecipeList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.GTMod;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public class DebugResearchStation extends MTEBasicMachine {

    public DebugResearchStation(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            1,
            new String[] { StatCollector.translateToLocal("Tooltip_DebugResearchStation_00"),
                StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                    + EnumChatFormatting.YELLOW
                    + StatCollector.translateToLocal("gt.blockmachines.multimachine.em.research.name")
                    + EnumChatFormatting.RESET },
            1,
            1,
            TextureFactory.of(
                TextureFactory.of(OVERLAY_SIDE_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(OVERLAY_SIDE_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(OVERLAY_SIDE_SCANNER),
                TextureFactory.builder()
                    .addIcon(OVERLAY_SIDE_SCANNER_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(OVERLAY_FRONT_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(OVERLAY_FRONT_SCANNER),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_SCANNER_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(OVERLAY_TOP_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(OVERLAY_TOP_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(OVERLAY_TOP_SCANNER),
                TextureFactory.builder()
                    .addIcon(OVERLAY_TOP_SCANNER_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(OVERLAY_BOTTOM_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(OVERLAY_BOTTOM_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(OVERLAY_BOTTOM_SCANNER),
                TextureFactory.builder()
                    .addIcon(OVERLAY_BOTTOM_SCANNER_GLOW)
                    .glow()
                    .build()));
    }

    public DebugResearchStation(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures, 1, 1);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DebugResearchStation(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public long maxEUStore() {
        return 0;
    }

    @Override
    public boolean hasEnoughEnergyToCheckRecipe() {
        return true;
    }

    @Override
    public boolean drainEnergyForProcess(long aEUt) {
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        this.mProgresstime = this.mMaxProgresstime;
        if (mProgresstime >= (mMaxProgresstime - 1)) {
            if ((this.mOutputItems[0] != null) && (this.mOutputItems[0].getUnlocalizedName()
                .equals("gt.metaitem.01.32707"))) {
                GTMod.achievements.issueAchievement(
                    aBaseMetaTileEntity.getWorld()
                        .getPlayerEntityByName(aBaseMetaTileEntity.getOwnerName()),
                    "scanning");
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public int checkRecipe() {
        if (getOutputAt(0) != null) {
            this.mOutputBlocked += 1;
            return 0;
        }
        ItemStack aStack = getInputAt(0);
        ItemStack dataStick = getSpecialSlot();

        if (!GTUtility.isStackValid(aStack) || aStack.stackSize <= 0
            || !ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)
            || dataStick.stackSize <= 0) {
            this.mEUt = 0;
            this.mMaxProgresstime = 0;
            return 0;
        }

        GTRecipe fakeRecipe = null;
        for (GTRecipe ttRecipe : researchStationFakeRecipes.getAllRecipes()) {
            if (GTUtility.areStacksEqual(ttRecipe.mInputs[0], aStack, true)) {
                fakeRecipe = ttRecipe;
                break;
            }
        }

        if (fakeRecipe == null) {
            this.mEUt = 0;
            this.mMaxProgresstime = 0;
            return 0;
        }

        if (aStack.stackSize < fakeRecipe.mInputs[0].stackSize) {
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }

        GTRecipe.RecipeAssemblyLine realALRecipe = null;
        for (GTRecipe.RecipeAssemblyLine assRecipe : researchableALRecipeList) {
            if (GTUtility.areStacksEqual(assRecipe.mResearchItem, aStack, true)) {
                realALRecipe = assRecipe;
                break;
            }
        }

        if (realALRecipe == null) {
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }
        this.mOutputItems[0] = GTUtility.copyAmount(1, dataStick);
        if (!AssemblyLineUtils.setAssemblyLineRecipeOnDataStick(this.mOutputItems[0], realALRecipe)) {
            this.mOutputItems[0] = null;
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }

        getInputAt(0).stackSize -= fakeRecipe.mInputs[0].stackSize;
        getSpecialSlot().stackSize -= 1;

        this.mMaxProgresstime = 1;
        this.mEUt = 0;

        return 2;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return researchStationFakeRecipes;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        BasicUIProperties uiProperties = getUIProperties();
        addIOSlots(builder, uiProperties);
        addProgressBar(builder, uiProperties);
        builder.widget(createChargerSlot(79, 62));
        builder.widget(createFluidAutoOutputButton());
        builder.widget(createItemAutoOutputButton());
    }

    @Override
    public boolean allowPutStackValidated(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return super.allowPutStackValidated(aBaseMetaTileEntity, aIndex, side, aStack)
            && getRecipeMap().containsInput(aStack);
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 1) {
            GTUtility.doSoundAtClient(SoundResource.IC2_MACHINES_MAGNETIZER_LOOP, 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public void startProcess() {
        sendLoopStart((byte) 1);
    }
}
