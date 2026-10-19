package com.science.gtnl.client.celestweave;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CwModel implements IResourceManagerReloadListener {

    public static final CwModel INSTANCE = new CwModel();
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
    private CwFrames frames;
    private IModelCustom[] models;
    private boolean failed;

    private CwModel() {}

    public static void register() {
        if (Minecraft.getMinecraft()
            .getResourceManager() instanceof IReloadableResourceManager manager)
            manager.registerReloadListener(INSTANCE);
    }

    public CwFrames frames() {
        ensure();
        return frames;
    }

    public void call(CwFrames.Group group) {
        int id = group.list;
        if (id == 0) {
            id = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(id, GL11.GL_COMPILE);
            try {
                models[group.type].renderPart(group.name);
            } finally {
                GL11.glEndList();
            }
            lists.add(id);
            group.list = id;
        }
        GL11.glCallList(id);
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        for (int id : lists) GLAllocation.deleteDisplayLists(id);
        lists.clear();
        frames = null;
        models = null;
        failed = false;
    }

    private void ensure() {
        if (frames != null || failed) return;
        try {
            var resource = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(new ResourceLocation(RESOURCE_ROOT_ID, ROOT + "celestweave_frames.json"));
            CwFrames parsed;
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                parsed = new Gson().fromJson(reader, CwFrames.class);
            }
            parsed.resolve();
            IModelCustom[] loaded = new IModelCustom[4];
            for (int type = 0; type < 4; type++) loaded[type] = AdvancedModelLoader
                .loadModel(new ResourceLocation(RESOURCE_ROOT_ID, ROOT + parsed.objFile(type)));
            models = loaded;
            frames = parsed;
            CwAnimator.bind(parsed);
        } catch (Exception e) {
            failed = true;
            ScienceNotLeisure.LOG.error("Failed to load Celestweave armor model", e);
        }
    }
}
