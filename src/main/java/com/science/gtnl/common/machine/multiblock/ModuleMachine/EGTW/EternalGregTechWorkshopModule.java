package com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.EternalGregTechWorkshop.ScreenOFF;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.formatNumbers;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static net.minecraft.util.EnumChatFormatting.*;
import static net.minecraft.util.EnumChatFormatting.RESET;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.Util.EternalGregTechWorkshopUI;

import bartworks.common.loaders.ItemRegistry;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.OverclockCalculator;
import tectech.thing.casing.TTCasingsContainer;
import tectech.thing.gui.TecTechUITextures;

public abstract class EternalGregTechWorkshopModule extends MultiMachineBase<EternalGregTechWorkshopModule> {

    protected UUID ownerUUID;
    protected boolean isConnected = false;
    protected double mEUtDiscount = 1;
    protected double mSpeedBoost = 1;
    protected int maxParallel = 1;
    protected int mHeatingCapacity = 0;
    protected int tCountCasing = 0;
    protected long maxUseEUt = 0;
    protected BigInteger powerTally = BigInteger.ZERO;
    protected long recipeTally = 0;
    protected long EUt = 0;
    protected int currentParallel = 0;

    protected static final String STRUCTURE_PIECE_MAIN = "main";
    protected static final String EGTWM_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eternal_gregTech_workshop/module";
    protected static final String[][] shape = StructureUtils.readStructureFromFile(EGTWM_STRUCTURE_FILE_PATH);
    protected final int HORIZONTAL_OFF_SET = 4;
    protected final int VERTICAL_OFF_SET = 3;
    protected final int DEPTH_OFF_SET = 0;
    protected final int CASING_INDEX = 960;

    public static final String TEXTURE_OVERLAY_FRONT_SCREEN_ON = Mods.GregTech.ID + ":"
        + "iconsets/GODFORGE_MODULE_ACTIVE";
    public static Textures.BlockIcons.CustomIcon ScreenON = new Textures.BlockIcons.CustomIcon(
        TEXTURE_OVERLAY_FRONT_SCREEN_ON);

    public EternalGregTechWorkshopModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public EternalGregTechWorkshopModule(String aName) {
        super(aName);
    }

    public void connect() {
        isConnected = true;
    }

    public void disconnect() {
        isConnected = false;
    }

    public double getEUtDiscount() {
        return mEUtDiscount;
    }

    public void setEUtDiscount(double discount) {
        mEUtDiscount = discount;
    }

    public long getMaxUseEUt() {
        return maxUseEUt;
    }

    public void setMaxUseEUt(long maxUse) {
        maxUseEUt = maxUse;
    }

    public void setHeat(int heat) {
        mHeatingCapacity = heat;
    }

    public int getHeat() {
        return mHeatingCapacity;
    }

    public double getSpeedBoost() {
        return mSpeedBoost;
    }

    public void setDurationModifier(double boost) {
        mSpeedBoost = boost;
    }

    @Override
    public int getMaxParallel() {
        return maxParallel;
    }

    @Override
    public void setMaxParallel(int parallel) {
        maxParallel = parallel;
    }

