package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.gui.GTNLGuiHandler;
import com.science.gtnl.utils.enums.GuiType;

import appeng.block.AEBaseTileBlock;
import appeng.client.texture.FlippableIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDirePatternEncoder extends AEBaseTileBlock {

    public BlockDirePatternEncoder() {
        super(Material.iron);
        setHardness(50.0F);
        setResistance(2000.0F);
        setHarvestLevel("pickaxe", 3);
        setTileEntity(TileEntityDirePatternEncoder.class);
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.dire_pattern_encoder";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        FlippableIcon top = new FlippableIcon(register.registerIcon(RESOURCE_ROOT_ID + ":dire_pattern_encoder"));
        FlippableIcon side = new FlippableIcon(register.registerIcon(RESOURCE_ROOT_ID + ":dire_pattern_encoder_side"));
        FlippableIcon bottom = new FlippableIcon(
            register.registerIcon(RESOURCE_ROOT_ID + ":dire_pattern_encoder_bottom"));
        blockIcon = top;
        getRendererInstance().updateIcons(bottom, top, side, side, side, side);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
        float par8, float par9) {
        if (!world.isRemote) {
            GTNLGuiHandler.openGui(player, GuiType.DirePatternEncoderGUI, null, world, x, y, z);
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
    }

}
