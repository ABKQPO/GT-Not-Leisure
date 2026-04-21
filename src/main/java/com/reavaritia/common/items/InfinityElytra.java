package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;
import static com.science.gtnl.ScienceNotLeisure.network;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.science.gtnl.common.packet.ElytraBoostPacket;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.compat.CompatBaublesExpanded;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

public class InfinityElytra extends ItemArmorElytra {

    private static final DamageSource ELYTRA_COLLISION = new DamageSource("infinityElytraCollision");

    public InfinityElytra() {
        super();
        setUnlocalizedName("InfinityElytra");
        setTextureName(RESOURCE_ROOT_ID + ":" + "InfinityElytra");
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        setMaxDamage(9999);
        GameRegistry.registerItem(this, "InfinityElytra");
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        ReAvaItemList.InfinityElytra.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumHelper.addRarity("COSMIC", EnumChatFormatting.RED, "Cosmic");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_InfinityElytra_00"));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {}

    @SubscribeEvent
    public void onPlayerFall(LivingFallEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) return;
        if (player.worldObj.isRemote) return;
        if (!isWearingInfinityElytra(player)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) return;
        if (!isWearingInfinityElytra(player)) return;

        if ("flyIntoWall".equals(event.source.getDamageType())) {
            event.setCanceled(true);
            return;
        }

        if (!event.source.isDamageAbsolute() && !event.source.isUnblockable()) {
            event.ammount *= 0.2F;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != Side.SERVER) return;

        EntityPlayer player = event.player;
        if (!isWearingInfinityElytra(player)) return;
        if (!(player instanceof IElytraPlayer elytraPlayer)) return;
        if (!elytraPlayer.etfu$isElytraFlying()) return;

        double speed = Math
            .sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ);
        if (speed < 0.5D) return;

        AxisAlignedBB hitbox = player.boundingBox.expand(1.0D, 0.5D, 1.0D);
        List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, hitbox);
        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase) {
                entity.attackEntityFrom(ELYTRA_COLLISION, 200.0F);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != Side.CLIENT) return;

        EntityPlayer player = event.player;
        if (player != Minecraft.getMinecraft().thePlayer) return;
        if (!isWearingInfinityElytra(player)) return;
        if (!(player instanceof IElytraPlayer)) return;
        if (!((IElytraPlayer) player).etfu$isElytraFlying()) return;

        if (Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed()) {
            network.sendToServer(new ElytraBoostPacket());
        }
    }

    public static boolean isWearingInfinityElytra(EntityPlayer entity) {
        if (!ModsList.BAUBLES_EXPANDED.isLoaded() || ConfigModCompat.elytraBaublesExpandedCompat != 2) {
            ItemStack armorSlot = entity.getEquipmentInSlot(3);
            if (armorSlot != null && armorSlot.getItem() instanceof InfinityElytra) {
                return true;
            }
        }
        if (ModsList.BAUBLES_EXPANDED.isLoaded() && ConfigModCompat.elytraBaublesExpandedCompat != 0) {
            ItemStack elytra = CompatBaublesExpanded.getElytraFromBaubles(entity);
            return elytra != null && elytra.getItem() instanceof InfinityElytra;
        }
        return false;
    }
}
