package com.science.gtnl.common.block.blocks.tile;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.MultiEssentiaJarGui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.modularui2.GTGuiThemes;
import gregtech.api.modularui2.GTModularScreen;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileJarFillable;

// 一个使用共享 4096 点容量池、可储存多种源质的罐子。
// 继承自原版罐子的单一源质字段仅用于与 Thaumcraft 标准罐渲染器保持同步。所有储存、
// 持久化和传输行为都由本类实现。多源质存储细节委托给共享组件 MultiEssentiaStorage。
public class TileEntityMultiEssentiaJar extends TileJarFillable implements IGuiHolder<GuiData> {

    private static final String STORED_ASPECTS_KEY = "StoredAspects";
    private static final String ACTIVE_ASPECT_KEY = "ActiveAspect";
    private static final String FILTER_ASPECT_KEY = "AspectFilter";
    private static final String FACING_KEY = "facing";
    private static final int TRANSFER_PER_TICK = 16;
    private static final int UNFILTERED_SUCTION = 32;
    private static final int FILTERED_SUCTION = 64;

    public static final int MAX_CAPACITY = 4096;

    private final AspectList storedAspects = new AspectList();
    private final MultiEssentiaStorage storage = new MultiEssentiaStorage(storedAspects, MAX_CAPACITY);
    private Aspect activeAspect;

    public TileEntityMultiEssentiaJar() {
        maxAmount = MAX_CAPACITY;
        syncRenderState();
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag) {
        storedAspects.aspects.clear();
        if (tag.hasKey(STORED_ASPECTS_KEY)) {
            storedAspects.readFromNBT(tag.getCompoundTag(STORED_ASPECTS_KEY));
        }

        activeAspect = Aspect.getAspect(tag.getString(ACTIVE_ASPECT_KEY));
        aspectFilter = Aspect.getAspect(tag.getString(FILTER_ASPECT_KEY));
        facing = tag.getByte(FACING_KEY);
        storage.reload();
        ensureActiveAspect();
        syncRenderState();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tag) {
        NBTTagCompound storedTag = new NBTTagCompound();
        storedAspects.writeToNBT(storedTag);
        tag.setTag(STORED_ASPECTS_KEY, storedTag);
        tag.setString(ACTIVE_ASPECT_KEY, activeAspect == null ? "" : activeAspect.getTag());
        tag.setString(FILTER_ASPECT_KEY, aspectFilter == null ? "" : aspectFilter.getTag());
        tag.setByte(FACING_KEY, (byte) facing);
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote || getTotalAmount() >= MAX_CAPACITY) {
            return;
        }

