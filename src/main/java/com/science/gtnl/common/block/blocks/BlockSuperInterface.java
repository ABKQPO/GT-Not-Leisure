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
import com.science.gtnl.common.block.blocks.item.ItemBlockSuperInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperInterface;
import com.science.gtnl.common.render.tile.SuperInterfaceRenderer;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.util.IOrientable;
import appeng.block.misc.BlockInterface;
import appeng.client.render.blocks.RenderBlockInterface;
import appeng.core.features.AEFeature;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSuperInterface extends BlockInterface {

    @SideOnly(Side.CLIENT)
    public IIcon iconAlternate;
    @SideOnly(Side.CLIENT)
    public IIcon iconAlternateArrow;

    public BlockSuperInterface() {
        super();
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("SuperInterface");
        setHarvestLevel("pickaxe", 3);
        setBlockTextureName(RESOURCE_ROOT_ID + ":SuperInterface");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockSuperInterface.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntitySuperInterface.class, "SuperInterfaceTileEntity");
        GTNLItemList.SuperInterface.set(new ItemStack(this, 1));
        this.setTileEntity(TileEntitySuperInterface.class);
        this.setFeature(EnumSet.of(AEFeature.Core));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.iconAlternate = register.registerIcon(RESOURCE_ROOT_ID + ":SuperInterfaceAlternate");
        this.iconAlternateArrow = register.registerIcon(RESOURCE_ROOT_ID + ":SuperInterfaceAlternateArrow");
        super.registerBlockIcons(register);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockInterface getRenderer() {
        return new SuperInterfaceRenderer();
    }

    @Override
    public boolean onActivated(final World w, final int x, final int y, final int z, final EntityPlayer p,
        final int side, final float hitX, final float hitY, final float hitZ) {
        if (p.isSneaking()) {
            return false;
        }

        final TileEntitySuperInterface tg = this.getTileEntity(w, x, y, z);
        if (tg == null) return false;

        CommonProxy.openGui(p, GuiType.SuperInterfaceGUI, null, w, x, y, z);
        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        TileEntitySuperInterface tile = this.getTileEntity(worldIn, x, y, z);
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
    public void customRotateBlock(final IOrientable rotatable, final ForgeDirection axis) {
        if (rotatable instanceof TileEntitySuperInterface superInterface) {
            superInterface.setSide(axis);
        }
    }
}
