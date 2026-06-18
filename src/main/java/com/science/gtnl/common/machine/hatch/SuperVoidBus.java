package com.science.gtnl.common.machine.hatch;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.science.gtnl.common.gui.modularui.SuperVoidBusGui;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.gui.widgets.PhantomItemButton;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchVoidBus;

public class SuperVoidBus extends MTEHatchVoidBus implements IAddGregtechLogo {

    public static String DATA_STICK_DATA_TYPE = "superVoidBusFilter";
    public static String LOCKED_ITEMS_NBT_KEY = "lockedItems";

    public ItemStack[] lockedItems = new ItemStack[100];
    public ItemStackHandler lockedInventoryHandler = new ItemStackHandler(lockedItems);

    public SuperVoidBus(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SuperVoidBus(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SuperVoidBus(mName, mTier, mDescriptionArray, mTextures);
    }

    public boolean storePartial(ItemStack stack, boolean simulate) {
        for (ItemStack lockedItem : lockedItems) {
            if (lockedItem != null && lockedItem.isItemEqual(stack)) {
                stack.stackSize = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        NBTTagList lockedItemList = new NBTTagList();
        for (int i = 0; i < lockedItems.length; i++) {
            if (lockedItems[i] == null) continue;
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            lockedItems[i].writeToNBT(itemTag);
            lockedItemList.appendTag(itemTag);
        }
        aNBT.setTag(LOCKED_ITEMS_NBT_KEY, lockedItemList);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        NBTTagList lockedItemList = aNBT.getTagList(LOCKED_ITEMS_NBT_KEY, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < lockedItemList.tagCount(); i++) {
            NBTTagCompound itemTag = lockedItemList.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot");
            if (slot < lockedItems.length) {
                lockedItems[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }

    @Override
    public int getGUIWidth() {
        return 220;
    }

    @Override
    public boolean isLocked() {
        for (ItemStack lockedItem : lockedItems) {
            if (lockedItem != null) {
                return true;
            }
        }
        return false;
    }

    public ItemStack[] getLockedItems() {
        return lockedItems;
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new SuperVoidBusGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after SuperVoidBus mui2 rollout is complete.
        if (lockedInventoryHandler == null) return;

        final Scrollable scrollable = new Scrollable().setVerticalScroll();
        SlotGroup slotGroup = SlotGroup.ofItemHandler(lockedInventoryHandler, 10)
            .startFromSlot(0)
            .endAtSlot(99)
            .background(PhantomItemButton.FILTER_BACKGROUND)
            .phantom(true)
            .slotCreator(index -> new BaseSlot(lockedInventoryHandler, index, true))
            .build();
        scrollable.widget(slotGroup);

        builder.widget(
            scrollable.setSize(18 * 10 + 4, 72)
                .setPos(20, 9));
        addGregTechLogo(builder);
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        final NBTTagCompound nbt = new NBTTagCompound();
        final NBTTagList lockedItemList = new NBTTagList();

        nbt.setString("type", DATA_STICK_DATA_TYPE);
        for (int i = 0; i < lockedItems.length; i++) {
            if (lockedItems[i] == null) continue;
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            lockedItems[i].writeToNBT(itemTag);
            lockedItemList.appendTag(itemTag);
        }

        nbt.setTag(LOCKED_ITEMS_NBT_KEY, lockedItemList);
        return nbt;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound nbt) {
        if (nbt == null || !DATA_STICK_DATA_TYPE.equals(nbt.getString("type"))) return false;
        if (!nbt.hasKey(LOCKED_ITEMS_NBT_KEY)) return false;

        Arrays.fill(lockedItems, null);

        NBTTagList lockedItemList = nbt.getTagList(LOCKED_ITEMS_NBT_KEY, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < lockedItemList.tagCount(); i++) {
            NBTTagCompound itemTag = lockedItemList.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot");
            if (slot < lockedItems.length) {
                lockedItems[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
        return true;
    }

    @Override
    @Deprecated
    public void addGregTechLogo(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after SuperVoidBus mui2 rollout is complete.
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(195, 83));
    }

}
