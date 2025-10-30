package com.science.gtnl.utils;

import static com.science.gtnl.ScienceNotLeisure.network;
import static com.science.gtnl.common.render.PlayerDollRenderManager.*;
import static com.science.gtnl.utils.steam.GlobalSteamWorldSavedData.loadInstance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.StatCollector;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.common.command.CommandTickrate;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.common.item.TimeStopManager;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;
import com.science.gtnl.common.packet.SoundPacket;
import com.science.gtnl.common.packet.SyncCircuitNanitesPacket;
import com.science.gtnl.common.packet.SyncConfigPacket;
import com.science.gtnl.common.packet.SyncRecipePacket;
import com.science.gtnl.common.packet.TitlePacket;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.AchievementsLoader;
import com.science.gtnl.loader.RecipeLoader;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.mixins.early.Minecraft.AccessorFoodStats;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.gui.recipe.ElectrocellGeneratorFrontend;
import com.science.gtnl.utils.gui.recipe.RocketAssemblerFrontend;
import com.science.gtnl.utils.recipes.CircuitNanitesRecipeData;

import bartworks.API.SideReference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import ic2.api.event.ExplosionEvent;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;

public class SubscribeEventUtils {

    private static final Set<String> MOD_BLACKLIST = new HashSet<>(
        Arrays.asList(
            ModList.QzMiner.ID,
            ModList.Baubles.ID,
            ModList.ReAvaritia.ID,
            ModList.ScienceNotLeisure.ID,
            ModList.Sudoku.ID,
            ModList.GiveCount.ID));

    private static final Map<UUID, Integer> foodTickTimers = new HashMap<>();

    public static final DamageSource CRUSHING_DAMAGE = new DamageSource("damage.gtnl.crushing")
        .setDamageBypassesArmor();

    public static boolean circuitNanitesDataLoad = false;

    // Player
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            player.triggerAchievement(AchievementsLoader.welcome);
            network.sendTo(new SyncConfigPacket(), player);
            network.sendTo(new SoundPacket(true), player);
            network.sendTo(new SyncCircuitNanitesPacket(player.worldObj.getSeed()), player);

