package com.science.gtnl.mixins.late.thaumcraft;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileAlembic;

@Mixin(value = TileAlembic.class, remap = false)
public abstract class MixinTileAlembic extends TileThaumcraft {

    @Unique
    private static final String GTNL_STORED_ASPECTS_KEY = "GTNLStoredAspects";
    @Unique
    private static final String GTNL_ACTIVE_ASPECT_KEY = "GTNLActiveAspect";
    @Unique
    private static final int GTNL_TOTAL_CAPACITY = 256;
    @Unique
    private static final int GTNL_MAX_ASPECT_TYPES = 8;
    @Unique
    private static final int GTNL_OUTPUT_PER_TICK = 16;

    @Shadow
    public Aspect aspect;
    @Shadow
    public Aspect aspectFilter;
    @Shadow
    public int amount;
    @Shadow
    public int maxAmount;
    @Shadow
    public int facing;

    @Unique
    private AspectList gtnl$storedAspects;
    @Unique
    private Aspect gtnl$activeAspect;
    @Unique
    private Aspect gtnl$lastLegacyAspect;
    @Unique
    private int gtnl$lastLegacyAmount;
    @Unique
    private long gtnl$lastOutputTick = Long.MIN_VALUE;
    @Unique
    private int gtnl$outputThisTick;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void gtnl$initialize(CallbackInfo ci) {
        gtnl$storedAspects = new AspectList();
        maxAmount = GTNL_TOTAL_CAPACITY;
        gtnl$syncLegacyState();
    }

    @Inject(method = "readCustomNBT", at = @At("TAIL"))
    private void gtnl$readStoredAspects(NBTTagCompound tag, CallbackInfo ci) {
        Aspect legacyAspect = aspect;
        int legacyAmount = amount;

        gtnl$storedAspects = new AspectList();
        if (tag.hasKey(GTNL_STORED_ASPECTS_KEY)) {
            gtnl$storedAspects.readFromNBT(tag.getCompoundTag(GTNL_STORED_ASPECTS_KEY));
        } else if (legacyAspect != null && legacyAmount > 0) {
            gtnl$storedAspects.add(legacyAspect, Math.min(legacyAmount, GTNL_TOTAL_CAPACITY));
        }

        gtnl$activeAspect = Aspect.getAspect(tag.getString(GTNL_ACTIVE_ASPECT_KEY));
        gtnl$removeInvalidAspects();
        gtnl$trimToLimits();
        gtnl$ensureActiveAspect();
        gtnl$syncLegacyState();
    }

    @Inject(method = "writeCustomNBT", at = @At("HEAD"))
    private void gtnl$prepareLegacyNBT(NBTTagCompound tag, CallbackInfo ci) {
        gtnl$reconcileLegacyState();
        gtnl$syncLegacyState();
    }

    @Inject(method = "writeCustomNBT", at = @At("TAIL"))
    private void gtnl$writeStoredAspects(NBTTagCompound tag, CallbackInfo ci) {
        NBTTagCompound storedTag = new NBTTagCompound();
        gtnl$getStoredAspects().writeToNBT(storedTag);
        tag.setTag(GTNL_STORED_ASPECTS_KEY, storedTag);
        tag.setString(GTNL_ACTIVE_ASPECT_KEY, gtnl$activeAspect == null ? "" : gtnl$activeAspect.getTag());
    }

    @Inject(method = "getAspects", at = @At("HEAD"), cancellable = true)
    private void gtnl$getAspects(CallbackInfoReturnable<AspectList> cir) {
        gtnl$reconcileLegacyState();
        AspectList copy = new AspectList();
        for (Aspect storedAspect : gtnl$getStoredAspectsSorted()) {
            copy.add(storedAspect, gtnl$getStoredAspects().getAmount(storedAspect));
        }
        cir.setReturnValue(copy);
    }

    @Inject(method = "setAspects", at = @At("HEAD"), cancellable = true)
    private void gtnl$setAspects(AspectList newAspects, CallbackInfo ci) {
        AspectList stored = gtnl$getStoredAspects();
        stored.aspects.clear();
        gtnl$activeAspect = null;

        if (newAspects != null) {
            int remaining = GTNL_TOTAL_CAPACITY;
            int types = 0;
            for (Aspect storedAspect : gtnl$getSortedAspects(newAspects)) {
                int accepted = Math.min(newAspects.getAmount(storedAspect), remaining);
                if (accepted <= 0) continue;

                stored.add(storedAspect, accepted);
                remaining -= accepted;
                if (++types >= GTNL_MAX_ASPECT_TYPES || remaining == 0) break;
            }
        }

        gtnl$markEssentiaChanged();
        ci.cancel();
    }

