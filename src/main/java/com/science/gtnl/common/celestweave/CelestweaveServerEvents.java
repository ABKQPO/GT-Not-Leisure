package com.science.gtnl.common.celestweave;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

import com.science.gtnl.common.equipment.EquipmentNet;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class CelestweaveServerEvents {

    public static final String SOULBOUND_TAG = "GTNLCelestweaveSoulbound";
    private static final double FX_RANGE = 64;
    public static Predicate<EntityPlayer> local = player -> false;
    private static final Map<EntityPlayer, Tracked> TRACKED = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<UUID, Long> LAST_GUI = new HashMap<>();
    private static boolean registered;

    private CelestweaveServerEvents() {}

    public static void register() {
        if (registered) return;
        registered = true;
        CelestweaveServerEvents handler = new CelestweaveServerEvents();
        CelestweaveShield shield = new CelestweaveShield();
        MinecraftForge.EVENT_BUS.register(handler);
        MinecraftForge.EVENT_BUS.register(shield);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }

    public static boolean guiThrottled(EntityPlayer player) {
        long now = player.worldObj.getTotalWorldTime();
        Long last = LAST_GUI.put(player.getUniqueID(), now);
        return last != null && now - last >= 0 && now - last < 5;
    }

    public static void sendFx(Entity entity, int type, double x, double y, double z) {
        EquipmentNet.sendAround(entity, new CelestweaveFxPacket(entity.getEntityId(), type, x, y, z), FX_RANGE);
    }

    static void landFx(EntityPlayer player, float distance) {
        if (!player.worldObj.isRemote && distance > 3 && CelestweaveData.piece(player, CelestweaveArmor.BOOTS) != null)
            sendFx(player, CelestweaveFxPacket.LAND, player.posX, player.boundingBox.minY, player.posZ);
    }

    private static int jumpLevel(EntityPlayer player) {
        ItemStack boots = CelestweaveData.piece(player, CelestweaveArmor.BOOTS);
        if (boots == null || !CelestweaveData.active(boots, CelestweaveModule.WALK_ASSIST)
            || !CelestweaveEnergy
                .affords(player, CelestweaveArmor.BOOTS, boots, MainConfig.item.celestweave.walkEuPerTick))
            return 1;
        return CelestweaveModuleSetting.WALK_JUMP.get(boots);
    }

    private static boolean samePiece(ItemStack previous, ItemStack stack) {
        return stack != null && stack.getItem() == previous.getItem()
            && CelestweaveData.root(stack)
                .equals(CelestweaveData.root(previous));
    }

    private static class Tracked {

        @SuppressWarnings("unchecked")
        final Set<CelestweaveModule>[] modules = new Set[CelestweaveArmor.PIECES];
        final ItemStack[] stacks = new ItemStack[CelestweaveArmor.PIECES];
        double x = Double.NaN, y, z;
        long lastTick = Long.MIN_VALUE, lastFlush;
        int checks;

        Tracked() {
            for (int i = 0; i < CelestweaveArmor.PIECES; i++) modules[i] = Collections.emptySet();
        }
    }

    @SubscribeEvent
    public void playerTickStart(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        EntityPlayer player = event.player;
        if (player.worldObj.isRemote && !local.test(player)) return;
        CelestweaveBehaviors.updateStep(player);
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        boolean server = !player.worldObj.isRemote;
        if (!server && !local.test(player)) return;
        Tracked tracked;
        synchronized (TRACKED) {
            tracked = TRACKED.computeIfAbsent(player, k -> new Tracked());
        }
        long now = player.worldObj.getTotalWorldTime();
        if (tracked.lastTick == now) return;
        tracked.lastTick = now;
        double moved = Double.isNaN(tracked.x) ? 0
            : Math.sqrt(
                (player.posX - tracked.x) * (player.posX - tracked.x)
                    + (player.posY - tracked.y) * (player.posY - tracked.y)
                    + (player.posZ - tracked.z) * (player.posZ - tracked.z));
        tracked.x = player.posX;
        tracked.y = player.posY;
        tracked.z = player.posZ;
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = player.isDead ? null : CelestweaveData.piece(player, piece);
            ItemStack previous = tracked.stacks[piece];
            if (server && previous != null && previous != stack)
                CelestweaveEnergy.flush(player, piece, samePiece(previous, stack) ? stack : previous);
            Set<CelestweaveModule> after = stack == null ? Collections.emptySet()
                : CelestweaveData.activeModules(stack);
            Set<CelestweaveModule> before = tracked.modules[piece];
            CelestweaveContext ctx = new CelestweaveContext(player, piece, stack != null ? stack : previous, moved);
            for (CelestweaveModule module : before) if (!after.contains(module)) CelestweaveBehaviors.of(module)
                .unequip(ctx);
            for (CelestweaveModule module : after) if (!before.contains(module)) CelestweaveBehaviors.of(module)
                .equip(ctx);
            for (CelestweaveModule module : after) CelestweaveBehaviors.of(module)
                .tick(ctx);
            tracked.modules[piece] = after;
            tracked.stacks[piece] = stack;
        }
        CelestweaveReach.update(player);
        if (!(player instanceof EntityPlayerMP serverPlayer)) return;
        CelestweaveFlight.update(serverPlayer);
        CelestweaveEnergy.flushLarge(player);
        if (now - tracked.lastFlush >= 20 || now < tracked.lastFlush) {
            tracked.lastFlush = now;
            if (++tracked.checks % 5 == 0) CelestweaveEnergy.flush(player);
            restoreSoulbound(player);
            for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
                ItemStack stack = CelestweaveData.piece(player, piece);
                if (stack != null) CelestweaveEnergy.refillFromWireless(stack, serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public void startTracking(StartTracking event) {
        if (event.target instanceof EntityPlayer target && event.entityPlayer instanceof EntityPlayerMP viewer)
            CelestweaveFlight.startTracking(target, viewer);
    }

    @SubscribeEvent
    public void fall(LivingFallEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        int jump = jumpLevel(player);
        if (jump > 1) event.distance = Math.max(0, event.distance - 3F * (jump - 1));
        if (!CelestweaveFlight.cancelsFall(player)) return;
        landFx(player, event.distance);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void flyableFall(PlayerFlyableFallEvent event) {
        landFx(event.entityPlayer, event.distance);
    }

    @SubscribeEvent
    public void jump(LivingEvent.LivingJumpEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player) || player.isSneaking()) return;
        int jump = jumpLevel(player);
        if (jump > 1) player.motionY += 0.275 * Math.pow(jump - 1, 1.08);
    }

    @SubscribeEvent
    public void breakSpeed(BreakSpeed event) {
        EntityPlayer player = event.entityPlayer;
        if (!CelestweaveEnergy.activeAndHasEnergy(player, CelestweaveArmor.BOOTS, CelestweaveModule.DIG_AFFINITY))
            return;
        float speed = event.newSpeed;
        if (player.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(player))
            speed *= 5F;
        if (!player.onGround) speed *= 5F;
        event.newSpeed = speed;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void death(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayerMP player)) return;
        CelestweaveEnergy.flush(player);
        CelestweaveReach.reset(player);
        ItemStack chest = CelestweaveData.piece(player, CelestweaveArmor.CHEST);
        if (chest == null || !CelestweaveData.active(chest, CelestweaveModule.SOULBOUND)
            || player.worldObj.getGameRules()
                .getGameRuleBooleanValue("keepInventory"))
            return;
        NBTTagList list = new NBTTagList();
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = CelestweaveData.piece(player, piece);
            if (stack == null) continue;
            NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
            tag.setByte("Type", (byte) piece);
            list.appendTag(tag);
            player.inventory.armorInventory[3 - piece] = null;
        }
        if (list.tagCount() > 0) persisted(player).setTag(SOULBOUND_TAG, list);
    }

    private static NBTTagCompound persisted(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10))
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static void restoreSoulbound(EntityPlayer player) {
        if (player.getHealth() <= 0 || !player.getEntityData()
            .hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) return;
        NBTTagCompound persisted = persisted(player);
        if (!persisted.hasKey(SOULBOUND_TAG, 9)) return;
        NBTTagList list = persisted.getTagList(SOULBOUND_TAG, 10);
        persisted.removeTag(SOULBOUND_TAG);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
            if (stack == null) continue;
            int slot = 3 - (tag.getByte("Type") & 3);
            if (player.inventory.armorInventory[slot] == null) player.inventory.armorInventory[slot] = stack;
            else if (!player.inventory.addItemStackToInventory(stack))
                player.dropPlayerItemWithRandomChoice(stack, false);
        }
        player.inventory.markDirty();
    }

    @SubscribeEvent
    public void respawn(PlayerEvent.PlayerRespawnEvent event) {
        CelestweaveReach.forget(event.player);
        CelestweaveBehaviors.forget(event.player);
        restoreSoulbound(event.player);
    }

    @SubscribeEvent
    public void login(PlayerEvent.PlayerLoggedInEvent event) {
        restoreSoulbound(event.player);
        if (event.player instanceof EntityPlayerMP player) CelestweaveFlight.restore(player);
    }

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        CelestweaveEnergy.flush(event.player);
        CelestweaveReach.forget(event.player);
        CelestweaveBehaviors.forget(event.player);
        CelestweaveFlight.forget(event.player);
        TRACKED.remove(event.player);
        LAST_GUI.remove(event.player.getUniqueID());
        if (event.player instanceof EntityPlayerMP player) CelestweaveDash.forget(player);
    }

    @SubscribeEvent
    public void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        CelestweaveFlight.changedDimension(event.player);
    }
}
