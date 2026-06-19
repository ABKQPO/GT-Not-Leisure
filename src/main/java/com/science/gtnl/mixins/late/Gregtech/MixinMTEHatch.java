package com.science.gtnl.mixins.late.Gregtech;

import java.util.List;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.api.mixinHelper.IMultiblockRecipeMap;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.util.IInterfaceViewable;
import appeng.helpers.ICustomNameObject;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEHatch.class, remap = false)
public abstract class MixinMTEHatch extends MTEBasicTank implements IMultiblockRecipeMap, ICustomNameObject {

    @Unique
    private String gtnl$multiBlockRecipeMapName = null;

    @Unique
    private String gtnl$customName = "";

    public MixinMTEHatch(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount, String aDescription,
        ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public String gtnl$getRecipeMapName() {
        return gtnl$multiBlockRecipeMapName;
    }

    @Override
    public void gtnl$setRecipeMapName(String recipeMap) {
        gtnl$multiBlockRecipeMapName = recipeMap;
    }

    @Override
    public void setCustomName(String name) {
        gtnl$customName = name;
    }

    @Override
    public boolean hasCustomName() {
        return !gtnl$customName.isEmpty() || MainConfig.machine.enableHatchInterfaceTerminalEnhance;
    }

    @Unique
    private String gtnl$getAutoNameKey() {
        StringBuilder sb = new StringBuilder();
        MTEHatch hatch = (MTEHatch) (Object) this;

        if (hatch instanceof IConfigurationCircuitSupport circuitHatch) {
            ItemStack circuit = getStackInSlot(circuitHatch.getCircuitSlot());
            if (circuit != null) {
                sb.append("gt_circuit_")
                    .append(circuit.getItemDamage())
                    .append('_');
            }
        }

        if (hatch instanceof MTEHatchInput inputHatch
            && (gtnl$multiBlockRecipeMapName != null || inputHatch.mRecipeMap != null)) {
            sb.append("extra_start_")
                .append(
                    gtnl$multiBlockRecipeMapName != null ? gtnl$multiBlockRecipeMapName
                        : inputHatch.mRecipeMap.unlocalizedName)
                .append("_extra_end_");
        }
        if (hatch instanceof MTEHatchInputBus inputBus
            && (gtnl$multiBlockRecipeMapName != null || inputBus.mRecipeMap != null)) {
            sb.append("extra_start_")
                .append(
                    gtnl$multiBlockRecipeMapName != null ? gtnl$multiBlockRecipeMapName
                        : inputBus.mRecipeMap.unlocalizedName)
                .append("_extra_end_");
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
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        var name = getCustomName();
        if (name == null || name.isEmpty()) {
            if (this instanceof IInterfaceViewable viewable) {
                name = viewable.getName();
                if (name == null || name.isEmpty()) return;
            }
        }
        if (!Objects.equals(name, getLocalName())) {
            tag.setString("name", name);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
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

    @ModifyVariable(method = "updateCraftingIcon", at = @At("HEAD"), argsOnly = true, index = 1)
    private ItemStack gtnl$modifyCraftingIcon(ItemStack value) {
        if (!gtnl$customName.isEmpty()) {
            var item = value.copy();
            item.setStackDisplayName(gtnl$customName);
            return item;
        }
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return value;
        if (value.hasDisplayName()) return value;
        MTEHatch hatch = (MTEHatch) (Object) this;
        StringBuilder sb = null;
        if (hatch instanceof IConfigurationCircuitSupport circuitHatch) {
            ItemStack circuit = getStackInSlot(circuitHatch.getCircuitSlot());
            if (circuit != null) {
                sb = new StringBuilder();
                sb.append("gt_circuit_")
                    .append(circuit.getItemDamage())
                    .append("_");
            }
        }
        if (hatch instanceof MTEHatchInput inputHatch
            && (gtnl$multiBlockRecipeMapName != null || inputHatch.mRecipeMap != null)) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("extra_start_")
                .append(
                    gtnl$multiBlockRecipeMapName != null ? gtnl$multiBlockRecipeMapName
                        : inputHatch.mRecipeMap.unlocalizedName)
                .append("_extra_end_");
        }
        if (hatch instanceof MTEHatchInputBus inputBus
            && (gtnl$multiBlockRecipeMapName != null || inputBus.mRecipeMap != null)) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("extra_start_")
                .append(
                    gtnl$multiBlockRecipeMapName != null ? gtnl$multiBlockRecipeMapName
                        : inputBus.mRecipeMap.unlocalizedName)
                .append("_extra_end_");
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
            ItemStack modified = value.copy();
            modified.setStackDisplayName(sb.toString());
            return modified;
        }
        return value;
    }

}
