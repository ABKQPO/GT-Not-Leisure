package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import com.reavaritia.common.playSound;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.entity.EntityArrowCustom;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TwilightSword extends ItemSword implements playSound {

    public static final Item.ToolMaterial[] TWILIGHT_MATERIALS = {
        EnumHelper.addToolMaterial("TWILIGHT_EXPLOSIVE", 3, 9999, 14.0F, 30.0F, 30),
        EnumHelper.addToolMaterial("TWILIGHT_MAGIC", 3, 9999, 14.0F, 40.0F, 30),
        EnumHelper.addToolMaterial("TWILIGHT_PHYSICAL", 3, 9999, 14.0F, 50.0F, 30),
        EnumHelper.addToolMaterial("TWILIGHT_VOID", 3, 9999, 14.0F, 80.0F, 30) };

    public enum DamageType {
        EXPLOSIVE,
        MAGIC,
        PHYSICAL,
        VOID
    }

    private final Map<UUID, Integer> cooldownMap = new HashMap<>();
    private int attackCount = 0;

    public TwilightSword() {
        super(TWILIGHT_MATERIALS[0]);
        this.setMaxStackSize(1);
        this.setUnlocalizedName("TwilightSword");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "TwilightSword");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        setMaxDamage(9999);
        MinecraftForge.EVENT_BUS.register(this);
        GTNLItemList.TwilightSword.set(new ItemStack(this, 1));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (player.worldObj.getTotalWorldTime() % 5 == 0) {
            new EntityArrowCustom(player.worldObj).generateAdvancedArrowRain(player);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        super.onUpdate(stack, world, entity, slot, isSelected);

        if (entity instanceof EntityPlayer player) {
            UUID playerId = player.getUniqueID();

            int cooldown = cooldownMap.getOrDefault(playerId, 0);
            if (cooldown > 0) {
                cooldownMap.put(playerId, cooldown - 1);
            }

            if (player.getCurrentEquippedItem() == stack && player.swingProgress == 0.5F
                && !world.isRemote
                && cooldown <= 0) {
                MovingObjectPosition rayTraceResult = rayTrace(player, false, true);

                if (rayTraceResult != null && rayTraceResult.entityHit != null) {
                    Entity hitEntity = rayTraceResult.entityHit;

                    if (hitEntity instanceof EntityLivingBase target) {
                        // 处理普通生物
                        target.attackEntityFrom(
                            DamageSource.causePlayerDamage(player),
                            TWILIGHT_MATERIALS[0].getDamageVsEntity());
                        hitEntity(stack, target, player);
                    } else if (hitEntity instanceof EntityDragonPart dragonPart) {
                        // 处理末影龙的部分
                        EntityDragon dragon = (EntityDragon) dragonPart.entityDragonObj; // 获取末影龙本体
                        dragon.attackEntityFrom(
                            DamageSource.causePlayerDamage(player),
                            TWILIGHT_MATERIALS[0].getDamageVsEntity());
                        hitEntity(stack, dragon, player); // 将伤害传递给末影龙本体
                    }
                }
                playSoundIfReady(world, player);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void playSoundIfReady(World world, EntityPlayer player) {
        UUID playerId = player.getUniqueID();
        world.playSoundAtEntity(player, RESOURCE_ROOT_ID + ":" + "twilight.sword.attack", 1, 1);
        cooldownMap.put(playerId, 50);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        DamageType currentDamageType = DamageType.values()[attackCount++ % 4];

        float baseDamage = TWILIGHT_MATERIALS[0].getDamageVsEntity() * (attacker.getHealth() < 10 ? 2 : 1);
        float finalDamage = baseDamage * 1.5f;

        applyBypassInvulnerabilityDamage(target, finalDamage, currentDamageType);

        addRandomEffect(target);
        return true;
    }

    private void applyBypassInvulnerabilityDamage(EntityLivingBase target, float damage, DamageType type) {
        if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.isCreativeMode) {
            return;
        }

        float newHealth = target.getHealth() - damage;
        if (newHealth <= 0) {
            target.setHealth(0);
            target.onDeath(DamageSourceTwilight.getDamageSource(type));
        } else {
            target.setHealth(newHealth);
        }

        target.hurtResistantTime = 0;
        target.attackEntityFrom(DamageSourceTwilight.getDamageSource(type), 0.0F);
    }

    public static class DamageSourceTwilight extends DamageSource {

        public static final DamageSource EXPLOSIVE = new DamageSourceTwilight("twilight_explosive").setExplosion();
        public static final DamageSource MAGIC = new DamageSourceTwilight("twilight_magic").setMagicDamage();
        public static final DamageSource PHYSICAL = new DamageSourceTwilight("twilight_physical")
            .setDamageBypassesArmor();
        public static final DamageSource VOID = new DamageSourceTwilight("twilight_void").setDamageBypassesArmor();

        protected DamageSourceTwilight(String name) {
            super(name);
        }

        @Override
        public boolean isUnblockable() {
            return true;
        }

        public static DamageSource getDamageSource(DamageType type) {
            return switch (type) {
                case EXPLOSIVE -> EXPLOSIVE;
                case MAGIC -> MAGIC;
                case VOID -> VOID;
                default -> PHYSICAL;
            };
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayer player) {
            if (isHoldingSword(player)) {
                applyBuffs(player);
                clearProjectiles(player);
            }
        }
    }

    @SubscribeEvent
    public void onProjectileHit(LivingAttackEvent event) {
        if (event.entityLiving instanceof EntityPlayer player) {
            if (isHoldingSword(player) && event.source.isProjectile()) {
                event.setCanceled(true);
                reflectProjectile(event.source.getEntity());
            }
        }
    }

    private void reflectProjectile(Entity projectile) {
        if (projectile instanceof IProjectile && !projectile.isDead) {
            projectile.motionX *= -1.5;
            projectile.motionY *= -0.5;
            projectile.motionZ *= -1.5;
            projectile.velocityChanged = true;
            ((IProjectile) projectile)
                .setThrowableHeading(projectile.motionX, projectile.motionY, projectile.motionZ, 1.5f, 1.0f);
        }
    }

    private boolean isHoldingSword(EntityPlayer player) {
        return player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof TwilightSword;
    }

    private void addRandomEffect(EntityLivingBase target) {
        int effect = new Random().nextInt(4);
        switch (effect) {
            case 0:
                target.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 5));
                break;
            case 1:
                target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 5));
                break;
            case 2:
                target.addPotionEffect(new PotionEffect(Potion.wither.id, 100, 5));
                break;
            case 3:
                target.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 5));
                break;
        }
    }

    private void applyBuffs(EntityPlayer player) {
        int duration = 1200;
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, duration, 2));
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, duration, 1));
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, duration, 5));
        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, duration, 1));
        player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, duration, 1));
        player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, duration, 9));
        player.addPotionEffect(new PotionEffect(Potion.jump.id, duration, 1));
        player.clearActivePotions();
    }

    private void clearProjectiles(EntityPlayer player) {
        List<Entity> projectiles = player.worldObj
            .getEntitiesWithinAABB(Entity.class, player.boundingBox.expand(3, 3, 3));
        for (Entity e : projectiles) {
            if (e instanceof IProjectile) {
                e.setDead();
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 720000;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_00"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_01"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_02"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_03"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_04"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_05"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_06"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_07"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_08"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_09"));
        toolTip.add(StatCollector.translateToLocal("Tooltip_TwilightSword_10"));
    }

    public static MovingObjectPosition rayTrace(final EntityPlayer p, final boolean hitBlocks,
        final boolean hitEntities) {
        final World w = p.getEntityWorld();

        final float f = 1.0F;
        float f1 = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * f;
        final float f2 = p.prevRotationYaw + (p.rotationYaw - p.prevRotationYaw) * f;
        final double d0 = p.prevPosX + (p.posX - p.prevPosX) * f;
        final double d1 = p.prevPosY + (p.posY - p.prevPosY) * f + 1.62D - p.yOffset;
        final double d2 = p.prevPosZ + (p.posZ - p.prevPosZ) * f;
        final Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        final float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        final float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        final float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        final float f6 = MathHelper.sin(-f1 * 0.017453292F);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        final double d3 = 100.0D;

        final Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);

        final AxisAlignedBB bb = AxisAlignedBB
            .getBoundingBox(
                Math.min(vec3.xCoord, vec31.xCoord),
                Math.min(vec3.yCoord, vec31.yCoord),
                Math.min(vec3.zCoord, vec31.zCoord),
                Math.max(vec3.xCoord, vec31.xCoord),
                Math.max(vec3.yCoord, vec31.yCoord),
                Math.max(vec3.zCoord, vec31.zCoord))
            .expand(16, 16, 16);

        Entity entity = null;
        double closest = 9999999.0D;
        if (hitEntities) {
            final List<Entity> list = w.getEntitiesWithinAABBExcludingEntity(p, bb);

            for (Entity o : list) {

                if (!o.isDead && o != p && !(o instanceof EntityItem)) {
                    if (o.isEntityAlive()) {
                        if (o.riddenByEntity == p) {
                            continue;
                        }

                        f1 = 0.3F;
                        final AxisAlignedBB boundingBox = o.boundingBox.expand(f1, f1, f1);
                        final MovingObjectPosition movingObjectPosition = boundingBox.calculateIntercept(vec3, vec31);

                        if (movingObjectPosition != null) {
                            final double nd = vec3.squareDistanceTo(movingObjectPosition.hitVec);

                            if (nd < closest) {
                                entity = o;
                                closest = nd;
                            }
                        }
                    }
                }
            }
        }

        MovingObjectPosition pos = null;
        Vec3 vec = null;

        if (hitBlocks) {
            vec = Vec3.createVectorHelper(d0, d1, d2);
            pos = w.rayTraceBlocks(vec3, vec31, true);
        }

        if (entity != null && pos != null && pos.hitVec.squareDistanceTo(vec) > closest) {
            pos = new MovingObjectPosition(entity);
        } else if (entity != null && pos == null) {
            pos = new MovingObjectPosition(entity);
        }

        return pos;
    }
}
