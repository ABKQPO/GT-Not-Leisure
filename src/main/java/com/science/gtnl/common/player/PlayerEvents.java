package com.science.gtnl.common.player;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.network.TitlePacketHandler;
import com.gtnllib.mod.ModList;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingMode;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionHitEffectState;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionState;
import com.science.gtnl.common.packet.DraconicArmorProjectionHitEffectPacket;
import com.science.gtnl.common.packet.SoundPacket;
import com.science.gtnl.common.packet.SyncCircuitNanitesPacket;
import com.science.gtnl.compat.galaxyspace.SteamRocketSchematic;
import com.science.gtnl.compat.overpowered.RecipeOutputChance;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.AchievementsLoader;
import com.science.gtnl.mixins.early.minecraft.AccessorFoodStats;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@EventBusSubscriber
public class PlayerEvents {

    public static final Object2IntMap<UUID> FOOD_TICK_TIMERS = new Object2IntOpenHashMap<>();

    public static final DamageSource CRUSHING_DAMAGE = new DamageSource("damage.gtnl.crushing")
        .setDamageBypassesArmor();

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) return;
        player.triggerAchievement(AchievementsLoader.welcome);
        ScienceNotLeisure.network.sendTo(new SoundPacket(true), player);
        ScienceNotLeisure.network.sendTo(new SyncCircuitNanitesPacket(player.worldObj.getSeed()), player);

        SteamRocketSchematic.unlockFor(player);

        TimeStopPocketWatch.setTimeStopped(false);

        if (ModList.allListedModsLoaded()) {
            AchievementsLoader.gtnlAchievementsPage.getAchievements()
                .add(AchievementsLoader.installAllCommunityMod);
            player.triggerAchievement(AchievementsLoader.installAllCommunityMod);
        }

        if (MainConfig.message.enableShowJoinMessage || MainConfig.debug.enableDebugMode) {

            if (MainConfig.message.enableShowAddMods) {
                for (ModList mod : ModList.loadedListedMods()) {
                    String translatedPrefix = StatCollector.translateToLocal("gtnl.message.mod_installed");
                    player.addChatMessage(
                        new ChatComponentText(mod.displayName + translatedPrefix)
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
                }
            }

            player.addChatMessage(
                new ChatComponentTranslation("gtnl.message.welcome.0")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BOLD)));
            player.addChatMessage(
                new ChatComponentTranslation("gtnl.message.welcome.1")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            player.addChatMessage(
                new ChatComponentTranslation("gtnl.message.welcome.2")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            player.addChatMessage(
                new ChatComponentTranslation("gtnl.message.welcome.3")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));

            if (MainConfig.recipe.enableDeleteRecipe) {
                player.addChatMessage(
                    new ChatComponentTranslation("gtnl.message.recipe_removal_enabled")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW)));
            }

            if (RecipeOutputChance.enabled()) {
                player.addChatMessage(
                    new ChatComponentTranslation("gtnl.message.recipe_output_chance_enabled")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
                player.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.message.recipe_output_chance_increase",
                        MainConfig.machine.recipeOutputChance + "%")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
            }

            if (MainConfig.recipe.enableShowDelRecipeTitle) {
                TitlePacketHandler.sendTimes(player, 10, 200, 20);
                TitlePacketHandler
                    .sendTitle(player, new ChatComponentTranslation("gtnl.message.recipe_removal_enabled"));
            }
        }

        if (MainConfig.debug.enableDebugMode) {
            player.addChatMessage(
                new ChatComponentTranslation("gtnl.message.debug_enabled")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoginOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TimeStopPocketWatch.setTimeStopped(false);
        BaubleItem.removePlayer(event.player.getUniqueID());
        DraconicArmorProjectionState.clear(event.player.getUniqueID());
        DraconicArmorProjectionHitEffectState.clear(event.player.getUniqueID());
        QuantumCraftingMode.onPlayerLoggedOut(event.player);
        FOOD_TICK_TIMERS.removeInt(event.player.getUniqueID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectedArmorPlayerAttacked(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) {
            return;
        }
        if (player.worldObj.isRemote || event.isCanceled() || event.ammount <= 0.0F) {
            return;
        }
        if (DraconicArmorProjectionState.get(player) == null) {
            return;
        }
        DamageSource source = event.source;
        if (source != null) {
            EntityLivingBase trueSource = null;
            if (source.getEntity() instanceof EntityLivingBase entitySource) {
                trueSource = entitySource;
            } else if (source.getSourceOfDamage() instanceof EntityLivingBase indirectSource) {
                trueSource = indirectSource;
            }
            if (trueSource == player) {
                return;
            }
        }

        ScienceNotLeisure.network.sendToAllAround(
            new DraconicArmorProjectionHitEffectPacket(player, 1.0F),
            new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64.0D));

        if (isFirstProjectedHitInResistanceWindow(player)) {
            player.worldObj.playSoundEffect(
                player.posX + 0.5D,
                player.posY + 0.5D,
                player.posZ + 0.5D,
                "draconicevolution:shieldStrike",
                0.9F,
                player.worldObj.rand.nextFloat() * 0.1F + 1.055F);
        }
    }

    private static boolean isFirstProjectedHitInResistanceWindow(EntityPlayer player) {
        int hurtResistantTime = player.hurtResistantTime;
        int maxHurtResistantTime = player.maxHurtResistantTime;
        if (maxHurtResistantTime <= 0) {
            return true;
        }
        return hurtResistantTime <= Math.max(0, maxHurtResistantTime - 2);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityLivingBase entityLiving) {
            World world = entityLiving.worldObj;

            int x = (int) Math.floor(entityLiving.posX);
            int y = (int) entityLiving.posY - 1;
            int z = (int) Math.floor(entityLiving.posZ);

            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);

            if (block == GTNLItemList.CrushingWheels.getBlock() && meta == 2) {
                if (entityLiving instanceof EntityPlayer player) {
                    player.attackEntityFrom(CRUSHING_DAMAGE, 0.4F);
                    player.hurtResistantTime = 0;
                } else {
                    entityLiving.attackEntityFrom(CRUSHING_DAMAGE, 1F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.worldObj.isRemote
            || !MainConfig.other.enableSaturationHeal) return;

        EntityPlayer player = event.player;
        FoodStats stats = player.getFoodStats();

        if (stats.getFoodLevel() >= 20) {
            UUID uuid = player.getUniqueID();
            int timer = FOOD_TICK_TIMERS.getOrDefault(uuid, 0) + 1;

            if (timer >= 10) {
                timer = 0;

                if (player.getHealth() < player.getMaxHealth()) {

                    if (stats.getSaturationLevel() >= 1.0f) {
                        player.heal(1);
                        ((AccessorFoodStats) stats).setFoodSaturationLevel(stats.getSaturationLevel() - 1);
                    } else {
                        ((AccessorFoodStats) stats).setFoodlevel(stats.getFoodLevel() - 1);
                    }
                }
            }

            FOOD_TICK_TIMERS.put(uuid, timer);
        } else {
            FOOD_TICK_TIMERS.removeInt(player.getUniqueID());
        }
    }
}
