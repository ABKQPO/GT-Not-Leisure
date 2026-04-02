package com.science.gtnl.common.machine.hatch;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.ImmutableSet;
import com.gtnewhorizons.modularui.api.GlStateManager;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.drawable.TextRenderer;
import com.gtnewhorizons.modularui.api.fluids.FluidTankLongDelegate;
import com.gtnewhorizons.modularui.api.fluids.FluidTanksHandler;
import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.ModularWindow.Builder;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Interactable;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularGui;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TabButton;
import com.gtnewhorizons.modularui.common.widget.TabContainer;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IRecipeProcessingAwareDualHatch;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.machine.FluidTankG;
import com.science.gtnl.utils.machine.ItemStackG;

import appeng.api.config.Actionable;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IItemDisplayRegistry.ItemRenderHook;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import appeng.client.render.AppEngRenderItem;
import appeng.core.AELog;
import appeng.core.localization.WailaText;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.Platform;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.objects.GTDualInputPattern;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.widget.AESlotWidget;
import gregtech.common.tileentities.machines.IDualInputHatchWithPattern;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.ISmartInputHatch;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class SuperDualInputHatchME extends MTEHatchInputBus
    implements IDualInputHatchWithPattern, IRecipeProcessingAwareDualHatch, IAddGregtechLogo, IDataCopyable,
    ISmartInputHatch, IPowerChannelState, IGridProxyable {

    public static int SLOT_COUNT = 100;
    public static final String COPIED_DATA_IDENTIFIER = "superStockingDualHatch";

    public boolean expediteRecipeCheck = false;
    public boolean justHadNewItems = false;
    public boolean allowAuto;

    public static Utils.VargsFunction<Object[], FluidStack[]> asFluidStack = (s) -> Arrays.stream(s)
        .flatMap(Arrays::stream)
        .map(f -> {
            if (f instanceof FluidTank tank) {
                return tank.getFluid();
            } else if (f instanceof FluidStack stack) {
                return stack;
            } else if (f == null) {
                /* ignore */return null;
            } else {
                throw new RuntimeException("only FluidStack or FluidTank are accepted");
            }
        })
        .filter(a -> a != null && a.amount > 0)
        .toArray(FluidStack[]::new);

    public SuperDualInputHatchME(int id, String name, String nameRegional, int tier, boolean autoPullAvailable) {
        super(id, name, nameRegional, tier, 1, new String[] { "" });
        allowAuto = autoPullAvailable;
    }

    public SuperDualInputHatchME(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures,
        boolean autoPullAvailable) {
        super(aName, aTier, 1, aDescription, aTextures);
        allowAuto = autoPullAvailable;
    }

    @Override
    public String[] getDescription() {
        List<String> strings = new ArrayList<>(8);
        strings.add(StatCollector.translateToLocal("Tooltip_SuperDualInputHatchME_00"));
        strings.add(
            StatCollector.translateToLocal("Tooltip_SuperDualInputHatchME_01") + GTValues.TIER_COLORS[mTier]
                + GTValues.VN[mTier]);
        strings.add(StatCollector.translateToLocal("Tooltip_SuperDualInputHatchME_02"));
        strings.add(StatCollector.translateToLocal("Tooltip_SuperDualInputHatchME_03"));

        if (allowAuto) {
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperDualInputHatchME_00"));
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperDualInputHatchME_01"));
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperDualInputHatchME_02"));
            strings.add(StatCollector.translateToLocal("Tooltip_AdvancedSuperDualInputHatchME_03"));
        }

        strings.add(StatCollector.translateToLocal("Tooltip_SuperDualInputHatchME_04"));
        strings.add(StatCollector.translateToLocal("Tooltip_SuperDualInputHatchME_05"));
        return strings.toArray(new String[0]);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperDualInputHatchME(mName, mTier, mDescriptionArray, mTextures, allowAuto);
    }

    public boolean autoPullItemList;
    public long minAutoPullItemAmount = 1;
    public long minAutoPullFluidAmount = 1;
    public int autoPullRefreshTime = 100;

    public ItemStack updateInformationSlot(int aIndex, ItemStack aStack) {

        if (aStack == null) {
            inventoryHandlerDisplay.setStackInSlot(aIndex, null);
        } else {
            AENetworkProxy proxy = getProxy();
            if (!proxy.isActive()) {
                inventoryHandlerDisplay.setStackInSlot(aIndex, null);
                return null;
            }

            if (!isAllowedToWork()) {
                this.i_shadow[aIndex] = null;
                this.i_saved[aIndex] = 0;
                this.i_client[aIndex] = 0;
                super.setInventorySlotContents(aIndex + SLOT_COUNT, null);
                return null;
            }

            try {
                IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                    .getItemInventory();
                IAEItemStack request = AEItemStack.create(i_mark[aIndex]);
                request.setStackSize(i_stored[aIndex]);
                IAEItemStack result = sg.extractItems(request, Actionable.SIMULATE, getRequestSource());
                ItemStack s = (result != null) ? result.getItemStack() : null;
                if (result != null) {
                    ItemStackG g = ItemStackG.fromAE(result, intmaxs);
                    result.setStackSize(g.stackSize());
                }
                i_client[aIndex] = result == null ? 0 : result.getStackSize();
                if (expediteRecipeCheck) {
                    ItemStack previous = this.i_mark[aIndex];
                    if (s != null) {
                        justHadNewItems = !ItemStack.areItemStacksEqual(s, previous);
                    }
                }
                inventoryHandlerDisplay.setStackInSlot(aIndex, s);
                return s;
            } catch (GridAccessException ignored) {}
        }

        return null;
    }

    public void updateInformationSlotF(int aIndex) {

        FluidStack fluidStack = f_mark[aIndex];
        if (fluidStack == null) {
            f_display[aIndex] = null;
            return;
        }

        AENetworkProxy proxy = getProxy();
        if (proxy == null || !proxy.isActive()) {
            f_display[aIndex] = null;
            return;
        }

        if (!isAllowedToWork()) {
            this.f_shadow[aIndex] = null;
            this.f_saved[aIndex] = 0;
            this.f_client[aIndex] = 0;
            return;
        }

        try {
            IMEMonitor<IAEFluidStack> sg = proxy.getStorage()
                .getFluidInventory();
            IAEFluidStack request = AEFluidStack.create(fluidStack);
            request.setStackSize(f_stored[aIndex]);
            IAEFluidStack result = sg.extractItems(request, Actionable.SIMULATE, getRequestSource());
            if (result != null) {
                FluidTankG g = new FluidTankG();
                g.fromAE(result, intmaxs);
                result.setStackSize(g.getFluidAmount());
            }
            FluidStack resultFluid = (result != null) ? result.getFluidStack() : null;
            f_client[aIndex] = result == null ? 0 : result.getStackSize();
            if (expediteRecipeCheck) {
                FluidStack previous = f_mark[aIndex];
                if (resultFluid != null) {
                    justHadNewItems = !resultFluid.isFluidEqual(previous);
                }
            }
            f_display[aIndex] = resultFluid;
        } catch (GridAccessException ignored) {}

    }

    public FluidStackTank createTankForFluidStack(FluidStack[] fluidStacks, int slotIndex, int capacity) {

        class IndexFluidStackTank extends FluidStackTank implements Supplier<Integer> {

            public IndexFluidStackTank(Supplier<FluidStack> getter, Consumer<FluidStack> setter, int capacity) {
                super(getter, setter, capacity);

            }

            @Override
            public Integer get() {

                return slotIndex;
            }
        }

        return new IndexFluidStackTank(() -> fluidStacks[slotIndex], (stack) -> {
            if (getBaseMetaTileEntity().isServerSide()) {
                return;
            }

            fluidStacks[slotIndex] = stack;
        }, capacity);

    }

    @Override
    public boolean doFastRecipeCheck() {
        return expediteRecipeCheck;
    }

    @Override
    public boolean justUpdated() {
        if (expediteRecipeCheck && isAllowedToWork()) {
            boolean ret = justHadNewItems;
            justHadNewItems = false;
            return ret;
        }
        return false;
    }

    public void setRecipeCheck(boolean value) {
        expediteRecipeCheck = value;
    }

    @Override
    public void setInventorySlotContents(int aIndex, ItemStack aStack) {
        if (expediteRecipeCheck && aStack != null) {
            justHadNewItems = true;
        }
        super.setInventorySlotContents(aIndex, aStack);
    }

    public boolean isAllowedToWork() {
        IGregTechTileEntity igte = getBaseMetaTileEntity();

        return igte != null && igte.isAllowedToWork();
    }

    @Override
    public int getCircuitSlot() {
        return 0;
    }

    @Override
    public int getCircuitSlotX() {
        return 8;
    }

    @Override
    public int getCircuitSlotY() {
        return 82;
    }

    @Override
    public int getGUIWidth() {
        return 392;
    }

    @Override
    public int getGUIHeight() {
        return 179;
    }

    @Override
    public void addUIWidgets(Builder builder, UIBuildContext buildContext) {
        updateAllInformationSlots();
        SlotWidget[] aeSlotWidgets = new SlotWidget[SLOT_COUNT];
        builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);

        for (int i = 15; i < SLOT_COUNT + 15; i++) {
            int slotID = i;
            buildContext.addSyncedWindow(i, (player) -> createStoredItemStackSizeWindow(player, slotID - 15));
        }

        for (int i = 15 + SLOT_COUNT; i < SLOT_COUNT + SLOT_COUNT + 15; i++) {
            int slotID = i;
            buildContext
                .addSyncedWindow(i, (player) -> createStoredFluidStackSizeWindow(player, slotID - 15 - SLOT_COUNT));
        }

        IDrawable tab1 = new ItemDrawable(ItemList.Hatch_Input_Bus_ME_Advanced.get(1)).withFixedSize(18, 18, 4, 6);
        IDrawable tab2 = new ItemDrawable(ItemList.Hatch_Input_ME_Advanced.get(1)).withFixedSize(18, 18, 4, 6);
        IDrawable tab3 = new ItemDrawable(GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Iron, 1))
            .withFixedSize(18, 18, 4, 6);

        Scrollable itemScrollable = new Scrollable().setVerticalScroll();
        Scrollable fluidScrollable = new Scrollable().setVerticalScroll();

        MultiChildWidget itemPage = new MultiChildWidget().addChild(
            SlotGroup.ofItemHandler(inventoryHandlerDisplay, 10)
                .startFromSlot(0)
                .endAtSlot(99)
                .phantom(true)
                .background(GTUITextures.SLOT_DARK_GRAY)
                .widgetCreator(slot -> aeSlotWidgets[slot.getSlotIndex()] = new AESlotWidget(slot) {

                    @Override
                    public List<String> getExtraTooltip() {
                        List<String> extraLines = new ArrayList<>();
                        if (i_client[slot.getSlotIndex()] >= 1000) {
                            extraLines.add(I18n.format("modularui.amount", i_client[slot.getSlotIndex()]));
                        }
                        if (isPhantom()) {
                            if (canControlAmount()) {
                                String[] lines = I18n.format("modularui.item.phantom.control")
                                    .split("\\\\n");
                                extraLines.addAll(Arrays.asList(lines));
                            } else if (!interactionDisabled) {
                                extraLines.add(I18n.format("modularui.phantom.single.clear"));
                            }
                        }
                        return extraLines.isEmpty() ? Collections.emptyList() : extraLines;
                    }

                    @SideOnly(Side.CLIENT)
                    public RenderItem setItemRender(RenderItem item) {
                        RenderItem ri = ModularGui.getItemRenderer();
                        ModularGui.setItemRenderer(item);
                        return ri;
                    }

                    @Override
                    @SideOnly(Side.CLIENT)
                    public void drawSlot(Slot slotIn) {
                        AppEngRenderItem aeRenderItem = new AppEngRenderItem();
                        AppEngRenderItem.POST_HOOKS.add(HookHolder.SKIP_ITEM_STACK_SIZE_HOOK);
                        RenderItem pIR = this.setItemRender(aeRenderItem);
                        try {
                            IAEItemStack is = Platform.getAEStackInSlot(slotIn);
                            if (is != null) {
                                is.setStackSize(i_client[slotIn.getSlotIndex()]);
                            }
                            aeRenderItem.setAeStack(is);

                            drawSlot(slotIn, true);
                        } catch (Exception err) {
                            AELog.warn("[AppEng] AE prevented crash while drawing slot: " + err);
                        }
                        AppEngRenderItem.POST_HOOKS.remove(HookHolder.SKIP_ITEM_STACK_SIZE_HOOK);
                        this.setItemRender(pIR);
                    }

                    @SideOnly(Side.CLIENT)
                    public TextRenderer textRenderer0;

                    {
                        if (FMLCommonHandler.instance()
                            .getSide() == Side.CLIENT) {
                            textRenderer0 = new TextRenderer();
                        }

                    }

                    @SideOnly(Side.CLIENT)
                    public void drawSlot(Slot slotIn, boolean drawStackSize) {
                        super.drawSlot(slotIn, false);

                        if (drawStackSize) {

                            ItemStack itemstack = getItemStackForRendering(slotIn);
                            if (itemstack != null) {

                                getContext().getScreen()
                                    .setZ(100f);
                                ModularGui.getItemRenderer().zLevel = 100.0F;
                                GlStateManager.enableRescaleNormal();
                                GlStateManager.enableLighting();
                                RenderHelper.enableGUIStandardItemLighting();
                                GlStateManager.enableDepth();
                                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                                GlStateManager.pushMatrix();
                                // so that item z levels are properly ordered
                                GlStateManager.translate(0, 0, 150 * getWindowLayer());

                                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                                GlStateManager.popMatrix();
                                long amount = i_client[slotIn.getSlotIndex()];
                                if (drawStackSize) {
                                    if (amount < 0) {
                                        amount = itemstack.stackSize;
                                    }
                                    // render the amount overlay
                                    if (amount > 1) {
                                        String amountText = numberFormat.formatWithSuffix(amount, new StringBuffer())
                                            .toString();
                                        float scale = 1f;
                                        if (amountText.length() == 3) {
                                            scale = 0.8f;
                                        } else if (amountText.length() == 4) {
                                            scale = 0.6f;
                                        } else if (amountText.length() > 4) {
                                            scale = 0.5f;
                                        }
                                        textRenderer0.setShadow(true);
                                        textRenderer0.setScale(scale);
                                        textRenderer0.setColor(Color.WHITE.normal);
                                        textRenderer0
                                            .setAlignment(Alignment.BottomRight, size.width - 1, size.height - 1);
                                        textRenderer0.setPos(1, 1);
                                        GlStateManager.disableLighting();
                                        GlStateManager.disableDepth();
                                        GlStateManager.disableBlend();
                                        textRenderer0.draw(amountText);
                                        GlStateManager.enableLighting();
                                        GlStateManager.enableDepth();
                                        GlStateManager.enableBlend();
                                    }
                                }

                                GlStateManager.disableDepth();
                                GL11.glDisable(GL11.GL_BLEND);
                                ModularGui.getItemRenderer().zLevel = 0.0F;
                                getContext().getScreen()
                                    .setZ(0f);

                            }
                        }

                    }

                }.setOverwriteItemStackTooltip(s -> rewriteItem(slot, s))
                    .disableInteraction())
                .build()
                .setPos(205, 0))

            .addChild(
                SlotGroup.ofItemHandler(inventoryHandlerMark, 10)
                    .startFromSlot(0)
                    .endAtSlot(99)
                    .phantom(true)
                    .slotCreator(index -> new BaseSlot(inventoryHandlerMark, index, true) {

                        @Override
                        public boolean isEnabled() {
                            return !autoPullItemList && super.isEnabled();
                        }
                    })
                    .widgetCreator(slot -> (SlotWidget) new SlotWidget(slot) {

                        @Override
                        public ClickResult onClick(int buttonId, boolean doubleClick) {
                            if (interactionDisabled) return ClickResult.ACCEPT;
                            if (buttonId == 2) {
                                ClickData clickData = ClickData.create(buttonId, doubleClick);
                                syncToServer(15, clickData::writeToPacket);
                                return ClickResult.SUCCESS;
                            }
                            return super.onClick(buttonId, doubleClick);
                        }

                        @Override
                        public void readOnServer(int id, PacketBuffer buf) throws IOException {
                            if (id == 15) click(ClickData.readPacket(buf));
                            super.readOnServer(id, buf);
                        }

                        public void click(ClickData clickData) {
                            if (interactionDisabled) return;
                            if (clickData.mouseButton == 2) {
                                getContext().openSyncedWindow(getMcSlot().slotNumber - 21 - SLOT_COUNT);
                            }
                        }

                        @Override
                        public void phantomClick(ClickData clickData, ItemStack cursorStack) {
                            if (clickData.mouseButton != 0 || !getMcSlot().isEnabled()) return;
                            int aSlotIndex = getMcSlot().getSlotIndex();
                            if (cursorStack == null) {
                                getMcSlot().putStack(null);
                            } else {
                                if (containsSuchStack(cursorStack)) return;
                                getMcSlot().putStack(GTUtility.copyAmount(1, cursorStack));
                            }
                            if (getBaseMetaTileEntity().isServerSide()) {
                                ItemStack newInfo = updateInformationSlot(aSlotIndex, cursorStack);
                                aeSlotWidgets[getMcSlot().getSlotIndex()].getMcSlot()
                                    .putStack(newInfo);
                            }
                        }

                        @Override
                        public boolean onMouseScroll(int direction) {
                            return false;
                        }

                        @Override
                        public IDrawable[] getBackground() {
                            IDrawable slot;
                            if (autoPullItemList) {
                                slot = GTUITextures.SLOT_DARK_GRAY;
                            } else {
                                slot = ModularUITextures.ITEM_SLOT;
                            }
                            return new IDrawable[] { slot, GTUITextures.OVERLAY_SLOT_ARROW_ME };
                        }

                        @Override
                        public List<String> getExtraTooltip() {
                            if (autoPullItemList) {
                                return Collections.singletonList(
                                    StatCollector.translateToLocal("GT5U.machines.stocking_bus.cannot_set_slot"));
                            } else {
                                return Collections
                                    .singletonList(StatCollector.translateToLocal("modularui.phantom.single.clear"));
                            }
                        }

                        public boolean containsSuchStack(ItemStack tStack) {
                            for (int i = 0; i < SLOT_COUNT; ++i) {
                                if (GTUtility.areStacksEqual(i_mark[i], tStack, false)) return true;
                            }
                            return false;
                        }
                    }.dynamicTooltip(() -> {
                        if (autoPullItemList) {
                            return Collections.singletonList(
                                StatCollector.translateToLocal("GT5U.machines.stocking_bus.cannot_set_slot"));
                        } else {
                            return Collections.emptyList();
                        }
                    })
                        .setUpdateTooltipEveryTick(true))
                    .build()
                    .setPos(7, 0));
        itemScrollable.widget(itemPage);

        MultiChildWidget fluidPage = new MultiChildWidget().addChild(
            SlotGroup.ofFluidTanks(
                IntStream.range(0, SLOT_COUNT)
                    .mapToObj(index -> createTankForFluidStack(f_mark, index, 1))
                    .collect(Collectors.toList()),
                10)
                .phantom(true)
                .widgetCreator((slotIndex, h) -> (FluidSlotWidget) new FluidSlotWidget(h) {

                    @Override
                    public void tryClickPhantom(ClickData clickData, ItemStack cursorStack) {
                        IGregTechTileEntity gtTE = getBaseMetaTileEntity();
                        if (gtTE.isServerSide() && clickData.mouseButton == 2) {
                            getContext().openSyncedWindow(slotIndex + 15 + SLOT_COUNT);
                            return;
                        }

                        if (clickData.mouseButton != 0 || autoPullItemList) return;

                        FluidStack heldFluid = getFluidForPhantomItem(cursorStack);
                        if (cursorStack == null) {
                            f_mark[slotIndex] = null;
                        } else {
                            if (containsSuchStack(heldFluid)) return;
                            f_mark[slotIndex] = heldFluid;
                        }
                        if (gtTE.isServerSide()) {
                            updateInformationSlotF(slotIndex);
                            detectAndSendChanges(false);
                        }
                    }

                    public boolean containsSuchStack(FluidStack tStack) {
                        for (int i = 0; i < SLOT_COUNT; ++i) {
                            if (GTUtility.areFluidsEqual(f_mark[i], tStack, false)) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public void tryScrollPhantom(int direction) {}

                    @Override
                    public IDrawable[] getBackground() {
                        IDrawable slot;
                        if (autoPullItemList) {
                            slot = GTUITextures.SLOT_DARK_GRAY;
                        } else {
                            slot = ModularUITextures.FLUID_SLOT;
                        }
                        return new IDrawable[] { slot, GTUITextures.OVERLAY_SLOT_ARROW_ME };
                    }

                    @Override
                    public void buildTooltip(List<Text> tooltip) {
                        FluidStack fluid = getContent();
                        if (fluid != null) {
                            addFluidNameInfo(tooltip, fluid);

                            if (!autoPullItemList) {
                                tooltip.add(Text.localised("modularui.phantom.single.clear"));
                            }
                        } else {
                            tooltip.add(
                                Text.localised("modularui.fluid.empty")
                                    .format(EnumChatFormatting.WHITE));
                        }

                        if (autoPullItemList) {
                            tooltip.add(Text.localised("GT5U.machines.stocking_bus.cannot_set_slot"));
                        }
                    }
                }.setUpdateTooltipEveryTick(true))
                .build()
                .setPos(7, 0))

            .addChild(
                SlotGroup.ofFluidTanks(
                    IntStream.range(0, SLOT_COUNT)
                        .mapToObj(index -> createTankForFluidStack(f_display, index, Integer.MAX_VALUE))
                        .collect(Collectors.toList()),
                    10)
                    .tankHandlerCreator(s -> new FluidTanksHandler(new FluidTankLongDelegate(s)) {

                        @Override
                        public long getTankStoredAmount(int tank) {
                            return f_client[((Supplier<Integer>) s).get()];
                        }

                    })
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

                                tooltip.add(
                                    Text.localised(
                                        "modularui.fluid.phantom.amount",
                                        /* df.format */(f_client[slotIndex])));

                                if (f_client[slotIndex] > Integer.MAX_VALUE) {
                                    double cp = f_client[slotIndex] * 1d / Integer.MAX_VALUE;

                                    tooltip.add(Text.localised("Info_AdvancedSuperDualInputHatchME_ExceedIntMax"));

                                    tooltip.add(new Text(df2.format(cp) + "*int.max"));
                                }

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
                    .setPos(new Pos2d(205, 0)));
        fluidScrollable.widget(fluidPage);

        builder.widget(
            new TabContainer().setButtonSize(28, 32)
                .addTabButton(
                    new TabButton(0)
                        .setBackground(
                            true,
                            ModularUITextures.VANILLA_TAB_RIGHT.getSubArea(0f, 0f, 1f, 1 / 3f)
                                .getSubArea(0, 0, 0.5f, 1f),
                            tab1)
                        .setBackground(
                            false,
                            ModularUITextures.VANILLA_TAB_RIGHT.getSubArea(0f, 0f, 1f, 1 / 3f)
                                .getSubArea(0.5f, 0, 1f, 1f),
                            tab1)
                        .setPos(getGUIWidth() - 4, 0))
                .addTabButton(
                    new TabButton(1)
                        .setBackground(
                            true,
                            ModularUITextures.VANILLA_TAB_RIGHT.getSubArea(0f, 1 / 3f, 1f, 2 / 3f)
                                .getSubArea(0, 0, 0.5f, 1f),
                            tab2)
                        .setBackground(
                            false,
                            ModularUITextures.VANILLA_TAB_RIGHT.getSubArea(0f, 1 / 3f, 1f, 2 / 3f)
                                .getSubArea(0.5f, 0, 1f, 1f),
                            tab2)
                        .setPos(getGUIWidth() - 4, 28))
                .addTabButton(
                    new TabButton(2)
                        .setBackground(
                            true,
                            ModularUITextures.VANILLA_TAB_RIGHT.getSubArea(0f, 1 / 3f, 1f, 2 / 3f)
                                .getSubArea(0, 0, 0.5f, 1f),
                            tab3)
                        .setBackground(
                            false,
                            ModularUITextures.VANILLA_TAB_RIGHT.getSubArea(0f, 1 / 3f, 1f, 2 / 3f)
                                .getSubArea(0.5f, 0, 1f, 1f),
                            tab3)
                        .setPos(getGUIWidth() - 4, 56))
                .addPage(
                    itemScrollable.setSize(18 * 21 + 10, 72)
                        .setPos(0, 9))
                .addPage(
                    fluidScrollable.setSize(18 * 21 + 10, 72)
                        .setPos(0, 9))
                .addPage(
                    new MultiChildWidget().addChild(
                        TextWidget.localised("GT5U.machines.stocking_bus.refresh_time")
                            .setPos(3, 22)
                            .setSize(74, 14))
                        .addChild(
                            new NumericWidget().setSetter(val -> autoPullRefreshTime = (int) val)
                                .setGetter(() -> autoPullRefreshTime)
                                .setBounds(1, Integer.MAX_VALUE)
                                .setScrollValues(1, 4, 64)
                                .setTextAlignment(Alignment.Center)
                                .setTextColor(Color.WHITE.normal)
                                .setSize(74, 18)
                                .setPos(3, 3)
                                .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD))
                        .addChild(
                            TextWidget.localised("Info_AdvancedSuperDualInputHatchME_IntMax")
                                .setPos(3, 64)
                                .setSize(70, 14)
                                .addTooltip(
                                    StatCollector.translateToLocal("Info_AdvancedSuperDualInputHatchME_IntMaxTooltip")))
                        .addChild(
                            new NumericWidget().setSetter(val -> intmaxs = (int) val)
                                .setGetter(() -> intmaxs)
                                .setBounds(1, 10000)
                                .setScrollValues(1, 10, 100)
                                .setTextAlignment(Alignment.Center)
                                .setTextColor(Color.WHITE.normal)
                                .setSize(74, 18)
                                .setPos(3, 43)
                                .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD))
                        .addChild(
                            TextWidget.localised("Info_SuperDualInputHatchME_03")
                                .setPos(80, 22)
                                .setSize(74, 14))
                        .addChild(
                            new NumericWidget().setSetter(val -> minAutoPullItemAmount = (long) val)
                                .setGetter(() -> minAutoPullItemAmount)
                                .setBounds(1, Long.MAX_VALUE)
                                .setScrollValues(1, 4, 64)
                                .setTextAlignment(Alignment.Center)
                                .setTextColor(Color.WHITE.normal)
                                .setSize(74, 18)
                                .setPos(80, 3)
                                .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD))
                        .addChild(
                            TextWidget.localised("Info_SuperDualInputHatchME_04")
                                .setPos(80, 64)
                                .setSize(74, 14))
                        .addChild(
                            new NumericWidget().setSetter(val -> minAutoPullFluidAmount = (long) val)
                                .setGetter(() -> minAutoPullFluidAmount)
                                .setBounds(1, Long.MAX_VALUE)
                                .setScrollValues(1, 4, 64)
                                .setTextAlignment(Alignment.Center)
                                .setTextColor(Color.WHITE.normal)
                                .setSize(74, 18)
                                .setPos(80, 43)
                                .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD))
                        .addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
                            if (clickData.mouseButton == 0) {
                                if (allowAuto) setAutoPullItemList(!autoPullItemList);
                            }
                        })
                            .setBackground(() -> {
                                if (autoPullItemList) {
                                    return new IDrawable[] { GTUITextures.BUTTON_STANDARD_PRESSED,
                                        GTUITextures.OVERLAY_BUTTON_AUTOPULL_ME };
                                } else {
                                    return new IDrawable[] { GTUITextures.BUTTON_STANDARD,
                                        GTUITextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED };
                                }
                            })
                            .addTooltip(
                                StatCollector.translateToLocal("GT5U.machines.stocking_bus.auto_pull.tooltip.1"))
                            .setEnabledForce(allowAuto)
                            .setSize(16, 16)
                            .setPos(157, 4))
                        .addChild(
                            TextWidget.localised("GT5U.machines.stocking_bus.force_check")
                                .setPos(177, 44)
                                .setSize(50, 14))
                        .addChild(
                            new CycleButtonWidget().setToggle(() -> expediteRecipeCheck, this::setRecipeCheck)
                                .setTextureGetter(
                                    state -> expediteRecipeCheck ? GTUITextures.OVERLAY_BUTTON_CHECKMARK
                                        : GTUITextures.OVERLAY_BUTTON_CROSS)
                                .setBackground(GTUITextures.BUTTON_STANDARD)
                                .setPos(157, 44)
                                .setSize(16, 16)
                                .addTooltip(
                                    StatCollector.translateToLocal("GT5U.machines.stocking_bus.hatch_warning")))));

        builder.widget(
            new FakeSyncWidget.BooleanSyncer(() -> autoPullItemList, SuperDualInputHatchME.this::setAutoPullItemList)
                .setSynced(false, true));

        builder.widget(TextWidget.dynamicText(() -> {
            boolean isActive = isActive();
            boolean isPowered = isPowered();
            boolean isBooting = isBooting();

            String state = WailaText.getPowerState(isActive, isPowered, isBooting);

            if (isActive && isPowered) {
                return new Text(
                    MessageFormat.format(
                        "{0}{1}§f ({2})",
                        EnumChatFormatting.GREEN,
                        state,
                        StatCollector
                            .translateToLocal(isAllowedToWork() ? "GT5U.gui.text.enabled" : "GT5U.gui.text.disabled")));
            } else {
                return new Text(EnumChatFormatting.DARK_RED + state);
            }
        })
            .setTextAlignment(Alignment.Center)
            .setSize(130, 9)
            .setPos(131, 84));

        for (int ii = 0; ii < SLOT_COUNT; ii++) {
            int i = ii;
            builder.widget(new FakeSyncWidget.LongSyncer(() -> {
                // i_client[i]=i_saved[i];
                return i_client[i];
            }, s -> i_client[i] = s).setSynced(false, true)

            );
        }

        for (int ii = 0; ii < SLOT_COUNT; ii++) {
            int i = ii;
            builder.widget(new FakeSyncWidget.LongSyncer(() -> {
                // f_client[i]=f_shadow[i].gtnl$getFluidAmount();
                return f_client[i];
            }, s -> f_client[i] = s).setSynced(false, true));
        }

        addGregTechLogo(builder);
    }

    public ModularWindow createStoredItemStackSizeWindow(EntityPlayer player, int slotID) {
        return createStoredStackSizeWindow(player, slotID, i_stored, "Info_SuperDualInputHatchME_00");
    }

    public ModularWindow createStoredFluidStackSizeWindow(EntityPlayer player, int slotID) {
        return createStoredStackSizeWindow(player, slotID, f_stored, "Info_SuperDualInputHatchME_01");
    }

    public ModularWindow createStoredStackSizeWindow(EntityPlayer player, int slotID, long[] storedArray,
        String titleKey) {
        final int WIDTH = 110;
        final int HEIGHT = 66;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();

        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)));

        builder.widget(
            TextWidget.localised(titleKey)
                .setPos(3, 6)
                .setSize(106, 14))
            .widget(
                new TextWidget(StatCollector.translateToLocal("Info_SuperDualInputHatchME_02") + slotID).setPos(3, 20)
                    .setSize(106, 14))
            .widget(
                new NumericWidget().setSetter(val -> storedArray[slotID] = (long) val)
                    .setGetter(() -> storedArray[slotID])
                    .setBounds(1, Long.MAX_VALUE)
                    .setScrollValues(1, 100_000, 1_000_000)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(106, 18)
                    .setPos(3, 36)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .attachSyncer(
                        new FakeSyncWidget.LongSyncer(() -> storedArray[slotID], i -> storedArray[slotID] = i),
                        builder));

        return builder.build();
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(367, 81));
    }

    public long[] i_client = new long[SLOT_COUNT];
    public long[] f_client = new long[SLOT_COUNT];

    public DecimalFormat df2 = new DecimalFormat("#,###.00");
    public DecimalFormat df = new DecimalFormat("#,###");

    public List<String> rewriteItem(BaseSlot slot, List<String> s) {

        int i = slot.getSlotIndex();
        s.add("size:" + df.format(i_client[i]));
        if (i_client[i] > Integer.MAX_VALUE) {
            double cp = i_client[i] * 1d / Integer.MAX_VALUE;
            s.add(StatCollector.translateToLocal("Info_AdvancedSuperDualInputHatchME_ExceedIntMax"));
            s.add(df2.format(cp) + "*int.max");

        }
        return s;
    }

    public void setAutoPullItemList(boolean pullItemList) {

        autoPullItemList = pullItemList;
        if (!autoPullItemList) {
            for (int i = 0; i < SLOT_COUNT; i++) {
                i_mark[i] = null;
                f_mark[i] = null;
            }
        } else {
            refreshItemList();
            refreshItemListF();
        }
        updateAllInformationSlots();
    }

    public void refreshItemList() {
        if (!isActive()) return;
        AENetworkProxy proxy = getProxy();
        try {
            IMEMonitor<IAEItemStack> sg = proxy.getStorage()
                .getItemInventory();
            Iterator<IAEItemStack> iterator = sg.getStorageList()
                .iterator();
            int index = 0;
            while (iterator.hasNext() && index < SLOT_COUNT) {
                IAEItemStack currItem = iterator.next();
                if (currItem.getStackSize() >= minAutoPullItemAmount) {
                    ItemStack itemstack = GTUtility.copyAmount(
                        i_stored[index] == Long.MAX_VALUE ? 1 : (int) Math.min(Integer.MAX_VALUE, i_stored[index]),
                        currItem.getItemStack());
                    if (expediteRecipeCheck) {
                        ItemStack previous = this.mInventory[index];
                        if (itemstack != null) {
                            justHadNewItems = !ItemStack.areItemStacksEqual(itemstack, previous);
                        }
                    }

                    this.i_mark[index] = itemstack;
                    index++;
                }
            }
            for (int i = index; i < SLOT_COUNT; i++) {
                i_mark[i] = null;
            }

        } catch (GridAccessException ignored) {}
    }

    public void refreshItemListF() {
        if (!isActive()) return;
        AENetworkProxy proxy = getProxy();
        try {
            IMEMonitor<IAEFluidStack> sg = proxy.getStorage()
                .getFluidInventory();
            Iterator<IAEFluidStack> iterator = sg.getStorageList()
                .iterator();
            int index = 0;
            while (iterator.hasNext() && index < SLOT_COUNT) {
                IAEFluidStack currItem = iterator.next();
                if (currItem.getStackSize() >= minAutoPullFluidAmount) {
                    FluidStack fluidstack = GTUtility.copyAmount(
                        f_stored[index] == Long.MAX_VALUE ? 1 : (int) Math.min(Integer.MAX_VALUE, f_stored[index]),
                        currItem.getFluidStack());
                    if (expediteRecipeCheck) {
                        FluidStack previous = this.f_mark[index];
                        if (fluidstack != null) {
                            justHadNewItems = !fluidstack.isFluidEqual(previous);
                        }
                    }

                    this.f_mark[index] = fluidstack;
                    index++;
                }
            }

            for (int i = index; i < SLOT_COUNT; i++) {
                f_mark[i] = null;
            }
        } catch (GridAccessException ignored) {}
    }

    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (aTimer % autoPullRefreshTime == 0 && autoPullItemList) {
                refreshItemList();
                refreshItemListF();
            }
            if (aTimer % 20 == 0) {
                getBaseMetaTileEntity().setActive(isActive());
            }
        }

        super.onPostTick(aBaseMetaTileEntity, aTimer);
    }

    public BaseActionSource getRequestSource() {
        if (requestSource == null) requestSource = new MachineSource((IActionHost) getBaseMetaTileEntity());
        return requestSource;
    }

    public MachineSource requestSource;// = new MachineSource(((IActionHost) getBaseMetaTileEntity()));
    public boolean recipe;
    public int intmaxs = 3;

    @Override
    public void startRecipeProcessing() {
        recipe = true;
        for (int i = 0; i < SLOT_COUNT; i++) {
            i_shadow[i] = null;
            i_saved[i] = 0;
            if (i_mark[i] != null) {

                try {
                    IAEItemStack possible = null;
                    if (i_mark[i] != null) possible = getProxy().getStorage()
                        .getItemInventory()
                        .extractItems(
                            AEItemStack.create(i_mark[i])
                                .setStackSize(i_stored[i]),
                            Actionable.SIMULATE,
                            getRequestSource());
                    i_shadow[i] = possible == null ? null : ItemStackG.fromAE(possible, intmaxs);
                    if (i_shadow[i] != null) i_saved[i] = i_shadow[i].stackSize();

                } catch (GridAccessException ignored) {}
            }
        }
        for (int i = 0; i < SLOT_COUNT; i++) {
            f_shadow[i].setFluid(null);
            f_saved[i] = 0;
            if (f_mark[i] != null) {

                try {
                    IAEFluidStack possible = null;
                    if (f_mark[i] != null) {
                        possible = getProxy().getStorage()
                            .getFluidInventory()
                            .extractItems(
                                AEFluidStack.create(f_mark[i])
                                    .setStackSize(f_stored[i]),
                                Actionable.SIMULATE,
                                getRequestSource());
                    }
                    if (possible != null) {
                        f_shadow[i].fromAE(possible, intmaxs);
                    }
                    if (f_shadow[i] != null) f_saved[i] = f_shadow[i].getFluidAmount();

                } catch (GridAccessException ignored) {}
            }
        }
    }

    @Override
    public CheckRecipeResult endRecipeProcessing(MTEMultiBlockBase controller) {
        recipe = false;
        for (int i = 0; i < SLOT_COUNT; i++) {
            long current = i_shadow[i] == null ? 0 : i_shadow[i].stackSize();
            long original = i_saved[i];
            if (current > original) {
                throw new AssertionError("?");
            }
            if (current < 0) {
                throw new AssertionError("??");
            }
            if (current < original) {
                long delta = original - current;
                if (i_mark[i] == null) {
                    ScienceNotLeisure.LOG.fatal("marked item missing!");
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }
                IAEItemStack toextract = AEItemStack.create(i_mark[i])
                    .setStackSize(delta);
                try {
                    IAEItemStack get = getProxy().getStorage()
                        .getItemInventory()
                        .extractItems(toextract, Actionable.MODULATE, getRequestSource());
                    if (get == null || get.getStackSize() != get.getStackSize()) {
                        IGregTechTileEntity te = controller.getBaseMetaTileEntity();
                        ScienceNotLeisure.LOG.fatal(
                            "Super Dual Input Hatch ME cannot extract! at {}, {}, {}",
                            te.getXCoord(),
                            te.getYCoord(),
                            te.getZCoord());
                        controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                        return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                    }
                } catch (GridAccessException e) {
                    e.printStackTrace();
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }

            }

        }

        for (int i = 0; i < SLOT_COUNT; i++) {
            long current = f_shadow[i] == null ? 0 : f_shadow[i].getFluidAmount();
            long original = f_saved[i];
            if (current > original) {
                throw new AssertionError("?");
            }
            if (current < 0) {
                throw new AssertionError("??");
            }
            if (current < original) {
                long delta = original - current;
                if (f_mark[i] == null) {
                    ScienceNotLeisure.LOG.fatal("marked fluid missing!");
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }
                IAEFluidStack toextract = AEFluidStack.create(f_mark[i])
                    .setStackSize(delta);
                try {
                    IAEFluidStack get = getProxy().getStorage()
                        .getFluidInventory()
                        .extractItems(toextract, Actionable.MODULATE, getRequestSource());
                    if (get == null || get.getStackSize() != get.getStackSize()) {
                        ScienceNotLeisure.LOG.fatal("cannot extract!");
                        controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                        return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                    }
                } catch (GridAccessException e) {
                    e.printStackTrace();
                    controller.stopMachine(ShutDownReasonRegistry.CRITICAL_NONE);
                    return SimpleCheckRecipeResult.ofFailurePersistOnShutdown("stocking_bus_fail_extraction");
                }

            }

        }

        updateAllInformationSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public void updateAllInformationSlots() {
        for (int index = 0; index < SLOT_COUNT; index++) {
            updateInformationSlot(index, i_mark[index]);
        }

        for (int index = 0; index < SLOT_COUNT; index++) {
            updateInformationSlotF(index/* , f_mark[index] */);
        }

    }

    public ItemStack[] i_mark = new ItemStack[SLOT_COUNT];
    public ItemStackG[] i_shadow = new ItemStackG[SLOT_COUNT];
    public ItemStack[] i_display = new ItemStack[SLOT_COUNT];
    public long[] i_saved = new long[SLOT_COUNT];
    public long[] i_stored = new long[SLOT_COUNT];

    public FluidStack[] f_mark = new FluidStack[SLOT_COUNT];
    public FluidTankG[] f_shadow = new FluidTankG[SLOT_COUNT];
    public FluidStack[] f_display = new FluidStack[SLOT_COUNT];
    public long[] f_saved = new long[SLOT_COUNT];
    public long[] f_stored = new long[SLOT_COUNT];

    {
        Arrays.fill(i_stored, Long.MAX_VALUE);
        Arrays.fill(f_stored, Long.MAX_VALUE);

        for (int i = 0; i < SLOT_COUNT; i++) {
            f_shadow[i] = new FluidTankG();

        }
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        getProxy().onReady();
    }

    @Override
    public Iterator<? extends IDualInputInventoryWithPattern> inventories() {
        if (off || !recipe || isEmpty()) {
            return Collections.emptyIterator();
        }

        Iterator<? extends IDualInputInventoryWithPattern> itr = getItr();
        return itr != null ? itr : Collections.emptyIterator();
    }

    public boolean isEmpty() {
        for (ItemStackG i : i_shadow) {
            if (i != null && i.stackSize() > 0) return false;
        }
        for (FluidTankG i : f_shadow) {
            if (i != null && i.getFluidAmount() > 0) return false;
        }
        return true;
    }

    public interface NonCachedDualInputInventory extends IDualInputInventoryWithPattern {

        @Override
        default boolean shouldBeCached() {

            return false;
        }
    }

    public Iterator<? extends IDualInputInventoryWithPattern> getItr() {

        IDualInputInventoryWithPattern inventory = new NonCachedDualInputInventory() {

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public ItemStack[] getItemInputs() {
                return Utils.filterStack.apply(Utils.flat(i_shadow), new ItemStack[] { getStackInSlot(0) });
            }

            @Override
            public FluidStack[] getFluidInputs() {
                return asFluidStack.apply(Utils.flat(f_shadow));
            }

            @Override
            public GTDualInputPattern getPatternInputs() {
                return new GTDualInputPattern() {

                    {
                        inputItems = getItemInputs();
                    }
                    {
                        inputFluid = getFluidInputs();
                    }
                };
            }
        };

        return ImmutableSet.of(inventory)
            .iterator();
    }

    @Override
    public Optional<IDualInputInventory> getFirstNonEmptyInventory() {
        if (off) return Optional.empty();
        if (!recipe) {
            return Optional.empty();// huh...
        }
        Iterator<? extends IDualInputInventory> x = inventories();

        return x.hasNext() ? Optional.of(x.next()) : Optional.empty();
    }

    @Override
    public boolean supportsFluids() {
        return true;
    }

    @Override
    public ItemStack[] getSharedItems() {
        return new ItemStack[0];
    }

    @Override
    public void setProcessingLogic(ProcessingLogic pl) {}

    public AENetworkProxy gridProxy;

    @Override
    public AENetworkProxy getProxy() {

        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(
                    this,
                    "proxy",
                    allowAuto ? GTNLItemList.AdvancedSuperDualInputHatchME.get(1)
                        : GTNLItemList.SuperDualInputHatchME.get(1),
                    true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                updateValidGridProxySides();
                if (getBaseMetaTileEntity().getWorld() != null) gridProxy.setOwner(
                    getBaseMetaTileEntity().getWorld()
                        .getPlayerEntityByName(getBaseMetaTileEntity().getOwnerName()));
            }
        }
        return this.gridProxy;
    }

    @Override
    public boolean isPowered() {
        return getProxy().isPowered();
    }

    @Override
    public boolean isActive() {
        return getProxy().isActive();
    }

    boolean additionalConnection;

    public void updateValidGridProxySides() {

        if (additionalConnection) {
            getProxy().setValidSides(EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN)));
        } else {
            getProxy().setValidSides(EnumSet.of(getBaseMetaTileEntity().getFrontFacing()));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setBoolean("additionalConnection", additionalConnection);
        aNBT.setBoolean("allowAuto", allowAuto);
        aNBT.setBoolean("expediteRecipeCheck", expediteRecipeCheck);
        getProxy().writeToNBT(aNBT);
        super.saveNBTData(aNBT);

        NBTTagList storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = f_stored[i];
            if (size != Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("f_stored", storedList);

        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            FluidStack fluidStack = f_mark[i];
            if (fluidStack == null) {
                nbtTagList.appendTag(new NBTTagCompound());
                continue;
            }
            NBTTagCompound fluidTag = fluidStack.writeToNBT(new NBTTagCompound());
            if (f_mark[i] != null) fluidTag.setInteger("informationAmount", f_mark[i].amount);
            nbtTagList.appendTag(fluidTag);
        }
        aNBT.setTag("storedFluids", nbtTagList);

        storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = i_stored[i];
            if (size != Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("i_stored", storedList);

        nbtTagList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack fluidStack = i_mark[i];
            if (fluidStack == null) {
                nbtTagList.appendTag(new NBTTagCompound());
                continue;
            }
            NBTTagCompound fluidTag = fluidStack.writeToNBT(new NBTTagCompound());
            if (i_mark[i] != null) fluidTag.setInteger("informationAmount", i_mark[i].stackSize);
            nbtTagList.appendTag(fluidTag);
        }
        aNBT.setTag("storedItems", nbtTagList);

        int[] sizesf = new int[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; ++i) sizesf[i] = f_display[i] == null ? 0 : f_display[i].amount;
        aNBT.setIntArray("sizesF", sizesf);
        int[] sizes = new int[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; ++i) sizes[i] = i_display[i] == null ? 0 : i_display[i].stackSize;
        aNBT.setIntArray("sizes", sizes);

        ByteBuffer b = ByteBuffer.allocate(Long.SIZE / Byte.SIZE * SLOT_COUNT);
        for (long l : i_client) {
            b.putLong(l);
        }
        aNBT.setByteArray("clientDisplayValue", b.array());

        b = ByteBuffer.allocate(Long.SIZE / Byte.SIZE * SLOT_COUNT);
        for (long l : f_client) {
            b.putLong(l);
        }
        aNBT.setByteArray("clientDisplayValueF", b.array());

        aNBT.setLong("itemMinAmount", minAutoPullItemAmount);
        aNBT.setLong("fluidMinAmount", minAutoPullFluidAmount);
        aNBT.setBoolean("autoPull", autoPullItemList);

        aNBT.setInteger("intmaxs", intmaxs);
        aNBT.setInteger("interval", autoPullRefreshTime);

        getProxy().writeToNBT(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        additionalConnection = aNBT.getBoolean("additionalConnection");
        allowAuto = aNBT.getBoolean("allowAuto");
        minAutoPullItemAmount = aNBT.getLong("itemMinAmount");
        minAutoPullFluidAmount = aNBT.getLong("fluidMinAmount");
        expediteRecipeCheck = aNBT.getBoolean("expediteRecipeCheck");
        getProxy().readFromNBT(aNBT);
        super.loadNBTData(aNBT);

        if (aNBT.hasKey("f_stored")) {
            NBTTagList list = aNBT.getTagList("f_stored", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("slot");
                long size = tag.getLong("size");
                if (slot < SLOT_COUNT) {
                    f_stored[slot] = size;
                }
            }
        }

        if (aNBT.hasKey("storedFluids")) {
            NBTTagList nbtTagList = aNBT.getTagList("storedFluids", 10);
            int c = Math.min(nbtTagList.tagCount(), SLOT_COUNT);
            for (int i = 0; i < c; i++) {
                NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
                FluidStack fluidStack = GTUtility.loadFluid(nbtTagCompound);
                f_mark[i] = fluidStack;

                if (nbtTagCompound.hasKey("informationAmount")) {
                    int informationAmount = nbtTagCompound.getInteger("informationAmount");
                    f_mark[i] = GTUtility.copyAmount(informationAmount, fluidStack);
                }
            }
        }

        if (aNBT.hasKey("i_stored")) {
            NBTTagList list = aNBT.getTagList("i_stored", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("slot");
                long size = tag.getLong("size");
                if (slot < SLOT_COUNT) {
                    i_stored[slot] = size;
                }
            }
        }

        if (aNBT.hasKey("storedItems")) {
            NBTTagList nbtTagList = aNBT.getTagList("storedItems", 10);
            int c = Math.min(nbtTagList.tagCount(), SLOT_COUNT);
            for (int i = 0; i < c; i++) {
                NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
                ItemStack fluidStack = GTUtility.loadItem(nbtTagCompound);
                i_mark[i] = fluidStack;

                if (nbtTagCompound.hasKey("informationAmount")) {
                    int informationAmount = nbtTagCompound.getInteger("informationAmount");
                    i_mark[i] = GTUtility.copyAmount(informationAmount, fluidStack);
                }
            }
        }

        if (aNBT.hasKey("sizesF")) {
            int[] size = aNBT.getIntArray("sizesF");
            for (int i = 0; i < SLOT_COUNT; i++) {
                if (f_mark[i] != null) {
                    f_display[i] = f_mark[i].copy();
                    f_display[i].amount = size[i];

                }

            }
        }

        if (aNBT.hasKey("sizes")) {
            int[] size = aNBT.getIntArray("sizes");
            for (int i = 0; i < 16; i++) {
                if (i_mark[i] != null) {
                    i_display[i] = i_mark[i].copy();
                    i_display[i].stackSize = size[i];
                }

            }
        }
        if (aNBT.hasKey("clientDisplayValue")) {
            ByteBuffer b = ByteBuffer.allocate(8 * SLOT_COUNT);
            b.put(aNBT.getByteArray("clientDisplayValue"));
            b.flip();
            LongBuffer l = b.asLongBuffer();
            for (int i = 0; i < SLOT_COUNT; i++) i_client[i] = l.get();

            b = ByteBuffer.allocate(8 * SLOT_COUNT);
            b.put(aNBT.getByteArray("clientDisplayValueF"));
            b.flip();
            l = b.asLongBuffer();
            for (int i = 0; i < SLOT_COUNT; i++) f_client[i] = l.get();
        }

        autoPullRefreshTime = aNBT.getInteger("interval");
        intmaxs = aNBT.getInteger("intmaxs");
        autoPullItemList = aNBT.getBoolean("autoPull");
    }

    public IItemHandlerModifiable inventoryHandlerMark = new ItemStackHandler(i_mark);
    public IItemHandlerModifiable inventoryHandlerDisplay = new ItemStackHandler(i_display);

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {

        return getProxy().getNode();
    }

    @Override
    public void securityBreak() {

    }

    @Override
    public DimensionalCoord getLocation() {

        return new DimensionalCoord((TileEntity) this.getBaseMetaTileEntity());
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return getTexturesInactive(aBaseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_BUFFER) };
    }

    public static class HookHolder {

        static ItemRenderHook SKIP_ITEM_STACK_SIZE_HOOK = new ItemRenderHook() {

            @Override
            public boolean renderOverlay(FontRenderer fr, TextureManager tm, ItemStack is, int x, int y) {
                return true;
            }

            @Override
            public boolean showStackSize(ItemStack is) {
                return false;
            }
        };
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack is) {
        additionalConnection = !additionalConnection;
        updateValidGridProxySides();
        aPlayer.addChatComponentMessage(
            new ChatComponentTranslation("GT5U.hatch.additionalConnection." + additionalConnection));
        return true;
    }

    @Override
    public void onEnableWorking() {
        if (expediteRecipeCheck) {
            justHadNewItems = true;
        }
    }

    @Override
    public void onColorChangeServer(byte aColor) {
        updateAE2ProxyColor();
    }

    public void updateAE2ProxyColor() {
        AENetworkProxy proxy = getProxy();
        byte color = this.getColor();
        if (color == -1) {
            proxy.setColor(AEColor.Transparent);
        } else {
            proxy.setColor(AEColor.values()[Dyes.transformDyeIndex(color)]);
        }
        if (proxy.getNode() != null) {
            proxy.getNode()
                .updateState();
        }
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aPlayer instanceof EntityPlayerMP) {
            ItemStack dataStick = aPlayer.inventory.getCurrentItem();
            if (ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) {
                dataStick.stackTagCompound = this.getCopiedData(aPlayer);
                dataStick.setStackDisplayName("Super Dual Input Hatch ME Link Configuration");
                aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.saved"));
            }
        }
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, ForgeDirection side,
        float aX, float aY, float aZ) {
        if (!(aPlayer instanceof EntityPlayerMP)) {
            this.openGui(aPlayer);
            return this.onRightclick(aBaseMetaTileEntity, aPlayer);
        } else {
            ItemStack dataStick = aPlayer.inventory.getCurrentItem();
            if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) {
                this.openGui(aPlayer);
                return this.onRightclick(aBaseMetaTileEntity, aPlayer);
            } else if (!this.pasteCopiedData(aPlayer, dataStick.stackTagCompound)) {
                return false;
            } else {
                aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.loaded"));
                return true;
            }
        }
    }

    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return COPIED_DATA_IDENTIFIER;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        if (aNBT == null || !COPIED_DATA_IDENTIFIER.equals(aNBT.getString("type"))) return false;
        ItemStack circuit = GTUtility.loadItem(aNBT, "circuit");
        if (GTUtility.isStackInvalid(circuit)) circuit = null;

        NBTTagList list = aNBT.getTagList("f_stored", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("slot");
            long size = tag.getLong("size");
            if (slot < SLOT_COUNT) {
                f_stored[slot] = size;
            }
        }
        list = aNBT.getTagList("i_stored", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("slot");
            long size = tag.getLong("size");
            if (slot < SLOT_COUNT) {
                i_stored[slot] = size;
            }
        }

        if (allowAuto) {
            setAutoPullItemList(aNBT.getBoolean("autoPull"));
            minAutoPullItemAmount = aNBT.getInteger("itemMinStackSize");;
            minAutoPullFluidAmount = aNBT.getInteger("fluidMinStackSize");
            // Data sticks created before refreshTime was implemented should not cause stocking buses to
            // spam divide by zero errors
            if (aNBT.hasKey("refreshTime")) {
                autoPullRefreshTime = aNBT.getInteger("refreshTime");
            }
            expediteRecipeCheck = aNBT.getBoolean("expediteRecipeCheck");
        }

        additionalConnection = aNBT.getBoolean("additionalConnection");
        if (!autoPullItemList) {
            NBTTagList stockingItems = aNBT.getTagList("itemsToStock", 10);
            for (int i = 0; i < stockingItems.tagCount(); i++) {
                i_mark[i] = GTUtility.loadItem(stockingItems.getCompoundTagAt(i));
            }

            NBTTagList stockingFluids = aNBT.getTagList("fluidsToStock", 10);
            for (int i = 0; i < stockingFluids.tagCount(); i++) {
                f_mark[i] = GTUtility.loadFluid(stockingFluids.getCompoundTagAt(i));
            }
        }
        setInventorySlotContents(getCircuitSlot(), circuit);
        updateValidGridProxySides();
        byte color = aNBT.getByte("color");
        this.getBaseMetaTileEntity()
            .setColorization(color);

        return true;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("type", COPIED_DATA_IDENTIFIER);
        aNBT.setBoolean("autoPull", autoPullItemList);
        aNBT.setLong("itemMinStackSize", minAutoPullItemAmount);
        aNBT.setLong("fluidMinStackSize", minAutoPullFluidAmount);
        aNBT.setBoolean("additionalConnection", additionalConnection);
        aNBT.setInteger("refreshTime", autoPullRefreshTime);
        aNBT.setBoolean("expediteRecipeCheck", expediteRecipeCheck);
        aNBT.setByte("color", this.getColor());

        NBTTagList storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = f_stored[i];
            if (size != Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("f_stored", storedList);

        storedList = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            long size = i_stored[i];
            if (size != Long.MAX_VALUE) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("slot", i);
            tag.setLong("size", size);
            storedList.appendTag(tag);
        }
        aNBT.setTag("i_stored", storedList);

        NBTTagList stockingItems = new NBTTagList();
        NBTTagList stockingFluids = new NBTTagList();

        if (!autoPullItemList) {
            for (int index = 0; index < SLOT_COUNT; index++) {
                FluidStack fluidStack = f_mark[index];
                if (fluidStack == null) {
                    continue;
                }
                stockingFluids.appendTag(fluidStack.writeToNBT(new NBTTagCompound()));
            }
            aNBT.setTag("fluidsToStock", stockingFluids);

            for (int index = 0; index < SLOT_COUNT; index++) {
                stockingItems.appendTag(GTUtility.saveItem(i_mark[index]));
            }
            aNBT.setTag("itemsToStock", stockingItems);
        }
        return aNBT;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (!allowAuto) {
            super.getWailaBody(itemStack, currenttip, accessor, config);
            return;
        }

        NBTTagCompound tag = accessor.getNBTData();
        boolean autopull = tag.getBoolean("autoPull");
        long itemMinSize = tag.getLong("itemMinAmount");
        long fluidMinSize = tag.getLong("fluidMinAmount");
        currenttip.add(
            StatCollector.translateToLocal("GT5U.waila.stocking_bus.auto_pull." + (autopull ? "enabled" : "disabled")));
        if (autopull) {
            currenttip.add(
                StatCollector
                    .translateToLocalFormatted("Info_SuperDualInputHatchME_03", GTUtility.formatNumbers(itemMinSize)));
            currenttip.add(
                StatCollector
                    .translateToLocalFormatted("Info_SuperDualInputHatchME_04", GTUtility.formatNumbers(fluidMinSize)));
        }
        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        if (!allowAuto) {
            super.getWailaNBTData(player, tile, tag, world, x, y, z);
            return;
        }

        tag.setBoolean("autoPull", autoPullItemList);
        tag.setLong("itemMinAmount", minAutoPullItemAmount);
        tag.setLong("fluidMinAmount", minAutoPullFluidAmount);
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    public boolean off;

    @Override
    public void trunOffME() {
        off = true;
    }

    @Override
    public void trunONME() {
        off = false;
    }

    @Override
    public void onFacingChange() {
        updateValidGridProxySides();
    }

}
