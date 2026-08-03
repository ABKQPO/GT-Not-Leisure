package com.science.gtnl.mixins.late.thaumcraft;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileAlchemyFurnace;

@Mixin(value = TileAlchemyFurnace.class, remap = false)
public abstract class MixinTileAlchemyFurnace extends TileThaumcraft {

    @Unique
    private static final int GTNL_MAX_ALEMBICS = 5;
    @Unique
    private static final int GTNL_TRANSFER_PER_TICK = 16;

    @Shadow
    public AspectList aspects;
    @Shadow
    public int vis;

    @Shadow
    public abstract boolean takeFromContainer(Aspect aspect, int amount);

    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void gtnl$transferToAlembicsEveryTick(CallbackInfo ci) {
        if (worldObj == null || worldObj.isRemote || aspects == null || aspects.size() == 0) return;

        for (int transferred = 0; transferred < GTNL_TRANSFER_PER_TICK; transferred++) {
            if (!gtnl$distributeOne()) break;
        }
    }

    @Redirect(
        method = "updateEntity",
        at = @At(
            value = "INVOKE",
            target = "Lthaumcraft/api/aspects/AspectList;size()I",
            ordinal = 0))
    private int gtnl$suppressVanillaAlembicDistribution(AspectList ignoredAspects) {
        return 0;
    }

    @Unique
    private boolean gtnl$distributeOne() {
        TileAlembic[] alembics = gtnl$getAlembics();

        // 优先处理贴有源质标签的蒸馏器
        for (TileAlembic alembic : alembics) {
            if (alembic == null) break;
            if (alembic.aspectFilter == null) continue;

            Aspect filteredAspect = gtnl$findExistingAspect(alembic);
            if (filteredAspect == null) {
                filteredAspect = gtnl$findNewAspect(alembic);
            }

            if (filteredAspect != null && gtnl$transferOne(alembic, filteredAspect)) {
                return true;
            }
        }

        // 优先向无标签蒸馏器添加新的源质种类
        for (TileAlembic alembic : alembics) {
            if (alembic == null) break;
            if (alembic.aspectFilter != null) continue;

            Aspect newAspect = gtnl$findNewAspect(alembic);
            if (newAspect != null && gtnl$transferOne(alembic, newAspect)) {
                return true;
            }
        }

        // 无法加入新品种时，补充已有源质
        for (TileAlembic alembic : alembics) {
            if (alembic == null) break;
            if (alembic.aspectFilter != null) continue;

            Aspect existingAspect = gtnl$findExistingAspect(alembic);
            if (existingAspect != null && gtnl$transferOne(alembic, existingAspect)) {
                return true;
            }
        }

        return false;
    }

    @Unique
    private TileAlembic[] gtnl$getAlembics() {
        TileAlembic[] alembics = new TileAlembic[GTNL_MAX_ALEMBICS];
        for (int depth = 1; depth <= GTNL_MAX_ALEMBICS; depth++) {
            TileEntity tile = worldObj.getTileEntity(xCoord, yCoord + depth, zCoord);
            if (!(tile instanceof TileAlembic alembic)) break;
            alembics[depth - 1] = alembic;
        }
        return alembics;
    }

    @Unique
    private Aspect gtnl$findExistingAspect(TileAlembic alembic) {
        AspectList stored = alembic.getAspects();
        if (alembic.aspectFilter != null) {
            return stored.getAmount(alembic.aspectFilter) > 0 && aspects.getAmount(alembic.aspectFilter) > 0
                && alembic.doesContainerAccept(alembic.aspectFilter)
                ? alembic.aspectFilter
                : null;
        }

        for (Aspect storedAspect : gtnl$getSortedAspects(stored)) {
            if (aspects.getAmount(storedAspect) > 0 && alembic.doesContainerAccept(storedAspect)) {
                return storedAspect;
            }
        }
        return null;
    }

    @Unique
    private Aspect gtnl$findNewAspect(TileAlembic alembic) {
        if (alembic.aspectFilter != null) {
            Aspect filter = alembic.aspectFilter;
            return alembic.getAspects().getAmount(filter) <= 0 && aspects.getAmount(filter) > 0
                && alembic.doesContainerAccept(filter)
                ? filter
                : null;
        }

        AspectList stored = alembic.getAspects();
        for (Aspect availableAspect : gtnl$getSortedAspects(aspects)) {
            if (stored.getAmount(availableAspect) <= 0 && alembic.doesContainerAccept(availableAspect)) {
                return availableAspect;
            }
        }
        return null;
    }

    @Unique
    private boolean gtnl$transferOne(TileAlembic alembic, Aspect transferredAspect) {
        if (transferredAspect == null || !takeFromContainer(transferredAspect, 1)) return false;

        int remainder = alembic.addToContainer(transferredAspect, 1);
        if (remainder > 0) {
            aspects.add(transferredAspect, remainder);
            vis += remainder;
            return false;
        }

        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        worldObj.markBlockForUpdate(alembic.xCoord, alembic.yCoord, alembic.zCoord);
        return true;
    }

    @Unique
    private static Aspect[] gtnl$getSortedAspects(AspectList aspectList) {
        if (aspectList == null) return new Aspect[0];
        Aspect[] storedAspects = aspectList.getAspects();
        return Arrays.stream(storedAspects)
            .filter(storedAspect -> storedAspect != null && aspectList.getAmount(storedAspect) > 0)
            .sorted(Comparator.comparing(Aspect::getTag))
            .toArray(Aspect[]::new);
    }
}
