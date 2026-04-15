package com.science.gtnl.mixins.late.Gregtech;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IOutputME;
import com.science.gtnl.common.machine.hatch.OutputHatchMEProxy;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.AENetworkProxy;
import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.IDataCopyable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.common.tileentities.machines.MTEHatchOutputME;
import lombok.Getter;
import lombok.Setter;

@Mixin(value = MTEHatchOutputME.class, remap = false)
public abstract class MixinMTEHatchOutputME extends MTEHatchOutput implements IOutputME, IDataCopyable {

    @Getter
    @Shadow
    @Final
    IItemList<IAEFluidStack> fluidCache;

    @Getter
    @Setter
    @Shadow
    long lastOutputTick;

    @Getter
    @Setter
    @Shadow
    long lastInputTick;

    @Getter
    @Setter
    @Shadow
    long tickCounter;

    @Getter
    @Setter
    @Shadow
    boolean additionalConnection;

    @Getter
    @Setter
    @Shadow
    EntityPlayer lastClickedPlayer;

    @Getter
    @Setter
    @Shadow
    List<String> lockedFluids;

    @Getter
    @Setter
    @Shadow
    @Nullable
    private AENetworkProxy gridProxy;

    @Shadow
    protected abstract void updateValidGridProxySides();

    @Shadow
    protected abstract void checkFluidLock();

    @Shadow
    protected abstract void flushCachedStack();

    public MixinMTEHatchOutputME(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    @Inject(method = "getCopiedData", at = @At("RETURN"), cancellable = true)
    private void gtnl$injectCopiedData(EntityPlayer player, CallbackInfoReturnable<NBTTagCompound> cir) {
        if ((Object) this instanceof OutputHatchMEProxy) return;

        IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity == null) return;

        NBTTagCompound tag = cir.getReturnValue();
        if (tag == null) {
            tag = new NBTTagCompound();
        }

        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("masterX", tileEntity.getXCoord());
        masterNBT.setInteger("masterY", tileEntity.getYCoord());
        masterNBT.setInteger("masterZ", tileEntity.getZCoord());
        masterNBT.setInteger("masterDim", tileEntity.getWorld().provider.dimensionId);

        tag.setTag("master", masterNBT);

        cir.setReturnValue(tag);
    }

    @Override
    public void gtnl$updateValidGridProxySides() {
        updateValidGridProxySides();
    }

    @Override
    public void gtnl$checkFluidLock() {
        checkFluidLock();
    }

    @Override
    public void gtnl$flushCachedStack() {
        flushCachedStack();
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!(aPlayer instanceof EntityPlayerMP)) return;

        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) return;

        dataStick.stackTagCompound = getCopiedData(aPlayer);
        dataStick.setStackDisplayName("Output Hatch ME Link Configuration");
        aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.saved"));
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, ForgeDirection side,
        float aX, float aY, float aZ) {
        if (!(aPlayer instanceof EntityPlayerMP)) {
            this.lastClickedPlayer = aPlayer;
            openGui(aPlayer);
            return onRightclick(aBaseMetaTileEntity, aPlayer);
        }

        ItemStack dataStick = aPlayer.inventory.getCurrentItem();

        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) {
            this.lastClickedPlayer = aPlayer;
            openGui(aPlayer);
            return onRightclick(aBaseMetaTileEntity, aPlayer);
        }

        if (!pasteCopiedData(aPlayer, dataStick.stackTagCompound)) return false;

        aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.loaded"));
        return true;
    }

    @Override
    public String getCopiedDataIdentifier(EntityPlayer player) {
        return OutputHatchMEProxy.COPIED_DATA_IDENTIFIER;
    }
}
