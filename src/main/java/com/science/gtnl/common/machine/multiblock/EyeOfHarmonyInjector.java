package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings10;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.GregTechAPI.sBlockCasings9;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static tectech.thing.block.TileEntityEyeOfHarmony.OrbitingObject;
import static tectech.thing.block.TileEntityEyeOfHarmony.generateRandomFloat;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Interactable;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.mixinHelper.IEyeOfHarmonyControllerLink;
import com.science.gtnl.api.mixinHelper.LinkedEyeOfHarmonyUnit;
import com.science.gtnl.common.render.tile.EyeOfHarmonyInjectorRenderer;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import appeng.api.util.DimensionalCoord;
import appeng.client.render.highlighter.BlockPosHighlighter;
import appeng.core.localization.PlayerMessages;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtil;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.render.IMTERenderer;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtneioreplugin.plugin.block.ModBlocks;
import tectech.TecTech;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.thing.block.TileEntityEyeOfHarmony;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.metaTileEntity.multi.MTEEyeOfHarmony;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class EyeOfHarmonyInjector extends TTMultiblockBase
    implements IConstructable, ISurvivalConstructable, IMTERenderer {

    public static float MAX_ANGLE = 50;
    public static int STATUS_WINDOW_ID = 10;
    public static FluidStack heliumStack = Materials.Helium.getGas(1);
    public static FluidStack hydrogenStack = Materials.Hydrogen.getGas(1);
    public static FluidStack rawStarMatterStack = MaterialsUEVplus.RawStarMatter.getFluid(1);
    public static double maxFluidAmount = Long.MAX_VALUE;

    public Parameters.Group.ParameterIn maxHeliumAmountSetting;
    public Parameters.Group.ParameterIn maxHydrogenAmountSetting;
    public Parameters.Group.ParameterIn maxRawStarMatterAmountSetting;
    public int mCountCasing;

    public float angle;
    public ArrayList<TileEntityEyeOfHarmony.OrbitingObject> orbitingObjects = new ArrayList<>();
    public boolean enableRender = true;

    public List<LinkedEyeOfHarmonyUnit> mLinkedUnits = new ArrayList<>();

    public EyeOfHarmonyInjector(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public EyeOfHarmonyInjector(String aName) {
        super(aName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        if (!mMachine || !enableRender || mTotalRunTime <= 0) return;
        EyeOfHarmonyInjectorRenderer.renderTileEntity(this, x, y, z, timeSinceLastTick);
    }

    @Override
    public void onValueUpdate(byte aValue) {
        enableRender = (aValue & 0x01) != 0;
    }

    @Override
    public byte getUpdateData() {
        byte data = 0;
        if (enableRender) data |= 0x01;
        return data;
    }

    public ChunkCoordinates getRenderPos() {
        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();

        int xOffset = 34 * back.offsetX;
        int yOffset = 34 * back.offsetY;
        int zOffset = 34 * back.offsetZ;

        return new ChunkCoordinates(xOffset, yOffset, zOffset);
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.enableRender = !enableRender;
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal("Info_Render_" + (this.enableRender ? "Enabled" : "Disabled")));
        }
        return true;
    }

    public void registerLinkedUnit(MTEEyeOfHarmony unit) {
        LinkedEyeOfHarmonyUnit newLink = new LinkedEyeOfHarmonyUnit(unit);

        cleanupInvalidLinks();

        boolean alreadyLinked = mLinkedUnits.stream()
            .anyMatch(link -> link.x == newLink.x && link.y == newLink.y && link.z == newLink.z);

        if (!alreadyLinked) {
            mLinkedUnits.add(newLink);
        }
    }

    public void unregisterLinkedUnit(MTEEyeOfHarmony unit) {
        this.mLinkedUnits.removeIf(link -> link.mMetaTileEntity == unit);
        cleanupInvalidLinks();
    }

    public void cleanupInvalidLinks() {
        Iterator<LinkedEyeOfHarmonyUnit> iterator = mLinkedUnits.iterator();

        while (iterator.hasNext()) {
            LinkedEyeOfHarmonyUnit link = iterator.next();
            TileEntity te = GTUtil.getTileEntity(getBaseMetaTileEntity().getWorld(), link.x, link.y, link.z, true);

            if (!(te instanceof IGregTechTileEntity gtTE)
                || !(gtTE.getMetaTileEntity() instanceof MTEEyeOfHarmony eyeOfHarmony)) {
                iterator.remove();
            } else {
                link.mMetaTileEntity = eyeOfHarmony;
            }
        }
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick_EM(aBaseMetaTileEntity);
        cleanupInvalidLinks();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        angle += 10f;
        if (!aBaseMetaTileEntity.isServerSide()) return;
        if (aTick % 100 == 0) {
            for (LinkedEyeOfHarmonyUnit unit : mLinkedUnits) {
                IEyeOfHarmonyControllerLink link = (IEyeOfHarmonyControllerLink) unit.mMetaTileEntity;
                long heliumStored = link.gtnl$getHeliumStored();
                long hydrogenStored = link.gtnl$getHydrogenStored();
                long rawStarMatterStored = link.gtnl$getStellarPlasmaStored();
                unit.heliumAmount = heliumStored;
                unit.hydrogenAmount = hydrogenStored;
                unit.rawStarMatterAmount = rawStarMatterStored;
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("enableRender", enableRender);
        NBTTagList linkedList = new NBTTagList();
        for (LinkedEyeOfHarmonyUnit unit : mLinkedUnits) {
            linkedList.appendTag(unit.writeLinkDataToNBT());
        }
        aNBT.setTag("LinkedUnits", linkedList);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("enableRender")) enableRender = aNBT.getBoolean("enableRender");
        mLinkedUnits.clear();
        if (aNBT.hasKey("LinkedUnits")) {
            NBTTagList linkedList = aNBT.getTagList("LinkedUnits", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < linkedList.tagCount(); i++) {
                NBTTagCompound unitTag = linkedList.getCompoundTagAt(i);
                try {
                    LinkedEyeOfHarmonyUnit unit = new LinkedEyeOfHarmonyUnit(unitTag, false);
                    mLinkedUnits.add(unit);
                } catch (Exception e) {
                    ScienceNotLeisure.LOG.error("Failed to load LinkedEyeOfHarmonyUnit at index {}", i);
                    e.printStackTrace();
                }
            }
        }
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
        ret.add(StatCollector.translateToLocal("GT5U.infodata.purification_plant.linked_units"));
        for (LinkedEyeOfHarmonyUnit unit : this.mLinkedUnits) {
            String text = EnumChatFormatting.AQUA + unit.mMetaTileEntity.getLocalName() + ": ";
            MTEEyeOfHarmony status = unit.mMetaTileEntity;
            if (status.mMachine) {
                if (status.mMaxProgresstime > 0) {
                    text = text + EnumChatFormatting.GREEN
                        + StatCollector.translateToLocal("GT5U.infodata.purification_plant.linked_units.status.online");
                } else {
                    text = text + EnumChatFormatting.YELLOW
                        + StatCollector
                            .translateToLocal("GT5U.infodata.purification_plant.linked_units.status.disabled");
                }
            } else {
                text = text + EnumChatFormatting.RED
                    + StatCollector.translateToLocal("GT5U.infodata.purification_plant.linked_units.status.incomplete");
            }
            ret.add(text);
        }
        return ret.toArray(new String[0]);
    }

    @Override
    public void onBlockDestroyed() {
        // When the controller is destroyed we want to notify all currently linked units
        for (LinkedEyeOfHarmonyUnit unit : this.mLinkedUnits) {
            ((IEyeOfHarmonyControllerLink) unit.mMetaTileEntity).gtnl$unlinkController();
        }
        super.onBlockDestroyed();
    }

    public ModularWindow createStatusWindow(EntityPlayer player) {
        final int windowWidth = 235;
        final int windowHeight = 220;
        ModularWindow.Builder builder = ModularWindow.builder(windowWidth, windowHeight);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.widget(
            ButtonWidget.closeWindowButton(true)
                .setPos(windowWidth - 15, 3));

        // Title widget
        builder.widget(
            new TextWidget(StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_Title"))
                .setTextAlignment(Alignment.Center)
                .setPos(5, 10)
                .setSize(windowWidth, 8));

        int currentYPosition = 20;
        Scrollable mainDisp = new Scrollable().setVerticalScroll()
            .setHorizontalScroll();

        int rowHeight = 140;
        for (int i = 0; i < this.mLinkedUnits.size(); i++) {
            int height = rowHeight * i;
            LinkedEyeOfHarmonyUnit unit = mLinkedUnits.get(i);
            if (unit == null) continue;
            MTEEyeOfHarmony mte = unit.mMetaTileEntity;
            if (mte == null) continue;
            IEyeOfHarmonyControllerLink link = (IEyeOfHarmonyControllerLink) mte;
            IGregTechTileEntity gtTE = mte.getBaseMetaTileEntity();

            EyeOfHarmonyRecipe recipe = findRecipe(mte.getControllerSlot());
            long astralAmount = link.gtnl$getAstralArrayAmount();

            unit.displayHeliumMax = (unit.maxHeliumAmount != -1) ? unit.maxHeliumAmount
                : getAutoComputedAmount(
                    recipe,
                    astralAmount,
                    (long) maxFluidAmount,
                    (long) maxHeliumAmountSetting.get(),
                    0);

            unit.displayHydrogenMax = (unit.maxHydrogenAmount != -1) ? unit.maxHydrogenAmount
                : getAutoComputedAmount(
                    recipe,
                    astralAmount,
                    (long) maxFluidAmount,
                    (long) maxHydrogenAmountSetting.get(),
                    1);

            unit.displayRawStarMatterMax = (unit.maxRawStarMatterAmount != -1) ? unit.maxRawStarMatterAmount
                : getAutoComputedAmount(
                    recipe,
                    astralAmount,
                    (long) maxFluidAmount,
                    (long) maxRawStarMatterAmountSetting.get(),
                    2);

            if (unit.displayRawStarMatterMax != 0 && unit.maxHeliumAmount == -1 && unit.maxHydrogenAmount == -1) {
                unit.displayHeliumMax = 0;
                unit.displayHydrogenMax = 0;
            }

            if ((unit.displayHeliumMax != 0 || unit.displayHydrogenMax != 0) && unit.maxRawStarMatterAmount == -1) {
                unit.displayRawStarMatterMax = 0;
            }

            mainDisp
                .widget(new FakeSyncWidget.LongSyncer(() -> unit.displayHeliumMax, val -> unit.displayHeliumMax = val));
            mainDisp.widget(
                new FakeSyncWidget.LongSyncer(() -> unit.displayHydrogenMax, val -> unit.displayHydrogenMax = val));
            mainDisp.widget(
                new FakeSyncWidget.LongSyncer(
                    () -> unit.displayRawStarMatterMax,
                    val -> unit.displayRawStarMatterMax = val));

            mainDisp
                .widget(new FakeSyncWidget.LongSyncer(() -> unit.maxHeliumAmount, val -> unit.maxHeliumAmount = val));
            mainDisp.widget(
                new FakeSyncWidget.LongSyncer(() -> unit.maxHydrogenAmount, val -> unit.maxHydrogenAmount = val));
            mainDisp.widget(
                new FakeSyncWidget.LongSyncer(
                    () -> unit.maxRawStarMatterAmount,
                    val -> unit.maxRawStarMatterAmount = val));

            mainDisp.widget(new FakeSyncWidget.LongSyncer(() -> unit.heliumAmount, val -> unit.heliumAmount = val));
            mainDisp.widget(new FakeSyncWidget.LongSyncer(() -> unit.hydrogenAmount, val -> unit.hydrogenAmount = val));
            mainDisp.widget(
                new FakeSyncWidget.LongSyncer(() -> unit.rawStarMatterAmount, val -> unit.rawStarMatterAmount = val));

            mainDisp.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (gtTE.isClientSide()) {
                    BlockPosHighlighter.highlightBlocks(
                        player,
                        Collections.singletonList(
                            new DimensionalCoord(
                                gtTE.getWorld(),
                                gtTE.getXCoord(),
                                gtTE.getYCoord(),
                                gtTE.getZCoord())),
                        mte.getLocalName(),
                        PlayerMessages.MachineHighlighted.getUnlocalized(),
                        PlayerMessages.MachineInOtherDim.getUnlocalized());
                }
                widget.getContext()
                    .tryClose();
            })
                .setBackground(
                    () -> new IDrawable[] { GTUITextures.BUTTON_STANDARD, new ItemDrawable(mte.getStackForm(1)) })
                .addTooltips(
                    Arrays.asList(
                        StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_00"),
                        StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_01"),
                        String.format("X: %s, Y: %s, Z: %s", gtTE.getXCoord(), gtTE.getYCoord(), gtTE.getZCoord()),
                        StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_02")))
                .setSize(18, 18)
                .setPos(0, height));

            mainDisp.widget(
                SlotGroup.ofFluidTanks(
                    Stream.of(heliumStack, hydrogenStack, rawStarMatterStack)
                        .map(stack -> new FluidStackTank(() -> stack, s -> {}, Integer.MAX_VALUE))
                        .collect(Collectors.toList()),
                    3)
                    .phantom(true)
                    .widgetCreator((slotIndex, h) -> (FluidSlotWidget) new FluidSlotWidget(h) {

                        @Override
                        public void tryClickPhantom(ClickData clickData, ItemStack cursorStack) {}

                        @Override
                        public void tryScrollPhantom(int direction) {}

                        @Override
                        public void buildTooltip(List<Text> tooltip) {
                            FluidStack fluid = getContent();
                            if (fluid != null) {
                                addFluidNameInfo(tooltip, fluid);

                                long amount = 0;

                                if (GTUtility.areFluidsEqual(fluid, heliumStack)) {
                                    amount = unit.heliumAmount;
                                } else if (GTUtility.areFluidsEqual(fluid, hydrogenStack)) {
                                    amount = unit.hydrogenAmount;
                                } else if (GTUtility.areFluidsEqual(fluid, rawStarMatterStack)) {
                                    amount = unit.rawStarMatterAmount;
                                }

                                tooltip.add(Text.localised("modularui.fluid.phantom.amount", amount));

                                addAdditionalFluidInfo(tooltip, fluid);
                                if (!Interactable.hasShiftDown()) {
                                    tooltip.add(Text.EMPTY);
                                    tooltip.add(Text.localised("modularui.tooltip.shift"));
                                }
                            } else {
                                tooltip.add(
                                    Text.localised("modularui.fluid.empty")
                                        .format(EnumChatFormatting.WHITE));
                            }
                        }
                    }.setUpdateTooltipEveryTick(true))
                    .background(GTUITextures.SLOT_DARK_GRAY)
                    .controlsAmount(true)
                    .build()
                    .setSize(54, 18)
                    .setPos(18, height));

            // Display machine name and status
            String name = mte.getLocalName();
            String statusString = name + " - " + unit.getStatusString();

            mainDisp.widget(
                TextWidget.dynamicText(() -> new Text(statusString))
                    .setSynced(true)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(75, 5 + height));

            mainDisp.widget(TextWidget.dynamicText(() -> {
                String title = StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_HeliumParametrization");
                if (unit.maxHeliumAmount != -1) return new Text(title);
                return new Text(title + " - " + StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_04"));
            })
                .setSize(200, 18)
                .setPos(15, 18 + height))
                .widget(
                    new NumericWidget().setSetter(val -> unit.maxHeliumAmount = (long) val)
                        .setGetter(() -> unit.maxHeliumAmount != -1 ? unit.maxHeliumAmount : unit.displayHeliumMax)
                        .setBounds(-1, Long.MAX_VALUE)
                        .setScrollValues(1, 10000, 1000000)
                        .setTextAlignment(Alignment.Center)
                        .setTextColor(Color.WHITE.normal)
                        .setSize(200, 18)
                        .setPos(15, 36 + height)
                        .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));

            mainDisp.widget(TextWidget.dynamicText(() -> {
                String title = StatCollector.translateToLocal("Tooltip_EyeOfHarmonyInjector_HydrogenParametrization");
                if (unit.maxHydrogenAmount != -1) return new Text(title);
                return new Text(title + " - " + StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_04"));
            })
                .setSize(200, 18)
                .setPos(15, 54 + height))
                .widget(
                    new NumericWidget().setSetter(val -> unit.maxHydrogenAmount = (long) val)
                        .setGetter(
                            () -> unit.maxHydrogenAmount != -1 ? unit.maxHydrogenAmount : unit.displayHydrogenMax)
                        .setBounds(-1, Long.MAX_VALUE)
                        .setScrollValues(1, 10000, 1000000)
                        .setTextAlignment(Alignment.Center)
                        .setTextColor(Color.WHITE.normal)
                        .setSize(200, 18)
                        .setPos(15, 72 + height)
                        .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD));

            mainDisp.widget(TextWidget.dynamicText(() -> {
                String title = StatCollector
                    .translateToLocal("Tooltip_EyeOfHarmonyInjector_RawStarMatterParametrization");
                if (unit.maxRawStarMatterAmount != -1) return new Text(title);
                return new Text(title + " - " + StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_04"));
            })
                .setSize(200, 18)
                .setPos(15, 90 + height))
                .widget(
                    new NumericWidget().setSetter(val -> unit.maxRawStarMatterAmount = (long) val)
                        .setGetter(
                            () -> unit.maxRawStarMatterAmount != -1 ? unit.maxRawStarMatterAmount
                                : unit.displayRawStarMatterMax)
                        .setBounds(-1, Long.MAX_VALUE)
                        .setScrollValues(1, 10000, 1000000)
                        .setTextAlignment(Alignment.Center)
                        .setTextColor(Color.WHITE.normal)
                        .setSize(200, 18)
                        .setPos(15, 108 + height)
                        .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD))
                .setSize(200, 140);
        }

        builder.widget(
            mainDisp.setPos(5, currentYPosition)
                .setSize(windowWidth - 10, windowHeight - currentYPosition - 5));
        return builder.build();
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        // Add value syncers, note that we do this here so
        // everything is updated once the status gui opens
        addSyncers(builder);

        buildContext.addSyncedWindow(STATUS_WINDOW_ID, this::createStatusWindow);

        builder.widget(
            new ButtonWidget().setOnClick(
                (clickData, widget) -> {
                    if (!widget.isClient()) widget.getContext()
                        .openSyncedWindow(STATUS_WINDOW_ID);
                })
                .setPlayClickSound(true)
                .setBackground(
                    () -> new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                        GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT })
                .addTooltip(StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_03"))
                .setPos(174, 97)
                .setSize(16, 16));

    }

    public void addSyncers(ModularWindow.Builder builder) {
        // Sync connection list to client
        builder.widget(new FakeSyncWidget.ListSyncer<>(() -> mLinkedUnits, links -> {
            mLinkedUnits.clear();
            mLinkedUnits.addAll(links);
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
                return new LinkedEyeOfHarmonyUnit(buffer.readNBTTagCompoundFromBuffer(), true);
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error(e.getCause());
            }
            return null;
        }));
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing_EM() {
        this.onMachineBlockUpdate();
        List<FluidStack> inputFluidStack = getStoredFluidsForColor(Optional.empty());
        List<FluidStack> outputFluidStack = new ArrayList<>();
        List<ItemStack> inputItemStack = getStoredInputs();
        boolean hasUpdate = false;

        int totalUnits = mLinkedUnits.size();
        int currentIndex = 0;

        for (LinkedEyeOfHarmonyUnit unit : mLinkedUnits) {
            currentIndex++;
            boolean isLast = currentIndex == totalUnits;

            MTEEyeOfHarmony core = unit.mMetaTileEntity;
            if (core == null) continue;
            IEyeOfHarmonyControllerLink link = (IEyeOfHarmonyControllerLink) core;

            long astralAmount = link.gtnl$getAstralArrayAmount();

            EyeOfHarmonyRecipe recipe = findRecipe(core.getControllerSlot());

            long autoHelium = getAutoComputedAmount(
                recipe,
                astralAmount,
                (long) maxFluidAmount,
                (long) maxHeliumAmountSetting.get(),
                0);
            long autoHydrogen = getAutoComputedAmount(
                recipe,
                astralAmount,
                (long) maxFluidAmount,
                (long) maxHydrogenAmountSetting.get(),
                1);
            long autoRSM = getAutoComputedAmount(
                recipe,
                astralAmount,
                (long) maxFluidAmount,
                (long) maxRawStarMatterAmountSetting.get(),
                2);

            if (autoRSM > 0 && unit.maxHeliumAmount == -1 && unit.maxHydrogenAmount == -1) {
                autoHelium = 0;
                autoHydrogen = 0;
            }

            if ((autoHelium > 0 || autoHydrogen > 0) && unit.maxRawStarMatterAmount == -1) {
                autoRSM = 0;
            }

            long heliumMaxAmount = unit.maxHeliumAmount != -1 ? unit.maxHeliumAmount : autoHelium;
            long hydrogenMaxAmount = unit.maxHydrogenAmount != -1 ? unit.maxHydrogenAmount : autoHydrogen;
            long rawstarmatterMaxAmount = unit.maxRawStarMatterAmount != -1 ? unit.maxRawStarMatterAmount : autoRSM;

            unit.displayHeliumMax = heliumMaxAmount;
            unit.displayHydrogenMax = hydrogenMaxAmount;
            unit.displayRawStarMatterMax = rawstarmatterMaxAmount;

            long storedHelium = link.gtnl$getHeliumStored();
            long storedHydrogen = link.gtnl$getHydrogenStored();
            long storedRawstarmatter = link.gtnl$getStellarPlasmaStored();

            long workingHelium = storedHelium;
            long workingHydrogen = storedHydrogen;
            long workingRawstarmatter = storedRawstarmatter;

            if (storedHelium >= heliumMaxAmount && storedHydrogen >= hydrogenMaxAmount
                && storedRawstarmatter >= rawstarmatterMaxAmount) {
                continue;
            }

            for (ItemStack stack : inputItemStack) {
                if (!(stack.getItem() instanceof ItemFluidPacket)) continue;
                NBTTagCompound stackNbt = stack.getTagCompound();
                if (stackNbt == null) continue;
                NBTTagCompound fluidNBT = stackNbt.getCompoundTag("FluidStack");
                String fluidName = fluidNBT.getString("FluidName");
                long amount = fluidNBT.getLong("Amount");

                switch (fluidName) {
                    case "helium" -> workingHelium = handleFluidInput(
                        Materials.Helium.mGas,
                        workingHelium,
                        amount,
                        heliumMaxAmount,
                        outputFluidStack);
                    case "hydrogen" -> workingHydrogen = handleFluidInput(
                        Materials.Hydrogen.mGas,
                        workingHydrogen,
                        amount,
                        hydrogenMaxAmount,
                        outputFluidStack);
                    case "rawstarmatter" -> workingRawstarmatter = handleFluidInput(
                        MaterialsUEVplus.RawStarMatter.mFluid,
                        workingRawstarmatter,
                        amount,
                        rawstarmatterMaxAmount,
                        outputFluidStack);
                }
                stack.stackSize--;
            }

            workingHelium = tryConsumeFluidLong(inputFluidStack, Materials.Helium.mGas, workingHelium, heliumMaxAmount);
            workingHydrogen = tryConsumeFluidLong(
                inputFluidStack,
                Materials.Hydrogen.mGas,
                workingHydrogen,
                hydrogenMaxAmount);
            workingRawstarmatter = tryConsumeFluidLong(
                inputFluidStack,
                MaterialsUEVplus.RawStarMatter.mFluid,
                workingRawstarmatter,
                rawstarmatterMaxAmount);

            updateSlots();

            unit.heliumAmount = workingHelium;
            unit.hydrogenAmount = workingHydrogen;
            unit.rawStarMatterAmount = workingRawstarmatter;

            if (!outputFluidStack.isEmpty() || storedHelium != workingHelium
                || storedHydrogen != workingHydrogen
                || storedRawstarmatter != workingRawstarmatter) {

                link.gtnl$setHeliumStored(workingHelium);
                link.gtnl$setHydrogenStored(workingHydrogen);
                link.gtnl$setStellarPlasmaStored(workingRawstarmatter);

                if (!isLast) {
                    mergeFluidStacks(inputFluidStack, outputFluidStack);
                    outputFluidStack.clear();
                }

                hasUpdate = true;
            }
        }

        if (hasUpdate) {
            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = 20;
            mOutputFluids = outputFluidStack.toArray(new FluidStack[0]);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public long handleFluidInput(Fluid fluid, long current, long amount, long max, List<FluidStack> outputs) {
        if (current >= max || amount <= 0) return current;

        long space = max - current;
        long accepted = Math.min(amount, space);
        long leftover = amount - accepted;

        current += accepted;

        if (leftover > 0) {
            while (leftover > 0) {
                int split = (int) Math.min(leftover, Integer.MAX_VALUE);
                outputs.add(new FluidStack(fluid, split));
                leftover -= split;
            }
        }

        return current;
    }

    public void mergeFluidStacks(List<FluidStack> input, List<FluidStack> output) {
        for (FluidStack out : output) {
            boolean merged = false;
            for (FluidStack in : input) {
                if (in.isFluidEqual(out)) {
                    long sum = (long) in.amount + out.amount;
                    if (sum > Integer.MAX_VALUE) {
                        in.amount = Integer.MAX_VALUE;
                        long leftover = sum - Integer.MAX_VALUE;
                        while (leftover > 0) {
                            int split = (int) Math.min(leftover, Integer.MAX_VALUE);
                            input.add(new FluidStack(out.getFluid(), split));
                            leftover -= split;
                        }
                    } else {
                        in.amount = (int) sum;
                    }
                    merged = true;
                    break;
                }
            }
            if (!merged) input.add(out.copy());
        }
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        Map<GTUtility.ItemId, ItemStack> inputsFromME = new HashMap<>();
        for (MTEHatchInputBus tHatch : GTUtility.validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(GTUtility.ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            rList.addAll(Arrays.asList(dualInputHatch.getAllItems()));
        }

        ItemStack stackInSlot1 = getStackInSlot(1);
        if (GTUtility.isAnyIntegratedCircuit(stackInSlot1)) rList.add(stackInSlot1);
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;

    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            byte hatchColor = tHatch.getColor();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            setHatchRecipeMap(tHatch);
            if (tHatch instanceof MTEHatchMultiInput multiInputHatch) {
                for (FluidStack tFluid : multiInputHatch.getStoredFluid()) {
                    if (tFluid != null) {
                        rList.add(tFluid);
                    }
                }
            } else if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                    if (fluidStack != null) {
                        // Prevent the same fluid from different ME hatches from being recognized
                        inputsFromME.put(fluidStack.getFluid(), fluidStack);
                    }
                }
            } else {
                FluidStack fillableStack = tHatch.getFillableStack();
                if (fillableStack != null) {
                    rList.add(fillableStack);
                }
            }
        }

        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            rList.addAll(Arrays.asList(dualInputHatch.getAllFluids()));
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    public long tryConsumeFluidLong(List<FluidStack> stored, Fluid target, long current, long max) {
        if (current >= max) return current;

        long need = max - current;
        long available = 0L;

        for (FluidStack fs : stored) {
            if (fs != null && fs.getFluid() == target) {
                available += fs.amount;
            }
        }

        if (available <= 0) return current;

        long consume = Math.min(need, available);

        long drained = drainFluidLong(target, consume, true);
        current += drained;

        return current;
    }

    public long drainFluidLong(Fluid target, long amount, boolean doDrain) {
        long remaining = amount;
        for (MTEHatch hatch : getAllInputHatches()) {
            if (remaining <= 0) break;

            if (hatch instanceof IDualInputHatch dual && dual.supportsFluids()) {
                for (FluidStack stack : dual.getAllFluids()) {
                    if (stack != null && stack.getFluid() == target && stack.amount > 0) {
                        long deduct = Math.min(remaining, stack.amount);
                        if (doDrain) stack.amount -= (int) deduct;
                        remaining -= deduct;
                    }
                }
            } else if (hatch instanceof MTEHatchInput inputHatch && inputHatch.isValid()) {
                FluidStack request = new FluidStack(target, (int) Math.min(Integer.MAX_VALUE, remaining));
                FluidStack drained = inputHatch.drain(ForgeDirection.UNKNOWN, request, doDrain);
                if (drained != null) {
                    remaining -= drained.amount;
                }
            }
        }

        return amount - remaining;
    }

    public List<MTEHatch> getAllInputHatches() {
        List<MTEHatch> dualHatches = mDualInputHatches.stream()
            .map(h -> (MTEHatch) h)
            .collect(Collectors.toList());

        List<MTEHatch> allHatches = new ArrayList<>(mInputHatches);
        allHatches.addAll(dualHatches);

        return allHatches;
    }

    public static EyeOfHarmonyRecipe findRecipe(@Nullable ItemStack stack) {
        if (stack == null) return null;
        return TecTech.eyeOfHarmonyRecipeStorage.recipeLookUp(stack);
    }

    public static long getAutoComputedAmount(EyeOfHarmonyRecipe recipe, long astralAmount, long maxFluidAmount,
        long maxSetting, int type) {
        if (recipe == null) {
            return Math.min(maxFluidAmount, maxSetting);
        }

        long computed = 0;

        if (astralAmount > 0 && type == 2) {
            long parallelExponent = (long) Math.floor(Math.log(8 * Math.min(astralAmount, 8637)) / Math.log(1.7));
            long parallelAmount = (long) GTUtility.powInt(2, parallelExponent);
            computed = (long) (recipe.getHeliumRequirement() * (12.4 / 1_000_000f) * parallelAmount);
        } else {
            if (type == 0) {
                computed = recipe.getHeliumRequirement();
            } else if (type == 1) {
                computed = recipe.getHydrogenRequirement();
            }
        }

        return Math.min(Math.min(maxFluidAmount, maxSetting), computed);
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
                    buildHatchAdder(EyeOfHarmonyInjector.class).casingIndex(BlockGTCasingsTT.textureOffset)
                        .dot(1)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy.or(ExoticEnergy))
                        .buildAndChain(),
                    onElementPass(e -> e.mCountCasing++, ofBlock(sBlockCasingsTT, 0))))
            .build();
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
        return structureCheck_EM(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    public static INameFunction<EyeOfHarmonyInjector> MAX_HELIUM_AMOUNT_SETTING_NAME = (base, p) -> StatCollector
        .translateToLocal("Tooltip_EyeOfHarmonyInjector_HeliumParametrization");

    public static INameFunction<EyeOfHarmonyInjector> MAX_HYDROGEN_AMOUNT_SETTING_NAME = (base, p) -> StatCollector
        .translateToLocal("Tooltip_EyeOfHarmonyInjector_HydrogenParametrization");

    public static INameFunction<EyeOfHarmonyInjector> MAX_RAWSTARMATTER_AMOUNT_SETTING_NAME = (base, p) -> StatCollector
        .translateToLocal("Tooltip_EyeOfHarmonyInjector_RawStarMatterParametrization");

    public static IStatusFunction<EyeOfHarmonyInjector> MAX_FLUID_AMOUNT_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, maxFluidAmount / 2, maxFluidAmount, maxFluidAmount);

    @Override
    public void parametersInstantiation_EM() {
        super.parametersInstantiation_EM();
        Parameters.Group hatch_0 = parametrization.getGroup(0, false);
        Parameters.Group hatch_1 = parametrization.getGroup(1, false);
        maxHeliumAmountSetting = hatch_0
            .makeInParameter(0, maxFluidAmount, MAX_HELIUM_AMOUNT_SETTING_NAME, MAX_FLUID_AMOUNT_STATUS);
        maxHydrogenAmountSetting = hatch_0
            .makeInParameter(1, maxFluidAmount, MAX_HYDROGEN_AMOUNT_SETTING_NAME, MAX_FLUID_AMOUNT_STATUS);
        maxRawStarMatterAmountSetting = hatch_1
            .makeInParameter(0, maxFluidAmount, MAX_RAWSTARMATTER_AMOUNT_SETTING_NAME, MAX_FLUID_AMOUNT_STATUS);
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

    public void generateImportantInfo() {
        float index = 0;
        for (Block block : ModBlocks.blocks.values()) {
            float xAngle = generateRandomFloat(-MAX_ANGLE, MAX_ANGLE);
            float zAngle = generateRandomFloat(-MAX_ANGLE, MAX_ANGLE);
            index += 0.4f;
            float distance = index + generateRandomFloat(-0.2f, 0.15f);
            float scale = generateRandomFloat(0.3f, 0.6f);
            float rotationSpeed = generateRandomFloat(0.25f, 1f);
            float orbitSpeed = generateRandomFloat(0.5f, 1.5f);
            orbitingObjects.add(new OrbitingObject(block, distance, rotationSpeed, orbitSpeed, xAngle, zAngle, scale));
        }
    }
}
