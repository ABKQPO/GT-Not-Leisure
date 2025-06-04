package com.science.gtnl.Utils;

import static com.science.gtnl.ScienceNotLeisure.network;
import static com.science.gtnl.Utils.steam.GlobalSteamWorldSavedData.loadInstance;
import static com.science.gtnl.common.render.PlayerDollRenderManager.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.enums.Mods;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.common.command.CommandTickrate;
import com.science.gtnl.common.item.TimeStopManager;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;
import com.science.gtnl.common.packet.ConfigSyncPacket;
import com.science.gtnl.common.packet.SoundPacket;
import com.science.gtnl.common.packet.TitlePacket;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.common.tileentities.machines.basic.MTEMonsterRepellent;
import ic2.api.event.ExplosionEvent;

public class SubscribeEventUtils {

    private static final Set<String> MOD_BLACKLIST = new HashSet<>(
        Arrays.asList(
            Mods.QzMiner.ID,
            Mods.Baubles.ID,
            Mods.ReAvaritia.ID,
            Mods.ScienceNotLeisure.ID,
            Mods.Sudoku.ID,
            Mods.GiveCount.ID));

    // Player
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            // construct message from current server config
            ConfigSyncPacket msg = new ConfigSyncPacket(new MainConfig());// or pass static values
            network.sendTo(msg, player);

            TimeStopManager.setTimeStopped(false);

            if (MainConfig.enableShowJoinMessage || MainConfig.enableDebugMode) {

                if (MainConfig.enableShowAddMods) {
                    for (Mods mod : Mods.values()) {
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

                if (!Mods.Overpowered.isModLoaded() && MainConfig.enableRecipeOutputChance) {
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

            network.sendTo(new SoundPacket(true), player);

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

    @SubscribeEvent
    public void onPlayerLoginOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TimeStopManager.setTimeStopped(false);
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
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
        if (event.entity instanceof EntityPlayer player) {
            World world = player.worldObj;

            int x = (int) Math.floor(player.posX);
            int y = (int) player.posY - 1;
            int z = (int) Math.floor(player.posZ);

            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);

            if (block == GTNLItemList.CrushingWheels.getBlock() && meta == 2) {
                player.attackEntityFrom(DamageSource.generic, 4.0F);
                player.hurtResistantTime = 0;
            }
        }
    }

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent event) {
        if (event.message instanceof ChatComponentTranslation t) {
            if (t.getKey()
                .equals("GTNLEarlyCoreMod.show.clientside")) {
                event.message = new ChatComponentText("");
                event.message.appendSibling(CommandTickrate.c("Your Current Client Tickrate: ", 'f', 'l'));
                event.message
                    .appendSibling(CommandTickrate.c(TickrateAPI.getClientTickrate() + " ticks per second", 'a'));
            }
        }
    }

    // Item

    // World
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        offlineMode = false;
        BLACKLISTED_UUIDS.clear();;
        BLACKLISTED_NAMES.clear();
        BLACKLISTED_SKIN_URLS.clear();
        BLACKLISTED_CAPE_URLS.clear();
        UUID_CACHE.clear();
        if (!world.isRemote) {
            GameRules rules = world.getGameRules();
            if (!rules.hasRule("doWeatherCycle")) {
                rules.setOrCreateGameRule("doWeatherCycle", "true");
            }
            if (event.world.provider.dimensionId == 0) {
                loadInstance(event.world);
            }
        }
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

    public static volatile List<int[]> mobReps = new CopyOnWriteArrayList<>();

    public static int getPoweredRepellentRange(int aTier) {
        return (int) Math.pow(2, 5 + aTier);
    }

    @SubscribeEvent
    public void denyMobSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getResult() == Event.Result.DENY) return;

        if (event.entityLiving instanceof EntitySlime slime && !slime.hasCustomNameTag()
            && event.getResult() == Event.Result.ALLOW) {
            event.setResult(Event.Result.DEFAULT);
        }

        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }

        if (event.entityLiving.isCreatureType(EnumCreatureType.monster, false)) {
            final double maxRangeCheck = Math.pow(getPoweredRepellentRange(GTValues.V.length - 1), 2);
            for (int[] rep : mobReps) {
                if (rep[3] == event.entity.worldObj.provider.dimensionId) {
                    // If the chunk isn't loaded, we ignore this Repellent
                    if (!event.entity.worldObj.blockExists(rep[0], rep[1], rep[2])) continue;
                    final double dx = rep[0] + 0.5F - event.entity.posX;
                    final double dy = rep[1] + 0.5F - event.entity.posY;
                    final double dz = rep[2] + 0.5F - event.entity.posZ;

                    final double check = (dx * dx + dz * dz + dy * dy);
                    // Fail early if outside of max range
                    if (check > maxRangeCheck) continue;

                    final TileEntity tTile = event.entity.worldObj.getTileEntity(rep[0], rep[1], rep[2]);
                    if (tTile instanceof BaseMetaTileEntity metaTile
                        && metaTile.getMetaTileEntity() instanceof MTEMonsterRepellent repellent
                        && check <= Math.pow(repellent.mRange, 2)) {
                        if (event.entityLiving instanceof EntitySlime slime) {
                            slime.setCustomNameTag("DoNotSpawnSlimes");
                        }
                        event.setResult(Event.Result.DENY);
                        // We're already DENYing it. No reason to keep checking
                        return;
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

    // Config
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Mods.ScienceNotLeisure.ID)) {
            MainConfig.reloadConfig();
        }
    }
}
