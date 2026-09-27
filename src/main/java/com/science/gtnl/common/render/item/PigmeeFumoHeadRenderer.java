/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.render.item;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.block.blocks.BlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.item.ItemBlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;
import com.science.gtnl.common.render.model.PigmeeFumoModel;
import com.science.gtnl.common.render.model.PigmeeFumoRenderHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Draws the spinning Pigmee Fumo on the head of every player wearing one.
 *
 * <p>
 * 1.7.10 has no {@code ItemRenderType.HEAD} member and no {@code display.head} baked transform: the vanilla
 * first-person renderer only draws the held item, and a head-worn {@code ItemBlock} in third person is drawn
 * as an {@code EQUIPPED} block, which shares its transform with the hand. There is therefore no item-renderer
 * hook for this. The supported route is this pair of player render events.
 *
 * <p>
 * {@link #onSpecialsPre} suppresses the vanilla helmet pass for our item only. Items are not {@code ItemArmor},
 * so nothing would be drawn anyway, but suppressing it keeps that assumption explicit and protects against
 * other mods adding a layer.
 *
 * <p>
 * The doll spins from a world clock rather than from tile state, so it turns for every player regardless of
 * whether any placed block is spinning: one full revolution per 60 ticks, exactly as upstream.
 */
@SideOnly(Side.CLIENT)
public class PigmeeFumoHeadRenderer {

    /** Head bone space offset that seats the model so its base rests on the head top. Model pixels. */
    private static final float HEAD_SEAT_OFFSET = 2.0F / 16.0F;

    /** Reused lightmap scratch; only ever touched on the render thread. */
    private final int[] brightness = new int[7];

    /**
     * Suppresses the vanilla helmet pass for our own head item only.
     *
     * @param event the Specials pre event; the event is never cancelled, no other decoration is touched and
     *              the player inventory is only read
     */
    @SubscribeEvent
    public void onSpecialsPre(RenderPlayerEvent.Specials.Pre event) {
        if (!isWearingFumo(event.entityPlayer)) return;
        if (PigmeeFumoModel.INSTANCE.get(ForgeDirection.NORTH) == null) return;
        event.renderHelmet = false;
    }

    /**
     * Draws the doll in head-bone space for every visible player wearing one.
     *
     * @param event the Specials post event; uses {@code entityPlayer}, {@code renderer} and
     *              {@code partialRenderTick}. Players without a world, invisible players, hidden head bones and an
     *              unloaded model are skipped. The GL matrix is restored in a finally block.
     */
    @SubscribeEvent
    public void onSpecialsPost(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (!isWearingFumo(player)) return;
        World world = player.worldObj;
        if (world == null) return;
        if (player.isInvisible()) return;

        ModelRenderer head = event.renderer.modelBipedMain.bipedHead;
        if (head.isHidden || !head.showModel) return;

        Geometry model = PigmeeFumoModel.INSTANCE.get(ForgeDirection.NORTH);
        if (model == null) return;

        float partialTick = event.partialRenderTick;
        // The head bone's rotateAngleY is already body-relative, so reconstruct the absolute head yaw and
        // subtract it back out. This keeps the model's body yaw clamp and riding corrections authoritative
        // instead of re-deriving body yaw from rotationYaw, which double-counts across +/-180.
        float headYaw = player.prevRotationYawHead
            + MathHelper.wrapAngleTo180_float(player.rotationYawHead - player.prevRotationYawHead) * partialTick;
        float bodyYaw = headYaw - head.rotateAngleY * (180.0F / (float) Math.PI);

        int light = player.getBrightnessForRender(partialTick);
        for (int i = 0; i < brightness.length; i++) {
            brightness[i] = light;
        }

        GL11.glPushMatrix();
        try {
            // ModelRenderer.postRender: pivot translation, then Z, Y, X rotations.
            GL11.glTranslatef(head.rotationPointX / 16.0F, head.rotationPointY / 16.0F, head.rotationPointZ / 16.0F);
            GL11.glRotatef(head.rotateAngleZ * (180.0F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-bodyYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(headYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(head.rotateAngleX * (180.0F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            // Seat the doll on the head top, then convert model Y-up/Z-forward into head bone space.
            GL11.glTranslatef(0.0F, -HEAD_SEAT_OFFSET, 0.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            // Upstream's display.head translation of fourteen pixels, plus the centred model origin.
            GL11.glTranslatef(0.0F, 14.0F / 16.0F, 0.0F);
            GL11.glRotatef(
                (world.getTotalWorldTime() % 60L + partialTick) * TileEntityPigmeeFumo.SPIN_DEGREES_PER_TICK,
                0.0F,
                1.0F,
                0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            PigmeeFumoRenderHelper.drawWorld(model, brightness);
        } finally {
            GL11.glPopMatrix();
        }
    }

    /** @return true when the player's helmet slot holds this mod's Pigmee Fumo block item. */
    private static boolean isWearingFumo(EntityPlayer player) {
        if (player == null) return false;
        ItemStack helmet = player.inventory.armorItemInSlot(3);
        return helmet != null && helmet.getItem() instanceof ItemBlockPigmeeFumo
            && ((ItemBlockPigmeeFumo) helmet.getItem()).field_150939_a instanceof BlockPigmeeFumo;
    }
}
