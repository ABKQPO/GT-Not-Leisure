package com.science.gtnl.common.item.items.bauble;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.common.packet.DraconicArmorProjectionSyncPacket;
import com.science.gtnl.utils.enums.GTNLItemList;

import baubles.api.BaubleType;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.registry.GameRegistry;

public class DraconicArmorProjectionBauble extends BaubleItem implements IBaubleExpanded {

    private final String displayNameKey;
    private final DraconicArmorProjectionType projectionType;

    public DraconicArmorProjectionBauble(String registrationName, String displayNameKey,
        DraconicArmorProjectionType projectionType, GTNLItemList itemEntry) {
        this.displayNameKey = displayNameKey;
        this.projectionType = projectionType;
        setUnlocalizedName(displayNameKey);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        setTextureName(RESOURCE_ROOT_ID + ":" + registrationName);
        setMaxStackSize(1);
        GameRegistry.registerItem(this, registrationName);
        itemEntry.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + displayNameKey;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return UNIVERSAL_TYPE;
    }

    @Override
    public String[] getBaubleTypes(ItemStack itemStack) {
        return UNIVERSAL_BAUBLE_TYPE;
    }

    @Override
    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
        super.onEquippedOrLoadedIntoWorld(stack, player);
        updateProjectionState(player);
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        super.onUnequipped(stack, player);
        clearProjectionState(player);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);
        updateProjectionState(player);
    }

    private void updateProjectionState(EntityLivingBase player) {
        if (!(player instanceof EntityPlayer entityPlayer)) {
            return;
        }
        DraconicArmorProjectionState.set(entityPlayer, projectionType);
        if (!entityPlayer.worldObj.isRemote) {
            ScienceNotLeisure.network.sendToAll(new DraconicArmorProjectionSyncPacket(entityPlayer, projectionType));
        }
    }

    private void clearProjectionState(EntityLivingBase player) {
        if (!(player instanceof EntityPlayer entityPlayer)) {
            return;
        }
        DraconicArmorProjectionState.clear(entityPlayer);
        if (!entityPlayer.worldObj.isRemote) {
            ScienceNotLeisure.network.sendToAll(new DraconicArmorProjectionSyncPacket(entityPlayer, null));
        }
    }
}
