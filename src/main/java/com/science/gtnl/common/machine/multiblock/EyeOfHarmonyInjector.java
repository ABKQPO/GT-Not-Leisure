package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.*;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.mixinHelper.IEyeOfHarmonyControllerLink;
import com.science.gtnl.api.mixinHelper.LinkedEyeOfHarmonyUnit;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.widget.TextButtonWidget;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.metaTileEntity.multi.MTEEyeOfHarmony;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class EyeOfHarmonyInjector extends TTMultiblockBase implements IConstructable, ISurvivalConstructable {

    public static int STATUS_WINDOW_ID = 10;

    public static FluidStack heliumStack = Materials.Helium.getGas(1);
    public static FluidStack hydrogenStack = Materials.Helium.getGas(1);
    public static FluidStack rawstarmatterStack = MaterialsUEVplus.RawStarMatter.getFluid(1);
    public static double maxFluidAmount = Long.MAX_VALUE;
    public ArrayList<MTEEyeOfHarmony> mEHO = new ArrayList<>();
    public Parameters.Group.ParameterIn maxFluidAmountSetting;
    public int tCountCasing;

    public List<LinkedEyeOfHarmonyUnit> mLinkedUnits = new ArrayList<>();

    public EyeOfHarmonyInjector(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public EyeOfHarmonyInjector(String aName) {
        super(aName);
    }

    public void registerLinkedUnit(MTEEyeOfHarmony unit) {
        LinkedEyeOfHarmonyUnit link = new LinkedEyeOfHarmonyUnit(unit);
        // Make sure to mark it as active if it is running a recipe. This happens on server restart and fixes
        // waterline multiblocks not resuming their progress until the next cycle.
        link.setActive(unit.mMaxProgresstime > 0);
        this.mLinkedUnits.add(link);
    }

    public void unregisterLinkedUnit(MTEEyeOfHarmony unit) {
        this.mLinkedUnits.removeIf(link -> link.metaTileEntity() == unit);
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!(aPlayer instanceof EntityPlayerMP)) return;

        // Save link data to data stick, very similar to Crafting Input Buffer.
        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) return;

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", "EyeOfHarmonyInjector");
        tag.setInteger("x", aBaseMetaTileEntity.getXCoord());
        tag.setInteger("y", aBaseMetaTileEntity.getYCoord());
        tag.setInteger("z", aBaseMetaTileEntity.getZCoord());

        dataStick.stackTagCompound = tag;
        dataStick.setStackDisplayName(
            "Eye Of Harmony Injector Link Data Stick (" + aBaseMetaTileEntity
                .getXCoord() + ", " + aBaseMetaTileEntity.getYCoord() + ", " + aBaseMetaTileEntity.getZCoord() + ")");
        aPlayer.addChatMessage(new ChatComponentText("Saved Link Data to Data Stick"));
    }

    @Override
    public String[] getInfoData() {
        var ret = new ArrayList<String>();
        // Show linked purification units and their status
        ret.add(translateToLocal("GT5U.infodata.purification_plant.linked_units"));
        for (LinkedEyeOfHarmonyUnit unit : this.mLinkedUnits) {
            String text = EnumChatFormatting.AQUA + unit.metaTileEntity()
                .getLocalName() + ": ";
            MTEEyeOfHarmony status = unit.metaTileEntity();
            if (status.mMachine) {
                if (status.mMaxProgresstime > 0) {
                    text = text + EnumChatFormatting.GREEN
                        + translateToLocal("GT5U.infodata.purification_plant.linked_units.status.online");
                } else {
                    text = text + EnumChatFormatting.YELLOW
                        + translateToLocal("GT5U.infodata.purification_plant.linked_units.status.disabled");
                }
            } else {
                text = text + EnumChatFormatting.RED
                    + translateToLocal("GT5U.infodata.purification_plant.linked_units.status.incomplete");
            }
            ret.add(text);
        }
        return ret.toArray(new String[0]);
    }

    @Override
    public void onBlockDestroyed() {
        // When the controller is destroyed we want to notify all currently linked units
        for (LinkedEyeOfHarmonyUnit unit : this.mLinkedUnits) {
            ((IEyeOfHarmonyControllerLink) unit.metaTileEntity()).unlinkController();
        }
        super.onBlockDestroyed();
    }

    public ModularWindow createStatusWindow(EntityPlayer player) {
        final int windowWidth = 260;
        final int windowHeight = 200;
        ModularWindow.Builder builder = ModularWindow.builder(windowWidth, windowHeight);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.widget(
            ButtonWidget.closeWindowButton(true)
                .setPos(windowWidth - 15, 3));

        // Title widget
        builder.widget(
            new TextWidget(translateToLocal("GT5U.infodata.purification_plant.status_title"))
                .setTextAlignment(Alignment.Center)
                .setPos(5, 10)
                .setSize(windowWidth, 8));

        int currentYPosition = 20;
        Scrollable mainDisp = new Scrollable().setVerticalScroll()
            .setHorizontalScroll();

        int rowHeight = 40;
        for (int i = 0; i < this.mLinkedUnits.size(); i++) {
            mainDisp.widget(makeUnitStatusWidget(mLinkedUnits.get(i)).setPos(0, rowHeight * (i + 1)));
        }

        builder.widget(
            mainDisp.setPos(5, currentYPosition)
                .setSize(windowWidth - 10, windowHeight - currentYPosition - 5));
        return builder.build();
    }

    public Widget makeStatusWindowButton() {
        TextButtonWidget widget = (TextButtonWidget) new TextButtonWidget(
            translateToLocal("GT5U.infodata.purification_plant.status_button")).setLeftMargin(4)
                .setSize(40, 16)
                .setPos(10, 40);
        widget.button()
            .setOnClick(
                (clickData, w) -> {
                    if (!w.isClient()) w.getContext()
                        .openSyncedWindow(STATUS_WINDOW_ID);
                })
            .setBackground(GTUITextures.BUTTON_STANDARD);
        widget.text()
            .setTextAlignment(Alignment.CenterLeft)
            .setDefaultColor(EnumChatFormatting.BLACK);
        return widget;
    }

    public Widget makeUnitStatusWidget(LinkedEyeOfHarmonyUnit unit) {
        // Draw small machine controller icon
        DynamicPositionedRow row = new DynamicPositionedRow();
        row.widget(new FakeSyncWidget.IntegerSyncer(() -> unit.windowID, val -> unit.windowID = val));

        // 提取 metaTileEntity
        MTEEyeOfHarmony mte = unit.metaTileEntity();
        IGregTechTileEntity gtTE = mte.getBaseMetaTileEntity();

        row.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(unit.windowID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(
                () -> new IDrawable[] { GTUITextures.BUTTON_STANDARD, new ItemDrawable(mte.getStackForm(1)) })
            .addTooltips(
                Arrays.asList(
                    StatCollector.translateToLocal("独立配置最大输入数量"),
                    StatCollector.translateToLocal("目标坐标: "),
                    String.format("X: %s, Y: %s, Z: %s", gtTE.getXCoord(), gtTE.getYCoord(), gtTE.getZCoord())))
            .setSize(18, 18));

        // Display machine name and status
        String name = mte.getLocalName();
        String statusString = name + "  " + unit.getStatusString();

        IEyeOfHarmonyControllerLink link = (IEyeOfHarmonyControllerLink) mte;
        long heliumStored = link.gtnl$getHeliumStored();
        long hydrogenStored = link.gtnl$getHydrogenStored();
        long rawStarMatterStored = link.gtnl$getStellarPlasmaStored();

        String fluidStatus = Materials.Helium.mLocalizedName + ": "
            + heliumStored
            + "  "
            + Materials.Hydrogen.mLocalizedName
            + ": "
            + hydrogenStored
            + "  "
            + MaterialsUEVplus.RawStarMatter.mLocalizedName
            + ": "
            + rawStarMatterStored;

        row.widget(
            TextWidget.dynamicString(() -> statusString)
                .setSynced(false)
                .setTextAlignment(Alignment.CenterLeft)
                .setPos(40, 2)
                .fillParent())
            .widget(
                TextWidget.dynamicString(() -> fluidStatus)
                    .setSynced(true)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(40, 20)
                    .fillParent())
            .widget(new FakeSyncWidget.StringSyncer(() -> name, _name -> {}))
            .widget(link.makeSyncerWidgets())
            .widget(new FakeSyncWidget.BooleanSyncer(unit::isActive, unit::setActive))
            .setSize(200, 50);

        return row;
    }

    public ModularWindow createConfigWindow(LinkedEyeOfHarmonyUnit unit) {
        ModularWindow.Builder builder = ModularWindow.builder(250, 120);
        builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);
        builder.widget(new FakeSyncWidget.LongSyncer(() -> unit.maxHeliumAmount, val -> unit.maxHeliumAmount = val));
        builder
            .widget(new FakeSyncWidget.LongSyncer(() -> unit.maxHydrogenAmount, val -> unit.maxHydrogenAmount = val));
        builder.widget(
            new FakeSyncWidget.LongSyncer(
                () -> unit.maxRawStarMatterSAmount,
                val -> unit.maxRawStarMatterSAmount = val));

        builder.widget(
            TextWidget.localised("设置氦最大拉取数量")
                .setSize(200, 14)
                .setPos(10, 2))
            .widget(
                new NumericWidget().setSetter(val -> unit.maxHeliumAmount = (long) val)
                    .setGetter(() -> unit.maxHeliumAmount)
                    .setBounds(1, Long.MAX_VALUE)
                    .setScrollValues(1, 10000, 1000000)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(200, 18)
                    .setPos(10, 18)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));

        builder.widget(
            TextWidget.localised("设置氢最大拉取数量")
                .setSize(200, 14)
                .setPos(10, 38))
            .widget(
                new NumericWidget().setSetter(val -> unit.maxHydrogenAmount = (long) val)
                    .setGetter(() -> unit.maxHydrogenAmount)
                    .setBounds(1, Long.MAX_VALUE)
                    .setScrollValues(1, 10000, 1000000)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(200, 18)
                    .setPos(10, 54)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));

        builder.widget(
            TextWidget.localised("设置浓缩原始恒星等离子体混合物最大拉取数量")
                .setSize(200, 14)
                .setPos(10, 74))
            .widget(
                new NumericWidget().setSetter(val -> unit.maxRawStarMatterSAmount = (long) val)
                    .setGetter(() -> unit.maxRawStarMatterSAmount)
                    .setBounds(1, Long.MAX_VALUE)
                    .setScrollValues(1, 10000, 1000000)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(200, 18)
                    .setPos(10, 90)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));

        return builder.build();
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        // Add value syncers, note that we do this here so
        // everything is updated once the status gui opens
        addSyncers(builder);

        buildContext.addSyncedWindow(STATUS_WINDOW_ID, this::createStatusWindow);

        int windowID = STATUS_WINDOW_ID + 1;

        for (LinkedEyeOfHarmonyUnit unit : mLinkedUnits) {
            unit.windowID = windowID++;
            buildContext.addSyncedWindow(unit.windowID, ctx -> createConfigWindow(unit));
        }

        // Add status window button
        builder.widget(makeStatusWindowButton());
    }

    public void addSyncers(ModularWindow.Builder builder) {
        // Sync connection list to client
        builder.widget(new FakeSyncWidget.ListSyncer<>(() -> mLinkedUnits, links -> {
            List<Integer> oldWindowIDs = new ArrayList<>();
            for (LinkedEyeOfHarmonyUnit unit : mLinkedUnits) {
                oldWindowIDs.add(unit.windowID);
            }

            mLinkedUnits.clear();
            mLinkedUnits.addAll(links);

            if (mLinkedUnits.size() == oldWindowIDs.size()) {
                for (int i = 0; i < mLinkedUnits.size(); i++) {
                    mLinkedUnits.get(i).windowID = oldWindowIDs.get(i);
                }
            }
        }, (buffer, link) -> {
            // Try to save link data to NBT, so we can reconstruct it on client
            try {
                buffer.writeNBTTagCompoundToBuffer(link.writeLinkDataToNBT());
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error(e.getCause());
            }
        }, buffer -> {
            // Try to load link data from NBT compound as constructed above.
            try {
                return new LinkedEyeOfHarmonyUnit(buffer.readNBTTagCompoundFromBuffer());
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error(e.getCause());
            }
            return null;
        }));
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing_EM() {
        this.onMachineBlockUpdate();

        NBTTagCompound nbt = new NBTTagCompound();
        mEHO.get(0)
            .onMachineBlockUpdate();
        mEHO.get(0)
            .saveNBTData(nbt);

        long maxAmount = (long) Math.min(maxFluidAmount, maxFluidAmountSetting.get());

        long helium = nbt.getLong("stored.fluid.helium");
        long hydrogen = nbt.getLong("stored.fluid.hydrogen");
        long rawstarmatter = nbt.getLong("stored.fluid.rawstarmatter");

        if (helium >= maxAmount && hydrogen >= maxAmount && rawstarmatter >= maxAmount) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        List<ItemStack> outputItemStack = new ArrayList<>();

        for (ItemStack stack : getStoredInputs()) {
            if (!(stack.getItem() instanceof ItemFluidPacket)) continue;
            NBTTagCompound fluidNBT = stack.getTagCompound()
                .getCompoundTag("FluidStack");
            String fluidName = fluidNBT.getString("FluidName");
            long amount = fluidNBT.getLong("Amount");

            switch (fluidName) {
                case "hydrogen" -> hydrogen = handleFluidInput(
                    Materials.Hydrogen.mGas,
                    hydrogen,
                    amount,
                    maxAmount,
                    outputItemStack);
                case "helium" -> helium = handleFluidInput(
                    Materials.Helium.mGas,
                    helium,
                    amount,
                    maxAmount,
                    outputItemStack);
                case "rawstarmatter" -> rawstarmatter = handleFluidInput(
                    MaterialsUEVplus.RawStarMatter.mFluid,
                    rawstarmatter,
                    amount,
                    maxAmount,
                    outputItemStack);
            }
            stack.stackSize--;
        }

        for (FluidStack fluidStack : getStoredFluids()) {
            if (GTUtility.areFluidsEqual(fluidStack, heliumStack)) {
                helium = tryConsumeFluid(fluidStack, helium, maxAmount);
            } else if (GTUtility.areFluidsEqual(fluidStack, hydrogenStack)) {
                hydrogen = tryConsumeFluid(fluidStack, hydrogen, maxAmount);
            } else if (GTUtility.areFluidsEqual(fluidStack, rawstarmatterStack)) {
                rawstarmatter = tryConsumeFluid(fluidStack, rawstarmatter, maxAmount);
            }
        }

        updateSlots();

        if (!outputItemStack.isEmpty() || helium != nbt.getLong("stored.fluid.helium")
            || hydrogen != nbt.getLong("stored.fluid.hydrogen")
            || rawstarmatter != nbt.getLong("stored.fluid.rawstarmatter")) {

            nbt.setLong("stored.fluid.helium", helium);
            nbt.setLong("stored.fluid.hydrogen", hydrogen);
            nbt.setLong("stored.fluid.rawstarmatter", rawstarmatter);

            mEHO.get(0)
                .loadNBTData(nbt);
            mEHO.get(0)
                .onMachineBlockUpdate();

            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = 20;
            mOutputItems = outputItemStack.toArray(new ItemStack[0]);

            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public long handleFluidInput(Fluid fluid, long current, long amount, long max, List<ItemStack> outputs) {
        if (current >= max || amount <= 0) return current;

        long space = max - current;
        long accepted = Math.min(amount, space);
        long leftover = amount - accepted;

        current += accepted;
        if (leftover > 0) {
            outputs.add(ItemFluidPacket.newStack(new FluidStack(fluid, (int) leftover)));
        }

        return current;
    }

    public long tryConsumeFluid(FluidStack fluidStack, long current, long max) {
        if (current >= max) return current;
        if (depleteInput(fluidStack, true)) {
            depleteInput(fluidStack);
            long input = Math.min(max - current, fluidStack.amount);
            current += input;
        }
        return current;
    }

    private static final int HORIZONTAL_OFF_SET = 26;
    private static final int VERTICAL_OFF_SET = 6;
    private static final int DEPTH_OFF_SET = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EOHI_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/eye_of_harmony_injector";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EOHI_STRUCTURE_FILE_PATH);

    @Override
    public IStructureDefinition<? extends EyeOfHarmonyInjector> getStructure_EM() {
        return StructureDefinition.<EyeOfHarmonyInjector>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasingsTT, 6))
            .addElement('B', ofBlock(sBlockCasingsTT, 7))
            .addElement('C', ofBlock(sBlockCasings1, 13))
            .addElement('D', ofBlock(sBlockCasings9, 14))
            .addElement('E', ofBlock(sBlockCasingsTT, 8))
            .addElement('F', ofBlock(sBlockCasings10, 12))
            .addElement('G', ofBlock(sBlockCasingsTT, 4))
            .addElement('H', ofBlock(sBlockCasings10, 8))
            .addElement('I', ofBlock(sBlockCasings1, 12))
            .addElement('J', ofFrame(Materials.CosmicNeutronium))
            .addElement('K', ofBlock(BlockLoader.metaBlockGlass, 2))
            .addElement('L', ofBlock(sBlockCasings1, 14))
            .addElement('M', ofBlock(sBlockCasings10, 7))
            .addElement('N', ofBlock(sBlockCasings3, 12))
            .addElement('O', ofBlock(sBlockCasings9, 11))
            .addElement(
                'P',
                ofChain(
                    buildHatchAdder(EyeOfHarmonyInjector.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy.or(ExoticEnergy))
                        .buildAndChain(),
                    onElementPass(e -> e.tCountCasing++, ofBlock(sBlockCasingsTT, 0))))
            .addElement(
                'Q',
                HatchElementBuilder.<EyeOfHarmonyInjector>builder()
                    .atLeast(EOH.EOH)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .build())
            .build();
    }

    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset;
    }

    public boolean addEHO(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEEyeOfHarmony) {
            return mEHO.add((MTEEyeOfHarmony) aMetaTileEntity);
        }
        return false;
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) {
            return -1;
        } else {
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
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM(
            STRUCTURE_PIECE_MAIN,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            stackSize,
            hintsOnly);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mEHO.clear();
        return structureCheck_EM(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    public static INameFunction<EyeOfHarmonyInjector> MAX_FLUID_AMOUNT_SETTING_NAME = (base, p) -> StatCollector
        .translateToLocal("Tooltip_EyeOfHarmonyInjector_Parametrization");

    public static IStatusFunction<EyeOfHarmonyInjector> MAX_FLUID_AMOUNT_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, maxFluidAmount / 2, maxFluidAmount, maxFluidAmount);

    @Override
    public void parametersInstantiation_EM() {
        super.parametersInstantiation_EM();
        Parameters.Group hatch_0 = parametrization.getGroup(0, false);
        maxFluidAmountSetting = hatch_0
            .makeInParameter(0, maxFluidAmount, MAX_FLUID_AMOUNT_SETTING_NAME, MAX_FLUID_AMOUNT_STATUS);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EyeOfHarmonyInjectorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_05"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(53, 13, 62, false)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_Casing"), 1)
            .toolTipFinisher();
        return tt;
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

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new EyeOfHarmonyInjector(mName);
    }

    public enum EOH implements IHatchElement<EyeOfHarmonyInjector> {

        EOH(EyeOfHarmonyInjector::addEHO, MTEEyeOfHarmony.class) {

            @Override
            public long count(EyeOfHarmonyInjector gtTieEntityMagesTower) {
                return 0;
            }
        };

        public final List<Class<? extends IMetaTileEntity>> mteClasses;
        public final IGTHatchAdder<EyeOfHarmonyInjector> adder;

        @SafeVarargs
        EOH(IGTHatchAdder<EyeOfHarmonyInjector> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return this.mteClasses;
        }

        @Override
        public IGTHatchAdder<? super EyeOfHarmonyInjector> adder() {
            return this.adder;
        }

    }
}
