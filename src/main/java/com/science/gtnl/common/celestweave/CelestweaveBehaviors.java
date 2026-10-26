package com.science.gtnl.common.celestweave;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.world.WorldServer;

import com.google.common.collect.MapMaker;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveFireballPacket;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.config.ItemConfig;
import com.science.gtnl.config.MainConfig;

import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.ManaItemHandler;

public class CelestweaveBehaviors {

    public static final float STEP_HEIGHT = 1.0031F;
    private static final UUID SPEED_UUID = UUID.fromString("5d1b6c1e-2f0a-4c3e-9a61-7c2e4b8f1a30");
    private static final UUID KNOCKBACK_UUID = UUID.fromString("a83f0d52-6e14-4b7c-8d29-3f5e1c9b7a42");
    private static final int ADD_OPERATION = 0, MULTIPLY_OPERATION = 2;
    private static final ICelestweaveBehavior NONE = new ICelestweaveBehavior() {};
    private static final Map<CelestweaveModule, ICelestweaveBehavior> BEHAVIORS = new EnumMap<>(
        CelestweaveModule.class);
    private static final Map<EntityPlayer, Float> STEP_ORIGINAL = new MapMaker().weakKeys()
        .makeMap();
    private static final Map<Entity, Boolean> REFLECTED = Collections.synchronizedMap(new WeakHashMap<>());

    static {
        for (CelestweaveModule module : CelestweaveModule.values()) {
            ICelestweaveBehavior behavior = module.newBehavior();
            if (behavior != null) BEHAVIORS.put(module, behavior);
        }
    }

    private CelestweaveBehaviors() {}

    public static ICelestweaveBehavior of(CelestweaveModule module) {
        return BEHAVIORS.getOrDefault(module, NONE);
    }

    private static ItemConfig.Celestweave config() {
        return MainConfig.item.celestweave;
    }

    private static void setModifier(EntityLivingBase entity, IAttributeInstance attribute, UUID id, String name,
        double amount, int operation) {
        if (attribute == null) return;
        AttributeModifier current = attribute.getModifier(id);
        if (current != null && current.getAmount() == amount) return;
        if (current != null) attribute.removeModifier(current);
        if (amount != 0) attribute.applyModifier(new AttributeModifier(id, name, amount, operation));
    }

    public static class NightVision implements ICelestweaveBehavior {

        private static final int REFRESH_BELOW_TICKS = 220, DURATION_TICKS = 300;

