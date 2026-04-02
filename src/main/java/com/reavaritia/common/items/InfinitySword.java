package com.reavaritia.common.items;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import com.google.common.collect.ImmutableList;
import com.reavaritia.client.ReAvaCreativeTabs;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.reavaritia.utils.item.SubtitleDisplay;
import com.reavaritia.utils.item.ToolHelper;
import com.science.gtnl.common.entity.EntitySaddleSlime;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.mixins.late.Botania.AccessorEntityDoppleganger;
import com.science.gtnl.mixins.late.DraconicEvolution.AccessorCustomArmorHandler;
import com.science.gtnl.utils.Utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.DamageSourceInfinitySword;
import fox.spiteful.avaritia.entity.EntityImmortalItem;
import fox.spiteful.avaritia.items.LudicrousItems;
import fox.spiteful.avaritia.render.ICosmicRenderItem;
import galaxyspace.core.entity.boss.EntityBlazeBoss;
import galaxyspace.core.entity.boss.EntityCrystalBoss;
import galaxyspace.core.entity.boss.EntityGhastBoss;
import galaxyspace.core.entity.boss.EntitySlimeBoss;
import galaxyspace.core.entity.boss.EntityWolfBoss;
import gregtech.api.enums.Mods;
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss;
import vazkii.botania.common.entity.EntityDoppleganger;

@Optional.Interface(iface = "fox.spiteful.avaritia.render.ICosmicRenderItem", modid = "Avaritia")
public class InfinitySword extends ItemSword implements ICosmicRenderItem, SubtitleDisplay {

    public static long COOLDOWN = 1000;
    public IIcon cosmicMask;
    public IIcon pommel;
    public static final DamageSource INFINITY_DAMAGE = new DamageSource("damage.gtnl.infinity").setExplosion()
        .setDamageBypassesArmor()
        .setDamageIsAbsolute()
        .setFireDamage()
        .setProjectile()
        .setDamageAllowedInCreativeMode()
        .setMagicDamage();

    public List<Entity> sweepAttackTargets = new ArrayList<>();
    public List<Entity> magnetTargets = new ArrayList<>();
    public static final int MAX_ENTITY_PROCESS_PER_TICK = 100;

    public InfinitySword() {
        super(ToolHelper.INFINITY);
        setUnlocalizedName("InfinitySword");
        setTextureName(RESOURCE_ROOT_ID + ":" + "InfinitySword");
        setCreativeTab(ReAvaCreativeTabs.ReAvaritia);
        this.setMaxDamage(9999);
        MinecraftForge.EVENT_BUS.register(this);
        ReAvaItemList.InfinitySword.set(new ItemStack(this, 1));
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase player) {
        if (player.worldObj.isRemote) return true;

        if (victim instanceof EntityPlayer targetPlayer) {
            boolean wearingInfinityArmor = false;
            for (ItemStack armorStack : targetPlayer.inventory.armorInventory) {
                if (Mods.Avaritia.isModLoaded() && isInfinityArmor(armorStack)) {
                    wearingInfinityArmor = true;
                    break;
                }
            }

            if (wearingInfinityArmor) {
                targetPlayer.setHealth(targetPlayer.getHealth() - 20.0F);
            } else {
                applyInfinityDamage(victim, player);
            }
            return true;
        }

        if (victim instanceof EntityDoppleganger livingTarget) {
            List<String> playersWhoAttacked = ((AccessorEntityDoppleganger) livingTarget).getPlayersWhoAttacked();
            String playerName = player.getCommandSenderName();
            if (!playersWhoAttacked.contains(playerName)) {
                playersWhoAttacked.add(playerName);
            }
            livingTarget.recentlyHit = 100;
            DamageSource playerSource = DamageSource.causePlayerDamage((EntityPlayer) player);
            livingTarget.attackEntityFrom(playerSource, Float.POSITIVE_INFINITY);
            livingTarget.setHealth(0);
            livingTarget.onDeath(playerSource);
            livingTarget.setDead();
            livingTarget.worldObj.removeEntity(livingTarget);
            return true;
        }

        if (victim instanceof EntityDragon) {
            victim.attackEntityFrom(INFINITY_DAMAGE, Float.POSITIVE_INFINITY);
            victim.setDead();
            return true;
        }

        victim.recentlyHit = 100;
        applyInfinityDamage(victim, player);

        return true;
    }

