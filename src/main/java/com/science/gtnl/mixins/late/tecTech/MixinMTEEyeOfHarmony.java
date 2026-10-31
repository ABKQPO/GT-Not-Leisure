package com.science.gtnl.mixins.late.tecTech;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IEyeOfHarmonyControllerLink;
import com.science.gtnl.common.machine.eyeofharmony.EyeOfHarmonyLink;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.multi.MTEEyeOfHarmony;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

@Mixin(value = MTEEyeOfHarmony.class, remap = false)
public abstract class MixinMTEEyeOfHarmony extends TTMultiblockBase implements IEyeOfHarmonyControllerLink {

    @Unique
    private final EyeOfHarmonyLink gtnl$link = new EyeOfHarmonyLink();

    @Final
    @Shadow
    private Map<Fluid, Long> validFluidMap;

    @Shadow
    protected abstract long getHeliumStored();

    @Shadow
    protected abstract long getHydrogenStored();

    @Shadow
    protected abstract long getStellarPlasmaStored();

    @Shadow
    private long astralArrayAmount;

    @Shadow
    private long parallelAmount;

    public MixinMTEEyeOfHarmony(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Unique
    private MTEEyeOfHarmony gtnl$self() {
        return (MTEEyeOfHarmony) (Object) this;
    }

    @Override
    public long gtnl$getHeliumStored() {
        return getHeliumStored();
    }

    @Override
    public long gtnl$getHydrogenStored() {
        return getHydrogenStored();
    }

    @Override
    public long gtnl$getStellarPlasmaStored() {
        return getStellarPlasmaStored();
    }

    @Override
    public long gtnl$getAstralArrayAmount() {
        return astralArrayAmount;
    }

    @Override
    public long gtnl$getParallelAmount() {
        return parallelAmount;
    }

    @Override
    public void gtnl$setHydrogenStored(long amount) {
        validFluidMap.put(Materials.Hydrogen.mGas, amount);
    }

    @Override
    public void gtnl$setHeliumStored(long amount) {
        validFluidMap.put(Materials.Helium.mGas, amount);
    }

    @Override
    public void gtnl$setStellarPlasmaStored(long amount) {
        validFluidMap.put(Materials.RawStarMatter.mFluid, amount);
    }

    @Override
    public void gtnl$unlinkController() {
        gtnl$link.unlink();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        gtnl$link.onPostTick(gtnl$self(), aTimer);
    }

    @Inject(method = "outputAfterRecipe_EM", at = @At("TAIL"))
    private void gtnl$notifyControllerOfRecipeCompletion(CallbackInfo ci) {
        gtnl$link.onRecipeDone();
    }

    @Inject(method = "loadNBTData", at = @At("HEAD"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        gtnl$link.load(aNBT);
    }

    @Inject(method = "saveNBTData", at = @At("HEAD"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        super.saveNBTData(aNBT);
        gtnl$link.save(aNBT);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        // Right-clicking could be a data stick linking action, so try this first.
        if (aBaseMetaTileEntity.isServerSide() && gtnl$link.tryLinkDataStick(gtnl$self(), aPlayer)) {
            return true;
        }
        return super.onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    @Override
    public void onBlockDestroyed() {
        gtnl$link.unregisterFrom(gtnl$self());
        super.onBlockDestroyed();
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        EyeOfHarmonyLink.addWailaBody(accessor.getNBTData(), currenttip);
        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        gtnl$link.writeWailaData(tag);
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }
}
