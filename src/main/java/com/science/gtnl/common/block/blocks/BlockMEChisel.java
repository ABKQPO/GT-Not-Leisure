package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockMEChisel;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.packet.MEChiselSyncParallel;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.block.AEBaseTileBlock;
import appeng.client.texture.FlippableIcon;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMEChisel extends AEBaseTileBlock {

    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;

    public BlockMEChisel() {
        super(Material.iron);
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("gtnl.me_chisel");
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockMEChisel.class, "me_chisel");
        GameRegistry.registerTileEntity(TileEntityMEChisel.class, "me_chisel_tile_entity");
        GTNLItemList.MEChisel.set(new ItemStack(this, 1));
        setTileEntity(TileEntityMEChisel.class);
        setBlockTextureName(RESOURCE_ROOT_ID + ":me_chisel");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        topIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":me_chisel");
        bottomIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":me_chisel_bottom");
        sideIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":me_chisel_side");

        FlippableIcon top = new FlippableIcon(topIcon);
        FlippableIcon bottom = new FlippableIcon(bottomIcon);
        FlippableIcon side = new FlippableIcon(sideIcon);
        blockIcon = topIcon;
        getRendererInstance().updateIcons(bottom, top, side, side, side, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return topIcon;
        if (side == 0) return bottomIcon;
        return sideIcon;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityMEChisel te) {
            if (!super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) {
                if (player instanceof EntityPlayerMP p) {
                    if (te.getParallel() != 1) {
                        ScienceNotLeisure.network.sendTo(new MEChiselSyncParallel.Clientbound(te), p);
                    }
                    CommonProxy.openGui(p, GuiType.MEChiselGUI, null, world, x, y, z);
                }
            }
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World w, int x, int y, int z, Block a, int b) {
        w.removeTileEntity(x, y, z);
    }

}
