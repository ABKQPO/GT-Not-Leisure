package com.science.gtnl.common.render;

import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class SpoceRenderHandler {

    public static final float[] buildBuf = new float[1 << 16];
    public static int buildCount;
    public static final double[] angleScratch = new double[9];
    public static final int UPDATE_VERT = 15;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        if (world == null) return;

        synchronized (SpoceEffect.effects) {
            Iterator<SpoceEffect> it = SpoceEffect.effects.iterator();

            while (it.hasNext()) {
                SpoceEffect effect = it.next();

                if (effect.removed) {
                    it.remove();
                    continue;
                }

                effect.update();

                if (world.getTotalWorldTime() % UPDATE_VERT == 0) {
                    for (SpoceEffect.Layer l : effect.layers) {
                        if (l.renderLines) l.dirty = true;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        if (world == null) return;

        synchronized (SpoceEffect.effects) {
            if (SpoceEffect.effects.isEmpty()) return;

            double px = RenderManager.instance.viewerPosX;
            double py = RenderManager.instance.viewerPosY;
            double pz = RenderManager.instance.viewerPosZ;

            for (SpoceEffect effect : SpoceEffect.effects) {
                renderEffect(world, effect, px, py, pz, event.partialTicks);
            }
        }
    }

    public static void renderEffect(World world, SpoceEffect effect, double px, double py, double pz, float partial) {
        float interp = effect.getInterp(partial);
        float time = (world.getTotalWorldTime() + partial) * effect.rotSpeed;

        GL11.glPushMatrix();
        GL11.glTranslated(effect.x - px, effect.y - py, effect.z - pz);
        GL11.glScalef(effect.globalScale, effect.globalScale, effect.globalScale);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);

        for (SpoceEffect.Layer layer : effect.layers) {
            float currentR = (float) (layer.radius * interp);

            // 1. 球体
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(layer.r, layer.g, layer.b, layer.a * 0.3f);
            drawSphere(currentR, 24, 24);

            // 2. 外壳
            GL11.glPushMatrix();
            GL11.glRotatef(time, 0, 1, 0);
            GL11.glRotatef(time * 0.5f, 1, 0, 0);
            GL11.glColor4f(layer.r * 1.2f, layer.g * 1.2f, layer.b * 1.2f, layer.a * 0.8f);
            GL11.glLineWidth(2.0f);
            drawBuckyBall(currentR + 0.02f);
            GL11.glPopMatrix();

            // 3. 贴地线
            if (layer.renderLines) {
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                GL11.glLineWidth(4.0f);
                GL11.glColor4f(layer.r * 1.5f, layer.g * 1.5f, layer.b * 1.5f, layer.a);
                float[] verts = getLayerVerts(world, effect, layer, interp);
                drawCachedLines(verts);
            }
        }

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public static float[] getLayerVerts(World world, SpoceEffect effect, SpoceEffect.Layer layer, float interp) {
        if (interp < 0.99f) return buildGeometry(world, effect, layer.radius * interp);
        if (layer.dirty) {
            layer.cachedVerts = buildGeometry(world, effect, layer.radius);
            layer.dirty = false;
        }
        return layer.cachedVerts;
    }

    public static float[] buildGeometry(World world, SpoceEffect effect, double radius) {
        buildCount = 0;
        int radInt = (int) Math.ceil(radius + 1.0);
        int cx = (int) Math.floor(effect.x), cy = (int) Math.floor(effect.y), cz = (int) Math.floor(effect.z);

        for (int dx = -radInt; dx <= radInt; dx++) {
            for (int dy = -radInt; dy <= radInt; dy++) {
                for (int dz = -radInt; dz <= radInt; dz++) {
                    int wx = cx + dx, wy = cy + dy, wz = cz + dz;
                    Block block = world.getBlock(wx, wy, wz);
                    if (block.isAir(world, wx, wy, wz)) continue;

                    AxisAlignedBB bb = block.getSelectedBoundingBoxFromPool(world, wx, wy, wz);
                    if (bb == null) continue;

                    AxisAlignedBB box = bb.getOffsetBoundingBox(-effect.x, -effect.y, -effect.z);
                    if (isBoxIntersectingSphere(box, radius)) {
                        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                            if (!world.getBlock(wx + dir.offsetX, wy + dir.offsetY, wz + dir.offsetZ)
                                .isOpaqueCube()) {
                                appendAABBFace(dir, box, radius);
                            }
                        }
                    }
                }
            }
        }
        return Arrays.copyOf(buildBuf, buildCount);
    }

    public static boolean isBoxIntersectingSphere(AxisAlignedBB b, double r) {
        double dmin = 0, rSq = r * r;
        if (0 < b.minX) dmin += b.minX * b.minX;
        else if (0 > b.maxX) dmin += b.maxX * b.maxX;
        if (0 < b.minY) dmin += b.minY * b.minY;
        else if (0 > b.maxY) dmin += b.maxY * b.maxY;
        if (0 < b.minZ) dmin += b.minZ * b.minZ;
        else if (0 > b.maxZ) dmin += b.maxZ * b.maxZ;
        if (dmin > rSq) return false;
        double dmax = 0;
        dmax += Math.max(b.minX * b.minX, b.maxX * b.maxX);
        dmax += Math.max(b.minY * b.minY, b.maxY * b.maxY);
        dmax += Math.max(b.minZ * b.minZ, b.maxZ * b.maxZ);
        return dmax >= rSq;
    }

    public static void appendAABBFace(ForgeDirection face, AxisAlignedBB box, double R) {
        double planeW, minU, maxU, minV, maxV;
        switch (face) {
            case UP:
                planeW = box.maxY;
                minU = box.minX;
                maxU = box.maxX;
                minV = box.minZ;
                maxV = box.maxZ;
                break;
            case DOWN:
                planeW = box.minY;
                minU = box.minX;
                maxU = box.maxX;
                minV = box.minZ;
                maxV = box.maxZ;
                break;
            case SOUTH:
                planeW = box.maxZ;
                minU = box.minX;
                maxU = box.maxX;
                minV = box.minY;
                maxV = box.maxY;
                break;
            case NORTH:
                planeW = box.minZ;
                minU = box.minX;
                maxU = box.maxX;
                minV = box.minY;
                maxV = box.maxY;
                break;
            case EAST:
                planeW = box.maxX;
                minU = box.minZ;
                maxU = box.maxZ;
                minV = box.minY;
                maxV = box.maxY;
                break;
            case WEST:
                planeW = box.minX;
                minU = box.minZ;
                maxU = box.maxZ;
                minV = box.minY;
                maxV = box.maxY;
                break;
            default:
                return;
        }
        double r2 = R * R - planeW * planeW;
        if (r2 < 0) return;
        double r = Math.sqrt(r2);
        if (r < minU || -r > maxU || r < minV || -r > maxV) return;
        double rp = planeW + (face.offsetY + face.offsetX + face.offsetZ > 0 ? 0.005 : -0.005);
        int ac = 1;
        angleScratch[0] = 0.0;
        ac = addCos(angleScratch, ac, minU / r);
        ac = addCos(angleScratch, ac, maxU / r);
        ac = addSin(angleScratch, ac, minV / r);
        ac = addSin(angleScratch, ac, maxV / r);
        Arrays.sort(angleScratch, 0, ac);
        for (int i = 0; i < ac; i++) {
            double aStart = angleScratch[i], aEnd = (i + 1 < ac) ? angleScratch[i + 1] : Math.PI * 2.0;
            if (aEnd - aStart < 1E-4) continue;
            double aMid = (aStart + aEnd) * 0.5;
            if (r * Math.cos(aMid) < minU - 1E-4 || r * Math.cos(aMid) > maxU + 1E-4
                || r * Math.sin(aMid) < minV - 1E-4
                || r * Math.sin(aMid) > maxV + 1E-4) continue;
            for (int j = 0; j < 8; j++) {
                double a1 = aStart + (aEnd - aStart) * j / 8.0, a2 = aStart + (aEnd - aStart) * (j + 1) / 8.0;
                addVert(face, r * Math.cos(a1), r * Math.sin(a1), rp);
                addVert(face, r * Math.cos(a2), r * Math.sin(a2), rp);
            }
        }
    }

    public static void addVert(ForgeDirection face, double u, double v, double p) {
        if (buildCount + 3 > buildBuf.length) return;
        if (face == ForgeDirection.UP || face == ForgeDirection.DOWN) {
            buildBuf[buildCount++] = (float) u;
            buildBuf[buildCount++] = (float) p;
            buildBuf[buildCount++] = (float) v;
        } else if (face == ForgeDirection.SOUTH || face == ForgeDirection.NORTH) {
            buildBuf[buildCount++] = (float) u;
            buildBuf[buildCount++] = (float) v;
            buildBuf[buildCount++] = (float) p;
        } else {
            buildBuf[buildCount++] = (float) p;
            buildBuf[buildCount++] = (float) v;
            buildBuf[buildCount++] = (float) u;
        }
    }

    public static int addCos(double[] b, int c, double v) {
        if (v < -1.0 || v > 1.0) return c;
        double a = Math.acos(v);
        b[c++] = a;
        b[c++] = Math.PI * 2.0 - a;
        return c;
    }

    public static int addSin(double[] b, int c, double v) {
        if (v < -1.0 || v > 1.0) return c;
        double a = Math.asin(v);
        b[c++] = (a < 0 ? a + Math.PI * 2.0 : a);
        b[c++] = Math.PI - a;
        return c;
    }

    public static void drawCachedLines(float[] v) {
        if (v.length == 0) return;
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_LINES);
        for (int i = 0; i < v.length; i += 3) t.addVertex(v[i], v[i + 1], v[i + 2]);
        t.draw();
    }

    public static void drawSphere(double r, int s, int t) {
        Tessellator tess = Tessellator.instance;
        for (int i = 0; i < s; i++) {
            double p1 = Math.PI * i / s, p2 = Math.PI * (i + 1) / s;
            tess.startDrawing(GL11.GL_QUAD_STRIP);
            for (int j = 0; j <= t; j++) {
                double th = 2.0 * Math.PI * j / t;
                tess.addVertex(r * Math.sin(p1) * Math.cos(th), r * Math.cos(p1), r * Math.sin(p1) * Math.sin(th));
                tess.addVertex(r * Math.sin(p2) * Math.cos(th), r * Math.cos(p2), r * Math.sin(p2) * Math.sin(th));
            }
            tess.draw();
        }
    }

    public static void drawBuckyBall(float r) {
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_LINES);
        for (int[] e : BuckyBallGeometry.edges) {
            double[] v1 = BuckyBallGeometry.vertices.get(e[0]), v2 = BuckyBallGeometry.vertices.get(e[1]);
            t.addVertex(v1[0] * r, v1[1] * r, v1[2] * r);
            t.addVertex(v2[0] * r, v2[1] * r, v2[2] * r);
        }
        t.draw();
    }
}
