package com.science.gtnl.common.machine.basicMachine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.dreammaster.gthandler.CustomItemList;
import com.google.common.collect.Sets;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.mixins.late.EnhancedLootBags.AccessorItemLootBag;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.common.Optional;
import eu.usrv.enhancedlootbags.core.LootGroupsHandler;
import eu.usrv.enhancedlootbags.core.items.ItemLootBag;
import eu.usrv.enhancedlootbags.core.serializer.LootGroups;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LootBagRedemption extends MTEBasicMachine {

    public static final Random random = new Random();
    public static final ItemStack coin = Mods.NewHorizonsCoreMod.isModLoaded() ? getCoinTechnician()
        : new ItemStack(Items.paper);

    public LootBagRedemption(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            1,
            new String[] { StatCollector.translateToLocal("Tooltip_LootBagRedemption_00"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_01"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_02"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_03"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_04"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_05") },
            2,
            9,
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_SIDE_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_SIDE_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_SIDE_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_FRONT_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_FRONT_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_TOP_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_TOP_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_TOP_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_BOTTOM_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_GLOW)
                    .glow()
                    .build()));
    }

    public LootBagRedemption(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures, 2, 9);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LootBagRedemption(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public int checkRecipe() {
        ItemStack slotA = getInputAt(0);
        ItemStack slotB = getInputAt(1);
        if (slotA == null && slotB == null) return 0;

        ItemStack lootBagStack;
        ItemStack coinStack;
        if (slotA != null && slotA.getItem() instanceof ItemLootBag) {
            lootBagStack = slotA;
        } else if (slotB != null && slotB.getItem() instanceof ItemLootBag) {
            lootBagStack = slotB;
        } else {
            return 0;
        }
        if (slotA != null && GTUtility.areStacksEqual(slotA, coin)) {
            coinStack = slotA;
        } else if (slotB != null && GTUtility.areStacksEqual(slotB, coin)) {
            coinStack = slotB;
        } else {
            return 0;
        }

        LootGroupsHandler lootGroupsHandler = ((AccessorItemLootBag) lootBagStack.getItem()).getLGHandler();
        if (lootGroupsHandler == null) return 0;

        ItemStack specialItem = getSpecialSlot();
        int groupID = lootBagStack.getItemDamage();

        LootGroups.LootGroup tGrp = lootGroupsHandler.getMergedGroupFromID(groupID, 3);
        if (tGrp == null) return 0;

        List<ItemStack> resultItems = new ArrayList<>();
        boolean specialItemFound = false;

        if (specialItem != null) {
            ItemStack targetLootDrop = getTargetLootDrop(lootBagStack, specialItem);
            if (targetLootDrop != null) {
                specialItemFound = true;
                while (targetLootDrop.stackSize > targetLootDrop.getMaxStackSize()) {
                    resultItems.add(targetLootDrop.splitStack(targetLootDrop.getMaxStackSize()));
                }
                resultItems.add(targetLootDrop);
            }
        }

        // expected count of items to reward
        int itemsToDropCount = tGrp.getMinItems();
        if (tGrp.getMaxItems() > tGrp.getMinItems()) {
            itemsToDropCount = random.nextInt(tGrp.getMaxItems() - tGrp.getMinItems() + 1) + tGrp.getMinItems();
        }

        if (specialItemFound) {
            int specialItemCount = 0;
            for (ItemStack stack : resultItems) {
                if (GTUtility.areStacksEqual(stack, specialItem, true)) {
                    specialItemCount += stack.stackSize;
                }
            }
            itemsToDropCount = Math.max(0, itemsToDropCount - specialItemCount);
        }

        while (itemsToDropCount > 0) {
            LootGroups.LootGroup.Drop tSelectedDrop = null;
            double tRnd = random.nextDouble() * tGrp.getMaxWeight();

            for (LootGroups.LootGroup.Drop tDr : tGrp.getDrops()) {
                tRnd -= tDr.getChance();
                if (tRnd <= 0.0D) {
                    tSelectedDrop = tDr;
                    break;
                }
            }

            if (tSelectedDrop != null) {
                List<LootGroups.LootGroup.Drop> tPossibleItemDrops = lootGroupsHandler
                    .getItemGroupDrops(tGrp, tSelectedDrop);
                for (LootGroups.LootGroup.Drop dr : tPossibleItemDrops) {
                    int tAmount = dr.getAmount();
                    if (dr.getIsRandomAmount()) tAmount = random.nextInt(tAmount) + 1;

                    ItemStack tStackAll = dr.getItemStack(tAmount);
                    if (tStackAll != null) {
                        while (tStackAll.stackSize > tStackAll.getMaxStackSize()) {
                            resultItems.add(tStackAll.splitStack(tStackAll.getMaxStackSize()));
                        }
                        resultItems.add(tStackAll);
                        itemsToDropCount -= 1;
                    }
                }
            }

            if (tSelectedDrop == null) {
                break;
            }
        }

        if (!resultItems.isEmpty() && this.canOutput(resultItems.toArray(new ItemStack[0]))) {

            if (coinStack.stackSize < 4) return 0;

            lootBagStack.stackSize -= 1;
            coinStack.stackSize -= 4;

            for (int i = 0; i < resultItems.size() && i < mOutputItems.length; i++) {
                this.mOutputItems[i] = resultItems.get(i);
            }

            this.mMaxProgresstime = 20;
            this.mEUt = (int) TierEU.RECIPE_LV;
            return 2;
        }

        return 0;
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 1) {
            GTUtility.doSoundAtClient(SoundResource.IC2_MACHINES_MAGNETIZER_LOOP, 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public void startProcess() {
        sendLoopStart((byte) 1);
    }

    @Override
    public long maxEUStore() {
        return Math.max(getEUVar(), GTValues.V[6]);
    }

    @Override
    public boolean hasEnoughEnergyToCheckRecipe() {
        return getBaseMetaTileEntity().getStoredEU() > TierEU.RECIPE_LV;
    }

    private static final int BONUS_MUL_PER_LEVEL = 3;

    /**
     * Find the target loot drop in the bag.
     * <p>
     * The result can be {@code null} if the target wasn't found in the loot bag and its chain. And the amount of the
     * result can be more than 64 because of level bonus.
     *
     * @param lootBagItemStack the loot bag item
     * @param targetItemStack  the target item to get
     * @return the reward item
     */
    public static ItemStack getTargetLootDrop(ItemStack lootBagItemStack, ItemStack targetItemStack) {
        Item lootBagItem = lootBagItemStack.getItem();
        assert lootBagItem instanceof ItemLootBag;

        LootGroupsHandler lgHandler = ((AccessorItemLootBag) lootBagItem).getLGHandler();
        int bagId = lootBagItemStack.getItemDamage();

        int depth = 0;
        LootGroups.LootGroup group = lgHandler.getGroupByID(bagId);
        // visited groups will be added to this set, so we can ensure that we won't drop into a dead loop.
        HashSet<LootGroups.LootGroup> visited = Sets.newHashSet();
        do {
            if (!visited.add(group)) {
                // oops, the group has already been searched, there's a circle in the reference chain!
                log.warn("Unexpected state, LootGroup {} has a loop reference chain.", group.getGroupID());
                return null;
            }
            for (LootGroups.LootGroup.Drop drop : group.getDrops()) {
                ItemStack rewardItemStack = drop.getItemStack();
                if (GTUtility.areStacksEqual(targetItemStack, rewardItemStack, true)) {
                    // grant bonus rewards for using a higher level loot bag.
                    // for example, if we will give 2x FOO in LV bag,
                    // we're expected to reward 6x (2 x 3¹) FOO in MV bag, and 18x (2 x 3²) in HV bag.
                    rewardItemStack.stackSize = (int) (drop.getAmount() * Math.pow(BONUS_MUL_PER_LEVEL, depth));
                    return rewardItemStack;
                }
            }
            // we've iterated all drops, but not found the target, so we try to find it in the lower level group.
            if (group.getCombineWithTrash()) { // the group has a trash (lower level) group
                LootGroups.LootGroup trash = lgHandler.getGroupByID(group.getTrashGroup());
                if (trash == null) { // a trash group is expected, but it's not valid
                    log.warn(
                        "Unexpected state, LootGroup {} has an invalid trash group reference {}",
                        group.getGroupID(),
                        group.getTrashGroup());
                    return null;
                }
                group = trash;
                depth++;
            } else { // nope, we've iterated all but still not got what we wanted. :(
                return null;
            }
        } while (true);
    }

    @Optional.Method(modid = "dreamcraft")
    private static ItemStack getCoinTechnician() {
        return CustomItemList.CoinTechnician.get(1);
    }
}
