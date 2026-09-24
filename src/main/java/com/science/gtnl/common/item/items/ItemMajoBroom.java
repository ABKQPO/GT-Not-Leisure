package com.science.gtnl.common.item.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMajoBroom extends Item {

    private final String broomName;

    public ItemMajoBroom() {
        this("majo_broom", GTNLItemList.MajoBroom);
    }

    protected ItemMajoBroom(String broomName, GTNLItemList itemList) {
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

    protected EntityMajoBroom createBroom(World world) {
        return new EntityMajoBroom(world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon("minecraft:stick");
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
        if (!world.getCollidingBoundingBoxes(broom, broom.boundingBox).isEmpty()) return false;
        if (world.isRemote) return true;
        if (!world.spawnEntityInWorld(broom)) return false;
        if (ride) player.mountEntity(broom);
        if (!player.capabilities.isCreativeMode) stack.stackSize--;
        return true;
    }
}
