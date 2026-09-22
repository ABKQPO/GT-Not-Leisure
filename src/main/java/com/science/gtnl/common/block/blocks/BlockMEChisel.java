package com.science.gtnl.common.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.DirectionBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMEChisel extends AEBaseTileBlock {

    private static final DirectionBlockProperty FACING_PROPERTY = new DirectionBlockProperty() {

        @Override
        public String getName() {
            return "facing";
        }

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return trait == BlockPropertyTrait.SupportsWorld || trait == BlockPropertyTrait.SupportsStacks;
        }

        @Override
        public ForgeDirection getValue(IBlockAccess world, int x, int y, int z) {
            if (world.getTileEntity(x, y, z) instanceof TileEntityMEChisel tile) {
                ForgeDirection facing = tile.getForward();
                if (facing != null && facing != ForgeDirection.UNKNOWN) return facing;
            }
            return ForgeDirection.NORTH;
        }

        @Override
        public ForgeDirection getValue(ItemStack stack) {
            return ForgeDirection.NORTH;
        }
    };

    public BlockMEChisel() {
        super(Material.iron);
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("gtnl.me_chisel");
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockMEChisel.class, "me_chisel");
        BlockPropertyRegistry.registerBlockItemProperty(this, FACING_PROPERTY);
        GameRegistry.registerTileEntity(TileEntityMEChisel.class, "me_chisel_tile_entity");
        GTNLItemList.MEChisel.set(new ItemStack(this, 1));
        setTileEntity(TileEntityMEChisel.class);
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

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

}
