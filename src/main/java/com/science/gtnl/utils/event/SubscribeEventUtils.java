package com.science.gtnl.utils.event;

import static com.science.gtnl.utils.world.steam.GlobalSteamWorldSavedData.loadInstance;

import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.network.TitlePacketHandler;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.common.command.CommandTickrate;
import com.science.gtnl.common.gui.recipe.ElectrocellGeneratorFrontend;
import com.science.gtnl.common.gui.recipe.RocketAssemblerFrontend;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionHitEffectState;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionState;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.packet.DraconicArmorProjectionHitEffectPacket;
import com.science.gtnl.common.packet.SoundPacket;
import com.science.gtnl.common.packet.SyncCircuitNanitesPacket;
import com.science.gtnl.common.render.PlayerDollRenderManager;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.AchievementsLoader;
import com.science.gtnl.loader.RecipeLoader;
import com.science.gtnl.mixins.early.minecraft.AccessorFoodStats;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.recipes.data.CircuitNanitesRecipeData;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import gregtech.api.enums.Mods;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import tectech.thing.casing.TTCasingsContainer;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.IManaDissolvable;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.recipe.RecipeManaInfusion;

public class SubscribeEventUtils {

    public static final ModList[] MOD_LIST_VALUES = ModList.values();
    public static final Set<String> MOD_BLACKLIST = new ObjectOpenHashSet<>(
        new String[] { ModList.QzMiner.ID, ModList.Baubles.ID, ModList.ReAvaritia.ID, ModList.ScienceNotLeisure.ID,
            ModList.Sudoku.ID, ModList.GiveCount.ID, ModList.ChromaticTooltips.ID, ModList.ChromaticTooltipsCompat.ID,
            ModList.NewHorizonsCoreMod.ID, ModList.GalaxySpace.ID, ModList.EnhancedLootBags.ID,
            ModList.NotEnoughEnergistics.ID, ModList.NEICustomDiagrams.ID, ModList.AvaritiaAddons.ID,
            ModList.EtFuturumRequiem.ID, ModList.NotEnoughItems.ID });

    public static final Object2IntMap<UUID> FOOD_TICK_TIMERS = new Object2IntOpenHashMap<>();

    public static final DamageSource CRUSHING_DAMAGE = new DamageSource("damage.gtnl.crushing")
        .setDamageBypassesArmor();

    public static boolean CIRCUIT_NANITES_DATA_LOAD = false;
    public static long SLEEP_TIME = 0;
    public static long SERVER_TICK = 0;
    public static final long MAX_SERVER_SLEEP_MILLIS = 2000L;

    // Player
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            player.triggerAchievement(AchievementsLoader.welcome);
            ScienceNotLeisure.network.sendTo(new SoundPacket(true), player);
            ScienceNotLeisure.network.sendTo(new SyncCircuitNanitesPacket(player.worldObj.getSeed()), player);

            if (Mods.GalaxySpace.isModLoaded() && Mods.GalacticraftCore.isModLoaded()) {
                addRocketUnlockedPage(player);
            }

            TimeStopPocketWatch.setTimeStopped(false);

            boolean giveAchievement = shouldGrantAllCommunityModAchievement();

            if (giveAchievement) {
                AchievementsLoader.gtnlAchievementsPage.getAchievements()
                    .add(AchievementsLoader.installAllCommunityMod);
                player.triggerAchievement(AchievementsLoader.installAllCommunityMod);
            }

