package com.science.gtnl.common.render.tile;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.science.gtnl.ClientProxy;
import com.science.gtnl.common.block.blocks.BlockEnderElevator;
import com.science.gtnl.common.block.blocks.tile.TileEntityEnderElevator;
import com.science.gtnl.mixins.early.minecraft.AccessorTessellator;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderElevatorRenderer extends TileEntitySpecialRenderer
    implements ISimpleBlockRenderingHandler, IItemRenderer {

    private final RenderBlocks rb = new RenderBlocks();
    private final TileEntityEnderElevator dummyTE = new TileEntityEnderElevator();

    @Override
    public int getRenderId() {
        return ClientProxy.ENDER_ELEVATOR_RENDER_ID;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Block block = Block.getBlockFromItem(item.getItem());
        if (block == null) return;

        RenderBlocks renderer = data.length > 0 && data[0] instanceof RenderBlocks renderBlocks ? renderBlocks : rb;
        renderInventoryBlock(block, item.getItemDamage(), getRenderId(), renderer);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        dummyTE.blockType = block;
        TileEntityRendererDispatcher.instance.renderTileEntityAt(dummyTE, 0.0D, 0.0D, 0.0D, 0.0F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        return false;
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float pt) {
        TileEntityEnderElevator te = (TileEntityEnderElevator) tile;
        Block disguise = te.getDisguiseBlock();
        BlockEnderElevator elevator = (BlockEnderElevator) te.getBlockType();
        Tessellator tessellator = Tessellator.instance;
        IBlockAccess previousBlockAccess = rb.blockAccess;
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        this.bindTexture(TextureMap.locationBlocksTexture);

        int meta;
        boolean hasWorld = te.hasWorldObj();
        int brightness = hasWorld
            ? elevator.getMixedBrightnessForBlock(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord)
            : 15 << 20 | 15 << 4;
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean light0Enabled = GL11.glIsEnabled(GL11.GL_LIGHT0);
        boolean light1Enabled = GL11.glIsEnabled(GL11.GL_LIGHT1);
        boolean colorMaterialEnabled = GL11.glIsEnabled(GL11.GL_COLOR_MATERIAL);
        boolean rescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            if (hasWorld) {
                RenderHelper.disableStandardItemLighting();
            } else {
                RenderHelper.enableStandardItemLighting();
            }
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslated(x, y, z);

            if (!hasWorld) {
                meta = 0;
                GL11.glTranslatef(0.0F, -0.1F, 0.0F);
            } else {
                meta = te.getBlockMetadata();
            }

            if (disguise != null) {
                try {
                    meta = te.getDisguiseMeta();
                    rb.blockAccess = new DisguiseBlockAccess(
                        te.getWorldObj(),
                        te.xCoord,
                        te.yCoord,
                        te.zCoord,
                        disguise,
                        meta);

                    tessellator.startDrawingQuads();
                    tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

                    tessellator.setTranslation(-te.xCoord, -te.yCoord, -te.zCoord);
                    rb.renderBlockByRenderType(disguise, te.xCoord, te.yCoord, te.zCoord);
                    tessellator.setTranslation(0, 0, 0);
                    tessellator.draw();
                } catch (Exception ignored) {
                    return;
                }
            } else {
                int color = BlockEnderElevator.COLOR_TABLE[meta % BlockEnderElevator.COLOR_TABLE.length];

                rb.setRenderBoundsFromBlock(elevator);
                tessellator.startDrawingQuads();
                tessellator.setBrightness(brightness);
                tessellator
                    .setColorOpaque_F((color >> 16 & 255) / 255f, (color >> 8 & 255) / 255f, (color & 255) / 255f);
                renderStandardCube(elevator, meta);
                tessellator.draw();
            }

            if (elevator.overlayIcon != null) {
                tessellator.startDrawingQuads();
                tessellator.setBrightness(brightness);
                tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                rb.renderFaceYPos(elevator, 0, 0.001, 0, elevator.overlayIcon);
                tessellator.draw();
            }
        } finally {
            try {
                tessellator.setTranslation(0, 0, 0);
                var accessor = (AccessorTessellator) tessellator;
                if (accessor.getIsDrawing()) tessellator.draw();
            } finally {
                rb.blockAccess = previousBlockAccess;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopAttrib();
                restoreCapability(GL11.GL_LIGHTING, lightingEnabled);
                restoreCapability(GL11.GL_LIGHT0, light0Enabled);
                restoreCapability(GL11.GL_LIGHT1, light1Enabled);
                restoreCapability(GL11.GL_COLOR_MATERIAL, colorMaterialEnabled);
                restoreCapability(GL12.GL_RESCALE_NORMAL, rescaleNormalEnabled);
                GL11.glPopMatrix();
            }
        }
    }

    private static void restoreCapability(int capability, boolean enabled) {
        if (enabled) {
            GL11.glEnable(capability);
        } else {
            GL11.glDisable(capability);
        }
    }

    public void renderStandardCube(Block block, int meta) {
        Tessellator tess = Tessellator.instance;
        tess.setNormal(0, -1, 0);
        rb.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, meta));
        tess.setNormal(0, 1, 0);
        rb.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, meta));
        tess.setNormal(0, 0, -1);
        rb.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, meta));
        tess.setNormal(0, 0, 1);
        rb.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, meta));
        tess.setNormal(-1, 0, 0);
        rb.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, meta));
        tess.setNormal(1, 0, 0);
        rb.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, meta));
    }

    public static class DisguiseBlockAccess implements IBlockAccess {

        private final IBlockAccess parent;
        private final int targetX, targetY, targetZ;
        private final Block disguise;
        private final int meta;

        public DisguiseBlockAccess(IBlockAccess parent, int x, int y, int z, Block disguise, int meta) {
            this.parent = parent;
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
            this.disguise = disguise;
            this.meta = meta;
        }

        @Override
        public Block getBlock(int x, int y, int z) {
            if (x == targetX && y == targetY && z == targetZ) {
                return disguise;
            }
            return parent.getBlock(x, y, z);
        }

        @Override
        public TileEntity getTileEntity(int x, int y, int z) {
            return parent.getTileEntity(x, y, z);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue) {
            return parent.getLightBrightnessForSkyBlocks(x, y, z, lightValue);
        }

        @Override
        public int getBlockMetadata(int x, int y, int z) {
            if (x == targetX && y == targetY && z == targetZ) {
                return meta;
            }
            return parent.getBlockMetadata(x, y, z);
        }

        @Override
        public int isBlockProvidingPowerTo(int x, int y, int z, int direction) {
            return parent.isBlockProvidingPowerTo(x, y, z, direction);
        }

        @Override
        public boolean isAirBlock(int x, int y, int z) {
            return parent.isAirBlock(x, y, z);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public BiomeGenBase getBiomeGenForCoords(int x, int z) {
            return parent.getBiomeGenForCoords(x, z);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getHeight() {
            return parent.getHeight();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean extendedLevelsInChunkCache() {
            return parent.extendedLevelsInChunkCache();
        }

        @Override
        public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
            return parent.isSideSolid(x, y, z, side, _default);
        }
    }
}
