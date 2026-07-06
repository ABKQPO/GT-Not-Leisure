package com.science.gtnl.mixins.late.tecTech;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.science.gtnl.api.mixinHelper.IEyeOfHarmonyControllerLink;
import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.multi.MTEEyeOfHarmony;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

@Mixin(value = MTEEyeOfHarmony.class, remap = false)
public abstract class MixinMTEEyeOfHarmony extends TTMultiblockBase implements IEyeOfHarmonyControllerLink {

    @Unique
    @Getter
    @Setter
    private int gtnl$controllerX, gtnl$controllerY, gtnl$controllerZ;

    @Unique
    @Getter
    @Setter
    private boolean gtnl$controllerSet = false;

    @Unique
    @Setter
    private EyeOfHarmonyInjector gtnl$controller = null;

    @Final
    @Shadow
    @Getter
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        // Try to re-link to controller periodically, for example on game load.
        if (aTimer % 100 == 5 && gtnl$controllerSet && getGtnl$controller() == null) {
            gtnl$trySetControllerFromCoord(gtnl$controllerX, gtnl$controllerY, gtnl$controllerZ);
        }
    }

    @Inject(method = "loadNBTData", at = @At("HEAD"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        if (aNBT.hasKey("controller")) {
            NBTTagCompound controllerNBT = aNBT.getCompoundTag("controller");
            gtnl$controllerX = controllerNBT.getInteger("x");
            gtnl$controllerY = controllerNBT.getInteger("y");
            gtnl$controllerZ = controllerNBT.getInteger("z");
            gtnl$controllerSet = true;
        }
    }

    @Unique
    public NBTTagCompound gtnl$saveLinkDataToNBT() {
        NBTTagCompound controllerNBT = new NBTTagCompound();
        controllerNBT.setInteger("x", gtnl$controllerX);
        controllerNBT.setInteger("y", gtnl$controllerY);
        controllerNBT.setInteger("z", gtnl$controllerZ);
        return controllerNBT;
    }

    @Inject(method = "saveNBTData", at = @At("HEAD"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        super.saveNBTData(aNBT);
        if (gtnl$controllerSet) {
            NBTTagCompound controllerNBT = gtnl$saveLinkDataToNBT();
            aNBT.setTag("controller", controllerNBT);
        }
    }

    @Unique
    private boolean gtnl$trySetControllerFromCoord(int x, int y, int z) {
        // First check whether the controller we try to link to is within range. The range is defined
        // as a max distance in each axis.

        // Find the block at the requested coordinated and check if it is a purification plant controller.
        var tileEntity = getBaseMetaTileEntity().getWorld()
            .getTileEntity(x, y, z);
        if (tileEntity == null) return false;
        if (!(tileEntity instanceof IGregTechTileEntity gtTileEntity)) return false;
        var metaTileEntity = gtTileEntity.getMetaTileEntity();
        if (!(metaTileEntity instanceof EyeOfHarmonyInjector injector)) return false;

        // Before linking, unlink from current controller, so we don't end up with units linked to multiple
        // controllers.
        EyeOfHarmonyInjector oldController = getGtnl$controller();
        if (oldController != null) {
            oldController.unregisterLinkedUnit((MTEEyeOfHarmony) (Object) this);
            this.gtnl$unlinkController();
        }

        // Now link to new controller
        gtnl$controllerX = x;
        gtnl$controllerY = y;
        gtnl$controllerZ = z;
        gtnl$controllerSet = true;
        gtnl$controller = injector;
        gtnl$controller.registerLinkedUnit((MTEEyeOfHarmony) (Object) this);
        return true;
    }

    @Unique
    public boolean gtnl$tryLinkDataStick(EntityPlayer aPlayer) {
        // Make sure the held item is a data stick
        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) {
            return false;
        }

        // Make sure this data stick is a proper purification plant link data stick.
        if (!dataStick.hasTagCompound() || !dataStick.stackTagCompound.getString("type")
            .equals("EyeOfHarmonyInjector")) {
            return false;
        }

        // Now read link coordinates from the data stick.
        NBTTagCompound nbt = dataStick.stackTagCompound;
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");

        // Try to link, and report the result back to the player.
        boolean result = gtnl$trySetControllerFromCoord(x, y, z);
        if (result) {
            aPlayer.addChatMessage(new ChatComponentText("Link successful"));
        } else {
            aPlayer.addChatMessage(new ChatComponentText("Link failed: No Injector found at link location"));
        }

        return true;
    }

    @Unique
    public Widget gtnl$makeSyncerWidgets() {
        return new MultiChildWidget()
            .addChild(new FakeSyncWidget.BooleanSyncer(() -> this.mMachine, machine -> this.mMachine = machine))
            .addChild(new FakeSyncWidget.BooleanSyncer(this::isAllowedToWork, _work -> {}));
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        // Right-clicking could be a data stick linking action, so try this first.
        if (aBaseMetaTileEntity.isServerSide() && gtnl$tryLinkDataStick(aPlayer)) {
            return true;
        }
        return super.onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    @Unique
    public EyeOfHarmonyInjector getGtnl$controller() {
        if (gtnl$controller == null) return null;
        // Controller disappeared
        if (gtnl$controller.getBaseMetaTileEntity() == null) return null;
        return gtnl$controller;
    }

    // If the controller is broken this can be called to explicitly unlink the controller, so we don't have any
    // references lingering around
    @Unique
    public void gtnl$unlinkController() {
        this.gtnl$controllerSet = false;
        this.gtnl$controller = null;
        this.gtnl$controllerX = 0;
        this.gtnl$controllerY = 0;
        this.gtnl$controllerZ = 0;
    }

    @Override
    public void onBlockDestroyed() {
        // When this block is destroyed, explicitly unlink it from the controller if there is any.
        EyeOfHarmonyInjector controller = getGtnl$controller();
        if (controller != null) {
            controller.unregisterLinkedUnit((MTEEyeOfHarmony) (Object) this);
        }
        super.onBlockDestroyed();
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();

        // Display linked controller in Waila.
        if (tag.getBoolean("linked")) {
            currenttip.add(
                EnumChatFormatting.AQUA + "Linked to Injector at "
                    + EnumChatFormatting.WHITE
                    + tag.getInteger("controllerX")
                    + ", "
                    + tag.getInteger("controllerY")
                    + ", "
                    + tag.getInteger("controllerZ")
                    + EnumChatFormatting.RESET);
        } else {
            currenttip.add(EnumChatFormatting.AQUA + "Unlinked");
        }

        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {

        tag.setBoolean("linked", getGtnl$controller() != null);
        if (getGtnl$controller() != null) {
            tag.setInteger("controllerX", gtnl$controllerX);
            tag.setInteger("controllerY", gtnl$controllerY);
            tag.setInteger("controllerZ", gtnl$controllerZ);
        }

        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }
}
