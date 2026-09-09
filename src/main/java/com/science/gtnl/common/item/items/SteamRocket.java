package com.science.gtnl.common.item.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.entity.EntitySteamRocket;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class SteamRocket extends Item implements IHoldableItem {

    public SteamRocket() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setTextureName("arrow");
        this.setUnlocalizedName("SteamRocket");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "steam_rocket");
        GTNLItemList.SteamRocket.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.item.steam_rocket";
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
        int par5, int par6, int par7, float par8, float par9, float par10) {
        boolean padFound = false;
        TileEntity tile = null;

        if (par3World.isRemote && par2EntityPlayer instanceof EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(8, (EntityPlayerSP) par2EntityPlayer);
            return false;
        }
        float centerX = -1;
        float centerY = -1;
        float centerZ = -1;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                final Block id = par3World.getBlock(par4 + i, par5, par6 + j);
                final int meta = par3World.getBlockMetadata(par4 + i, par5, par6 + j);

                if (id == GCBlocks.landingPadFull && meta == 0) {
                    padFound = true;
                    tile = par3World.getTileEntity(par4 + i, par5, par6 + j);

                    centerX = par4 + i + 0.5F;
                    centerY = par5 + 0.4F;
                    centerZ = par6 + j + 0.5F;

                    break;
                }
            }

            if (padFound) {
                break;
            }
        }

        if (!padFound) {
            return false;
        }
        // Check whether there is already a rocket on the pad
        if (tile instanceof TileEntityLandingPad) {
            if (((TileEntityLandingPad) tile).getDockedEntity() != null) {
                return false;
            }
        } else {
            return false;
        }

        final EntitySteamRocket spaceship = new EntitySteamRocket(
            par3World,
            centerX,
            centerY,
            centerZ,
            EnumRocketType.values()[par1ItemStack.getItemDamage()]);

        spaceship.setPosition(spaceship.posX, spaceship.posY + spaceship.getOnPadYOffset(), spaceship.posZ);
        par3World.spawnEntityInWorld(spaceship);

        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound()
            .hasKey("RocketFuel")) {
            spaceship.fuelTank.fill(
                new FluidStack(
                    GalacticraftCore.fluidFuel,
                    par1ItemStack.getTagCompound()
                        .getInteger("RocketFuel")),
                true);
        }

        if (!par2EntityPlayer.capabilities.isCreativeMode) {
            par1ItemStack.stackSize--;
        }

        if (spaceship.rocketType.getPreFueled()) {
            spaceship.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, spaceship.getMaxFuel()), true);
        }
        return true;
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        for (int i = 0; i < EnumRocketType.values().length; i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
        boolean advancedToolTips) {
        final EnumRocketType type = EnumRocketType.values()[itemStack.getItemDamage()];

        if (!type.getTooltip()
            .isEmpty()) {
            toolTip.add(type.getTooltip());
        }

        if (type.getPreFueled()) {
            toolTip.add(EnumColor.RED + "§o" + GCCoreUtil.translate("gui.creativeOnly.desc"));
        }

        if (itemStack.hasTagCompound() && itemStack.getTagCompound()
            .hasKey("RocketFuel")) {
            final EntitySteamRocket rocket = new EntitySteamRocket(
                FMLClientHandler.instance()
                    .getWorldClient(),
                0,
                0,
                0,
                EnumRocketType.values()[itemStack.getItemDamage()]);
            toolTip.add(
                GCCoreUtil.translate("gui.message.fuel.name") + ": "
                    + itemStack.getTagCompound()
                        .getInteger("RocketFuel")
                    + " / "
                    + rocket.fuelTank.getCapacity());
        }

        toolTip.add(StatCollector.translateToLocal("gtnl.item.steam_rocket.tooltip.0"));
        toolTip.add("§o" + StatCollector.translateToLocal("gtnl.item.steam_rocket.tooltip.1"));

    }

    @Override
    public boolean shouldHoldLeftHandUp(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean shouldHoldRightHandUp(EntityPlayer player) {
        return true;
    }

    @Override
    public boolean shouldCrouch(EntityPlayer player) {
        return true;
    }
}
