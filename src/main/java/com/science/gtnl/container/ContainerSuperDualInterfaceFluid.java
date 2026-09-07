package com.science.gtnl.container;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.inventory.IDualHost;
import com.glodblock.github.inventory.slot.OptionalFluidSlotFakeTypeOnly;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.me.dual.SuperDualInterfaceSlots;
import com.science.gtnl.common.packet.SuperDualInterfaceFluidSyncPacket;

import appeng.api.storage.data.IAEFluidStack;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.slot.IOptionalSlotHost;
import appeng.util.item.AEFluidStack;
import lombok.Getter;

public class ContainerSuperDualInterfaceFluid extends AEBaseContainer implements IOptionalSlotHost {

    private static final int FLUIDS_PER_PAGE = SuperDualInterfaceSlots.FLUID_SLOTS_PER_PAGE;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_START_X = 35;
    @Getter
    private final IDualHost tile;
    private final OptionalFluidSlotFakeTypeOnly[][] configSlotPages;
    @GuiSync(1)
    public int currentPage = 0;
    @GuiSync(2)
    public int maxPage;

    public ContainerSuperDualInterfaceFluid(InventoryPlayer inventoryPlayer, IDualHost tile) {
        super(inventoryPlayer, tile);
        this.tile = tile;
        this.maxPage = SuperDualInterfaceSlots.FLUID_PAGE_COUNT;
        this.configSlotPages = new OptionalFluidSlotFakeTypeOnly[maxPage][FLUIDS_PER_PAGE];
        int y = 35;
        for (int i = 0; i < FLUIDS_PER_PAGE * maxPage; i++) {
            int page = i / FLUIDS_PER_PAGE;
            int offset = i % FLUIDS_PER_PAGE;
            OptionalFluidSlotFakeTypeOnly slot = new OptionalFluidSlotFakeTypeOnly(
                tile.getConfig(),
                tile.getDualityFluid()
                    .getConfig(),
                this,
                i,
                SLOT_START_X,
                y,
                offset,
                0,
                0);
            this.configSlotPages[page][offset] = slot;
            addSlotToContainer(slot);
            if (page > 0) {
                slot.xDisplayPosition = Integer.MIN_VALUE;
            }
        }
        bindPlayerInventory(inventoryPlayer, 0, 149);
    }

    public void previousPage() {
        if (currentPage > 0) {
            setPageSlotsVisible(currentPage, false);
            currentPage--;
            setPageSlotsVisible(currentPage, true);
        }
    }

    public void nextPage() {
        if (currentPage < maxPage - 1) {
            setPageSlotsVisible(currentPage, false);
            currentPage++;
            setPageSlotsVisible(currentPage, true);
        }
    }

    private void setPageSlotsVisible(int page, boolean visible) {
        for (int i = 0; i < configSlotPages[page].length; i++) {
            Slot slot = configSlotPages[page][i];
            if (slot != null) {
                slot.xDisplayPosition = visible ? SLOT_START_X + SLOT_SIZE * i : Integer.MIN_VALUE;
            }
        }
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return true;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        Map<Integer, IAEFluidStack> update = new HashMap<>();
        for (int i = 0; i < FLUIDS_PER_PAGE * maxPage; i++) {
            update.put(
                i,
                tile.getInternalFluid()
                    .getFluidInSlot(i));
        }
        for (int i = 0; i < FLUIDS_PER_PAGE * maxPage; i++) {
            update.put(
                i + 100,
                AEFluidStack.create(
                    ItemFluidPacket.getFluidStack(
                        tile.getConfig()
                            .getStackInSlot(i))));
        }
        for (Object crafter : this.crafters) {
            if (crafter instanceof EntityPlayer) {
                ScienceNotLeisure.network
                    .sendTo(new SuperDualInterfaceFluidSyncPacket(update), (EntityPlayerMP) crafter);
            }
        }
    }
}