    @Inject(method = "addToContainer", at = @At("HEAD"), cancellable = true)
    private void gtnl$addToContainer(Aspect addedAspect, int addedAmount, CallbackInfoReturnable<Integer> cir) {
        gtnl$reconcileLegacyState();
        if (addedAspect == null || addedAmount <= 0) {
            cir.setReturnValue(addedAmount);
            return;
        }

        AspectList stored = gtnl$getStoredAspects();
        boolean alreadyStored = stored.getAmount(addedAspect) > 0;
        if ((!alreadyStored && gtnl$getStoredTypeCount() >= GTNL_MAX_ASPECT_TYPES)
            || gtnl$getTotalAmount() >= GTNL_TOTAL_CAPACITY) {
            cir.setReturnValue(addedAmount);
            return;
        }

        int accepted = Math.min(addedAmount, GTNL_TOTAL_CAPACITY - gtnl$getTotalAmount());
        stored.add(addedAspect, accepted);
        if (gtnl$activeAspect == null) gtnl$activeAspect = addedAspect;
        gtnl$markEssentiaChanged();
        cir.setReturnValue(addedAmount - accepted);
    }

    @Inject(method = "takeFromContainer(Lthaumcraft/api/aspects/Aspect;I)Z", at = @At("HEAD"), cancellable = true)
    private void gtnl$takeFromContainer(Aspect takenAspect, int takenAmount, CallbackInfoReturnable<Boolean> cir) {
        gtnl$reconcileLegacyState();
        if (takenAspect == null || takenAmount <= 0 || gtnl$getStoredAspects().getAmount(takenAspect) < takenAmount) {
            cir.setReturnValue(false);
            return;
        }

        gtnl$getStoredAspects().remove(takenAspect, takenAmount);
        if (takenAspect == gtnl$activeAspect && gtnl$getStoredAspects().getAmount(takenAspect) <= 0) {
            gtnl$activeAspect = null;
        }
        gtnl$markEssentiaChanged();
        cir.setReturnValue(true);
    }

    @Inject(method = "takeFromContainer(Lthaumcraft/api/aspects/AspectList;)Z", at = @At("HEAD"), cancellable = true)
    private void gtnl$takeAspectList(AspectList requestedAspects, CallbackInfoReturnable<Boolean> cir) {
        gtnl$reconcileLegacyState();
        if (!gtnl$containsAll(requestedAspects)) {
            cir.setReturnValue(false);
            return;
        }

        for (Aspect requestedAspect : gtnl$getSortedAspects(requestedAspects)) {
            gtnl$getStoredAspects().remove(requestedAspect, requestedAspects.getAmount(requestedAspect));
        }
        gtnl$markEssentiaChanged();
        cir.setReturnValue(true);
    }

    @Inject(method = "doesContainerContain", at = @At("HEAD"), cancellable = true)
    private void gtnl$doesContainerContain(AspectList requestedAspects, CallbackInfoReturnable<Boolean> cir) {
        gtnl$reconcileLegacyState();
        cir.setReturnValue(gtnl$containsAll(requestedAspects));
    }

    @Inject(method = "doesContainerContainAmount", at = @At("HEAD"), cancellable = true)
    private void gtnl$doesContainerContainAmount(Aspect requestedAspect, int requestedAmount,
        CallbackInfoReturnable<Boolean> cir) {
        gtnl$reconcileLegacyState();
        cir.setReturnValue(
            requestedAspect != null && requestedAmount >= 0
                && gtnl$getStoredAspects().getAmount(requestedAspect) >= requestedAmount);
    }

    @Inject(method = "containerContains", at = @At("HEAD"), cancellable = true)
    private void gtnl$containerContains(Aspect requestedAspect, CallbackInfoReturnable<Integer> cir) {
        gtnl$reconcileLegacyState();
        cir.setReturnValue(requestedAspect == null ? 0 : gtnl$getStoredAspects().getAmount(requestedAspect));
    }