        TileEntity tile = ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UP);
        if (!(tile instanceof IEssentiaTransport source)) return;

        ForgeDirection sourceSide = ForgeDirection.DOWN;
        if (!source.canOutputTo(sourceSide) || source.getEssentiaAmount(sourceSide) <= 0) return;

        int suction = getSuctionAmount(ForgeDirection.UP);
        if (source.getSuctionAmount(sourceSide) >= suction || suction < source.getMinimumSuction()) return;

        Aspect sourceAspect = source.getEssentiaType(sourceSide);
        if (sourceAspect == null || !doesContainerAccept(sourceAspect)) return;

        int requested = Math
            .min(TRANSFER_PER_TICK, Math.min(MAX_CAPACITY - getTotalAmount(), source.getEssentiaAmount(sourceSide)));

        int taken = source.takeEssentia(sourceAspect, requested, sourceSide);
        if (taken > 0) {
            addEssentia(sourceAspect, taken, ForgeDirection.UP);
        }
    }

    @Override
    public AspectList getAspects() {
        return storage.copyAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
        activeAspect = null;

        AspectList filtered = new AspectList();
        if (aspects != null) {
            for (Aspect storedAspect : MultiEssentiaStorage.getSortedAspects(aspects)) {
                if (aspectFilter != null && storedAspect != aspectFilter) continue;
                filtered.add(storedAspect, aspects.getAmount(storedAspect));
            }
        }

        storage.setAspects(filtered);
        ensureActiveAspect();
        markEssentiaChanged();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect != null && !storage.isFull() && (aspectFilter == null || aspectFilter == aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0 || !doesContainerAccept(aspect)) return amount;

        int remaining = storage.addToContainer(aspect, amount);
        if (remaining != amount) {
            if (activeAspect == null) activeAspect = aspect;
            markEssentiaChanged();
        }
        return remaining;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!doesContainerContainAmount(aspect, amount)) return false;

        boolean taken = storage.takeFromContainer(aspect, amount);
        if (storage.getAmount(aspect) <= 0 && aspect == activeAspect) {
            activeAspect = null;
            ensureActiveAspect();
        }
        markEssentiaChanged();
        return taken;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList aspects) {
        if (!doesContainerContain(aspects)) return false;

        storage.takeFromContainer(aspects);
        ensureActiveAspect();
        markEssentiaChanged();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return storage.doesContainerContainAmount(aspect, amount);
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList aspects) {
        return storage.doesContainerContain(aspects);
    }

    @Override
    public int containerContains(Aspect aspect) {
        return storage.getAmount(aspect);
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return face == ForgeDirection.UP;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return face == ForgeDirection.UP;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return face == ForgeDirection.UP;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return aspectFilter;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        if (!canInputFrom(face) || getTotalAmount() >= MAX_CAPACITY) return 0;
        return aspectFilter == null ? UNFILTERED_SUCTION : FILTERED_SUCTION;
    }

    @Override
    public int getMinimumSuction() {
        return aspectFilter == null ? UNFILTERED_SUCTION : FILTERED_SUCTION;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        if (!canOutputTo(face) || amount <= 0) return 0;

        int taken = Math.min(amount, containerContains(aspect));
        return taken > 0 && takeFromContainer(aspect, taken) ? taken : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return canInputFrom(face) ? amount - addToContainer(aspect, amount) : 0;
    }

    public int addEssentiaFromSmeltery(Aspect aspect, int amount, ForgeDirection face) {
        if (face == null || face == ForgeDirection.UNKNOWN || face == ForgeDirection.UP) return 0;
        return amount - addToContainer(aspect, amount);
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        ensureActiveAspect();
        return activeAspect;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        ensureActiveAspect();
        return activeAspect == null ? 0 : storage.getAmount(activeAspect);
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    public int getTotalAmount() {
        return storage.getTotalAmount();
    }

    public int getStoredTypeCount() {
        return storage.getStoredTypeCount();
    }

    public int clearAllEssentia() {
        int clearedAmount = storage.clearAll();
        if (clearedAmount <= 0) return 0;

        activeAspect = null;
        markEssentiaChanged();

        return clearedAmount;
    }

    public boolean hasFilterLabel() {
        return aspectFilter != null;
    }

    public Aspect getFilterAspect() {
        return aspectFilter;
    }

    public boolean installFilterLabel(Aspect filterAspect) {
        // 只有空罐可以安装或更换标签
        if (filterAspect == null || getTotalAmount() > 0) return false;

        if (aspectFilter == filterAspect) return false;

        aspectFilter = filterAspect;
        markEssentiaChanged();
        return true;
    }

    public boolean removeFilterLabel() {
        if (aspectFilter == null) return false;

        aspectFilter = null;
        markEssentiaChanged();
        return true;
    }

    public Aspect getActiveAspect() {
        ensureActiveAspect();
        return activeAspect;
    }

    public Aspect selectAspectWithAmount(int requiredAmount) {
        ensureActiveAspect();
        if (activeAspect != null && storage.getAmount(activeAspect) >= requiredAmount) return activeAspect;

        for (Aspect storedAspect : storage.getStoredAspectsSorted()) {
            if (storage.getAmount(storedAspect) >= requiredAmount) {
                activeAspect = storedAspect;
                markEssentiaChanged();
                return activeAspect;
            }
        }
        return null;
    }

    public Aspect cycleActiveAspect() {
        return cycleActiveAspect(1);
    }

    public Aspect cyclePreviousActiveAspect() {
        return cycleActiveAspect(-1);
    }

    private Aspect cycleActiveAspect(int step) {
        Aspect[] sorted = storage.getStoredAspectsSorted();
        if (sorted.length == 0) {
            activeAspect = null;
            markEssentiaChanged();
            return null;
        }

        int current = Arrays.asList(sorted)
            .indexOf(activeAspect);
        int next = current < 0 ? (step > 0 ? 0 : sorted.length - 1) : Math.floorMod(current + step, sorted.length);
        activeAspect = sorted[next];
        markEssentiaChanged();
        return activeAspect;
    }

    public boolean setActiveAspect(Aspect selectedAspect) {
        if (selectedAspect == null || storage.getAmount(selectedAspect) <= 0 || selectedAspect == activeAspect) {
            return false;
        }

        activeAspect = selectedAspect;
        markEssentiaChanged();
        return true;
    }

    public void writeToItemStack(ItemStack stack) {
        if (stack == null) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        writeCustomNBT(stack.getTagCompound());
    }

    public void readFromItemStack(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return;

        readCustomNBT(stack.getTagCompound());
        markEssentiaChanged();
    }

    public static AspectList getStoredAspects(ItemStack stack) {
        AspectList result = new AspectList();
        if (stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey(STORED_ASPECTS_KEY)) {
            result.readFromNBT(
                stack.getTagCompound()
                    .getCompoundTag(STORED_ASPECTS_KEY));
        }
        return result;
    }

    public static Aspect getActiveAspect(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return null;
        return Aspect.getAspect(
            stack.getTagCompound()
                .getString(ACTIVE_ASPECT_KEY));
    }

    public static Aspect cycleActiveAspect(ItemStack stack) {
        return cycleActiveAspect(stack, 1);
    }

    public static Aspect cyclePreviousActiveAspect(ItemStack stack) {
        return cycleActiveAspect(stack, -1);
    }

    private static Aspect cycleActiveAspect(ItemStack stack, int step) {
        AspectList storedAspects = getStoredAspects(stack);
        Aspect[] sorted = MultiEssentiaStorage.getSortedAspects(storedAspects);
        if (sorted.length == 0) return null;

        Aspect activeAspect = getActiveAspect(stack);
        int current = Arrays.asList(sorted)
            .indexOf(activeAspect);
        int next = current < 0 ? (step > 0 ? 0 : sorted.length - 1) : Math.floorMod(current + step, sorted.length);
        Aspect nextAspect = sorted[next];

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setString(ACTIVE_ASPECT_KEY, nextAspect.getTag());
        return nextAspect;
    }

    public static boolean setActiveAspect(ItemStack stack, Aspect selectedAspect) {
        if (stack == null || selectedAspect == null) return false;

        AspectList storedAspects = getStoredAspects(stack);
        if (storedAspects.getAmount(selectedAspect) <= 0) return false;
        if (getActiveAspect(stack) == selectedAspect) return false;

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound()
            .setString(ACTIVE_ASPECT_KEY, selectedAspect.getTag());

        return true;
    }

    public static Aspect getFilterAspect(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return null;

        return Aspect.getAspect(
            stack.getTagCompound()
                .getString(FILTER_ASPECT_KEY));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(GuiData data, ModularPanel mainPanel) {
        return new GTModularScreen(mainPanel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(GuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new MultiEssentiaJarGui(this, data.getPlayer(), syncManager).build();
    }

    private void ensureActiveAspect() {
        if (activeAspect != null && storage.getAmount(activeAspect) > 0) return;

        Aspect[] sorted = storage.getStoredAspectsSorted();
        activeAspect = sorted.length == 0 ? null : sorted[0];
    }

    private void syncRenderState() {
        maxAmount = MAX_CAPACITY;
        amount = storage.getTotalAmount();
        aspect = activeAspect;
    }

    private void markEssentiaChanged() {
        storage.markDirty();
        ensureActiveAspect();
        syncRenderState();
        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
