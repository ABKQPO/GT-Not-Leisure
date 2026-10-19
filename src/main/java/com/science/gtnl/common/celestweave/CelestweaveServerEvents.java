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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.common.railgun.RailgunNet;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class CelestweaveServerEvents {

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

    public static ItemStack piece(EntityLivingBase entity, int type) {
        ItemStack stack = entity.getEquipmentInSlot(4 - type);
        return stack != null && stack.getItem() instanceof CelestweaveArmor armor && armor.armorType == type ? stack
            : null;
    }

    public static boolean wearsAny(EntityLivingBase entity) {
        for (int type = 0; type < 4; type++) if (piece(entity, type) != null) return true;
        return false;
    }

    public static boolean activeAndPowered(EntityPlayer player, int type, CelestweaveModule module) {
        ItemStack stack = piece(player, type);
        return stack != null && CelestweaveData.active(stack, module) && CelestweaveEnergy.powered(player, type, stack);
    }

    public static boolean activeAndAffords(EntityPlayer player, int type, CelestweaveModule module, double eu) {
        ItemStack stack = piece(player, type);
        return stack != null && CelestweaveData.active(stack, module)
            && CelestweaveEnergy.affords(player, type, stack, eu);
    }

    public static boolean guiThrottled(EntityPlayer player) {
        long now = player.worldObj.getTotalWorldTime();
        Long last = LAST_GUI.put(player.getUniqueID(), now);
        return last != null && now - last >= 0 && now - last < 5;
    }

    public static void sendFx(Entity entity, int type, double x, double y, double z) {
        RailgunNet.sendAround(
            entity.worldObj,
            entity,
            new CelestweaveFxPacket(entity.getEntityId(), type, x, y, z),
            FX_RANGE);
    }

    private static int jumpLevel(EntityPlayer player) {
        ItemStack boots = piece(player, 3);
        if (boots == null || !CelestweaveData.active(boots, CelestweaveModule.WALK_ASSIST)
            || !CelestweaveEnergy.affords(player, 3, boots, MainConfig.item.celestweave.walkEuPerTick)) return 1;
        return CelestweaveData.cfgInt(
            boots,
            CelestweaveModule.WALK_ASSIST,
            CelestweaveBehaviors.JUMP,
            1,
            1,
            MainConfig.item.celestweave.walkMaxLevel);
    }

    private static final class Tracked {

        @SuppressWarnings("unchecked")
        final Set<CelestweaveModule>[] modules = new Set[4];
        final ItemStack[] stacks = new ItemStack[4];
        double x = Double.NaN, y, z;
        long lastTick = Long.MIN_VALUE, lastFlush;
        int checks;

        Tracked() {
            for (int i = 0; i < 4; i++) modules[i] = Collections.emptySet();
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
        long time = player.worldObj.getTotalWorldTime();
        if (tracked.lastTick == time) return;
        tracked.lastTick = time;
        double moved = Double.isNaN(tracked.x) ? 0
            : Math.sqrt(
                (player.posX - tracked.x) * (player.posX - tracked.x)
                    + (player.posY - tracked.y) * (player.posY - tracked.y)
                    + (player.posZ - tracked.z) * (player.posZ - tracked.z));
        tracked.x = player.posX;
        tracked.y = player.posY;
        tracked.z = player.posZ;
        for (int type = 0; type < 4; type++) {
            ItemStack stack = player.isDead ? null : piece(player, type);
            ItemStack previous = tracked.stacks[type];
            if (server && previous != null && previous != stack) CelestweaveEnergy.flush(player, type, previous);
            Set<CelestweaveModule> now = stack == null ? Collections.emptySet() : CelestweaveData.activeModules(stack);
            Set<CelestweaveModule> before = tracked.modules[type];
            CelestweaveContext ctx = new CelestweaveContext(player, type, stack != null ? stack : previous, moved);
            for (CelestweaveModule module : before) if (!now.contains(module)) CelestweaveBehaviors.of(module)
                .unequip(ctx);
            for (CelestweaveModule module : now) if (!before.contains(module)) CelestweaveBehaviors.of(module)
                .equip(ctx);
            for (CelestweaveModule module : now) CelestweaveBehaviors.of(module)
                .tick(ctx);
            tracked.modules[type] = now;
            tracked.stacks[type] = stack;
        }
        CelestweaveReach.update(player);
        if (!(player instanceof EntityPlayerMP serverPlayer)) return;
        CelestweaveFlight.update(serverPlayer);
        CelestweaveEnergy.flushLarge(player);
        if (time - tracked.lastFlush >= 20 || time < tracked.lastFlush) {
            tracked.lastFlush = time;
            if (++tracked.checks % 5 == 0) CelestweaveEnergy.flush(player);
            restoreSoulbound(player);
            for (int type = 0; type < 4; type++) {
                ItemStack stack = piece(player, type);
                if (stack != null) CelestweaveEnergy.refillFromWireless(stack, serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public void startTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (event.target instanceof EntityPlayer target && event.entityPlayer instanceof EntityPlayerMP viewer)
            CelestweaveFlight.startTracking(target, viewer);
    }

    @SubscribeEvent
    public void fall(LivingFallEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        int jump = jumpLevel(player);
        if (jump > 1) event.distance = Math.max(0, event.distance - 3F * (jump - 1));
        if (!CelestweaveFlight.cancelsFall(player)) return;
        if (!player.worldObj.isRemote && event.distance > 3 && piece(player, 3) != null)
            sendFx(player, CelestweaveFxPacket.LAND, player.posX, player.boundingBox.minY, player.posZ);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void flyableFall(PlayerFlyableFallEvent event) {
        EntityPlayer player = event.entityPlayer;
        if (player.worldObj.isRemote || event.distance <= 3 || piece(player, 3) == null) return;
        sendFx(player, CelestweaveFxPacket.LAND, player.posX, player.boundingBox.minY, player.posZ);
    }

    @SubscribeEvent
    public void jump(LivingEvent.LivingJumpEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player) || player.isSneaking()) return;
        int jump = jumpLevel(player);
        if (jump > 1) player.motionY += 0.275 * Math.pow(jump - 1, 1.08);
    }

    @SubscribeEvent
    public void breakSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.entityPlayer;
        if (!activeAndPowered(player, 3, CelestweaveModule.DIG_AFFINITY)) return;
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
        ItemStack chest = piece(player, 1);
        if (chest == null || !CelestweaveData.active(chest, CelestweaveModule.SOULBOUND)
            || player.worldObj.getGameRules()
                .getGameRuleBooleanValue("keepInventory"))
            return;
        NBTTagList list = new NBTTagList();
        for (int type = 0; type < 4; type++) {
            ItemStack stack = piece(player, type);
            if (stack == null) continue;
            NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
            tag.setByte("Type", (byte) type);
            list.appendTag(tag);
            player.inventory.armorInventory[3 - type] = null;
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