    @Optional.Method(modid = "Avaritia")
    public static boolean isInfinityArmor(ItemStack armorStack) {
        if (armorStack == null) return false;
        return armorStack.getItem() == LudicrousItems.infinity_helm
            || armorStack.getItem() == LudicrousItems.infinity_armor
            || armorStack.getItem() == LudicrousItems.infinity_pants
            || armorStack.getItem() == LudicrousItems.infinity_shoes;
    }

    public void applyInfinityDamage(EntityLivingBase target, EntityLivingBase attacker) {
        if (target instanceof EntitySaddleSlime) return;
        if (target instanceof EntityDragon) {
            target.attackEntityFrom(INFINITY_DAMAGE, Float.POSITIVE_INFINITY);
            target.setDead();
        } else if (target instanceof EntityPlayer playerTarget && attacker.isSneaking()) {
            for (int i = 0; i < playerTarget.inventory.armorInventory.length; i++) {
                ItemStack armorStack = playerTarget.inventory.armorInventory[i];
                if (armorStack != null) {
                    if (armorStack.hasTagCompound()) {
                        if (armorStack.getTagCompound()
                            .hasKey("Energy")) {
                            armorStack.getTagCompound()
                                .setInteger("Energy", 0);
                        }
                        if (armorStack.getTagCompound()
                            .hasKey("charge")) {
                            armorStack.getTagCompound()
                                .setDouble("charge", 0.0);
                        }
                    }
                }
            }
            target.attackEntityFrom(AccessorCustomArmorHandler.getAdminKill(), Float.POSITIVE_INFINITY);
        }

        DamageSource playerSource = DamageSource.causePlayerDamage((EntityPlayer) attacker);
        target.attackEntityFrom(playerSource, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(INFINITY_DAMAGE, Float.POSITIVE_INFINITY);
        if (Mods.Avaritia.isModLoaded()) attackEntityInfinity(target, attacker);

        target.attackEntityFrom(DamageSource.anvil, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.fallingBlock, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.wither, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.magic, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.generic, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.fall, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.cactus, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.starve, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.drown, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.inWall, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.lava, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.inFire, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.onFire, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.outOfWorld, Float.POSITIVE_INFINITY);

        if (!(target instanceof EntityPlayer || (Mods.GalaxySpace.isModLoaded() && isGalaxyBoss(target)))) {
            target.setHealth(0);
            target.onDeath(INFINITY_DAMAGE);
            target.setDead();
            target.worldObj.removeEntity(target);
        }
    }

    @Optional.Method(modid = "GalaxySpace")
    public boolean isGalaxyBoss(EntityLivingBase target) {
        return target instanceof EntityCreeperBoss || target instanceof EntityBlazeBoss
            || target instanceof EntityCrystalBoss
            || target instanceof EntityGhastBoss
            || target instanceof EntitySlimeBoss
            || target instanceof EntityWolfBoss
            || target instanceof EntitySkeletonBoss;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            magnetTargets.clear();
            AxisAlignedBB area = player.boundingBox.expand(50.0, 50.0, 50.0);
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, area);

            for (Entity entity : entities) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
                    magnetTargets.add(entity);
                }
            }
        }

        handleSwordAttack(stack, world, player);
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    public void handleSwordAttack(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        long lastUsed = nbt.getLong("LastUsed");

        if (System.currentTimeMillis() - lastUsed < COOLDOWN) {
            if (world.isRemote) {
                long remainingTime = (COOLDOWN - (System.currentTimeMillis() - lastUsed)) / 1000;
                showSubtitle(
                    StatCollector.translateToLocal("Tooltip_InfinitySword_Aura_00") + remainingTime
                        + StatCollector.translateToLocal("Tooltip_InfinitySword_Aura_01"));
            }
            return;
        }

        if (!world.isRemote) {
            sweepAttackTargets.clear();
            AxisAlignedBB area = player.boundingBox.expand(100.0, 100.0, 100.0);
            List<Entity> targets = world.getEntitiesWithinAABBExcludingEntity(player, area);

            for (Entity target : targets) {
                if (target instanceof EntitySaddleSlime) continue;
                if (!player.isSneaking() && !(target instanceof IMob || target instanceof EntityPlayer)) continue;

                sweepAttackTargets.add(target);
            }
        }

        nbt.setLong("LastUsed", System.currentTimeMillis());
    }

    public void tickSweepAttack(World world, EntityPlayer player) {
        if (world.isRemote || sweepAttackTargets.isEmpty()) return;

        int processed = 0;
        Iterator<Entity> iterator = sweepAttackTargets.iterator();

        while (iterator.hasNext() && processed < MAX_ENTITY_PROCESS_PER_TICK) {
            Entity target = iterator.next();
            boolean shouldRemove = false;

            if (target.isDead || target.worldObj != world) {
                shouldRemove = true;
            } else if (target instanceof EntityDoppleganger livingTarget) {

                List<String> playersWhoAttacked = ((AccessorEntityDoppleganger) livingTarget).getPlayersWhoAttacked();

                String playerName = player.getCommandSenderName();
                if (!playersWhoAttacked.contains(playerName)) {
                    playersWhoAttacked.add(playerName);
                }

                livingTarget.recentlyHit = 100;
                DamageSource playerSource = DamageSource.causePlayerDamage(player);

                livingTarget.attackEntityFrom(playerSource, Float.POSITIVE_INFINITY);
                livingTarget.setHealth(0);
                livingTarget.onDeath(playerSource);
                livingTarget.setDead();
                livingTarget.worldObj.removeEntity(livingTarget);

                shouldRemove = true;
            } else if (target instanceof EntityDragon) {
                target.attackEntityFrom(INFINITY_DAMAGE, Float.POSITIVE_INFINITY);
                target.setDead();
                shouldRemove = true;
            } else if (target instanceof EntityPlayer targetPlayer) {
                if (player.isSneaking()) {
                    handlePlayerTarget(targetPlayer, player, world);
                }
                shouldRemove = true;
            } else if (target instanceof EntityLivingBase livingTarget) {
                livingTarget.recentlyHit = 100;
                applyDoubleSweepDamage(livingTarget, player);
                shouldRemove = true;
            }

            processed++;

            if (shouldRemove) {
                iterator.remove();
            }
        }
    }

    public void handlePlayerTarget(EntityPlayer target, EntityPlayer attacker, World world) {
        if (Mods.Avaritia.isModLoaded() && isInfinite(target)) {
            target.attackEntityFrom(DamageSource.causePlayerDamage(attacker), 20.0F);

            float newHealth = target.getHealth() - 20.0F;
            if (newHealth <= 0) {
                target.setHealth(0);
                target.onDeath(DamageSource.causePlayerDamage(attacker));
            } else {
                target.setHealth(newHealth);
            }

            if (MainConfig.re_avaritia.infinity_sword.enableExplosion) {
                world.newExplosion(target, target.posX, target.posY, target.posZ, 250.0F, true, true);
            }
        } else {
            applyDoubleSweepDamage(target, attacker);
        }
    }

    @Optional.Method(modid = "Avaritia")
    public static boolean isInfinite(EntityPlayer target) {
        return LudicrousItems.isInfinite(target);
    }

    @Optional.Method(modid = "Avaritia")
    public void attackEntityInfinity(EntityLivingBase target, EntityLivingBase attacker) {
        target.attackEntityFrom(new DamageSourceInfinitySword(attacker), Float.POSITIVE_INFINITY);
    }

    public void applyDoubleSweepDamage(EntityLivingBase target, EntityPlayer attacker) {
        if (target instanceof EntitySaddleSlime) return;
        if (!attacker.isSneaking() && target instanceof EntityPlayer) {
            return;
        }

        DamageSource playerSource = DamageSource.causePlayerDamage(attacker);
        target.attackEntityFrom(playerSource, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(INFINITY_DAMAGE, Float.POSITIVE_INFINITY);
        if (Mods.Avaritia.isModLoaded()) attackEntityInfinity(target, attacker);

        target.attackEntityFrom(DamageSource.anvil, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.fallingBlock, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.wither, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.magic, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.generic, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.fall, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.cactus, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.starve, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.drown, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.inWall, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.lava, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.inFire, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.onFire, Float.POSITIVE_INFINITY);
        target.attackEntityFrom(DamageSource.outOfWorld, Float.POSITIVE_INFINITY);

        target.onDeath(INFINITY_DAMAGE);
        if (!(target instanceof EntityPlayer || isGalaxyBoss(target))) {
            target.setHealth(0);
            target.onDeath(INFINITY_DAMAGE);
            target.setDead();
            target.worldObj.removeEntity(target);
        }
    }

    public void tickMagnetEffect(World world, EntityPlayer player) {
        if (world.isRemote || magnetTargets.isEmpty()) return;

        double centerX = player.posX;
        double centerY = player.posY + (player.height / 2.0);
        double centerZ = player.posZ;

        int processed = 0;
        Iterator<Entity> iterator = magnetTargets.iterator();
        while (iterator.hasNext() && processed < MAX_ENTITY_PROCESS_PER_TICK) {
            Entity entity = iterator.next();

            if (entity.isDead || entity.worldObj != world
                || entity.getDistanceSq(player.posX, player.posY, player.posZ) > 2500.0) {
                iterator.remove();
                continue;
            }

            if (entity instanceof EntityItem item) {
                item.setPosition(centerX, centerY, centerZ);
                iterator.remove();
            }

            if (entity instanceof EntityXPOrb xpOrb) {
                xpOrb.setPosition(centerX, centerY, centerZ);
                iterator.remove();
            }
            processed++;
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity victim) {
        if (player.worldObj.isRemote) return true;
        if (victim instanceof EntityLivingBase entity) {
            if (entity instanceof EntityPlayer targetPlayer) {
                boolean wearingInfinityArmor = false;
                for (ItemStack armorStack : targetPlayer.inventory.armorInventory) {
                    if (Mods.Avaritia.isModLoaded() && isInfinityArmor(armorStack)) {
                        wearingInfinityArmor = true;
                        break;
                    }
                }

                if (wearingInfinityArmor) {
                    targetPlayer.setHealth(targetPlayer.getHealth() - 20.0F);
                } else {
                    applyInfinityDamage(targetPlayer, player);
                }
                return true;
            }

            if (entity instanceof EntityDoppleganger livingTarget) {
                List<String> playersWhoAttacked = ((AccessorEntityDoppleganger) livingTarget).getPlayersWhoAttacked();
                String playerName = player.getCommandSenderName();
                if (!playersWhoAttacked.contains(playerName)) {
                    playersWhoAttacked.add(playerName);
                }
                livingTarget.recentlyHit = 100;
                DamageSource playerSource = DamageSource.causePlayerDamage(player);
                livingTarget.attackEntityFrom(playerSource, Float.POSITIVE_INFINITY);
                livingTarget.setHealth(0);
                livingTarget.onDeath(playerSource);
                livingTarget.setDead();
                livingTarget.worldObj.removeEntity(livingTarget);
                return true;
            }

            if (entity instanceof EntityDragon) {
                entity.attackEntityFrom(INFINITY_DAMAGE, Float.POSITIVE_INFINITY);
                entity.setDead();
                return true;
            }

            entity.recentlyHit = 100;
            applyInfinityDamage(entity, player);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_InfinitySword_00"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_InfinitySword_01"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_InfinitySword_02"));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        super.onUpdate(stack, world, entity, slot, isSelected);

        if (!world.isRemote && isSelected && entity instanceof EntityPlayer player) {
            tickSweepAttack(world, player);
            tickMagnetEffect(world, player);
        }

        if (entity instanceof EntityPlayer player) {
            if (player.getCurrentEquippedItem() == stack && player.swingProgress == 0.5F && !world.isRemote) {
                MovingObjectPosition rayTraceResult = Utils.rayTrace(player, false, true, false, 100);

                if (rayTraceResult != null && rayTraceResult.entityHit != null) {
                    Entity hitEntity = rayTraceResult.entityHit;

                    if (hitEntity instanceof EntityLivingBase livingEntity) {
                        hitEntity(stack, livingEntity, player);

                    } else if (hitEntity instanceof EntityDragonPart dragonPart) {
                        EntityDragon dragon = (EntityDragon) dragonPart.entityDragonObj;
                        hitEntity(stack, dragon, player);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void showSubtitle(String messageKey) {
        IChatComponent component = new ChatComponentTranslation(messageKey);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) return;
        if (!isHoldingInfinitySword(player)) return;

        if (player.isBurning()) {
            player.extinguish();
        }

        for (PotionEffect effect : ImmutableList.copyOf(player.getActivePotionEffects())) {
            if (isBadEffect(effect)) {
                player.removePotionEffect(effect.getPotionID());
            }
        }

        player.setHealth(player.getMaxHealth());
        player.getFoodStats()
            .addStats(200, 200.0F);

        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 220, 0));

        if (player.posY < -10) {
            player.setPositionAndUpdate(player.posX, 255, player.posZ);
        }
    }

    public boolean isBadEffect(PotionEffect effect) {
        if (FMLCommonHandler.instance()
            .getSide()
            .isClient()) {
            return isBadEffectClient(effect);
        } else {
            return isBadEffectServer(effect);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isBadEffectClient(PotionEffect effect) {
        return net.minecraft.potion.Potion.potionTypes[effect.getPotionID()].isBadEffect();
    }

    public boolean isBadEffectServer(PotionEffect effect) {
        int potionID = effect.getPotionID();
        return potionID >= 1 && potionID < 256;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAttacked(LivingAttackEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) return;
        if (!isHoldingInfinitySword(player)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) return;
        if (!isHoldingInfinitySword(player)) return;
        event.setCanceled(true);
        player.setHealth(player.getMaxHealth());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumHelper.addRarity("COSMIC", EnumChatFormatting.RED, "Cosmic");
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public IIcon getMaskTexture(ItemStack stack, EntityPlayer player) {
        return cosmicMask;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public float getMaskMultiplier(ItemStack stack, EntityPlayer player) {
        return 1.0f;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir) {
        super.registerIcons(ir);

        this.cosmicMask = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "InfinitySword_Mask");
        this.pommel = ir.registerIcon(RESOURCE_ROOT_ID + ":" + "InfinitySword_Pommel");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 1) {
            return this.pommel;
        }

        return super.getIcon(stack, pass);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return Mods.Avaritia.isModLoaded() ? createImmortalItem(world, location, itemstack) : null;
    }

    @Optional.Method(modid = "Avaritia")
    public Entity createImmortalItem(World world, Entity location, ItemStack itemstack) {
        return new EntityImmortalItem(world, location, itemstack);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return false;
    }

    public static boolean isHoldingInfinitySword(EntityPlayer player) {
        if (player == null) return false;

        ItemStack held = player.getHeldItem();
        if (held == null) return false;

        return held.getItem() instanceof InfinitySword;
    }
}
