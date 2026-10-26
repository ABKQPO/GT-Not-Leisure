package com.science.gtnl.client.celestweave;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.lwjgl.BufferUtils;

import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class CelestweaveFrames {

    public static final String[] BONES = { "head", "body", "rightArm", "leftArm", "rightLeg", "leftLeg" };
    public static final int RIGHT_ARM = 2;
    public static final String[] FILE_KEYS = { "helmet", "chest", "legs", "boots" };

    public enum Layer {
        SOLID,
        SOLID_DETAIL,
        GLOW_FACES,
        GLOW,
        PANE
    }

    public enum Part {

        HELMET(CelestweaveArmor.HEAD, null, false),
        CHEST(CelestweaveArmor.CHEST, null, false),
        LEGS(CelestweaveArmor.LEGS, null, false),
        BOOTS(CelestweaveArmor.BOOTS, null, false),
        GOGGLES(CelestweaveArmor.HEAD, CelestweaveModule.REVEALING, false),
        HOLO(CelestweaveArmor.HEAD, CelestweaveModule.HOLO, false),
        PACK(CelestweaveArmor.CHEST, CelestweaveModule.BACKPACK, false),
        COILS(CelestweaveArmor.CHEST, CelestweaveModule.BACKPACK, true),
        WINGS(CelestweaveArmor.LEGS, CelestweaveModule.FLIGHT, false);

        public final int piece;
        public final CelestweaveModule module;
        public final boolean whenAbsent;

        Part(int piece, CelestweaveModule module, boolean whenAbsent) {
            this.piece = piece;
            this.module = module;
            this.whenAbsent = whenAbsent;
        }

        public boolean visible(CelestweaveLook look) {
            return module == null || look.visible(module) != whenAbsent;
        }
    }

    public enum Tint {
        NONE,
        GLOW,
        FIXED,
        DYNAMIC
    }

    public Map<String, String> objFiles;
    public Textures textures;
    public List<Frame> frames;
    public List<Group> groups;
    public Arcs arcs;

    private transient Map<String, Frame> frameByName;
    private transient String[] objByPiece;
    private transient List<Group>[] groupsByPiece;
    private transient CelestweaveSeam[] seams;

    public static class Frame {

        public String name, file, part, parent;
        public float[] chain, pivot, rot;

        transient int index;
        transient int bone = -1;
        transient Frame parentFrame;
        transient FloatBuffer chainBuffer;

        public int index() {
            return index;
        }

        public String name() {
            return name;
        }
    }

    public static class Group {

        public String obj, name, frame, part, layer, tint, blend, texture;
        public int[] color;
        public int quads;

        transient int piece;
        transient int bone = -1;
        transient Frame frameRef;
        transient Layer layerKind;
        transient Part partKind;
        transient Tint tintKind;
        transient boolean additive;
        transient Strip strip;
        transient float[] rgb;
        transient int list;
    }

    public static class Textures {

        public Tex base, glow, fx;
    }

    public static class Tex {

        public String file;
        public int[] size;
        public Map<String, Strip> strips;
    }

    public static class Strip {

        public int frames;
        public float strideU;
    }

    public static class Arcs {

        public List<Seam> seams;
        public Line antenna;
        public Core core;
        public Map<String, Line> dash, wingFore;
    }

    public static class Seam {

        public String part, anchor;
        public float[] a, b, half;
    }

    public static class Line {

        public String anchor;
        public float[] a, b;
    }

    public static class Core {

        public String anchor;
        public float[] center, inner, innerRadius, hitRadius, hitReach;
        public float lowRadius;
    }

    @SuppressWarnings("unchecked")
    void resolve() {
        frameByName = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < frames.size(); i++) {
            Frame frame = frames.get(i);
            frame.index = i;
            frameByName.put(frame.name, frame);
        }
        for (Frame frame : frames) {
            if (frame.pivot == null || frame.pivot.length != 3) frame.pivot = new float[3];
            if (frame.rot == null || frame.rot.length != 3) frame.rot = new float[3];
            if (frame.chain != null && frame.chain.length == 16) {
                frame.chainBuffer = BufferUtils.createFloatBuffer(16);
                frame.chainBuffer.put(frame.chain)
                    .flip();
            }
            frame.parentFrame = frameByName.get(frame.parent);
        }
        for (Frame frame : frames) frame.bone = boneOf(frame);
        objByPiece = new String[CelestweaveArmor.PIECES];
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++)
            objByPiece[piece] = objFiles.get(FILE_KEYS[piece]);
        groupsByPiece = new List[CelestweaveArmor.PIECES];
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) groupsByPiece[piece] = new ObjectArrayList<>();
        Map<String, Strip> strips = textures != null && textures.fx != null && textures.fx.strips != null
            ? textures.fx.strips
            : Collections.emptyMap();
        for (Group group : groups) {
            group.piece = Arrays.asList(objByPiece)
                .indexOf(group.obj);
            if (group.piece < 0) continue;
            group.frameRef = frameByName.get(group.frame);
            group.bone = group.frameRef != null ? group.frameRef.bone : boneIndex(group.frame);
            if (group.bone < 0) continue;
            group.layerKind = switch (group.layer) {
                case "solidDetail" -> Layer.SOLID_DETAIL;
                case "glowFaces" -> Layer.GLOW_FACES;
                case "glow" -> Layer.GLOW;
                case "pane" -> Layer.PANE;
                default -> Layer.SOLID;
            };
            group.partKind = Part.valueOf(group.part.toUpperCase(Locale.ROOT));
            group.tintKind = group.tint == null ? Tint.NONE : switch (group.tint) {
                case "glow" -> Tint.GLOW;
                case "fixed" -> Tint.FIXED;
                case "dynamic" -> Tint.DYNAMIC;
                default -> Tint.NONE;
            };
            group.additive = !"alpha".equals(group.blend);
            group.rgb = group.color != null && group.color.length == 3
                ? new float[] { group.color[0] / 255F, group.color[1] / 255F, group.color[2] / 255F }
                : new float[] { 1F, 1F, 1F };
            if (group.texture != null) {
                group.strip = strips.get(group.texture);
                if (group.strip == null) group.strip = strips.get(group.texture + "_n");
            }
            groupsByPiece[group.piece].add(group);
        }
        List<Seam> data = arcs != null && arcs.seams != null ? arcs.seams : Collections.emptyList();
        seams = new CelestweaveSeam[data.size()];
        for (int i = 0; i < seams.length; i++) {
            Seam seam = data.get(i);
            Part part = Part.valueOf(seam.part.toUpperCase(Locale.ROOT));
            seams[i] = seam.half == null ? CelestweaveSeam.line(part, seam.anchor, seam.a, seam.b)
                : CelestweaveSeam.patch(part, seam.anchor, seam.a, seam.b, seam.half);
        }
    }

    public CelestweaveSeam[] seams() {
        return seams;
    }

    public Frame frame(String name) {
        return frameByName.get(name);
    }

    public List<Group> groups(int piece) {
        return groupsByPiece[piece];
    }

    public String objFile(int piece) {
        return objByPiece[piece];
    }

    public static int boneIndex(String name) {
        if (name == null || !name.startsWith("bone:")) return -1;
        return Arrays.asList(BONES)
            .indexOf(name.substring(5));
    }

    private int boneOf(Frame frame) {
        Frame current = frame;
        for (int depth = 0; current != null && depth < 64; depth++) {
            int bone = boneIndex(current.parent);
            if (bone >= 0) return bone;
            current = current.parentFrame;
        }
        return -1;
    }

    public int anchorBone(String anchor) {
        int bone = boneIndex(anchor);
        if (bone >= 0) return bone;
        Frame frame = frameByName.get(anchor);
        return frame != null ? frame.bone : -1;
    }
}