    @Inject(method = "doesContainerAccept", at = @At("HEAD"), cancellable = true)
    private void gtnl$doesContainerAccept(Aspect acceptedAspect, CallbackInfoReturnable<Boolean> cir) {
        gtnl$reconcileLegacyState();
        boolean alreadyStored = acceptedAspect != null && gtnl$getStoredAspects().getAmount(acceptedAspect) > 0;
        cir.setReturnValue(
            acceptedAspect != null && gtnl$getTotalAmount() < GTNL_TOTAL_CAPACITY
                && (alreadyStored || gtnl$getStoredTypeCount() < GTNL_MAX_ASPECT_TYPES));
    }

    @Inject(method = "getEssentiaType", at = @At("HEAD"), cancellable = true)
    private void gtnl$getEssentiaType(ForgeDirection face, CallbackInfoReturnable<Aspect> cir) {
        gtnl$reconcileLegacyState();
        cir.setReturnValue(gtnl$getOfferedAspect(face));
    }

    @Inject(method = "getEssentiaAmount", at = @At("HEAD"), cancellable = true)
    private void gtnl$getEssentiaAmount(ForgeDirection face, CallbackInfoReturnable<Integer> cir) {
        gtnl$reconcileLegacyState();
        Aspect offeredAspect = gtnl$getOfferedAspect(face);
        cir.setReturnValue(offeredAspect == null ? 0 : gtnl$getStoredAspects().getAmount(offeredAspect));
    }

    @Inject(method = "takeEssentia", at = @At("HEAD"), cancellable = true)
    private void gtnl$takeEssentia(Aspect requestedAspect, int requestedAmount, ForgeDirection face,
        CallbackInfoReturnable<Integer> cir) {
        gtnl$reconcileLegacyState();
        if (!gtnl$canOutputTo(face) || requestedAspect == null || requestedAmount <= 0) {
            cir.setReturnValue(0);
            return;
        }

        gtnl$resetOutputBudgetIfNeeded();
        int remainingBudget = GTNL_OUTPUT_PER_TICK - gtnl$outputThisTick;
        int taken = Math
            .min(Math.min(requestedAmount, remainingBudget), gtnl$getStoredAspects().getAmount(requestedAspect));
        if (taken <= 0) {
            cir.setReturnValue(0);
            return;
        }

        gtnl$getStoredAspects().remove(requestedAspect, taken);
        gtnl$outputThisTick += taken;
        if (requestedAspect == gtnl$activeAspect && gtnl$getStoredAspects().getAmount(requestedAspect) <= 0) {
            gtnl$activeAspect = null;
        }
        gtnl$markEssentiaChanged();
        cir.setReturnValue(taken);
    }

    @Unique
    private AspectList gtnl$getStoredAspects() {
        if (gtnl$storedAspects == null) gtnl$storedAspects = new AspectList();
        return gtnl$storedAspects;
    }

