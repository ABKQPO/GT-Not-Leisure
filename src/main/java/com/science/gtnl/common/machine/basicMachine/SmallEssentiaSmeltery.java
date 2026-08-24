package com.science.gtnl.common.machine.basicMachine;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.gui.modularui.GTNLBasicMachineGui;
import com.science.gtnl.utils.AspectTooltipUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

        // 过滤 null / 非正数条目：TC 解析某些物品标签可能返回 null 要素，
        // 若混入 AspectList，outputEssentia 排序取到 null 会永久卡死输出。
        if (aspects != null) {
            for (Aspect aspect : aspects.getAspects()) {
                if (aspect == null) continue;
                int amount = aspects.getAmount(aspect);
                if (amount > 0) result.add(aspect, amount);
            }
        }
        if (result.size() == 0) {
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
        // 忽略目标吸力：吸力报告可能因读档/风箱/缓存恢复时序而失真（报 0 导致整机堵死），
        // 是否接受由目标的 addEssentia/addToContainer 直接判定。
        if (!forceMultiJarInput) {
            if (!transport.isConnectable(targetSide) || !transport.canInputFrom(targetSide)) return false;
        }

        // 目标明确请求的源质作为首选；但若缓冲里没有该源质，回退到自身缓冲。
        // 跳过 null / 非正数条目：TC 解析可能混入 null 要素，取到 null 会永久卡死输出。
        Aspect aspect = null;
        Aspect preferred = transport.getSuctionType(targetSide);
        if (preferred != null && outputAspects.getAmount(preferred) > 0) {
            aspect = preferred;
        } else {
            for (Aspect candidate : outputAspects.getAspectsSortedAmount()) {
                if (candidate != null && outputAspects.getAmount(candidate) > 0) {
                    aspect = candidate;
                    break;
                }
            }
            if (aspect == null) return false;
        }

        int available = outputAspects.getAmount(aspect);

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

            private StringSyncValue inProcessSync;

            private String cachedInProcessRaw = "\u0000";
            private AspectList cachedInProcess = new AspectList();

            @Override
            protected boolean supportsBottomLeftCornerFlow() {
                return false;
            }

            @Override
            protected void registerSyncValues(PanelSyncManager syncManager) {
                super.registerSyncValues(syncManager);
                inProcessSync = new StringSyncValue(() -> encodeAspects(getInProcessEssentia()));
                syncManager.syncValue("smelteryInProcess", inProcessSync);
            }

            @Override
            protected ParentWidget<?> createItemOutputSlots() {
                return new ParentWidget<>().size(3 * SLOT_SIZE, 50)
                    .child(
                        new IDrawable.DrawableWidget(new EssentiaPreviewDrawable(this::getInProcessAspects))
                            .size(54, 50)
                            .tooltipDynamic(this::buildEssentiaTooltip)
                            .tooltipAutoUpdate(true)
                            .tooltipShowUpTimer(TOOLTIP_DELAY));
            }

            private AspectList getInProcessAspects() {
                String raw = inProcessSync.getValue();
                if (!raw.equals(cachedInProcessRaw)) {
                    cachedInProcessRaw = raw;
                    cachedInProcess = decodeAspects(raw);
                }
                return cachedInProcess;
            }

            private void buildEssentiaTooltip(RichTooltip tooltip) {
                tooltip.addLine(StatCollector.translateToLocal("GTNL.gui.small_essentia_smeltery.output"));
                AspectList aspects = getInProcessAspects();
                if (aspects == null || aspects.size() == 0) return;
                int lines = 0;
                for (Aspect aspect : aspects.getAspectsSortedAmount()) {
                    if (aspect == null) continue;
                    int amount = aspects.getAmount(aspect);
                    if (amount <= 0) continue;
                    if (++lines > 9) {
                        tooltip.addLine(" ...");
                        break;
                    }
                    tooltip.addLine(" - " + AspectTooltipUtils.getClientAspectDisplay(aspect, amount));
                }
            }
        }.build(data, syncManager, uiSettings);
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    private AspectList getInProcessEssentia() {
        if (mMaxProgresstime > 0) {
            return pendingAspects.size() > 0 ? pendingAspects : new AspectList();
        }
        ItemStack input = getInputAt(0);
        if (!GTUtility.isStackValid(input)) return new AspectList();
        return getEssentia(input);
    }

    private static String encodeAspects(AspectList aspects) {
        if (aspects == null || aspects.size() == 0) return "";
        StringBuilder builder = new StringBuilder();
        for (Aspect aspect : aspects.getAspectsSortedAmount()) {
            if (aspect == null) continue;
            int amount = aspects.getAmount(aspect);
            if (amount <= 0) continue;
            if (builder.length() > 0) builder.append(',');
            builder.append(aspect.getTag())
                .append(':')
                .append(amount);
        }
        return builder.toString();
    }

    private static AspectList decodeAspects(String encoded) {
        AspectList result = new AspectList();
        if (encoded == null || encoded.isEmpty()) return result;
        for (String entry : encoded.split(",")) {
            int separator = entry.indexOf(':');
            if (separator <= 0) continue;
            try {
                Aspect aspect = Aspect.getAspect(entry.substring(0, separator));
                int amount = Integer.parseInt(entry.substring(separator + 1));
                if (aspect != null && amount > 0) result.add(aspect, amount);
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    private static class EssentiaPreviewDrawable implements IDrawable {

        private static final int ICON_SIZE = 16;
        private static final int ICON_SPACING = 1;
        private static final int COLUMNS = 3;
        private static final int MAX_ICONS = 9;

        private final Supplier<AspectList> supplier;

        private EssentiaPreviewDrawable(Supplier<AspectList> supplier) {
            this.supplier = supplier;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
            AspectList aspects = supplier.get();
            if (aspects == null || aspects.size() == 0) return;

            Aspect[] sortedAspects = aspects.getAspectsSortedAmount();
            int iconCount = Math.min(sortedAspects.length, MAX_ICONS);
            int rows = (iconCount + COLUMNS - 1) / COLUMNS;
            int gridWidth = COLUMNS * (ICON_SIZE + ICON_SPACING) - ICON_SPACING;
            int gridHeight = rows * (ICON_SIZE + ICON_SPACING) - ICON_SPACING;
            int startX = x + (width - gridWidth) / 2;
            int startY = y + (height - gridHeight) / 2;

            Minecraft mc = Minecraft.getMinecraft();
            for (int i = 0; i < iconCount; i++) {
                Aspect aspect = sortedAspects[i];
                if (aspect == null) continue;
                int amount = aspects.getAmount(aspect);
                if (amount <= 0) continue;

                ItemStack icon = new ItemStack(ModItems.itemAspect);
                ItemAspect.setAspect(icon, aspect);

                int iconX = startX + (i % COLUMNS) * (ICON_SIZE + ICON_SPACING);
                int iconY = startY + (i / COLUMNS) * (ICON_SIZE + ICON_SPACING);

                applyColor(widgetTheme.getColor());
                GuiDraw.drawItem(icon, iconX, iconY, ICON_SIZE, ICON_SIZE, context.getCurrentDrawingZ());

                String amountText = String.valueOf(amount);
                mc.fontRenderer.drawStringWithShadow(
                    amountText,
                    iconX + ICON_SIZE - mc.fontRenderer.getStringWidth(amountText),
                    iconY + ICON_SIZE - 9,
                    0xFFFFFF);
            }
        }
    }
}
