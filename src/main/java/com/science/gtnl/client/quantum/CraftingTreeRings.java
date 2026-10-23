package com.science.gtnl.client.quantum;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jetbrains.annotations.Nullable;

import appeng.api.storage.data.IAEStack;
import appeng.client.gui.widgets.GuiCraftingTree;
import appeng.crafting.v2.CraftingRequest;
import appeng.crafting.v2.resolvers.CraftableItemResolver.CraftFromPatternTask;

/**
 * Finds the crafting cycles of a crafting tree, whatever calculated it: a request for what a craft above it makes is
 * the way back around a cycle, from the request that craft makes down to it. The nodes of the tree are private to
 * {@link GuiCraftingTree}, so they are read by reflection.
 */
public final class CraftingTreeRings {

    /**
     * A cycle of the tree: the request it is entered by, then the crafts and requests down to the ones that lead back
     * into it, in tree order, and the requests of its crafts that come from outside it.
     */
    public record Ring(Object entry, List<Object> members, Set<Object> backEdges, List<Object> externalInputs) {

        public int crafts() {
            int count = 0;
            for (var member : this.members) {
                if (isTask(member)) count++;
            }
            return count;
        }
    }

    private static final Class<?> NODE;
    private static final Class<?> REQUEST_NODE;
    private static final Class<?> TASK_NODE;
    private static final Field ALL_NODES;
    private static final Field TREE_NODES;
    private static final Field SUBTREE_WIDTH;
    private static final Field PRESSED_NODE;
    private static final Field TOOLTIP_NODE;
    private static final Field DRAGGING;
    private static final Field LAST_DRAG_X;
    private static final Field LAST_DRAG_Y;
    private static final Field PARENT;
    private static final Method REBUILD_LAYOUT;
    private static final Method NODE_AT;
    private static final Field X;
    private static final Field Y;
    private static final Field VISIBLE;
    private static final Field PARENT_NODE;
    private static final Field CHILD_NODES;
    private static final Field COLLAPSED;
    private static final Field REQUEST;
    private static final Field RESOLVER;
    private static final Field RESOLVER_TASK;
    private static final Field ZOOM;
    private static final Field TREE_WIDTH;
    private static final Field TREE_HEIGHT;
    private static final Field MISSING;
    private static final Field HAS_MISSING;
    private static final Method DRAW;
    private static final Method PARENT_LINE;
    private static final Method SLOT_OUTLINE;
    private static final Constructor<?> NEW_REQUEST_NODE;
    private static final Constructor<?> NEW_TASK_NODE;
    private static final Field CRAFTS_DONE;

