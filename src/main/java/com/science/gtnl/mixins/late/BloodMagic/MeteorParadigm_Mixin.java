package com.science.gtnl.mixins.late.BloodMagic;

import static WayofTime.alchemicalWizardry.common.summoning.meteor.Meteor.*;
import static com.science.gtnl.Utils.bloodMagic.MeteorParadigmHelper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import WayofTime.alchemicalWizardry.api.alchemy.energy.Reagent;
import WayofTime.alchemicalWizardry.common.summoning.meteor.Meteor;
import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorComponent;

@SuppressWarnings("UnusedMixin")
@Mixin(value = Meteor.class, remap = false)
public abstract class MeteorParadigm_Mixin {

    @Inject(
        method = "createMeteorImpact",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/Explosion;",
            shift = At.Shift.AFTER),
        cancellable = true)
    private void onCreateMeteorImpact(World world, int x, int y, int z, List<Reagent> reagents, CallbackInfo ci) {
        Meteor self = (Meteor) (Object) this;

        final int originalRadius = self.radius;
        final float originalFillerChance = self.fillerChance;
        final List<MeteorComponent> oreList = new ArrayList<>(self.ores);
        final List<MeteorComponent> originalFillerList = new ArrayList<>(self.filler);

        int newRadius = getNewRadius(originalRadius, reagents);

        float fillerChance = getNewFillerChance(originalFillerChance, reagents);
        List<MeteorComponent> currentFillerList = getNewFillerList(originalFillerList, reagents);

        final SphereData sphereData = precomputeSphereData(world, x, y, z, newRadius);
        final WeightData totalComponentWeight = precomputeWeights(oreList);
        final WeightData fillerWeights = precomputeWeights(currentFillerList);
        final float finalFillerChance = fillerChance;

        AtomicInteger counter = new AtomicInteger(0);
        List<Future<?>> futures = new ArrayList<>();

        for (int chunkStart = 0; chunkStart < sphereData.positions.size(); chunkStart += CHUNK_SIZE) {
            final int start = chunkStart;
            final int end = Math.min(start + CHUNK_SIZE, sphereData.positions.size());

            futures.add(
                METEOR_PLACEMENT_POOL.submit(
                    () -> processChunk(
                        world,
                        sphereData,
                        totalComponentWeight,
                        fillerWeights,
                        finalFillerChance,
                        start,
                        end,
                        counter)));
        }

        METEOR_PLACEMENT_POOL.submit(
            () -> world.markBlockRangeForRenderUpdate(
                x - newRadius,
                y - newRadius,
                z - newRadius,
                x + newRadius,
                y + newRadius,
                z + newRadius));

        METEOR_PLACEMENT_POOL.submit(() -> {
            try {
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                futures.clear();
            }
        });

        ci.cancel();
    }
}
