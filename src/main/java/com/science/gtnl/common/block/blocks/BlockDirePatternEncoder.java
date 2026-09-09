package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockDirePatternEncoder;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.block.AEBaseTileBlock;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockDirePatternEncoder extends AEBaseTileBlock {

    public BlockDirePatternEncoder() {
        super(Material.iron);
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("gtnl.block.dire_pattern_encoder");
        setHarvestLevel("pickaxe", 3);
        setBlockTextureName(RESOURCE_ROOT_ID + ":dire_pattern_encoder");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockDirePatternEncoder.class, "dire_pattern_encoder");
        GameRegistry.registerTileEntity(TileEntityDirePatternEncoder.class, "dire_pattern_encoder_tile_entity");
        GTNLItemList.DirePatternEncoder.set(new ItemStack(this, 1));
        setTileEntity(TileEntityDirePatternEncoder.class);
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.dire_pattern_encoder";
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
        float par8, float par9) {
        if (!world.isRemote) {
            CommonProxy.openGui(player, GuiType.DirePatternEncoderGUI, null, world, x, y, z);
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
    }

}
