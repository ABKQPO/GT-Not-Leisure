package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockSuperDualInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperDualInterface;
import com.science.gtnl.common.render.tile.SuperDualInterfaceRenderer;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.util.IOrientable;
import appeng.block.misc.BlockInterface;
import appeng.client.render.blocks.RenderBlockInterface;
import appeng.core.features.AEFeature;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSuperDualInterface extends BlockInterface {

    @SideOnly(Side.CLIENT)
    public IIcon iconAlternate;
    @SideOnly(Side.CLIENT)
    public IIcon iconAlternateArrow;

    public BlockSuperDualInterface() {
        super();
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("gtnl.block.super_dual_interface");
        setHarvestLevel("pickaxe", 3);
        setBlockTextureName(RESOURCE_ROOT_ID + ":super_dual_interface");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockSuperDualInterface.class, "super_dual_interface");
        GameRegistry.registerTileEntity(TileEntitySuperDualInterface.class, "super_dual_interface_tile_entity");
        GTNLItemList.SuperDualInterface.set(new ItemStack(this, 1));
        setTileEntity(TileEntitySuperDualInterface.class);
        setFeature(EnumSet.of(AEFeature.Core));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.super_dual_interface";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconAlternate = register.registerIcon(RESOURCE_ROOT_ID + ":super_dual_interface_alternate");
        iconAlternateArrow = register.registerIcon(RESOURCE_ROOT_ID + ":super_dual_interface_alternate_arrow");
        super.registerBlockIcons(register);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockInterface getRenderer() {
        return new SuperDualInterfaceRenderer();
    }

    @Override
    public boolean onActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY,
        float hitZ) {
        if (player.isSneaking()) {
            return false;
        }

        var tile = getTileEntity(world, x, y, z);
        if (tile == null) {
            return false;
        }

        CommonProxy.openGui(player, GuiType.SuperDualInterfaceGUI, null, world, x, y, z);
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        TileEntitySuperDualInterface tile = getTileEntity(world, x, y, z);
        if (tile != null) {
            tile.getInterfaceDuality()
                .updateRedstoneState();
        }
    }

    @Override
    public boolean hasCustomRotation() {
        return true;
    }

    @Override
    public void customRotateBlock(IOrientable rotatable, ForgeDirection axis) {
        if (rotatable instanceof TileEntitySuperDualInterface superDualInterface) {
            superDualInterface.setSide(axis);
        }
    }
}
