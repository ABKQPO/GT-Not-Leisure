// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.render.item;

import java.util.List;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.science.gtnl.common.block.blocks.item.ItemBlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.render.model.PigmeeFumoModel;
import com.science.gtnl.common.render.model.PigmeeFumoRenderHelper;
import com.science.gtnl.common.render.model.PigmeeFumoRenderHelper.FacedQuad;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Head-worn dolls have no item-renderer hook in 1.7.10 (no ItemRenderType.HEAD), so they use the player render events.
@SideOnly(Side.CLIENT)
public class PigmeeFumoHeadRenderer {

    // Head bone space offset that seats the model so its base rests on the head top. Model pixels.
    private static final float HEAD_SEAT_OFFSET = 2.0F / 16.0F;

    // Reused lightmap scratch; only ever touched on the render thread.
    private final int[] brightness = new int[7];

    // Suppresses the vanilla helmet pass for our own head item only, and never cancels the event.
    @SubscribeEvent
    public void onSpecialsPre(RenderPlayerEvent.Specials.Pre event) {
        if (!isWearingFumo(event.entityPlayer)) return;
        if (PigmeeFumoModel.INSTANCE.get(PigmeeFumoModel.DEFAULT_ORIENTATION) == null) return;
        event.renderHelmet = false;
    }

    // Draws the doll in head-bone space for every visible player wearing one.
    @SubscribeEvent
    public void onSpecialsPost(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (!isWearingFumo(player)) return;
        World world = player.worldObj;
        if (world == null) return;
        if (player.isInvisible()) return;

        ModelRenderer head = event.renderer.modelBipedMain.bipedHead;
        if (head.isHidden || !head.showModel) return;

        BakedModel model = PigmeeFumoModel.INSTANCE.get(PigmeeFumoModel.DEFAULT_ORIENTATION);
        if (model == null) return;

        float partialTick = event.partialRenderTick;
        // rotateAngleY is body-relative, so the absolute head yaw is reconstructed, then subtracted out for body yaw.
        float headYaw = player.prevRotationYawHead
            + MathHelper.wrapAngleTo180_float(player.rotationYawHead - player.prevRotationYawHead) * partialTick;
        float bodyYaw = headYaw - head.rotateAngleY * (180.0F / (float) Math.PI);

        // Lights the head block like a placed doll; Entity.getBrightnessForRender would sample chest height only.
        PigmeeFumoRenderHelper.fillBrightness(
            world,
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY + player.getEyeHeight()),
            MathHelper.floor_double(player.posZ),
            brightness);

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
            List<FacedQuad> quads = PigmeeFumoRenderHelper.getAllQuads(model);
            PigmeeFumoRenderHelper.drawWorld(quads, brightness);
        } finally {
            GL11.glPopMatrix();
        }
    }

    // Returns true when the doll is worn, either in the helmet slot or in any Baubles slot.
    private static boolean isWearingFumo(EntityPlayer player) {
        if (player == null) return false;
        if (isFumo(player.inventory.armorItemInSlot(3))) return true;
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return false;
        for (int slot = 0; slot < baubles.getSizeInventory(); slot++) {
            if (isFumo(baubles.getStackInSlot(slot))) return true;
        }
        return false;
    }

    private static boolean isFumo(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBlockPigmeeFumo;
    }
}