    static {
        try {
            NODE = Class.forName("appeng.client.gui.widgets.GuiCraftingTree$Node");
            REQUEST_NODE = Class.forName("appeng.client.gui.widgets.GuiCraftingTree$RequestNode");
            TASK_NODE = Class.forName("appeng.client.gui.widgets.GuiCraftingTree$TaskNode");
            ALL_NODES = field(GuiCraftingTree.class, "allNodes");
            TREE_NODES = field(GuiCraftingTree.class, "treeNodes");
            PRESSED_NODE = field(GuiCraftingTree.class, "pressedNode");
            TOOLTIP_NODE = field(GuiCraftingTree.class, "tooltipNode");
            DRAGGING = field(GuiCraftingTree.class, "dragging");
            LAST_DRAG_X = field(GuiCraftingTree.class, "lastDragX");
            LAST_DRAG_Y = field(GuiCraftingTree.class, "lastDragY");
            PARENT = field(GuiCraftingTree.class, "parent");
            REBUILD_LAYOUT = GuiCraftingTree.class.getDeclaredMethod("rebuildLayout");
            REBUILD_LAYOUT.setAccessible(true);
            NODE_AT = GuiCraftingTree.class.getDeclaredMethod("getNodeAt", int.class, int.class);
            NODE_AT.setAccessible(true);
            X = field(NODE, "x");
            Y = field(NODE, "y");
            VISIBLE = field(NODE, "visible");
            PARENT_NODE = field(NODE, "parentNode");
            CHILD_NODES = field(NODE, "childNodes");
            COLLAPSED = field(NODE, "childrenCollapsed");
            REQUEST = field(REQUEST_NODE, "request");
            RESOLVER = field(TASK_NODE, "resolver");
            RESOLVER_TASK = field(Class.forName("appeng.crafting.v2.CraftingRequest$UsedResolverEntry"), "task");
            ZOOM = field(GuiCraftingTree.class, "zoomLevel");
            TREE_WIDTH = field(GuiCraftingTree.class, "treeWidth");
            TREE_HEIGHT = field(GuiCraftingTree.class, "treeHeight");
            MISSING = field(NODE, "missing");
            SUBTREE_WIDTH = field(NODE, "subtreeWidth");
            HAS_MISSING = field(NODE, "hasMissing");
            DRAW = NODE.getDeclaredMethod("draw");
            DRAW.setAccessible(true);
            PARENT_LINE = NODE.getDeclaredMethod("drawParentLine");
            PARENT_LINE.setAccessible(true);
            SLOT_OUTLINE = GuiCraftingTree.class
                .getDeclaredMethod("drawSlotOutline", int.class, int.class, int.class, boolean.class);
            SLOT_OUTLINE.setAccessible(true);
            NEW_REQUEST_NODE = REQUEST_NODE
                .getDeclaredConstructor(GuiCraftingTree.class, int.class, int.class, NODE, CraftingRequest.class);
            NEW_REQUEST_NODE.setAccessible(true);
            NEW_TASK_NODE = TASK_NODE.getDeclaredConstructor(
                GuiCraftingTree.class,
                int.class,
                int.class,
                NODE,
                CraftingRequest.UsedResolverEntry.class);
            NEW_TASK_NODE.setAccessible(true);
            CRAFTS_DONE = field(CraftFromPatternTask.class, "totalCraftsDone");
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Field field(Class<?> owner, String name) throws NoSuchFieldException {
        var field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static Object get(Field field, Object owner) {
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void set(Field field, Object owner, Object value) {
        try {
            field.set(owner, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Finds the cycles of the tree, each by the request it is entered by. Rings sharing members are merged into the
     * outermost one.
     */
    public static Map<Object, Ring> fold(GuiCraftingTree tree) {
        var rings = new IdentityHashMap<Object, Ring>();
        var nodes = allNodes(tree);
        // Per entry, the nodes of all ways back into it
        var members = new IdentityHashMap<Object, LinkedHashSet<Object>>();
        var backEdges = new IdentityHashMap<Object, Set<Object>>();
        for (var node : nodes) {
            if (!isRequest(node)) continue;
            var maker = maker(node);
            var entry = maker == null ? null : parent(maker);
            if (entry == null) continue;
            var path = new ArrayList<Object>();
            for (Object step = node; step != null; step = parent(step)) {
                path.add(0, step);
                if (step == entry) break;
            }
            members.computeIfAbsent(entry, e -> new LinkedHashSet<>())
                .addAll(path);
            backEdges.computeIfAbsent(entry, e -> Collections.newSetFromMap(new IdentityHashMap<>()))
                .add(node);
        }
        // An entry inside another ring belongs to that one
        var outer = new IdentityHashMap<Object, Object>();
        for (var entry : members.keySet()) {
            Object top = entry;
            boolean merged = true;
            while (merged) {
                merged = false;
                for (var other : members.entrySet()) {
                    if (other.getKey() != top && other.getValue()
                        .contains(top)) {
                        top = other.getKey();
                        merged = true;
                        break;
                    }
                }
            }
            outer.put(entry, top);
        }
        var mergedMembers = new IdentityHashMap<Object, LinkedHashSet<Object>>();
        var mergedEdges = new IdentityHashMap<Object, Set<Object>>();
        for (var entry : members.keySet()) {
            var top = outer.get(entry);
            mergedMembers.computeIfAbsent(top, e -> new LinkedHashSet<>())
                .addAll(members.get(entry));
            mergedEdges.computeIfAbsent(top, e -> Collections.newSetFromMap(new IdentityHashMap<>()))
                .addAll(backEdges.get(entry));
        }
        for (var entry : mergedMembers.entrySet()) {
            var inRing = entry.getValue();
            // In tree order, so the ring is drawn the way the tree reads
            var ordered = new ArrayList<Object>();
            for (var node : nodes) {
                if (inRing.contains(node)) ordered.add(node);
            }
            var external = new ArrayList<Object>();
            for (var node : ordered) {
                if (!isTask(node)) continue;
                for (var child : children(node)) {
                    if (!inRing.contains(child)) external.add(child);
                }
            }
            rings.put(entry.getKey(), new Ring(entry.getKey(), ordered, mergedEdges.get(entry.getKey()), external));
        }
        return rings;
    }

    /**
     * The craft above a request that makes what it asks for, the one it leads back to, if any.
     */
    public static @Nullable Object maker(Object request) {
        var stack = request(request).stack;
        for (Object ancestor = parent(request); ancestor != null; ancestor = parent(ancestor)) {
            if (isTask(ancestor) && makes(ancestor, stack)) return ancestor;
        }
        return null;
    }

    private static boolean makes(Object task, IAEStack<?> stack) {
        if (!(task(task) instanceof CraftFromPatternTask craft)) return false;
        for (var output : craft.pattern.getCondensedAEOutputs()) {
            if (output != null && output.isSameType(stack)) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> allNodes(GuiCraftingTree tree) {
        return (List<Object>) get(ALL_NODES, tree);
    }

    // Visible nodes by row, each row by x
    @SuppressWarnings("unchecked")
    public static TreeMap<Integer, ArrayList<Object>> rows(GuiCraftingTree tree) {
        return (TreeMap<Integer, ArrayList<Object>>) get(TREE_NODES, tree);
    }

    public static int subtreeWidth(Object node) {
        return (int) get(SUBTREE_WIDTH, node);
    }

    public static void setSubtreeWidth(Object node, int width) {
        set(SUBTREE_WIDTH, node, width);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> children(Object node) {
        return (List<Object>) get(CHILD_NODES, node);
    }

    public static @Nullable Object parent(Object node) {
        return get(PARENT_NODE, node);
    }

    public static boolean isRequest(Object node) {
        return REQUEST_NODE.isInstance(node);
    }

    public static boolean isTask(Object node) {
        return TASK_NODE.isInstance(node);
    }

    public static CraftingRequest request(Object node) {
        return (CraftingRequest) get(REQUEST, node);
    }

    public static Object task(Object node) {
        return get(RESOLVER_TASK, get(RESOLVER, node));
    }

    public static int x(Object node) {
        return (int) get(X, node);
    }

    public static int y(Object node) {
        return (int) get(Y, node);
    }

    public static void moveTo(Object node, int x, int y) {
        set(X, node, x);
        set(Y, node, y);
    }

    public static boolean visible(Object node) {
        return (boolean) get(VISIBLE, node);
    }

    public static void setVisible(Object node, boolean visible) {
        set(VISIBLE, node, visible);
    }

    public static boolean collapsed(Object node) {
        return (boolean) get(COLLAPSED, node);
    }

    public static void setCollapsed(Object node, boolean collapsed) {
        set(COLLAPSED, node, collapsed);
    }

    public static boolean missing(Object node) {
        return (boolean) get(MISSING, node);
    }

    public static boolean hasMissing(Object node) {
        return (boolean) get(HAS_MISSING, node);
    }

    // Draws the node the way the tree does
    public static void draw(Object node) {
        try {
            DRAW.invoke(node);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    // The line up to its parent, the way the tree draws it
    public static void drawParentLine(Object node) {
        try {
            PARENT_LINE.invoke(node);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void drawSlotOutline(GuiCraftingTree tree, int x, int y, int color, boolean operation) {
        try {
            SLOT_OUTLINE.invoke(tree, x, y, color, operation);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * A node of the tree's own kind that is not in the tree, linked under its parent.
     */
    public static Object newRequestNode(GuiCraftingTree tree, @Nullable Object parent, CraftingRequest request) {
        return link(construct(NEW_REQUEST_NODE, tree, 0, 0, parent, request), parent);
    }

    public static Object newTaskNode(GuiCraftingTree tree, Object parent, CraftingRequest.UsedResolverEntry resolver) {
        return link(construct(NEW_TASK_NODE, tree, 0, 0, parent, resolver), parent);
    }

    public static void setCraftsDone(CraftFromPatternTask task, long crafts) {
        set(CRAFTS_DONE, task, crafts);
    }

    private static Object construct(Constructor<?> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object link(Object node, @Nullable Object parent) {
        if (parent != null) children(parent).add(node);
        return node;
    }

    public static float zoom(GuiCraftingTree tree) {
        return (float) get(ZOOM, tree);
    }

    public static void setZoom(GuiCraftingTree tree, float zoom) {
        set(ZOOM, tree, zoom);
    }

    // The extent scrolling is held to
    public static int[] treeSize(GuiCraftingTree tree) {
        return new int[] { (int) get(TREE_WIDTH, tree), (int) get(TREE_HEIGHT, tree) };
    }

    public static void setTreeSize(GuiCraftingTree tree, int width, int height) {
        set(TREE_WIDTH, tree, width);
        set(TREE_HEIGHT, tree, height);
    }

    public static void setTooltipNode(GuiCraftingTree tree, @Nullable Object node) {
        set(TOOLTIP_NODE, tree, node);
    }

    public static @Nullable Object pressedNode(GuiCraftingTree tree) {
        return get(PRESSED_NODE, tree);
    }

    public static @Nullable Object tooltipNode(GuiCraftingTree tree) {
        return get(TOOLTIP_NODE, tree);
    }

    public static boolean dragging(GuiCraftingTree tree) {
        return (boolean) get(DRAGGING, tree);
    }

    public static Object parentGui(GuiCraftingTree tree) {
        return get(PARENT, tree);
    }

    public static @Nullable Object nodeAt(GuiCraftingTree tree, int guiMouseX, int guiMouseY) {
        try {
            return NODE_AT.invoke(tree, guiMouseX, guiMouseY);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    // Ends a press the way mouseReleased does, without toggling the node
    public static void releasePress(GuiCraftingTree tree) {
        set(PRESSED_NODE, tree, null);
        set(LAST_DRAG_X, tree, Float.NEGATIVE_INFINITY);
        set(LAST_DRAG_Y, tree, Float.NEGATIVE_INFINITY);
    }

    public static void rebuildLayout(GuiCraftingTree tree) {
        try {
            REBUILD_LAYOUT.invoke(tree);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
