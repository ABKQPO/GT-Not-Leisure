package com.science.gtnl.common.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityDoppleganger;

public abstract class BaubleItem extends Item implements IBauble {

    public static final String TAG_BAUBLE_UUID_MOST = "baubleUUIDMost";
    public static final String TAG_BAUBLE_UUID_LEAST = "baubleUUIDLeast";

    public static final HashMap<UUID, UUID> itemToPlayerRemote = new HashMap<>();
    public static final HashMap<UUID, UUID> itemToPlayer = new HashMap<>();
    public static final HashSet<UUID> toRemoveItems = new HashSet<>();

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (!EntityDoppleganger.isTruePlayer(par3EntityPlayer)) return par1ItemStack;

        if (canEquip(par1ItemStack, par3EntityPlayer)) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(par3EntityPlayer);
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                if (baubles.isItemValidForSlot(i, par1ItemStack)) {
                    ItemStack stackInSlot = baubles.getStackInSlot(i);
                    if (stackInSlot == null || ((IBauble) Objects.requireNonNull(stackInSlot.getItem()))
                        .canUnequip(stackInSlot, par3EntityPlayer)) {
                        if (!par2World.isRemote) {
                            baubles.setInventorySlotContents(i, par1ItemStack.copy());
                            if (!par3EntityPlayer.capabilities.isCreativeMode) par3EntityPlayer.inventory
                                .setInventorySlotContents(par3EntityPlayer.inventory.currentItem, null);
                        }

                        if (stackInSlot != null) {
                            ((IBauble) stackInSlot.getItem()).onUnequipped(stackInSlot, par3EntityPlayer);
                            return stackInSlot.copy();
                        }
                        break;
                    }
                }
            }
        }

        return par1ItemStack;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        UUID itemUUID = getBaubleUUID(stack);
        if (toRemoveItems.contains(itemUUID)) {
            // this is done like this because on server worn tick gets called after unequip
            // so it would get reapplied
            unapplyItem(itemUUID, player.worldObj.isRemote);
            toRemoveItems.remove(itemUUID);
            return;
        }
        if (!wasPlayerApplied(itemUUID, player.getUniqueID(), player.worldObj.isRemote)) {
            onEquippedOrLoadedIntoWorld(stack, player);
            applyToPlayer(itemUUID, player.getUniqueID(), player.worldObj.isRemote);
        }
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player != null) {
            if (!player.worldObj.isRemote) player.worldObj.playSoundAtEntity(player, "botania:equipBauble", 0.1F, 1.3F);

            if (player instanceof EntityPlayer) ((EntityPlayer) player).addStat(ModAchievements.baubleWear, 1);

            onEquippedOrLoadedIntoWorld(stack, player);
            applyToPlayer(getBaubleUUID(stack), player.getUniqueID(), player.worldObj.isRemote);
        }
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
        // NO-OP
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        toRemoveItems.add(getBaubleUUID(stack));
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    public static UUID getBaubleUUID(ItemStack stack) {
        long most = ItemNBTHelper.getLong(stack, TAG_BAUBLE_UUID_MOST, 0);
        if (most == 0) {
            UUID uuid = UUID.randomUUID();
            ItemNBTHelper.setLong(stack, TAG_BAUBLE_UUID_MOST, uuid.getMostSignificantBits());
            ItemNBTHelper.setLong(stack, TAG_BAUBLE_UUID_LEAST, uuid.getLeastSignificantBits());
            return getBaubleUUID(stack);
        }

        long least = ItemNBTHelper.getLong(stack, TAG_BAUBLE_UUID_LEAST, 0);
        return new UUID(most, least);
    }

    public static boolean wasPlayerApplied(UUID itemUUID, UUID playerUUID, boolean remote) {
        return playerUUID.equals(remote ? itemToPlayerRemote.get(itemUUID) : itemToPlayer.get(itemUUID));
    }

    public static void applyToPlayer(UUID itemUUID, UUID playerUUID, boolean remote) {
        if (remote) {
            itemToPlayerRemote.put(itemUUID, playerUUID);
        } else {
            itemToPlayer.put(itemUUID, playerUUID);
        }
    }

    public static void unapplyItem(UUID itemUUID, boolean remote) {
        if (remote) {
            itemToPlayerRemote.remove(itemUUID);
        } else {
            itemToPlayer.remove(itemUUID);
        }
    }

    public static void removePlayer(UUID playerUUID) {
        for (UUID item : itemToPlayerRemote.keySet()
            .toArray(new UUID[0])) {
            if (playerUUID.equals(itemToPlayerRemote.get(item))) {
                itemToPlayerRemote.remove(item);
            }
        }
        for (UUID item : itemToPlayer.keySet()
            .toArray(new UUID[0])) {
            if (playerUUID.equals(itemToPlayer.get(item))) {
                itemToPlayer.remove(item);
            }
        }
    }
}
