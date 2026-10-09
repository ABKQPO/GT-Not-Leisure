package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.mixins.early.minecraft.AccessorFoodStats;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.effect.TextEffects;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElainaBrooch extends BaubleItem implements IBaubleExpanded {

    private static final String TITLE_TYPE = "title";
    private static final String[] BAUBLE_TYPES = { TITLE_TYPE };
    private static final String FLIGHT_TAG = "GTNLElainaBroochFlight";
    private static final String INVULNERABLE_TICKS_TAG = "GTNLElainaBroochInvulnerableTicks";
    private static final String NEXT_BUFF_REFRESH_TAG = "GTNLElainaBroochNextBuffRefresh";
    private static final String TRAVELER_ACTIVE_TAG = "GTNLMajoTravelerActive";
    private static final String TRAVELER_FLIGHT_TAG = "GTNLMajoTravelerFlight";
    private static final UUID MAX_HEALTH_MODIFIER_ID = UUID.fromString("61f8a7e4-2c5d-4bb7-b1f4-2cfcdbef1eb6");
    private static final int ARMOR_VALUE = 40;
    private static final double MAX_HEALTH = 100.0D;
    private static final int INVULNERABLE_TICKS = 50;
    private static final int BUFF_DURATION_TICKS = 30 * 20;
    private static final int BUFF_REFRESH_TICKS = 20 * 20;

    public ItemElainaBrooch() {
        BaubleExpandedSlots.tryRegisterType(TITLE_TYPE);
        setUnlocalizedName("gtnl.elaina_brooch");
        setTextureName(RESOURCE_ROOT_ID + ":elaina_brooch");
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "elaina_brooch");
        GTNLItemList.ElainaBrooch.set(new ItemStack(this));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return null;
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return BAUBLE_TYPES;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        for (int line = 0; line < 4; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.elaina_brooch.lore." + line),
                    TextEffects.GENESIS_COMPONENT_RARITY_SHADER));
        }
        tooltip.add("");
        String worn = StatCollector.translateToLocal("item.gtnl.elaina_brooch.worn");
        tooltip.add(TextEffects.apply(worn, TextEffects.EVERCOLD_CYAN));
        for (int line = 0; line < 3; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.elaina_brooch.tooltip." + line),
                    TextEffects.EVERCOLD_CYAN));
        }
    }

    @Override
    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase wearer) {
        if (!wearer.worldObj.isRemote) wearer.getEntityData()
            .removeTag(NEXT_BUFF_REFRESH_TAG);
    }

    public static boolean isEquipped(EntityPlayer player) {
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return false;
        for (int slot = 0; slot < baubles.getSizeInventory(); slot++) {
            if (!TITLE_TYPE.equals(BaubleExpandedSlots.getSlotType(slot))) continue;
            ItemStack stack = baubles.getStackInSlot(slot);
            if (stack != null && stack.getItem() instanceof ItemElainaBrooch) return true;
        }
        return false;
    }

    public static boolean hasGrantedFlight(EntityPlayer player) {
        return player.getEntityData()
            .getBoolean(FLIGHT_TAG);
    }

    public static int getArmorValue() {
        return ARMOR_VALUE;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player) || player.worldObj.isRemote) return;

        NBTTagCompound data = player.getEntityData();
        int invulnerableTicks = data.getInteger(INVULNERABLE_TICKS_TAG);
        if (invulnerableTicks > 0) data.setInteger(INVULNERABLE_TICKS_TAG, invulnerableTicks - 1);

        if (!isEquipped(player)) {
            updateMaxHealth(player, false);
            if (data.getBoolean(FLIGHT_TAG)) {
                data.removeTag(FLIGHT_TAG);
                if (!player.capabilities.isCreativeMode && !data.getBoolean(TRAVELER_ACTIVE_TAG)) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
            }
            data.removeTag(INVULNERABLE_TICKS_TAG);
            data.removeTag(NEXT_BUFF_REFRESH_TAG);
            return;
        }

        updateMaxHealth(player, true);
        if (!player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            data.setBoolean(FLIGHT_TAG, true);
            player.sendPlayerAbilities();
        } else if (data.getBoolean(TRAVELER_FLIGHT_TAG)) {
            data.setBoolean(FLIGHT_TAG, true);
        }

        long worldTime = player.worldObj.getTotalWorldTime();
        long nextBuffRefresh = data.getLong(NEXT_BUFF_REFRESH_TAG);
        if (!data.hasKey(NEXT_BUFF_REFRESH_TAG) || worldTime >= nextBuffRefresh
            || nextBuffRefresh - worldTime > BUFF_REFRESH_TICKS) {
            refreshBuffs(player);
            data.setLong(NEXT_BUFF_REFRESH_TAG, worldTime + BUFF_REFRESH_TICKS);
        }

        if (player.ticksExisted % 20 != 0) return;
        player.setHealth(player.getMaxHealth());
        FoodStats food = player.getFoodStats();
        AccessorFoodStats foodAccessor = (AccessorFoodStats) food;
        foodAccessor.setFoodlevel(20);
        foodAccessor.setFoodSaturationLevel(20.0F);
        foodAccessor.setFoodExhaustionLevel(0.0F);
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            int potionId = effect.getPotionID();
            if (potionId < 0 || potionId >= Potion.potionTypes.length) continue;
            Potion potion = Potion.potionTypes[potionId];
            if (potion != null && potion.isBadEffect()) player.removePotionEffect(potionId);
        }
    }

    private static void refreshBuffs(EntityPlayer player) {
        refreshBuff(player, Potion.nightVision, 0);
        refreshBuff(player, Potion.fireResistance, 0);
        refreshBuff(player, Potion.regeneration, 2);
        refreshBuff(player, Potion.waterBreathing, 0);
        refreshBuff(player, Potion.digSpeed, 0);
        refreshBuff(player, Potion.resistance, 1);
        refreshBuff(player, Potion.damageBoost, 4);
    }

    private static void refreshBuff(EntityPlayer player, Potion potion, int amplifier) {
        PotionEffect current = player.getActivePotionEffect(potion);
        if (current != null && (current.getAmplifier() > amplifier
            || current.getAmplifier() == amplifier && current.getDuration() >= BUFF_DURATION_TICKS)) return;
        player.addPotionEffect(new PotionEffect(potion.id, BUFF_DURATION_TICKS, amplifier, true));
    }

    private static void updateMaxHealth(EntityPlayer player, boolean equipped) {
        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (attribute == null) return;
        AttributeModifier current = attribute.getModifier(MAX_HEALTH_MODIFIER_ID);
        if (!equipped) {
            if (current == null) return;
            attribute.removeModifier(current);
            if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            return;
        }
        if (Math.abs(attribute.getAttributeValue() - MAX_HEALTH) < 0.001D) return;
        if (current != null) attribute.removeModifier(current);
        double baseValue = attribute.getAttributeValue();
        int operation = baseValue > 0.0D ? 2 : 0;
        double amount = operation == 2 ? MAX_HEALTH / baseValue - 1.0D : MAX_HEALTH;
        attribute.applyModifier(
            new AttributeModifier(MAX_HEALTH_MODIFIER_ID, "Elaina brooch maximum health", amount, operation)
                .setSaved(false));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player) || player.worldObj.isRemote) return;
        if (isEquipped(player) && player.getEntityData()
            .getInteger(INVULNERABLE_TICKS_TAG) > 0) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player) || player.worldObj.isRemote) return;
        if (!isEquipped(player) || event.ammount <= 0.0F) return;
        player.getEntityData()
            .setInteger(INVULNERABLE_TICKS_TAG, INVULNERABLE_TICKS);
        if (!event.source.isUnblockable()) {
            event.setCanceled(true);
            return;
        }
        if (event.ammount > player.getMaxHealth()) {
            event.setCanceled(true);
            player.setHealth(1.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player) || player.worldObj.isRemote) return;
        if (!isEquipped(player)) return;
        event.setCanceled(true);
        player.setHealth(1.0F);
        player.getEntityData()
            .setInteger(INVULNERABLE_TICKS_TAG, INVULNERABLE_TICKS);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.wasDeath) return;
        NBTTagCompound originalData = event.original.getEntityData();
        NBTTagCompound newData = event.entityPlayer.getEntityData();
        if (originalData.getBoolean(FLIGHT_TAG)) newData.setBoolean(FLIGHT_TAG, true);
        int invulnerableTicks = originalData.getInteger(INVULNERABLE_TICKS_TAG);
        if (invulnerableTicks > 0) newData.setInteger(INVULNERABLE_TICKS_TAG, invulnerableTicks);
    }
}
