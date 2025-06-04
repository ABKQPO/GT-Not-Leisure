package com.science.gtnl.common.machine.hatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class HumongousInputBus extends MTEHatchInputBus {

    public HumongousInputBus(int id, String name, String nameRegional, int tier) {
        super(id, name, nameRegional, tier);
        this.disableLimited = true;
        this.disableFilter = true;
    }

    protected HumongousInputBus(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        this.disableLimited = true;
        this.disableFilter = true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HumongousInputBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    protected void fillStacksIntoFirstSlots() {
        final int L = mInventory.length - 1;
        HashMap<GTUtility.ItemId, Integer> slots = new HashMap<>(L);
        HashMap<GTUtility.ItemId, ItemStack> stacks = new HashMap<>(L);
        List<GTUtility.ItemId> order = new ArrayList<>(L);
        List<Integer> validSlots = new ArrayList<>(L);
        for (int i = 0; i < L; i++) {
            if (!isValidSlot(i)) continue;
            validSlots.add(i);
            ItemStack s = mInventory[i];
            if (s == null) continue;
            GTUtility.ItemId sID = GTUtility.ItemId.createNoCopy(s);
            slots.merge(sID, s.stackSize, Integer::sum);
            if (!stacks.containsKey(sID)) stacks.put(sID, s);
            order.add(sID);
            mInventory[i] = null;
        }
        int slotindex = 0;
        for (GTUtility.ItemId sID : order) {
            int toSet = slots.get(sID);
            if (toSet == 0) continue;
            int slot = validSlots.get(slotindex);
            slotindex++;
            mInventory[slot] = stacks.get(sID)
                .copy();
            mInventory[slot].stackSize = Math.min(toSet, Integer.MAX_VALUE);
            slots.merge(sID, mInventory[slot].stackSize, (a, b) -> a - b);
        }
    }

    @Override
    protected boolean limitedAllowPutStack(int aIndex, ItemStack aStack) {
        return GTUtility.areStacksEqual(GTOreDictUnificator.get_nocopy(aStack), mInventory[aIndex]);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        CustomItemStackHandler inventoryHandler = new CustomItemStackHandler(30);
        int maxItemTypes = (mTier + 1) * 2 + 1;

        int maxSlots = inventoryHandler.getSlots();

        Set<String> itemTypes = new HashSet<>();
        for (int i = 0; i < maxSlots; i++) {
            ItemStack itemStack = inventoryHandler.getStackInSlot(i);
            if (itemStack != null && itemStack.stackSize > 0) {
                itemTypes.add(itemStack.getDisplayName());
            }
        }
        int itemTypeCount = itemTypes.size();

        int slotCount = Math.min(Math.max(itemTypeCount, maxItemTypes), maxSlots);

        builder.widget(new TextWidget("物品槽位: " + itemTypeCount + "/" + slotCount).setPos(8, 6));

        Map<String, Integer> itemCountMap = new HashMap<>();
        for (int i = 0; i < maxSlots; i++) {
            ItemStack itemStack = inventoryHandler.getStackInSlot(i);
            if (itemStack != null && itemStack.stackSize > 0) {
                String itemName = itemStack.getDisplayName();
                itemCountMap.put(itemName, itemCountMap.getOrDefault(itemName, 0) + itemStack.stackSize);
            }
        }

        int yOffset = 16;
        if (itemCountMap.isEmpty()) {
            builder.widget(new TextWidget("空").setPos(8, yOffset));
        } else {
            for (Map.Entry<String, Integer> entry : itemCountMap.entrySet()) {
                builder.widget(new TextWidget(entry.getKey() + ": " + entry.getValue()).setPos(8, yOffset));
                yOffset += 10;
            }
        }

        for (int i = 0; i < slotCount; i++) {
            builder.widget(new SlotWidget(inventoryHandler, i).setEnabled(false));
        }
    }

    public static class CustomItemStackHandler extends ItemStackHandler {

        public CustomItemStackHandler(int size) {
            super(size);
        }

        @Override
        public int getSlots() {
            return 30;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            this.validateSlotIndex(slot);
            return this.stacks.get(slot);
        }

    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (!getBaseMetaTileEntity().getCoverAtSide(side)
            .isGUIClickable()) return;
        if (aPlayer.isSneaking()) {
            disableSort = !disableSort;
            GTUtility
                .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableSort." + disableSort));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("disableLimited", true);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        disableLimited = true;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == getBaseMetaTileEntity().getFrontFacing() && aIndex != getCircuitSlot()
            && (mRecipeMap == null || mRecipeMap.containsInput(aStack));
    }

    @Override
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
    }
}
