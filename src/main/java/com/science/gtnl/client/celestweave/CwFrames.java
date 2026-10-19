package com.science.gtnl.client.celestweave;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.lwjgl.BufferUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CwFrames {

    public static final String[] BONES = { "head", "body", "rightArm", "leftArm", "rightLeg", "leftLeg" };
    public static final String[] FILE_KEYS = { "helmet", "chest", "legs", "boots" };

    public enum Layer {
        SOLID,
        SOLID_DETAIL,
        GLOW_FACES,
        GLOW,
        PANE
    }

    public enum Piece {
        HELMET,
        CHEST,
        LEGS,
        BOOTS,
        GOGGLES,
        HOLO,
        PACK,
        COILS,
        WINGS
    }

    public enum Tint {
        NONE,
        THEME,
        FIXED,
        DYNAMIC
    }

    public Map<String, String> objFiles;
    public Textures textures;
    public List<Frame> frames;
    public List<Group> groups;

    private transient Map<String, Frame> frameByName;
    private transient String[] objByType;
    private transient List<Group>[] groupsByType;

    public static final class Frame {

        public String name, file, piece, parent;
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

    public static final class Group {

        public String obj, name, frame, piece, layer, tint, blend, texture;
        public int[] color;
        public int quads;

        transient int type;
        transient int bone = -1;
        transient Frame frameRef;
        transient Layer layerKind;
        transient Piece pieceKind;
        transient Tint tintKind;
        transient boolean additive;
        transient Strip strip;
        transient float[] rgb;
        transient int list;
    }

    public static final class Textures {

        public Tex base, glow, fx;
    }

    public static final class Tex {

        public String file;
        public int[] size;
        public Map<String, Strip> strips;
    }

    public static final class Strip {

        public int frames;
        public float strideU;
    }

    @SuppressWarnings("unchecked")
    void resolve() {
        frameByName = new HashMap<>();
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
        objByType = new String[4];
        for (int type = 0; type < 4; type++) objByType[type] = objFiles.get(FILE_KEYS[type]);
        groupsByType = new List[4];
        for (int type = 0; type < 4; type++) groupsByType[type] = new ArrayList<>();
        Map<String, Strip> strips = textures != null && textures.fx != null && textures.fx.strips != null
            ? textures.fx.strips
            : Collections.emptyMap();
        for (Group group : groups) {
            group.type = Arrays.asList(objByType)
                .indexOf(group.obj);
            if (group.type < 0) continue;
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
            group.pieceKind = Piece.valueOf(group.piece.toUpperCase(Locale.ROOT));
            group.tintKind = group.tint == null ? Tint.NONE : switch (group.tint) {
                case "theme" -> Tint.THEME;
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
            groupsByType[group.type].add(group);
        }
    }

    public Frame frame(String name) {
        return frameByName.get(name);
    }

    public List<Group> groups(int type) {
        return groupsByType[type];
    }

    public String objFile(int type) {
        return objByType[type];
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
