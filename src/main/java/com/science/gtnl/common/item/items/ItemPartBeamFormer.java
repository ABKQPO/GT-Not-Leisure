package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartBeamFormer;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartBeamFormer extends Item implements IPartItem {

    @SideOnly(Side.CLIENT)
    public static IIcon iconBase;
    @SideOnly(Side.CLIENT)
    public static IIcon iconStatusOff;
    @SideOnly(Side.CLIENT)
    public static IIcon iconStatusOn;
    @SideOnly(Side.CLIENT)
    public static IIcon iconStatusBeaming;
    @SideOnly(Side.CLIENT)
    public static IIcon iconPrism;

    public ItemPartBeamFormer() {
        this.setMaxStackSize(64);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        this.setUnlocalizedName("PartBeamFormer");
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "PartBeamFormer");
        GameRegistry.registerItem(this, getUnlocalizedName());
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
        GTNLItemList.PartBeamFormer.set(new ItemStack(this, 1));
    }

    @Override
    public void registerIcons(IIconRegister ir) {
        iconBase = ir.registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":part/beam_former_base");
        iconStatusOff = ir.registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":part/beam_former_status_off");
        iconStatusOn = ir.registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":part/beam_former_status_on");
        iconStatusBeaming = ir.registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":part/beam_former_status_beaming");
        iconPrism = ir.registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":part/beam_former_prism");
        super.registerIcons(ir);
    }

    @Nullable
    @Override
    public PartBeamFormer createPartFromItemStack(ItemStack is) {
        return new PartBeamFormer(is);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float xOffset, float yOffset, float zOffset) {
        return AEApi.instance()
            .partHelper()
            .placeBus(player.getHeldItem(), x, y, z, side, player, world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }
}
