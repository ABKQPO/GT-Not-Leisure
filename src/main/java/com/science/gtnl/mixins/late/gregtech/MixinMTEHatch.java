package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.item.ItemUtils;

import appeng.helpers.ICustomNameObject;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

@Mixin(value = MTEHatch.class, remap = false)
public abstract class MixinMTEHatch extends MTEBasicTank implements ICustomNameObject {

    @Unique
    private String gtnl$customName = "";

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
        gtnl$customName = name;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (hasCustomName()) {
            tag.setString("gtnl$interfaceName", gtnl$customName);
            return;
        }

        ItemStack craftingIcon = getMachineCraftingIcon();
        if (craftingIcon == null) craftingIcon = getStackForm(1);
        tag.setTag("gtnl$interfaceIcon", craftingIcon.writeToNBT(new NBTTagCompound()));

        if (this instanceof IConfigurationCircuitSupport circuitSupport && circuitSupport.allowSelectCircuit()) {
            ItemStack circuit = getStackInSlot(circuitSupport.getCircuitSlot());
            if (circuit != null && circuit.getItemDamage() > 0) {
                tag.setInteger("gtnl$interfaceCircuit", circuit.getItemDamage());
            }
        }

        RecipeMap<?> recipeMap = gtnl$getRecipeMap();
        if (recipeMap != null) tag.setString("gtnl$interfaceRecipeMap", recipeMap.unlocalizedName);

        NBTTagList nonConsumedItems = new NBTTagList();
        for (ItemStack stack : mInventory) {
            if (ItemUtils.isExtraItem(stack)) {
                nonConsumedItems.appendTag(stack.writeToNBT(new NBTTagCompound()));
                break;
            }
        }
        if (nonConsumedItems.tagCount() > 0) tag.setTag("gtnl$interfaceItems", nonConsumedItems);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        StringBuilder name = new StringBuilder(tag.getString("gtnl$interfaceName"));
        if (!name.isEmpty()) {
            currenttip.add(EnumChatFormatting.AQUA + name.toString() + EnumChatFormatting.RESET);
            return;
        }
        if ((name.isEmpty()) && tag.hasKey("gtnl$interfaceIcon")) {
            ItemStack icon = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("gtnl$interfaceIcon"));
            if (icon != null) name = new StringBuilder(icon.getDisplayName());
        }
        if (name.isEmpty()) return;

        if (tag.hasKey("gtnl$interfaceCircuit")) {
            name.append(" - ")
                .append(tag.getInteger("gtnl$interfaceCircuit"));
        }

        if (tag.hasKey("gtnl$interfaceRecipeMap")) {
            name.append(" - ")
                .append(gtnl$localize(tag.getString("gtnl$interfaceRecipeMap")));
        }

        NBTTagList nonConsumedItems = tag.getTagList("gtnl$interfaceItems", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nonConsumedItems.tagCount(); i++) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nonConsumedItems.getCompoundTagAt(i));
            if (stack != null) name.append(" - ")
                .append(gtnl$getWailaItemName(stack));
        }

        currenttip.add(EnumChatFormatting.AQUA + name.toString() + EnumChatFormatting.RESET);
    }

    @Unique
    private static String gtnl$localize(String key) {
        if (StatCollector.canTranslate(key)) return StatCollector.translateToLocal(key);
        String blockName = key + ".name";
        return StatCollector.canTranslate(blockName) ? StatCollector.translateToLocal(blockName)
            : StatCollector.translateToFallback(key);
    }

    @Unique
    private static String gtnl$getWailaItemName(ItemStack stack) {
        ItemStack defaultStack = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
        String name = defaultStack.getDisplayName();
        return stack.hasDisplayName() ? name + " (" + stack.getDisplayName() + ")" : name;
    }

    @Unique
    private RecipeMap<?> gtnl$getRecipeMap() {
        Object hatch = this;
        if (hatch instanceof MTEHatchInput inputHatch) return inputHatch.mRecipeMap;
        if (hatch instanceof MTEHatchInputBus inputBus) return inputBus.mRecipeMap;
        return null;
    }
}
