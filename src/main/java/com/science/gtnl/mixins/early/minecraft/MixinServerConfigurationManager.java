package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IAnchorRespawn;
import com.science.gtnl.common.block.blocks.BlockDimensionRespawnAnchor;
import com.science.gtnl.common.block.blocks.tile.TileEntityDimensionRespawnAnchor;
import com.science.gtnl.config.EffectConfig;
import com.science.gtnl.loader.EffectLoader;

@Mixin(value = ServerConfigurationManager.class, remap = true)
public abstract class MixinServerConfigurationManager {

    @Shadow
    @Final
    private MinecraftServer mcServer;

    @Inject(method = "respawnPlayer", at = @At("RETURN"), cancellable = true)
    private void onRespawnPlayer(EntityPlayerMP player, int dimension, boolean conqueredEnd,
        CallbackInfoReturnable<EntityPlayerMP> cir) {
        if (!EffectConfig.enableGhostlyShape) return;
        EntityPlayerMP entityplayermp1 = cir.getReturnValue();

        if (entityplayermp1 != null) {
            entityplayermp1.addPotionEffect(new PotionEffect(EffectLoader.ghostly_shape.id, 6000, 0));
        }

        cir.setReturnValue(entityplayermp1);
    }

    @ModifyVariable(method = "respawnPlayer", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int modifyRespawnDimension(int originalDimension, EntityPlayerMP player) {
        return TileEntityDimensionRespawnAnchor.respawnDimension(mcServer, originalDimension, player);
    }

    @Redirect(
        method = "respawnPlayer",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canRespawnHere()Z"))
    private boolean redirectCanRespawnHere(WorldProvider instance, EntityPlayerMP player) {
        IAnchorRespawn anchor = (IAnchorRespawn) player;

        if (anchor.getAnchorPos() != null) {
            return true;
        }

        return instance.canRespawnHere();
    }

    @Redirect(
        method = "respawnPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayerMP;getBedLocation(I)Lnet/minecraft/util/ChunkCoordinates;",
            remap = false))
    private ChunkCoordinates redirectBedLocation(EntityPlayerMP player, int dimension) {
        IAnchorRespawn anchor = (IAnchorRespawn) player;

        if (anchor.getAnchorPos() != null) {
            return anchor.getAnchorPos();
        }

        return player.getBedLocation(dimension);
    }

    @Redirect(
        method = "respawnPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayerMP;isSpawnForced(I)Z",
            remap = false))
    private boolean redirectSpawnForced(EntityPlayerMP player, int dimension) {

        IAnchorRespawn anchor = (IAnchorRespawn) player;

        if (anchor.getAnchorPos() != null) {
            return true;
        }

        return player.isSpawnForced(dimension);
    }

    @Redirect(
        method = "respawnPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;verifyRespawnCoordinates(Lnet/minecraft/world/World;Lnet/minecraft/util/ChunkCoordinates;Z)Lnet/minecraft/util/ChunkCoordinates;"))
    private ChunkCoordinates redirectAnchorVerifyRespawn(World world, ChunkCoordinates coordinates, boolean material1,
        EntityPlayerMP player) {
        IAnchorRespawn anchor = (IAnchorRespawn) player;

        if (anchor.getAnchorPos() != null) {
            return BlockDimensionRespawnAnchor.verifyAnchorRespawnCoordinates(world, coordinates, material1);
        }

        return EntityPlayer.verifyRespawnCoordinates(world, coordinates, material1);
    }

}
