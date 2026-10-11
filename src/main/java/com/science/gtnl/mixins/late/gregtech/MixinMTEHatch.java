package com.science.gtnl.mixins.late.gregtech;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IInterfaceNameController;
import com.science.gtnl.api.mixinHelper.IInterfaceNameHatch;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameControllerContext;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import appeng.api.interfaces.IInterfaceNameProvider;
import appeng.helpers.ICustomNameObject;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEHatch.class, remap = false)
public abstract class MixinMTEHatch extends MTEBasicTank implements ICustomNameObject, IInterfaceNameHatch {

    @Unique
    private String gtnl$customName = "";

    @Unique
    private WeakReference<MTEMultiBlockBase> gtnl$interfaceNameController;

    @Unique
    private long gtnl$interfaceNameStructureVersion;

    @Unique
    private Map<MTEMultiBlockBase, Long> gtnl$interfaceNameControllers;

    public MixinMTEHatch(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount, String aDescription,
        ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    private void gtnl$loadCustomName(NBTTagCompound nbt, CallbackInfo callbackInfo) {
        gtnl$customName = nbt.getString("gtnl$customName");
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    private void gtnl$saveCustomName(NBTTagCompound nbt, CallbackInfo callbackInfo) {
        if (!gtnl$customName.isEmpty()) nbt.setString("gtnl$customName", gtnl$customName);
    }

    @Override
    public String getCustomName() {
        return gtnl$customName;
    }

    @Override
    public boolean hasCustomName() {
        return !gtnl$customName.isEmpty();
    }

    @Override
    public void setCustomName(String name) {
        gtnl$customName = name == null ? "" : name;
    }

    @Override
    public void setInterfaceNameController(MTEMultiBlockBase controller) {
        if (!(controller instanceof IInterfaceNameController controllerInfo)) return;
        long version = controllerInfo.getInterfaceNameStructureVersion();
        if (gtnl$interfaceNameControllers == null) gtnl$interfaceNameControllers = new WeakHashMap<>();
        gtnl$interfaceNameControllers.put(controller, version);
        if (gtnl$interfaceNameController != null && gtnl$interfaceNameController.get() == controller
            && gtnl$interfaceNameStructureVersion == version) {
            return;
        }
        gtnl$interfaceNameController = new WeakReference<>(controller);
        gtnl$interfaceNameStructureVersion = version;
    }

    @Override
    public Set<MTEMultiBlockBase> getInterfaceNameControllers() {
        if (gtnl$interfaceNameControllers == null) return Set.of();
        gtnl$interfaceNameControllers.entrySet()
            .removeIf(
                entry -> !entry.getKey()
                    .isValid() || !(entry.getKey() instanceof IInterfaceNameController info)
                    || entry.getValue() != info.getInterfaceNameStructureVersion());
        return gtnl$interfaceNameControllers.keySet()
            .stream()
            .filter(controller -> controller.mMachine)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public RecipeMap<?> getInterfaceNameRecipeMap() {
        if (gtnl$interfaceNameController != null) {
            MTEMultiBlockBase controller = gtnl$interfaceNameController.get();
            if (controller instanceof IInterfaceNameController controllerInfo && controller.isValid()
                && gtnl$interfaceNameStructureVersion == controllerInfo.getInterfaceNameStructureVersion()) {
                RecipeMap<?> recipeMap = controller.getRecipeMap();
                if (recipeMap != null) return recipeMap;
            }
        }
        Object hatch = this;
        if (hatch instanceof MTEHatchInput inputHatch) return inputHatch.mRecipeMap;
        if (hatch instanceof MTEHatchInputBus inputBus) return inputBus.mRecipeMap;
        return null;
    }

    @Inject(
        method = "updateCraftingIcon(Lnet/minecraft/item/ItemStack;Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;)V",
        at = @At("TAIL"))
    private void gtnl$bindInterfaceNameController(ItemStack icon, IGregTechTileEntity owner,
        CallbackInfo callbackInfo) {
        if (icon == null) return;
        if (owner != null && owner.getMetaTileEntity() instanceof MTEMultiBlockBase controller) {
            setInterfaceNameController(controller);
        } else gtnl$bindInterfaceNameController();
    }

    @Inject(method = "updateTexture", at = @At("HEAD"))
    private void gtnl$bindInterfaceNameController(int textureId, CallbackInfo callbackInfo) {
        gtnl$bindInterfaceNameController();
    }

    @Unique
    private void gtnl$bindInterfaceNameController() {
        MTEMultiBlockBase controller = InterfaceNameControllerContext.current();
        if (controller != null) setInterfaceNameController(controller);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (handlesOwnInterfaceName()) {
            return;
        }
        if (hasCustomName()) {
            InterfaceNameLocalization.writeName(tag, "gtnl$interface", gtnl$customName, null, null);
            return;
        }

        ItemStack craftingIcon = getMachineCraftingIcon();
        if (craftingIcon == null) craftingIcon = getStackForm(1);
        IChatComponent suffix = null;
        if (getBaseMetaTileEntity() instanceof IInterfaceNameProvider provider) {
            suffix = provider.getInterfaceNameSuffix();
        }
        if (craftingIcon != null) InterfaceNameLocalization
            .writeName(tag, "gtnl$interface", craftingIcon.getUnlocalizedName(), suffix, craftingIcon);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        if (handlesOwnInterfaceName()) return;
        NBTTagCompound tag = accessor.getNBTData();
        String name = InterfaceNameLocalization.localizeName(tag, "gtnl$interface");
        if (!name.isEmpty()) currenttip.add(EnumChatFormatting.AQUA + name + EnumChatFormatting.RESET);
    }
}
