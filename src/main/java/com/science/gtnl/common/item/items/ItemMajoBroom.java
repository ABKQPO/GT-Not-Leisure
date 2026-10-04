package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.effect.TextEffects;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMajoBroom extends Item {

    public final String broomName;

    public ItemMajoBroom() {
        this("majo_broom", GTNLItemList.MajoBroom);
    }

    public ItemMajoBroom(String broomName, GTNLItemList itemList) {
        this.broomName = broomName;
        setUnlocalizedName("gtnl." + broomName);
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, broomName);
        itemList.set(new ItemStack(this));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl." + broomName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (!"majo_broom".equals(broomName)) return;
        for (int line = 0; line < 2; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.majo_broom.lore." + line),
                    TextEffects.GENESIS_COMPONENT_RARITY_SHADER));
        }
        tooltip.add("");
        for (int line = 0; line < 2; line++) {
            tooltip.add(
                TextEffects.apply(
                    StatCollector.translateToLocal("item.gtnl.majo_broom.tooltip." + line),
                    TextEffects.EVERCOLD_CYAN));
        }
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.gtnl.majo_broom.view_control_hint"));
    }

    protected EntityMajoBroom createBroom(World world) {
        return new EntityMajoBroom(world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(RESOURCE_ROOT_ID + ":" + broomName);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return placeOnBlock(stack, player, world, x, y, z, side);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition hit = getMovingObjectPositionFromPlayer(world, player, false);
        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            placeOnBlock(stack, player, world, hit.blockX, hit.blockY, hit.blockZ, hit.sideHit);
        return stack;
    }

    public boolean placeOnBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        return side == 1 && player.canPlayerEdit(x, y, z, side, stack)
            && place(stack, player, world, x + 0.5D, y + 1.0D, z + 0.5D, false);
    }

    public boolean place(ItemStack stack, EntityPlayer player, World world, double x, double y, double z,
        boolean ride) {
        if (stack == null || stack.getItem() != this || player.ridingEntity != null) return false;
        EntityMajoBroom broom = createBroom(world);
        broom.setPosition(x, y, z);
        broom.rotationYaw = player.rotationYaw;
        broom.setBroomStack(stack);
        if (!world.getCollidingBoundingBoxes(broom, broom.boundingBox)
            .isEmpty()) return false;
        if (world.isRemote) return true;
        if (!world.spawnEntityInWorld(broom)) return false;
        if (ride) player.mountEntity(broom);
        if (!player.capabilities.isCreativeMode) stack.stackSize--;
        return true;
    }
}