            SchematicRegistry
                .addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(MainConfig.idSchematicRocketSteam));

            TimeStopManager.setTimeStopped(false);

            boolean giveAchievement = Arrays.stream(ModList.values())
                .filter(mod -> !MOD_BLACKLIST.contains(mod.getModId()))
                .allMatch(ModList::isModLoaded);

            if (giveAchievement) {
                AchievementsLoader.gtnlAchievementsPage.getAchievements()
                    .add(AchievementsLoader.installAllCommunityMod);
                player.triggerAchievement(AchievementsLoader.installAllCommunityMod);
            }

            if (MainConfig.enableShowJoinMessage || MainConfig.enableDebugMode) {

                if (MainConfig.enableShowAddMods) {
                    for (ModList mod : ModList.values()) {
                        if (mod.isModLoaded() && !MOD_BLACKLIST.contains(mod.getModId())) {
                            String translatedPrefix = StatCollector.translateToLocal("Welcome_GTNL_ModInstall");
                            player.addChatMessage(
                                new ChatComponentText(mod.displayName + translatedPrefix)
                                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
                        }
                    }
                }

                player.addChatMessage(
                    new ChatComponentTranslation("Welcome_GTNL_00")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BOLD)));
                player.addChatMessage(
                    new ChatComponentTranslation("Welcome_GTNL_01")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
                player.addChatMessage(
                    new ChatComponentTranslation("Welcome_GTNL_02")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
                player.addChatMessage(
                    new ChatComponentTranslation("Welcome_GTNL_03")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));

                if (MainConfig.enableDeleteRecipe || MainConfig.enableDebugMode) {
                    player.addChatMessage(
                        new ChatComponentTranslation("Welcome_GTNL_DeleteRecipe")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW)));
                }

                if (!ModList.Overpowered.isModLoaded() && MainConfig.enableRecipeOutputChance) {
                    player.addChatMessage(
                        new ChatComponentTranslation("Welcome_GTNL_RecipeOutputChance_00")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
                    player.addChatMessage(
                        new ChatComponentTranslation(
                            "Welcome_GTNL_RecipeOutputChance_01",
                            MainConfig.recipeOutputChance + "%")
                                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
                }

                if (MainConfig.enableShowDelRecipeTitle) {
                    TitlePacket.sendTitleToPlayer(player, "Welcome_GTNL_DeleteRecipe", 200, 0xFFFF55, 2);
                }
            }

            if (MainConfig.enableDebugMode) {
                player.addChatMessage(
                    new ChatComponentTranslation("Welcome_GTNL_Debug")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            }

            float tickrate = MainConfig.defaultTickrate;
            try {
                GameRules rules = MinecraftServer.getServer()
                    .getEntityWorld()
                    .getGameRules();
                if (rules.hasRule(GTNLEarlyCoreMod.GAME_RULE)) {
                    tickrate = Float.parseFloat(rules.getGameRuleStringValue(GTNLEarlyCoreMod.GAME_RULE));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            TickrateAPI.changeClientTickrate(event.player, tickrate);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void EntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event == null || !(event.entity instanceof EntityPlayerMP player) || !SideReference.Side.Server) return;
        network.sendTo(new SyncRecipePacket(), player);
    }

    @SubscribeEvent
    public void onPlayerLoginOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TimeStopManager.setTimeStopped(false);
        BaubleItem.removePlayer(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            BaubleItem.removePlayer(Minecraft.getMinecraft().thePlayer.getUniqueID());
        }
        // reload the config from disk (undoing the server push)
        MainConfig.reloadConfig();
    }

    @SubscribeEvent
    public void connect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal) {
            float tickrate = MainConfig.defaultTickrate;
            try {
                GameRules rules = MinecraftServer.getServer()
                    .getEntityWorld()
                    .getGameRules();
                if (rules.hasRule(GTNLEarlyCoreMod.GAME_RULE)) {
                    tickrate = Float.parseFloat(rules.getGameRuleStringValue(GTNLEarlyCoreMod.GAME_RULE));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            TickrateAPI.changeServerTickrate(tickrate);
            TickrateAPI.changeClientTickrate(null, tickrate);
        } else {
            TickrateAPI.changeClientTickrate(null, 20F);
        }
    }

    @SubscribeEvent
    public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        TickrateAPI.changeServerTickrate(MainConfig.defaultTickrate);
        TickrateAPI.changeClientTickrate(null, MainConfig.defaultTickrate);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
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
    public void chat(ClientChatReceivedEvent event) {
        if (event.message instanceof ChatComponentTranslation chat) {
            if (chat.getKey()
                .equals("GTNLEarlyCoreMod.show.clientside")) {
                event.message = new ChatComponentText("");
                event.message.appendSibling(CommandTickrate.c("Your Current Client Tickrate: ", 'f', 'l'));
                event.message
                    .appendSibling(CommandTickrate.c(TickrateAPI.getClientTickrate() + " ticks per second", 'a'));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.worldObj.isRemote || !MainConfig.enableSaturationHeal)
            return;

        EntityPlayer player = event.player;
        FoodStats stats = player.getFoodStats();

        if (stats.getFoodLevel() >= 20) {
            UUID uuid = player.getUniqueID();
            int timer = foodTickTimers.getOrDefault(uuid, 0) + 1;

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

            foodTickTimers.put(uuid, timer);
        } else {
            foodTickTimers.remove(player.getUniqueID());
        }
    }

    // World
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world.isRemote) return;
        if (!circuitNanitesDataLoad) {
            RecipeLoader.loadCircuitNanitesData(world.getSeed());
            circuitNanitesDataLoad = true;
        }

        GameRules rules = world.getGameRules();
        if (!rules.hasRule("doWeatherCycle")) {
            rules.setOrCreateGameRule("doWeatherCycle", "true");
        }
        if (event.world.provider.dimensionId == 0) {
            loadInstance(event.world);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        offlineMode = false;
        circuitNanitesDataLoad = false;
        BLACKLISTED_UUIDS.clear();
        BLACKLISTED_NAMES.clear();
        BLACKLISTED_SKIN_URLS.clear();
        BLACKLISTED_CAPE_URLS.clear();
        UUID_CACHE.clear();
        ElectrocellGeneratorFrontend.initializedRecipes.clear();
        RocketAssemblerFrontend.initializedRecipes.clear();
        CircuitNanitesRecipeData.recipeDataMap.clear();
        RecipePool.CircuitNanitesDataRecipes.getBackend()
            .clearRecipes();
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.world.isRemote) return;

        World world = event.world;
        GameRules rules = world.getGameRules();

        if (rules.hasRule("doWeatherCycle") && !rules.getGameRuleBooleanValue("doWeatherCycle")) {
            WorldInfo info = world.getWorldInfo();

            info.setRainTime(1000000);
            info.setThunderTime(1000000);

            info.setRaining(info.isRaining());
            info.setThundering(info.isThundering());

            world.rainingStrength = info.isRaining() ? 1.0f : 0.0f;
            world.thunderingStrength = info.isThundering() ? 1.0f : 0.0f;
        }
    }

    @SubscribeEvent
    public void onExplosionEvent(ExplosionEvent event) {
        World world = event.world;
        double explosionX = event.x;
        double explosionY = event.y;
        double explosionZ = event.z;
        float power = (float) event.power;

        int ex = (int) explosionX;
        int ey = (int) explosionY;
        int ez = (int) explosionZ;

        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {
                    if (!world.blockExists(x, y, z)) continue;
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof BaseMetaTileEntity te) {
                        IMetaTileEntity mte = te.getMetaTileEntity();
                        if (mte instanceof ExplosionDynamoHatch machine) {
                            double dx = te.getXCoord() + 0.5 - explosionX;
                            double dy = te.getYCoord() + 0.5 - explosionY;
                            double dz = te.getZCoord() + 0.5 - explosionZ;
                            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                            if (distance <= 10.0) {
                                double efficiency = Math.max(0.0, 1.0 - 0.05 * distance);
                                long addedEU = (long) (power * 2048 * efficiency);
                                long stored = machine.getEUVar();
                                long maxEU = machine.maxEUStore();
                                if (stored + addedEU <= maxEU) {
                                    machine.setEUVar(stored + addedEU);
                                    event.setCanceled(true);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Mobs
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entity instanceof EntityCreeper creeper) {
            if (event.source.isExplosion() && event.source.getSourceOfDamage() instanceof EntityCreeper) {
                NBTTagCompound nbt = creeper.getEntityData();
                if (!nbt.hasKey("creeperExplosionDelayed")) {
                    nbt.setInteger("creeperExplosionDelay", 30);
                    nbt.setBoolean("creeperExplosionDelayed", true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onZombieDeath(LivingDeathEvent event) {
        if (!(event.entity instanceof EntityZombie zombie)) return;
        if (zombie.worldObj.isRemote) return;
        if (!zombie.isChild()) return;
        if (!(zombie.ridingEntity instanceof EntityChicken)) return;
        EntityItem drop = new EntityItem(
            zombie.worldObj,
            zombie.posX,
            zombie.posY,
            zombie.posZ,
            GTNLItemList.RecordLavaChicken.get(1));
        drop.delayBeforeCanPickup = 10;
        zombie.worldObj.spawnEntityInWorld(drop);
    }

    // Config
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ModList.ScienceNotLeisure.ID)) {
            MainConfig.reloadConfig();
        }
    }
}
