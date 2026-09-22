package com.science.gtnl.common.machine.basicMachine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.modularui.DebugResearchStationGui;

import gregtech.GTMod;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.item.PhantomSingleSlotItemStackHandler;
import tectech.recipe.TecTechRecipeMaps;

public class DebugResearchStation extends MTEBasicMachine {

    private final ItemStack[] researchOutputFilter = new ItemStack[1];
    private final IItemHandlerModifiable researchOutputFilterInventory = new PhantomSingleSlotItemStackHandler(
        () -> researchOutputFilter[0],
        this::setResearchOutputFilter);

    public DebugResearchStation(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            1,
            new String[] { StatCollector.translateToLocal("gtnl.machine.debug_research_station.tooltip.0"),
                StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                    + EnumChatFormatting.YELLOW
                    + StatCollector.translateToLocal("gt.blockmachines.multimachine.em.research.name")
                    + EnumChatFormatting.RESET },
            1,
            1,
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_SIDE_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_SIDE_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_SIDE_SCANNER),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_SIDE_SCANNER_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_FRONT_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_FRONT_SCANNER),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_SCANNER_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_TOP_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_TOP_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_TOP_SCANNER),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_TOP_SCANNER_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_BOTTOM_SCANNER_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_BOTTOM_SCANNER_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.OVERLAY_BOTTOM_SCANNER),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_BOTTOM_SCANNER_GLOW)
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
        for (GTRecipe ttRecipe : TecTechRecipeMaps.researchStationFakeRecipes.getAllRecipes()) {
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
        ItemStack filteredOutput = researchOutputFilter[0];
        for (GTRecipe.RecipeAssemblyLine assRecipe : TecTechRecipeMaps.researchableALRecipeList) {
            if (GTUtility.areStacksEqual(assRecipe.mResearchItem, aStack, true)
                && (filteredOutput == null || GTUtility.areStacksEqual(assRecipe.mOutput, filteredOutput, true))) {
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
        return TecTechRecipeMaps.researchStationFakeRecipes;
    }

    public IItemHandlerModifiable getResearchOutputFilterInventory() {
        return researchOutputFilterInventory;
    }

    private void setResearchOutputFilter(ItemStack filter) {
        researchOutputFilter[0] = filter == null ? null : GTUtility.copyAmount(1, filter);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (researchOutputFilter[0] == null) return;

        NBTTagCompound filterTag = new NBTTagCompound();
        researchOutputFilter[0].writeToNBT(filterTag);
        aNBT.setTag("researchOutputFilter", filterTag);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        setResearchOutputFilter(
            aNBT.hasKey("researchOutputFilter", Constants.NBT.TAG_COMPOUND)
                ? ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("researchOutputFilter"))
                : null);
    }

    @Override
    public boolean allowPutStackValidated(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return super.allowPutStackValidated(aBaseMetaTileEntity, aIndex, side, aStack)
            && getRecipeMap().containsInput(aStack);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new DebugResearchStationGui(this, getUIProperties()).build(data, syncManager, uiSettings);
    }

    @Override
    protected boolean useMui2() {
        return true;
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