            if (MainConfig.message.enableShowJoinMessage || MainConfig.debug.enableDebugMode) {

                if (MainConfig.message.enableShowAddMods) {
                    for (ModList mod : MOD_LIST_VALUES) {
                        if (mod.isModLoaded() && !MOD_BLACKLIST.contains(mod.getID())) {
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

                if (MainConfig.recipe.enableDeleteRecipe) {
                    player.addChatMessage(
                        new ChatComponentTranslation("Welcome_GTNL_DeleteRecipe")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW)));
                }

                if (!ModList.Overpowered.isModLoaded() && MainConfig.machine.enableRecipeOutputChance) {
                    player.addChatMessage(
                        new ChatComponentTranslation("Welcome_GTNL_RecipeOutputChance_00")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
                    player.addChatMessage(
                        new ChatComponentTranslation(
                            "Welcome_GTNL_RecipeOutputChance_01",
                            MainConfig.machine.recipeOutputChance + "%")
                                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
                }

                if (MainConfig.recipe.enableShowDelRecipeTitle) {
                    TitlePacketHandler.sendTimes(player, 10, 200, 20);
                    TitlePacketHandler.sendTitle(player, new ChatComponentTranslation("Welcome_GTNL_DeleteRecipe"));
                }
            }

            if (MainConfig.debug.enableDebugMode) {
                player.addChatMessage(
                    new ChatComponentTranslation("Welcome_GTNL_Debug")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            }

            float tickrate = MainConfig.tickrate.defaultTickrate;
            try {
                GameRules rules = MinecraftServer.getServer()
                    .getEntityWorld()
                    .getGameRules();
                if (rules.hasRule(TickrateAPI.GAME_RULE)) {
                    tickrate = Float.parseFloat(rules.getGameRuleStringValue(TickrateAPI.GAME_RULE));
                }
            } catch (Exception ex) {
                ScienceNotLeisure.LOG.warn("Failed to read tickrate game rule", ex);
            }
            TickrateAPI.changeClientTickrate(event.player, tickrate);
        }
    }

    public void addRocketUnlockedPage(EntityPlayerMP player) {
        SchematicRegistry.addUnlockedPage(
            player,
            SchematicRegistry.getMatchingRecipeForID(MainConfig.item.steam_rocket.idSchematicRocketSteam));
    }

    @SubscribeEvent
    public void onPlayerLoginOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TimeStopPocketWatch.setTimeStopped(false);
        BaubleItem.removePlayer(event.player.getUniqueID());
        DraconicArmorProjectionState.clear(event.player.getUniqueID());
        DraconicArmorProjectionHitEffectState.clear(event.player.getUniqueID());
        FOOD_TICK_TIMERS.removeInt(event.player.getUniqueID());
        CIRCUIT_NANITES_DATA_LOAD = false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onProjectedArmorPlayerAttacked(LivingAttackEvent event) {
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

    private boolean isFirstProjectedHitInResistanceWindow(EntityPlayer player) {
        int hurtResistantTime = player.hurtResistantTime;
        int maxHurtResistantTime = player.maxHurtResistantTime;
        if (maxHurtResistantTime <= 0) {
            return true;
        }
        return hurtResistantTime <= Math.max(0, maxHurtResistantTime - 2);
    }

    @SubscribeEvent
    public void connect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal) {
            float tickrate = MainConfig.tickrate.defaultTickrate;
            try {
                GameRules rules = MinecraftServer.getServer()
                    .getEntityWorld()
                    .getGameRules();
                if (rules.hasRule(TickrateAPI.GAME_RULE)) {
                    tickrate = Float.parseFloat(rules.getGameRuleStringValue(TickrateAPI.GAME_RULE));
                }
            } catch (Exception ex) {
                ScienceNotLeisure.LOG.warn("Failed to read tickrate game rule", ex);
            }
            TickrateAPI.changeServerTickrate(tickrate);
            TickrateAPI.changeClientTickrate(null, tickrate);
        } else {
            TickrateAPI.changeClientTickrate(null, 20F);
        }
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

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ++SERVER_TICK;
        } else if (SLEEP_TIME > 0 && SERVER_TICK % 20 == 0) {
            try {
                long sleepMillis = Math.min(MAX_SERVER_SLEEP_MILLIS, SLEEP_TIME);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                Thread.currentThread()
                    .interrupt();
                ScienceNotLeisure.LOG.warn("Server tick sleep was interrupted", e);
            } finally {
                SLEEP_TIME = 0;
            }
        }
    }