        @Override
        public void tick(CelestweaveContext ctx) {
            if (!ctx.server()) return;
            EntityPlayer player = ctx.player();
            if (!ctx.drain(config().nightVisionEuPerTick)) {
                clear(player);
                return;
            }
            PotionEffect effect = player.getActivePotionEffect(Potion.nightVision);
            if (effect == null || effect.getIsAmbient() && effect.getDuration() < REFRESH_BELOW_TICKS)
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, DURATION_TICKS, 0, true));
            if (player.isPotionActive(Potion.blindness)) player.removePotionEffect(Potion.blindness.id);
        }

        @Override
        public void unequip(CelestweaveContext ctx) {
            if (ctx.server()) clear(ctx.player());
        }

        private static void clear(EntityPlayer player) {
            PotionEffect effect = player.getActivePotionEffect(Potion.nightVision);
            if (effect != null && effect.getIsAmbient() && effect.getDuration() <= DURATION_TICKS)
                player.removePotionEffect(Potion.nightVision.id);
        }
    }

    public static class Saturation implements ICelestweaveBehavior {

        private static final int INTERVAL_TICKS = 20, MAX_FOOD = 20;
        private static final float LOW_SATURATION = 5F, SATURATION_MODIFIER = 1F;

        @Override
        public void tick(CelestweaveContext ctx) {
            EntityPlayer player = ctx.player();
            if (!ctx.server() || player.worldObj.getTotalWorldTime() % INTERVAL_TICKS != 0) return;
            FoodStats food = player.getFoodStats();
            int missing = MAX_FOOD - food.getFoodLevel();
            if ((missing > 0 || food.getSaturationLevel() < LOW_SATURATION)
                && ctx.drain(config().saturationEu * Math.max(1, missing)))
                food.addStats(MAX_FOOD, SATURATION_MODIFIER);
        }
    }

    public static class Purification implements ICelestweaveBehavior {

        private static final int INTERVAL_TICKS = 10;

        @Override
        public void tick(CelestweaveContext ctx) {
            EntityPlayer player = ctx.player();
            if (!ctx.server() || player.worldObj.getTotalWorldTime() % INTERVAL_TICKS != 0) return;
            for (Object entry : player.getActivePotionEffects()
                .toArray()) {
                if (!(entry instanceof PotionEffect effect)) continue;
                int id = effect.getPotionID();
                Potion potion = id >= 0 && id < Potion.potionTypes.length ? Potion.potionTypes[id] : null;
                if (potion != null && potion.isBadEffect && ctx.drain(config().purificationEu))
                    player.removePotionEffect(id);
            }
        }
    }

    public static class Terrasteel implements ICelestweaveBehavior {

        @Override
        public void tick(CelestweaveContext ctx) {
            if (ctx.server() && ctx.affords(config().manaEu)
                && ManaItemHandler.dispatchManaExact(ctx.stack(), ctx.player(), 1, true)) ctx.drain(config().manaEu);
        }
    }

    public static class Shield implements ICelestweaveBehavior {

        @Override
        public void tick(CelestweaveContext ctx) {
            if (!ctx.server()) return;
            EntityPlayer player = ctx.player();
            boolean canAbsorb = CelestweaveShield.canAbsorb(player);
            if (canAbsorb && player.isBurning()) player.extinguish();
            setModifier(
                player,
                player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance),
                KNOCKBACK_UUID,
                "Celestweave shield",
                canAbsorb ? 1 : 0,
                ADD_OPERATION);
        }

        @Override
        public void unequip(CelestweaveContext ctx) {
            if (ctx.server()) setModifier(
                ctx.player(),
                ctx.player()
                    .getEntityAttribute(SharedMonsterAttributes.knockbackResistance),
                KNOCKBACK_UUID,
                "Celestweave shield",
                0,
                ADD_OPERATION);
        }
    }

    public static class Reflect implements ICelestweaveBehavior {

        private static final double RADIUS = 4, MIN_SPEED = 0.05, INCOMING_COS = -0.7071, EPSILON = 1.0E-3;
        private static final double FIREBALL_ACCELERATION = 0.1;

        @Override
        public void tick(CelestweaveContext ctx) {
            if (!ctx.server() || !CelestweaveModuleSetting.REFLECT_PROJECTILE.on(ctx.stack())
                || !ctx.affords(config().reflectEu)) return;
            EntityPlayer player = ctx.player();
            Vec3d center = Vec3d.center(player);
            List<?> nearby = nearbyProjectiles(player);
            for (Object entry : nearby) {
                Entity entity = (Entity) entry;
                if (REFLECTED.containsKey(entity)) continue;
                Entity shooter = shooter(entity);
                if (shooter == player) continue;
                double vx = entity.motionX, vy = entity.motionY, vz = entity.motionZ;
                double speed = Math.sqrt(vx * vx + vy * vy + vz * vz);
                double dx = entity.posX - center.x(), dy = entity.posY - center.y(), dz = entity.posZ - center.z();
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (speed < MIN_SPEED || distance < EPSILON
                    || (dx * vx + dy * vy + dz * vz) / (distance * speed) > INCOMING_COS) continue;
                if (!ctx.drain(config().reflectEu)) return;
                double tx = -vx, ty = -vy, tz = -vz;
                if (shooter != null && shooter.worldObj == entity.worldObj) {
                    tx = shooter.posX - entity.posX;
                    ty = shooter.posY + shooter.height * 0.5 - entity.posY;
                    tz = shooter.posZ - entity.posZ;
                }
                double length = Math.sqrt(tx * tx + ty * ty + tz * tz);
                if (length < EPSILON) continue;
                entity.motionX = tx / length * speed;
                entity.motionY = ty / length * speed;
                entity.motionZ = tz / length * speed;
                if (entity instanceof EntityFireball fireball) {
                    fireball.accelerationX = tx / length * FIREBALL_ACCELERATION;
                    fireball.accelerationY = ty / length * FIREBALL_ACCELERATION;
                    fireball.accelerationZ = tz / length * FIREBALL_ACCELERATION;
                    fireball.shootingEntity = player;
                    // Vanilla velocity packets omit acceleration, so clients otherwise keep accelerating inward.
                    ((WorldServer) player.worldObj).getEntityTracker()
                        .func_151247_a(
                            fireball,
                            ScienceNotLeisure.network.getPacketFrom(new CelestweaveFireballPacket(fireball)));
                } else if (entity instanceof EntityArrow arrow) arrow.shootingEntity = player;
                entity.velocityChanged = true;
                REFLECTED.put(entity, Boolean.TRUE);
                CelestweaveServerEvents
                    .sendFx(player, CelestweaveFxPacket.Type.REFLECT, entity.posX, entity.posY, entity.posZ);
            }
        }

        private static List<?> nearbyProjectiles(EntityPlayer player) {
            // Vanilla fireballs do not implement IProjectile.
            return player.worldObj.selectEntitiesWithinAABB(
                Entity.class,
                player.boundingBox.expand(RADIUS, RADIUS, RADIUS),
                entity -> (entity instanceof IProjectile || entity instanceof EntityFireball)
                    && !(entity instanceof IManaBurst));
        }

        private static Entity shooter(Entity entity) {
            if (entity instanceof EntityArrow arrow) return arrow.shootingEntity;
            if (entity instanceof EntityFireball fireball) return fireball.shootingEntity;
            if (entity instanceof EntityThrowable throwable) return throwable.getThrower();
            return null;
        }
    }

    public static class WalkAssist implements ICelestweaveBehavior {

        private static final double SPEED_PER_LEVEL = 0.25;

        @Override
        public void tick(CelestweaveContext ctx) {
            if (!ctx.server()) return;
            EntityPlayer player = ctx.player();
            boolean on = ctx.affords(config().walkEuPerTick);
            if (on && player.onGround && ctx.moving()) on = ctx.drain(config().walkEuPerTick);
            int speed = CelestweaveModuleSetting.WALK_SPEED.get(ctx.stack());
            setModifier(
                player,
                player.getEntityAttribute(SharedMonsterAttributes.movementSpeed),
                SPEED_UUID,
                "Celestweave walk assist",
                on && speed > 1 ? (speed - 1) * SPEED_PER_LEVEL : 0,
                MULTIPLY_OPERATION);
        }

        @Override
        public void unequip(CelestweaveContext ctx) {
            if (!ctx.server()) return;
            EntityPlayer player = ctx.player();
            setModifier(
                player,
                player.getEntityAttribute(SharedMonsterAttributes.movementSpeed),
                SPEED_UUID,
                "Celestweave walk assist",
                0,
                MULTIPLY_OPERATION);
        }
    }

    public static void updateStep(EntityPlayer player) {
        ItemStack boots = CelestweaveData.piece(player, CelestweaveArmor.BOOTS);
        boolean on = boots != null && !player.isDead
            && !player.isSneaking()
            && CelestweaveData.active(boots, CelestweaveModule.WALK_ASSIST)
            && CelestweaveModuleSetting.WALK_STEP.on(boots)
            && CelestweaveEnergy.affords(player, CelestweaveArmor.BOOTS, boots, config().walkEuPerTick);
        if (on) {
            if (player.stepHeight < STEP_HEIGHT) {
                STEP_ORIGINAL.putIfAbsent(player, player.stepHeight);
                player.stepHeight = STEP_HEIGHT;
            }
            return;
        }
        Float original = STEP_ORIGINAL.remove(player);
        if (original != null) player.stepHeight = original;
    }

    public static void forget(EntityPlayer player) {
        STEP_ORIGINAL.remove(player);
    }
}
