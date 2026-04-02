package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockMEChisel extends AEBaseTileBlock {

    public BlockMEChisel() {
        super(Material.iron);
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("MEChisel");
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockMEChisel.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityMEChisel.class, "MEChiselTileEntity");
        GTNLItemList.MEChisel.set(new ItemStack(this, 1));
        setTileEntity(TileEntityMEChisel.class);
        setBlockTextureName(RESOURCE_ROOT_ID + ":MEChisel");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityMEChisel te) {
            if (!super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) {
                if (player instanceof EntityPlayerMP p) {
                    if (te.getParallel() != 1) {
                        ScienceNotLeisure.network.sendTo(new MEChiselSyncParallel(te), p);
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
