package com.science.gtnl.client.celestweave;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveObjModel implements IResourceManagerReloadListener {

    public static final CelestweaveObjModel INSTANCE = new CelestweaveObjModel();
    public static final ResourceLocation BASE = new ResourceLocation(
        RESOURCE_ROOT_ID,
        "textures/models/armor/celestweave_base.png");
    public static final ResourceLocation GLOW = new ResourceLocation(
        RESOURCE_ROOT_ID,
        "textures/models/armor/celestweave_glow.png");
    public static final ResourceLocation FX = new ResourceLocation(
        RESOURCE_ROOT_ID,
        "textures/models/armor/celestweave_fx.png");
    private static final String ROOT = "models/armor/";

    private final List<Integer> lists = new ArrayList<>();
    private CelestweaveFrames frames;
    private IModelCustom[] models;
    private int[] glowPixels;
    private int glowWidth, glowHeight;
    private boolean failed;

    private CelestweaveObjModel() {}

    public static void register() {
        if (Minecraft.getMinecraft()
            .getResourceManager() instanceof IReloadableResourceManager manager)
            manager.registerReloadListener(INSTANCE);
    }

    public CelestweaveFrames frames() {
        ensure();
        return frames;
    }

    public void call(CelestweaveFrames.Group group) {
        int id = group.list;
        if (id == 0) {
            id = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(id, GL11.GL_COMPILE);
            try {
                if (group.layerKind == CelestweaveFrames.Layer.GLOW_FACES && glowPixels != null
                    && models[group.piece] instanceof WavefrontObject obj) renderRaised(obj, group.name);
                else models[group.piece].renderPart(group.name);
            } finally {
                GL11.glEndList();
            }
            lists.add(id);
            group.list = id;
        }
        GL11.glCallList(id);
    }

    private void renderRaised(WavefrontObject obj, String name) {
        List<Face> faces = new ArrayList<>();
        for (GroupObject part : obj.groupObjects) if (name.equalsIgnoreCase(part.name)) faces.addAll(part.faces);
        List<float[]> quads = CelestweaveRaise.raise(faces, glowPixels, glowWidth, glowHeight, CelestweaveRaise.HEIGHT);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (float[] quad : quads) for (int i = 0; i < 20; i += 5)
            tessellator.addVertexWithUV(quad[i], quad[i + 1], quad[i + 2], quad[i + 3], quad[i + 4]);
        tessellator.draw();
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        for (int id : lists) GLAllocation.deleteDisplayLists(id);
        lists.clear();
        frames = null;
        models = null;
        glowPixels = null;
        failed = false;
    }

    private void ensure() {
        if (frames != null || failed) return;
        try {
            var resource = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(new ResourceLocation(RESOURCE_ROOT_ID, ROOT + "celestweave_frames.json"));
            CelestweaveFrames parsed;
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                parsed = new Gson().fromJson(reader, CelestweaveFrames.class);
            }
            parsed.resolve();
            IModelCustom[] loaded = new IModelCustom[CelestweaveArmor.PIECES];
            for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) loaded[piece] = AdvancedModelLoader
                .loadModel(new ResourceLocation(RESOURCE_ROOT_ID, ROOT + parsed.objFile(piece)));
            BufferedImage glow;
            try (InputStream stream = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(GLOW)
                .getInputStream()) {
                glow = ImageIO.read(stream);
            }
            glowWidth = glow.getWidth();
            glowHeight = glow.getHeight();
            glowPixels = glow.getRGB(0, 0, glowWidth, glowHeight, null, 0, glowWidth);
            models = loaded;
            frames = parsed;
            CelestweaveAnimator.bind(parsed);
        } catch (Exception e) {
            failed = true;
            ScienceNotLeisure.LOG.error("Failed to load Celestweave armor model", e);
        }
    }
}
