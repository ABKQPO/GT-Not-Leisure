package com.science.gtnl.api.mixinHelper;

import java.util.List;

import appeng.api.config.YesNo;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.MultiCraftingTracker;
import appeng.helpers.UnlockCraftingEvent;
import appeng.me.helpers.AENetworkProxy;
import appeng.parts.automation.UpgradeInventory;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.ConfigManager;
import appeng.util.ScheduledReason;
import appeng.util.inv.WrapperInvSlot;

public interface IDualityInterface {

    int getConfigSlots();

    void setConfigSlots(int slots);

    int getStorageSlots();

    void setStorageSlots(int slots);

    int getPatternSlots();

    void setPatternSlots(int slots);

    int getUpgradeSlots();

    void setUpgradeSlots(int slots);

    int[] getSides();

    void setSides(int[] sides);

    IAEItemStack[] getRequireWork();

    void setRequireWork(IAEItemStack[] requireWork);

    boolean[] getHasFuzzyConfig();

    void setHasFuzzyConfig(boolean[] hasFuzzyConfig);

    MultiCraftingTracker getCraftingTracker();

    void setCraftingTracker(MultiCraftingTracker craftingTracker);

    AENetworkProxy getGridProxy();

    void setGridProxy(AENetworkProxy gridProxy);

    IInterfaceHost getIHost();

    void setIHost(IInterfaceHost iHost);

    BaseActionSource getMySource();

    void setMySource(BaseActionSource mySource);

    BaseActionSource getInterfaceRequestSource();

    void setInterfaceRequestSource(BaseActionSource source);

    ConfigManager getCm();

    void setCm(ConfigManager cm);

    AppEngInternalAEInventory getConfig();

    void setConfig(AppEngInternalAEInventory config);

    AppEngInternalInventory getPatterns();

    void setPatterns(AppEngInternalInventory patterns);

    UpgradeInventory getUpgrades();

    void setUpgrades(UpgradeInventory upgrades);

    AppEngInternalInventory getStorage();

    void setStorage(AppEngInternalInventory storage);

    WrapperInvSlot getSlotInv();

    void setSlotInv(WrapperInvSlot slotInv);

    boolean isHasConfig();

    void setHasConfig(boolean value);

    int getPriority();

    void setPriority(int priority);

    List<ICraftingPatternDetails> getCraftingList();

    void setCraftingList(List<ICraftingPatternDetails> list);

    boolean isSharedInventory();

    void setSharedInventory(boolean value);

    List<IAEStack<?>> getWaitingToSend();

    void setWaitingToSend(List<IAEStack<?>> list);

    IMEInventory<IAEItemStack> getDestination();

    void setDestination(IMEInventory<IAEItemStack> destination);

    boolean isWorking();

    void setWorking(boolean working);

    YesNo getRedstoneState();

    void setRedstoneState(YesNo state);

    UnlockCraftingEvent getUnlockEvent();

    void setUnlockEvent(UnlockCraftingEvent event);

    List<IAEStack<?>> getUnlockStacks();

    void setUnlockStacks(List<IAEStack<?>> stacks);

    int getLastInputHash();

    void setLastInputHash(int hash);

    ScheduledReason getScheduledReason();

    void setScheduledReason(ScheduledReason reason);

    void gtnl$updatePlan(int slot);
}