    @Unique
    private boolean gtnl$containsAll(AspectList requestedAspects) {
        if (requestedAspects == null) return false;
        for (Aspect requestedAspect : gtnl$getSortedAspects(requestedAspects)) {
            if (gtnl$getStoredAspects().getAmount(requestedAspect) < requestedAspects.getAmount(requestedAspect)) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private Aspect gtnl$getOfferedAspect(ForgeDirection face) {
        if (!gtnl$canOutputTo(face)) return null;

        if (face != ForgeDirection.UNKNOWN && worldObj != null) {
            TileEntity tile = ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, face);
            if (tile instanceof IEssentiaTransport transport) {
                Aspect requestedAspect = transport.getSuctionType(face.getOpposite());
                if (requestedAspect != null) {
                    return gtnl$getStoredAspects().getAmount(requestedAspect) > 0 ? requestedAspect : null;
                }
            }
        }

        gtnl$ensureActiveAspect();
        return gtnl$activeAspect;
    }

    @Unique
    private boolean gtnl$canOutputTo(ForgeDirection face) {
        return face != null && face != ForgeDirection.DOWN && face != ForgeDirection.getOrientation(facing);
    }

    @Unique
    private void gtnl$resetOutputBudgetIfNeeded() {
        long currentTick = worldObj == null ? Long.MIN_VALUE : worldObj.getTotalWorldTime();
        if (currentTick != gtnl$lastOutputTick) {
            gtnl$lastOutputTick = currentTick;
            gtnl$outputThisTick = 0;
        }
    }

    @Unique
    private void gtnl$ensureActiveAspect() {
        AspectList stored = gtnl$getStoredAspects();
        if (aspectFilter != null && stored.getAmount(aspectFilter) > 0) {
            gtnl$activeAspect = aspectFilter;
        } else if (gtnl$activeAspect == null || stored.getAmount(gtnl$activeAspect) <= 0) {
            Aspect[] sorted = gtnl$getStoredAspectsSorted();
            gtnl$activeAspect = sorted.length == 0 ? null : sorted[0];
        }
    }

    @Unique
    private int gtnl$getTotalAmount() {
        int total = 0;
        for (Aspect storedAspect : gtnl$getStoredAspectsSorted()) {
            total += gtnl$getStoredAspects().getAmount(storedAspect);
        }
        return total;
    }

    @Unique
    private int gtnl$getStoredTypeCount() {
        return gtnl$getStoredAspectsSorted().length;
    }

    @Unique
    private void gtnl$removeInvalidAspects() {
        gtnl$getStoredAspects().aspects.entrySet()
            .removeIf(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0);
    }

    @Unique
    private void gtnl$trimToLimits() {
        AspectList trimmed = new AspectList();
        int remaining = GTNL_TOTAL_CAPACITY;
        int types = 0;
        for (Aspect storedAspect : gtnl$getStoredAspectsSorted()) {
            int accepted = Math.min(gtnl$getStoredAspects().getAmount(storedAspect), remaining);
            if (accepted > 0) {
                trimmed.add(storedAspect, accepted);
                remaining -= accepted;
                types++;
            }
            if (types >= GTNL_MAX_ASPECT_TYPES || remaining == 0) break;
        }

        gtnl$getStoredAspects().aspects.clear();
        for (Aspect storedAspect : gtnl$getSortedAspects(trimmed)) {
            gtnl$getStoredAspects().add(storedAspect, trimmed.getAmount(storedAspect));
        }
    }

    @Unique
    private void gtnl$reconcileLegacyState() {
        AspectList stored = gtnl$getStoredAspects();
        if (aspect == gtnl$lastLegacyAspect && amount == gtnl$lastLegacyAmount) return;

        if (gtnl$lastLegacyAspect != null) {
            int previousAmount = stored.getAmount(gtnl$lastLegacyAspect);
            int newAmount = aspect == gtnl$lastLegacyAspect ? Math.max(0, amount) : 0;
            if (newAmount < previousAmount) {
                stored.remove(gtnl$lastLegacyAspect, previousAmount - newAmount);
            } else if (newAmount > previousAmount) {
                stored.add(gtnl$lastLegacyAspect, newAmount - previousAmount);
            }
        }

        if (aspect != null && aspect != gtnl$lastLegacyAspect && amount > 0) {
            int existing = stored.getAmount(aspect);
            if (amount > existing) stored.add(aspect, amount - existing);
            gtnl$activeAspect = aspect;
        }

        gtnl$removeInvalidAspects();
        gtnl$trimToLimits();
        gtnl$ensureActiveAspect();
        gtnl$syncLegacyState();
    }

    @Unique
    private void gtnl$syncLegacyState() {
        gtnl$ensureActiveAspect();
        maxAmount = GTNL_TOTAL_CAPACITY;
        aspect = gtnl$activeAspect;
        amount = aspect == null ? 0 : gtnl$getStoredAspects().getAmount(aspect);
        gtnl$lastLegacyAspect = aspect;
        gtnl$lastLegacyAmount = amount;
    }

    @Unique
    private void gtnl$markEssentiaChanged() {
        gtnl$removeInvalidAspects();
        gtnl$ensureActiveAspect();
        gtnl$syncLegacyState();
        markDirty();
        if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Unique
    private Aspect[] gtnl$getStoredAspectsSorted() {
        return gtnl$getSortedAspects(gtnl$getStoredAspects());
    }

    @Unique
    private static Aspect[] gtnl$getSortedAspects(AspectList aspectList) {
        if (aspectList == null) return new Aspect[0];
        Aspect[] aspects = aspectList.getAspects();
        return Arrays.stream(aspects)
            .filter(storedAspect -> storedAspect != null && aspectList.getAmount(storedAspect) > 0)
            .sorted(Comparator.comparing(Aspect::getTag))
            .toArray(Aspect[]::new);
    }
}