    // World
    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.world.isRemote) return;
        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;

        Block block = event.block;
        int meta = world.getBlockMetadata(x, y, z);

        if (block == TTCasingsContainer.GodforgeCasings && meta == 8) {

            if (world.rand.nextInt(1000) == 0) {
                world.playSoundEffect(x, y, z, ScienceNotLeisure.RESOURCE_ROOT_ID + ":tidal.wave", 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world.isRemote) return;
        if (!CIRCUIT_NANITES_DATA_LOAD) {
            RecipeLoader.loadCircuitNanitesData(world.getSeed());
            CIRCUIT_NANITES_DATA_LOAD = true;
        }
        if (event.world.provider.dimensionId == 0) {
            loadInstance(event.world);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        PlayerDollRenderManager.offlineMode = false;
        CIRCUIT_NANITES_DATA_LOAD = false;
        FOOD_TICK_TIMERS.clear();
        PlayerDollRenderManager.BLACKLISTED_UUIDS.clear();
        PlayerDollRenderManager.BLACKLISTED_NAMES.clear();
        PlayerDollRenderManager.BLACKLISTED_SKIN_URLS.clear();
        PlayerDollRenderManager.BLACKLISTED_CAPE_URLS.clear();
        PlayerDollRenderManager.UUID_CACHE.clear();
        ElectrocellGeneratorFrontend.initializedRecipes.clear();
        RocketAssemblerFrontend.initializedRecipes.clear();
        CircuitNanitesRecipeData.recipeDataMap.clear();
        GTNLRecipeMaps.CircuitNanitesDataRecipes.getBackend()
            .clearRecipes();
    }

    @SubscribeEvent
    public void onExplosionEvent(net.minecraftforge.event.world.ExplosionEvent event) {
        if (tryAbsorbExplosion(
            event.world,
            event.explosion.explosionX,
            event.explosion.explosionY,
            event.explosion.explosionZ,
            event.explosion.explosionSize)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onExplosionEvent(ic2.api.event.ExplosionEvent event) {
        if (tryAbsorbExplosion(event.world, event.x, event.y, event.z, (float) event.power)) {
            event.setCanceled(true);
        }
    }

    public boolean tryAbsorbExplosion(World world, double explosionX, double explosionY, double explosionZ,
        float power) {
        int ex = (int) explosionX;
        int ey = (int) explosionY;
        int ez = (int) explosionZ;

        for (int x = ex - 10; x <= ex + 10; x++) {
            for (int y = ey - 10; y <= ey + 10; y++) {
                for (int z = ez - 10; z <= ez + 10; z++) {

                    if (!world.blockExists(x, y, z)) continue;

                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (!(tile instanceof BaseMetaTileEntity te)) continue;

                    IMetaTileEntity mte = te.getMetaTileEntity();
                    if (!(mte instanceof ExplosionDynamoHatch machine)) continue;

                    double dx = te.getXCoord() + 0.5 - explosionX;
                    double dy = te.getYCoord() + 0.5 - explosionY;
                    double dz = te.getZCoord() + 0.5 - explosionZ;
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (distance > 10.0) continue;

                    double efficiency = Math.max(0.0, 1.0 - 0.05 * distance);
                    long addedEU = (long) (power * 2048 * efficiency);

                    long stored = machine.getEUVar();
                    long maxEU = machine.maxEUStore();

                    if (stored + addedEU <= maxEU) {
                        machine.setEUVar(stored + addedEU);
                        return true;
                    }
                }
            }
        }
        return false;
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

    // Botania
    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj == null) return;

        if (player.worldObj.isRemote) return;

        ItemStack held = player.getHeldItem();
        if (held == null) return;

        TileEntity te = player.worldObj.getTileEntity(event.x, event.y, event.z);

        if (!(te instanceof IManaPool pool)) return;

        boolean didSomething = false;

        if (held.getItem() instanceof IManaDissolvable dissolvable) {
            int before = held.stackSize;

            EntityItem fakeItem = new EntityItem(player.worldObj, event.x + 0.5, event.y + 1.0, event.z + 0.5, held);

            dissolvable.onDissolveTick(pool, held, fakeItem);

            if (held.stackSize != before) {
                didSomething = true;
            }
        } else if (held.getItem() instanceof IManaItem) {
            didSomething = tryManaTransfer(player, te, held);
        } else {
            didSomething = tryInfusion(player, pool, held);
        }

        if (didSomething) {
            event.setCanceled(true);
        }
    }

    public boolean tryInfusion(EntityPlayer player, IManaPool pool, ItemStack stack) {
        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
            if (!recipe.matches(stack)) continue;

            int manaPer = recipe.getManaToConsume();
            if (manaPer <= 0) return false;

            int maxByMana = pool.getCurrentMana() / manaPer;
            int maxByItems = stack.stackSize;

            int times = Math.min(maxByMana, maxByItems);

            if (times <= 0) return false;

            pool.recieveMana(-manaPer * times);

            stack.stackSize -= times;
            if (stack.stackSize <= 0) {
                player.setCurrentItemOrArmor(0, null);
            }

            ItemStack out = recipe.getOutput()
                .copy();
            out.stackSize *= times;

            EntityItem outputItem = new EntityItem(player.worldObj, player.posX, player.posY + 0.5, player.posZ, out);

            player.worldObj.spawnEntityInWorld(outputItem);
            return true;
        }

        return false;
    }

    public boolean tryManaTransfer(EntityPlayer player, TileEntity pool, ItemStack stack) {
        IManaItem manaItem = (IManaItem) stack.getItem();
        IManaPool manaPool = (IManaPool) pool;

        boolean outputting = manaPool.isOutputtingPower();
        int transferRate = manaItem.getMaxMana(stack);

        if (outputting) {
            // Pool -> Item
            if (!manaItem.canReceiveManaFromPool(stack, pool)) return false;

            int space = transferRate - manaItem.getMana(stack);
            if (space <= 0) return false;

            int available = manaPool.getCurrentMana();
            if (available <= 0) return false;

            int move = Math.min(transferRate, Math.min(space, available));
            manaItem.addMana(stack, move);
            manaPool.recieveMana(-move);

        } else if (pool instanceof ISparkAttachable attachable) {
            // Item -> Pool
            if (!manaItem.canExportManaToPool(stack, pool)) return false;

            int available = manaItem.getMana(stack);
            if (available <= 0) return false;

            int space = attachable.getAvailableSpaceForMana();
            if (space <= 0) return false;

            int move = Math.min(transferRate, Math.min(space, available));
            manaItem.addMana(stack, -move);
            manaPool.recieveMana(move);
        }
        return true;
    }

    public static boolean shouldGrantAllCommunityModAchievement() {
        for (ModList mod : MOD_LIST_VALUES) {
            if (!MOD_BLACKLIST.contains(mod.getID()) && !mod.isModLoaded()) {
                return false;
            }
        }
        return true;
    }

    public static void queueServerSleep(long sleepMillis) {
        if (sleepMillis <= 0L) {
            return;
        }
        SLEEP_TIME = Math.min(MAX_SERVER_SLEEP_MILLIS, SLEEP_TIME + sleepMillis);
    }
}