    @Override
    public int getMaxParallelRecipes() {
        return getMaxParallel();
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setAvailableVoltage(getMaxUseEUt());
                setAvailableAmperage(1);
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                if (!addEUToGlobalEnergyMap(ownerUUID, -calculatedEut * duration)) {
                    return CheckRecipeResultRegistry.insufficientPower(calculatedEut * duration);
                }

                addToPowerTally(
                    BigInteger.valueOf(calculatedEut)
                        .multiply(BigInteger.valueOf(duration)));
                addToRecipeTally(calculatedParallels);

                currentParallel = calculatedParallels;
                EUt = calculatedEut;
                calculatedEut = 0;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setMachineHeat(getHeat())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getSpeedBoost());
            }
        }.setMaxParallelSupplier(this::getMaxParallel);
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>();
        str.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.progress",
                GREEN + formatNumbers(mProgresstime / 20) + RESET,
                YELLOW + formatNumbers(mMaxProgresstime / 20) + RESET));
        str.add(
            StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.currently_using",
                RED + (getBaseMetaTileEntity().isActive() ? formatNumbers(EUt) : "0") + RESET));
        str.add(
            YELLOW + StatCollector
                .translateToLocalFormatted("tt.infodata.multi.max_parallel", RESET + formatNumbers(Integer.MAX_VALUE)));
        str.add(
            YELLOW + StatCollector.translateToLocalFormatted(
                "GT5U.infodata.parallel.current",
                RESET + (getBaseMetaTileEntity().isActive() ? formatNumbers(currentParallel) : "0")));
        str.add(
            YELLOW + StatCollector
                .translateToLocalFormatted("tt.infodata.multi.capacity.heat", RESET + formatNumbers(getHeat())));
        str.add(
            YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.recipe_time",
                RESET + formatNumbers(getSpeedBonus())));
        return str.toArray(new String[0]);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void saveNBTData(NBTTagCompound NBT) {
        NBT.setBoolean("isConnected", isConnected);
        NBT.setLong("maxUseEUt", maxUseEUt);
        NBT.setLong("recipeTally", recipeTally);
        NBT.setByteArray("powerTally", powerTally.toByteArray());
        super.saveNBTData(NBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound NBT) {
        isConnected = NBT.getBoolean("isConnected");
        maxUseEUt = NBT.getLong("maxUseEUt");
        recipeTally = NBT.getLong("recipeTally");
        powerTally = new BigInteger(NBT.getByteArray("powerTally"));
        super.loadNBTData(NBT);
    }

    @Override
    public float getSpeedBonus() {
        return 1;
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX),
                TextureFactory.builder()
                    .addIcon(ScreenON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(ScreenON)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX), TextureFactory.builder()
                .addIcon(ScreenOFF)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(ScreenOFF)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CASING_INDEX) };
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && isConnected) {
            super.onPostTick(aBaseMetaTileEntity, aTick);
            if (mEfficiency < 0) mEfficiency = 0;
        }
    }

    @Override
    public IStructureDefinition<EternalGregTechWorkshopModule> getStructureDefinition() {
        return StructureDefinition.<EternalGregTechWorkshopModule>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(ItemRegistry.bw_realglas2, 0))
            .addElement('B', ofBlock(Loaders.componentAssemblylineCasing, 12))
            .addElement('C', ofBlock(GregTechAPI.sBlockCasings1, 13))
            .addElement('D', ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement('E', ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('F', ofBlock(GregTechAPI.sBlockCasings9, 14))
            .addElement('G', ofBlock(GregTechAPI.sBlockCasingsSEMotor, 4))
            .addElement('H', ofFrame(Materials.NaquadahAlloy))
            .addElement('I', ofBlock(TTCasingsContainer.GodforgeCasings, 0))
            .addElement('J', ofBlock(TTCasingsContainer.GodforgeCasings, 1))
            .addElement(
                'K',
                ofChain(
                    isAir(),
                    buildHatchAdder(EternalGregTechWorkshopModule.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch)
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .buildAndChain(
                            onElementPass(x -> ++x.tCountCasing, ofBlock(TTCasingsContainer.GodforgeCasings, 0)))))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    private static final int GENERAL_INFO_WINDOW_ID = 10;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        final SlotWidget inventorySlot = new SlotWidget(inventoryHandler, 1);
        drawTexts(screenElements, inventorySlot);
        buildContext.addSyncedWindow(GENERAL_INFO_WINDOW_ID, this::createGeneralInfoWindow);

        builder.widget(
            new DrawableWidget().setSize(18, 18)
                .setPos(172, 67)
                .addTooltip(translateToLocal("gt.blockmachines.multimachine.FOG.clickhere"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY));

        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.BACKGROUND_SCREEN_BLUE)
                .setPos(4, 4)
                .setSize(190, 85))
            .widget(
                inventorySlot.setPos(173, 167)
                    .setBackground(getGUITextureSet().getItemSlot(), TecTechUITextures.OVERLAY_SLOT_MESH))
            .widget(
                new DrawableWidget().setDrawable(TecTechUITextures.PICTURE_HEAT_SINK_SMALL)
                    .setPos(173, 185)
                    .setSize(18, 6))
            .widget(
                new Scrollable().setVerticalScroll()
                    .widget(screenElements.setPos(10, 0))
                    .setPos(0, 7)
                    .setSize(190, 79))
            .widget(
                TextWidget.dynamicText(this::connectionStatus)
                    .setDefaultColor(EnumChatFormatting.BLACK)
                    .setPos(75, 94)
                    .setSize(100, 10))
            .widget(
                new ButtonWidget().setOnClick(
                    (data, widget) -> {
                        if (!widget.isClient()) widget.getContext()
                            .openSyncedWindow(GENERAL_INFO_WINDOW_ID);
                    })
                    .setSize(18, 18)
                    .setPos(172, 67)
                    .setTooltipShowUpDelay(TOOLTIP_DELAY))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (isAllowedToWork()) {
                    disableWorking();
                } else {
                    enableWorking();
                }
            })
                .setPlayClickSoundResource(
                    () -> isAllowedToWork() ? SoundResource.GUI_BUTTON_UP.resourceLocation
                        : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                .setBackground(() -> {
                    if (isAllowedToWork()) {
                        return new IDrawable[] { TecTechUITextures.BUTTON_CELESTIAL_32x32,
                            TecTechUITextures.OVERLAY_BUTTON_POWER_SWITCH_ON };
                    } else {
                        return new IDrawable[] { TecTechUITextures.BUTTON_CELESTIAL_32x32,
                            TecTechUITextures.OVERLAY_BUTTON_POWER_SWITCH_OFF };
                    }
                })
                .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::isAllowedToWork, val -> {
                    if (val) enableWorking();
                    else disableWorking();
                }), builder)
                .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.power_switch"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(174, 148)
                .setSize(16, 16))
            .widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (!widget.isClient()) {
                    checkMachine(this.getBaseMetaTileEntity(), null);
                }
            })
                .setSize(16, 16)
                .setBackground(() -> {
                    List<UITexture> ret = new ArrayList<>();
                    ret.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                    if (getStructureUpdateTime() > -20) {
                        ret.add(TecTechUITextures.OVERLAY_BUTTON_STRUCTURE_CHECK);
                    } else {
                        ret.add(TecTechUITextures.OVERLAY_BUTTON_STRUCTURE_CHECK_OFF);
                    }
                    return ret.toArray(new IDrawable[0]);
                })
                .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.structure_update"))
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setPos(174, 130)
                .setTooltipShowUpDelay(TOOLTIP_DELAY));

        if (supportsVoidProtection()) builder.widget(createVoidExcessButton(builder));
        if (supportsInputSeparation()) builder.widget(createInputSeparationButton(builder));
        if (supportsBatchMode()) builder.widget(createBatchModeButton(builder));
        if (supportsSingleRecipeLocking()) builder.widget(createLockToSingleRecipeButton(builder));
    }

    private Text connectionStatus() {
        String status = EnumChatFormatting.RED
            + StatCollector.translateToLocal("gt.blockmachines.multimachine.FOG.modulestatus.false");
        if (isConnected) {
            status = EnumChatFormatting.GREEN
                + StatCollector.translateToLocal("gt.blockmachines.multimachine.FOG.modulestatus.true");
        }
        return new Text(
            StatCollector.translateToLocal("gt.blockmachines.multimachine.FOG.modulestatus") + " " + status);
    }

    protected ModularWindow createGeneralInfoWindow(final EntityPlayer player) {
        return EternalGregTechWorkshopUI.createGeneralInfoWindow();
    }

    @Override
    public ButtonWidget createInputSeparationButton(IWidgetBuilder<?> builder) {
        return EternalGregTechWorkshopUI.createInputSeparationButton(getBaseMetaTileEntity(), this, builder);
    }

    @Override
    public ButtonWidget createBatchModeButton(IWidgetBuilder<?> builder) {
        return EternalGregTechWorkshopUI.createBatchModeButton(getBaseMetaTileEntity(), this, builder);
    }

    @Override
    public ButtonWidget createLockToSingleRecipeButton(IWidgetBuilder<?> builder) {
        return EternalGregTechWorkshopUI.createLockToSingleRecipeButton(getBaseMetaTileEntity(), this, builder);
    }

    @Override
    public ButtonWidget createVoidExcessButton(IWidgetBuilder<?> builder) {
        return EternalGregTechWorkshopUI.createVoidExcessButton(getBaseMetaTileEntity(), this, builder);
    }

    public void setPowerTally(BigInteger amount) {
        powerTally = amount;
    }

    public BigInteger getPowerTally() {
        return powerTally;
    }

    public void addToPowerTally(BigInteger amount) {
        powerTally = powerTally.add(amount);
    }

    public void setRecipeTally(long amount) {
        recipeTally = amount;
    }

    public long getRecipeTally() {
        return recipeTally;
    }

    public void addToRecipeTally(long amount) {
        recipeTally += amount;
    }

    public static void queryMilestoneStats(EternalGregTechWorkshopModule module, EternalGregTechWorkshop egtw) {
        egtw.addTotalPowerConsumed(module.getPowerTally());
        module.setPowerTally(BigInteger.ZERO);
        egtw.addTotalRecipesProcessed(module.getRecipeTally());
        module.setRecipeTally(0);
    }
}
