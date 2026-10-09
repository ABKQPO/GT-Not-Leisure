package com.science.gtnl.client;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Temporary, client-only markers; never requests chunk loads or retains markers across worlds. */
@SideOnly(Side.CLIENT)
public final class WirelessWorldHighlight {

    public static final WirelessWorldHighlight INSTANCE = new WirelessWorldHighlight();
    private static final long DURATION_MS = 5_000;

    private record Position(int x, int y, int z) {}

    private record Selection(World world, List<Position> positions, long expires) {}

    private record Arrival(WeakReference<NetHandlerPlayClient> connection, int dimension, Position position,
        long expires) {}

    private static volatile Selection selection;
    private static Arrival arrival;

    public static void show(Minecraft mc, NBTTagList positions) {
        if (mc.theWorld == null) return;
        arrival = null;
        List<Position> targets = new ArrayList<>();
        for (int i = 0; i < Math.min(512, positions.tagCount()); i++) {
            var pos = positions.getCompoundTagAt(i);
            if (pos.getInteger("dimension") == mc.theWorld.provider.dimensionId)
                targets.add(new Position(pos.getInteger("x"), pos.getInteger("y"), pos.getInteger("z")));
        }
        selection = new Selection(
            mc.theWorld,
            targets.stream()
                .distinct()
                .toList(),
            Minecraft.getSystemTime() + DURATION_MS);
        if (!targets.isEmpty() && mc.thePlayer != null && mc.currentScreen instanceof WirelessCardGui) {
            // Use the same eye position as vanilla ray tracing (including the 1.7.10 client player offset).
            var player = mc.thePlayer;
            var eyes = player.getPosition(1.0F);
            Position target = targets.stream()
                .min(
                    Comparator
                        .comparingDouble(pos -> eyes.squareDistanceTo(pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5)))
                .orElseThrow();
            double dx = target.x() + 0.5 - eyes.xCoord;
            double dy = target.y() + 0.5 - eyes.yCoord;
            double dz = target.z() + 0.5 - eyes.zCoord;
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            if (horizontal > 1.0E-6) {
                float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
                player.rotationYaw += MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw);
            }
            if (horizontal > 1.0E-6 || Math.abs(dy) > 1.0E-6)
                player.rotationPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
            player.prevRotationYaw = player.rotationYaw;
            player.prevRotationPitch = player.rotationPitch;
            player.rotationYawHead = player.prevRotationYawHead = player.rotationYaw;
            // Notify the server too, releasing the card container's locked inventory slot.
            player.closeScreen();
        }
    }

    public static void afterTeleport(Minecraft mc, int dimension, int x, int y, int z) {
        selection = null;
        // Respawn and chunk packets may still be queued. Start the visible duration only after arrival.
        arrival = new Arrival(
            new WeakReference<>(mc.getNetHandler()),
            dimension,
            new Position(x, y, z),
            Minecraft.getSystemTime() + 10_000);
    }

    private static void showArrival(Minecraft mc) {
        Arrival pending = arrival;
        if (pending == null) return;
        long now = Minecraft.getSystemTime();
        if (mc.getNetHandler() == null || mc.getNetHandler() != pending.connection()
            .get() || now >= pending.expires()) {
            arrival = null;
            return;
        }
        Position pos = pending.position();
        if (mc.theWorld != null && mc.theWorld.provider.dimensionId == pending.dimension()
            && mc.theWorld.blockExists(pos.x(), pos.y(), pos.z())
            && !mc.theWorld.isAirBlock(pos.x(), pos.y(), pos.z())) {
            selection = new Selection(mc.theWorld, List.of(pos), now + DURATION_MS);
            arrival = null;
        }
    }

    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
        Selection current = selection;
        if (current != null && current.world() == event.world) selection = null;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        showArrival(mc);
        Selection current = selection;
        if (current == null) return;
        if (current.world() != mc.theWorld || Minecraft.getSystemTime() >= current.expires()) {
            selection = null;
            return;
        }
        Entity camera = mc.renderViewEntity;
        if (camera == null) return;
        double x = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * event.partialTicks;
        double y = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * event.partialTicks;
        double z = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * event.partialTicks;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(-x, -y, -z);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(2.0F);
            GL11.glColor4f(0.2F, 0.95F, 0.85F, 0.85F);
            for (Position pos : current.positions()) {
                if (camera.getDistanceSq(pos.x(), pos.y(), pos.z()) > 256 * 256
                    || !mc.theWorld.blockExists(pos.x(), pos.y(), pos.z())
                    || mc.theWorld.isAirBlock(pos.x(), pos.y(), pos.z())) continue;
                RenderGlobal.drawOutlinedBoundingBox(
                    AxisAlignedBB.getBoundingBox(
                        pos.x() - 0.002,
                        pos.y() - 0.002,
                        pos.z() - 0.002,
                        pos.x() + 1.002,
                        pos.y() + 1.002,
                        pos.z() + 1.002),
                    -1);
            }
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }
}
