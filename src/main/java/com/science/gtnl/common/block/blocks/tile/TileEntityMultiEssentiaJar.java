package com.science.gtnl.common.block.blocks.tile;

import java.util.Arrays;
import java.util.Comparator;

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

/**
 * A jar that stores several essentia types in one shared 4096-point pool.
 *
 * <p>The inherited single-aspect fields are only kept in sync for Thaumcraft's standard jar renderer. All storage,
 * persistence, and transport behaviour is implemented by this class.</p>
 */
public class TileEntityMultiEssentiaJar extends TileJarFillable implements IGuiHolder<GuiData> {

    private static final String STORED_ASPECTS_KEY = "StoredAspects";
    private static final String ACTIVE_ASPECT_KEY = "ActiveAspect";
    private static final String FACING_KEY = "facing";
    private static final int TRANSFER_INTERVAL = 5;
    private static final int TRANSFER_PER_TICK = 16;
    private static final int SUCTION = 32;

    public static final int MAX_CAPACITY = 4096;

    private final AspectList storedAspects = new AspectList();
    private Aspect activeAspect;
    private int transferTick;

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
        facing = tag.getByte(FACING_KEY);
        removeInvalidAspects();
        trimToCapacity();
        ensureActiveAspect();
        syncRenderState();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tag) {
        NBTTagCompound storedTag = new NBTTagCompound();
        storedAspects.writeToNBT(storedTag);
        tag.setTag(STORED_ASPECTS_KEY, storedTag);
        tag.setString(ACTIVE_ASPECT_KEY, activeAspect == null ? "" : activeAspect.getTag());
        tag.setByte(FACING_KEY, (byte) facing);
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote || ++transferTick % TRANSFER_INTERVAL != 0
            || getTotalAmount() >= MAX_CAPACITY) {
            return;
        }

        TileEntity tile = ThaumcraftApiHelper
            .getConnectableTile(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UP);
        if (!(tile instanceof IEssentiaTransport source)) return;

        ForgeDirection sourceSide = ForgeDirection.DOWN;
        if (!source.canOutputTo(sourceSide) || source.getEssentiaAmount(sourceSide) <= 0) return;

        int suction = getSuctionAmount(ForgeDirection.UP);
        if (source.getSuctionAmount(sourceSide) >= suction || suction < source.getMinimumSuction()) return;

        Aspect sourceAspect = source.getEssentiaType(sourceSide);
        if (sourceAspect == null || !doesContainerAccept(sourceAspect)) return;

        int requested = Math.min(
            TRANSFER_PER_TICK,
            Math.min(
                MAX_CAPACITY - getTotalAmount(),
                source.getEssentiaAmount(sourceSide)));

        int taken = source.takeEssentia(sourceAspect, requested, sourceSide);
        if (taken > 0) {
            addEssentia(sourceAspect, taken, ForgeDirection.UP);
        }
    }

    @Override
    public AspectList getAspects() {
        AspectList copy = new AspectList();
        for (Aspect storedAspect : getStoredAspectsSorted()) {
            copy.add(storedAspect, storedAspects.getAmount(storedAspect));
        }
        return copy;
    }

    @Override
    public void setAspects(AspectList aspects) {
        storedAspects.aspects.clear();
        activeAspect = null;

        if (aspects != null) {
            int remaining = MAX_CAPACITY;
            Aspect[] sorted = getSortedAspects(aspects);
            for (Aspect storedAspect : sorted) {
                int accepted = Math.min(aspects.getAmount(storedAspect), remaining);
                if (accepted <= 0) continue;

                storedAspects.add(storedAspect, accepted);
                remaining -= accepted;
                if (remaining == 0) break;
            }
        }

        ensureActiveAspect();
        markEssentiaChanged();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect != null && getTotalAmount() < MAX_CAPACITY;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) return amount;

        int accepted = Math.min(amount, MAX_CAPACITY - getTotalAmount());
        if (accepted <= 0) return amount;

        storedAspects.add(aspect, accepted);
        if (activeAspect == null) activeAspect = aspect;
        markEssentiaChanged();
        return amount - accepted;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!doesContainerContainAmount(aspect, amount)) return false;

        storedAspects.remove(aspect, amount);
        if (storedAspects.getAmount(aspect) <= 0 && aspect == activeAspect) {
            activeAspect = null;
            ensureActiveAspect();
        }
        markEssentiaChanged();
        return true;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList aspects) {
        if (!doesContainerContain(aspects)) return false;

        for (Aspect storedAspect : getSortedAspects(aspects)) {
            storedAspects.remove(storedAspect, aspects.getAmount(storedAspect));
        }
        ensureActiveAspect();
        markEssentiaChanged();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return aspect != null && amount >= 0 && storedAspects.getAmount(aspect) >= amount;
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList aspects) {
        if (aspects == null) return false;

        for (Aspect storedAspect : getSortedAspects(aspects)) {
            if (!doesContainerContainAmount(storedAspect, aspects.getAmount(storedAspect))) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == null ? 0 : storedAspects.getAmount(aspect);
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
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return canInputFrom(face) && getTotalAmount() < MAX_CAPACITY ? SUCTION : 0;
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

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        ensureActiveAspect();
        return activeAspect;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        ensureActiveAspect();
        return activeAspect == null ? 0 : storedAspects.getAmount(activeAspect);
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    public int getTotalAmount() {
        int total = 0;
        for (Aspect storedAspect : getStoredAspectsSorted()) {
            total += storedAspects.getAmount(storedAspect);
        }
        return total;
    }

    public int getStoredTypeCount() {
        return getStoredAspectsSorted().length;
    }

    public Aspect getActiveAspect() {
        ensureActiveAspect();
        return activeAspect;
    }

    public Aspect selectAspectWithAmount(int requiredAmount) {
        ensureActiveAspect();
        if (activeAspect != null && storedAspects.getAmount(activeAspect) >= requiredAmount) return activeAspect;

        for (Aspect storedAspect : getStoredAspectsSorted()) {
            if (storedAspects.getAmount(storedAspect) >= requiredAmount) {
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
        Aspect[] sorted = getStoredAspectsSorted();
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
        if (selectedAspect == null || storedAspects.getAmount(selectedAspect) <= 0) return false;

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
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound()
            .hasKey(STORED_ASPECTS_KEY)) {
            result.readFromNBT(stack.getTagCompound()
                .getCompoundTag(STORED_ASPECTS_KEY));
        }
        return result;
    }

    public static Aspect getActiveAspect(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return null;
        return Aspect.getAspect(stack.getTagCompound()
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
        Aspect[] sorted = getSortedAspects(storedAspects);
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

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setString(ACTIVE_ASPECT_KEY, selectedAspect.getTag());
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(GuiData data, ModularPanel mainPanel) {
        return new GTModularScreen(mainPanel, GTGuiThemes.STANDARD);
    }

    @Override
    public ModularPanel buildUI(GuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new MultiEssentiaJarGui(this).build();
    }

    private void removeInvalidAspects() {
        storedAspects.aspects.entrySet()
            .removeIf(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0);
    }

    private void trimToCapacity() {
        int remaining = MAX_CAPACITY;
        AspectList trimmed = new AspectList();
        for (Aspect storedAspect : getStoredAspectsSorted()) {
            int accepted = Math.min(storedAspects.getAmount(storedAspect), remaining);
            if (accepted > 0) {
                trimmed.add(storedAspect, accepted);
                remaining -= accepted;
            }
            if (remaining == 0) break;
        }

        storedAspects.aspects.clear();
        for (Aspect storedAspect : getSortedAspects(trimmed)) {
            storedAspects.add(storedAspect, trimmed.getAmount(storedAspect));
        }
    }

    private void ensureActiveAspect() {
        if (activeAspect != null && storedAspects.getAmount(activeAspect) > 0) return;

        Aspect[] sorted = getStoredAspectsSorted();
        activeAspect = sorted.length == 0 ? null : sorted[0];
    }

    private Aspect[] getStoredAspectsSorted() {
        return getSortedAspects(storedAspects);
    }

    private static Aspect[] getSortedAspects(AspectList aspects) {
        return aspects.aspects.entrySet()
            .stream()
            .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
            .map(entry -> entry.getKey())
            .sorted(Comparator.comparing(Aspect::getTag))
            .toArray(Aspect[]::new);
    }

    private void syncRenderState() {
        maxAmount = MAX_CAPACITY;
        amount = getTotalAmount();
        aspect = activeAspect;
        aspectFilter = null;
    }

    private void markEssentiaChanged() {
        ensureActiveAspect();
        syncRenderState();
        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
