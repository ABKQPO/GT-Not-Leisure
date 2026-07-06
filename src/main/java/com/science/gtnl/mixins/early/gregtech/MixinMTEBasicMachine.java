package com.science.gtnl.mixins.early.gregtech;

import java.util.List;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.helpers.ICustomNameObject;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEBasicMachine.class, remap = false)
public abstract class MixinMTEBasicMachine extends MTEBasicTank implements ICustomNameObject {

    @Shadow
    public abstract int getCircuitSlot();

    @Unique
    private String gtnl$customName = "";

    public MixinMTEBasicMachine(String aName, int aTier, int aInvSlotCount, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    public void loadNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        gtnl$customName = aNBT.getString("gtnl$customName");
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    public void saveNBTData(NBTTagCompound aNBT, CallbackInfo ci) {
        if (!gtnl$customName.isEmpty()) aNBT.setString("gtnl$customName", gtnl$customName);
    }

    @Override
    public boolean hasCustomName() {
        return !gtnl$customName.isEmpty() || MainConfig.machine.enableHatchInterfaceTerminalEnhance;
    }

    @Override
    public ItemStack getMachineCraftingIcon() {
        if (!gtnl$customName.isEmpty()) {
            var itemStack = super.getMachineCraftingIcon();
            itemStack.setStackDisplayName(gtnl$customName);
            return itemStack;
        }
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return super.getMachineCraftingIcon();

        StringBuilder sb = null;

        ItemStack circuit = getStackInSlot(getCircuitSlot());
        if (circuit != null) {
            sb = new StringBuilder();
            sb.append("gt_circuit_")
                .append(circuit.getItemDamage())
                .append('_');
        }

        if (((Object) this) instanceof MTEBasicMachineWithRecipe machine) {
            var recipeMap = machine.getRecipeMap();
            if (recipeMap != null) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append("extra_start_")
                    .append(recipeMap.unlocalizedName)
                    .append("_extra_end_");
            }
        }

        for (int i = 0; i < 10 && i < mInventory.length; i++) {
            ItemStack stack = mInventory[i];
            if (!ItemUtils.isExtraItem(stack)) continue;
            if (sb == null) {
                sb = new StringBuilder();
            }

            String registryName = GameRegistry.findUniqueIdentifierFor(stack.getItem())
                .toString();

            sb.append("extra_item_start_")
                .append(registryName)
                .append("@")
                .append(stack.getItemDamage());

            if (stack.hasDisplayName()) {
                sb.append("{")
                    .append(stack.getDisplayName())
                    .append("}");
            }

            sb.append("extra_item_end_");
            break;
        }

        if (sb != null && !sb.isEmpty()) {
            ItemStack modified = super.getMachineCraftingIcon().copy();
            modified.setStackDisplayName(sb.toString());
            return modified;
        }

        return super.getMachineCraftingIcon();
    }

    @Unique
    private String gtnl$getAutoNameKey() {
        StringBuilder sb = new StringBuilder();

        ItemStack circuit = getStackInSlot(getCircuitSlot());
        if (circuit != null) {
            sb.append("gt_circuit_")
                .append(circuit.getItemDamage())
                .append('_');
        }

        if (((Object) this) instanceof MTEBasicMachineWithRecipe machine) {
            var recipeMap = machine.getRecipeMap();
            if (recipeMap != null) {
                sb.append("extra_start_")
                    .append(recipeMap.unlocalizedName)
                    .append("_extra_end_");
            }
        }

        for (int i = 0; i < 10 && i < mInventory.length; i++) {
            ItemStack stack = mInventory[i];
            if (!ItemUtils.isExtraItem(stack)) continue;

            String registryName = GameRegistry.findUniqueIdentifierFor(stack.getItem())
                .toString();

            sb.append("extra_item_start_")
                .append(registryName)
                .append("@")
                .append(stack.getItemDamage());

            if (stack.hasDisplayName()) {
                sb.append("{")
                    .append(stack.getDisplayName())
                    .append("}");
            }

            sb.append("extra_item_end_");
            break;
        }

        return sb.append(super.getMachineCraftingIcon().getUnlocalizedName())
            .toString();
    }

    @Override
    public String getCustomName() {
        if (!gtnl$customName.isEmpty()) return gtnl$customName;
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return getLocalName();
        return gtnl$getAutoNameKey();
    }

    @Override
    public void setCustomName(String name) {
        gtnl$customName = name;
    }

    @Inject(method = "getWailaNBTData", at = @At("TAIL"))
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z, CallbackInfo ci) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        if (!Objects.equals(getCustomName(), getLocalName())) {
            tag.setString("name", getCustomName());
        }
    }

    @Inject(method = "getWailaBody", at = @At("TAIL"))
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config, CallbackInfo ci) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("name")) {
            currenttip.add(
                EnumChatFormatting.AQUA
                    + (MainConfig.machine.enableHatchInterfaceTerminalEnhance
                        ? Utils.getExtraInterfaceName(tag.getString("name"))
                        : tag.getString("name"))
                    + EnumChatFormatting.RESET);
        }
    }
}
