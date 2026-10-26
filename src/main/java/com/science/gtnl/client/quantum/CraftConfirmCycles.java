package com.science.gtnl.client.quantum;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;
import com.science.gtnl.common.packet.RecursivePatternQueryPacket;

import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.widgets.GuiAeButton;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Crafting cycles on AE2's crafting confirm screen: finds the cycles of the request once it is calculated, marks them
 * in
 * the crafting tree and the list, and tells once where the machines of those the plan cannot start because of are. The
 * button lists them all, the highlight key highlights the cycle of what is under the mouse, and shift-clicking an item
 * of a cycle in the list shows the cycle as a ring in the tree view, left by its button or Esc.
 */
public class CraftConfirmCycles {

    public static final String NO_NET_OUTPUT = "gtnl.quantum_crafting.message.CraftingNoNetOutput";

    private CraftConfirmCycles() {}

    public static GuiAeButton backToTreeButton(int guiLeft, int guiTop, int treeWidth, FontRenderer font) {
        var back = StatCollector.translateToLocal("gtnl.gui.crafting_ring.back");
        var button = new GuiAeButton(
            0,
            guiLeft + 9,
            guiTop + 20,
            Math.min(treeWidth, font.getStringWidth(back) + 10),
            18,
            back,
            "");
        button.visible = false;
        return button;
    }

    public static String ringSummary(CraftingRingView view) {
        var ring = view.ring();
        var text = StatCollector.translateToLocalFormatted(
            "gtnl.gui.crafting_ring.summary",
            ring.crafts(),
            ring.backEdges()
                .size(),
            ring.externalInputs()
                .size());
        if (view.omitted() > 0) {
            text += StatCollector.translateToLocalFormatted("gtnl.gui.crafting_ring.omitted", view.omitted());
        }
        return text;
    }

    private static boolean has(IItemList<IAEStack<?>> list, IAEStack<?> item) {
        var found = list.findPrecise(item);
        return found != null && (found.getStackSize() > 0 || found.getCountRequestable() > 0);
    }

    /**
     * The cycles the plan cannot start because of: those without net output when that is the error, those short of
     * something of their own, and when the plan cannot start while showing nothing missing, as the lightweight and
     * standard calculations leave a cycle they could not close, those the plan goes through.
     */
    public static List<Cycle> blocking(List<Cycle> cycles, String error, boolean simulation,
        IItemList<IAEStack<?>> missing, IItemList<IAEStack<?>> storage, IItemList<IAEStack<?>> pending) {
        boolean noNetOutput = NO_NET_OUTPUT.equals(error);
        var blocking = new ObjectArrayList<Cycle>();
        if (!noNetOutput && !simulation) return blocking;
        for (var cycle : cycles) {
            boolean blocks = noNetOutput && !cycle.netOutput();
            for (var item : cycle.items()) {
                if (blocks) break;
                blocks = has(missing, item);
            }
            if (blocks) blocking.add(cycle);
        }
        if (blocking.isEmpty() && (noNetOutput || missing.isEmpty())) {
            for (var cycle : cycles) {
                for (var item : cycle.items()) {
                    if (has(storage, item) || has(pending, item)) {
                        blocking.add(cycle);
                        break;
                    }
                }
            }
        }
        if (blocking.isEmpty() && noNetOutput) blocking.addAll(cycles);
        return blocking;
    }

    public static void markList(Minecraft mc, CraftingCycleIndex index, List<IAEStack<?>> visible, int viewStart,
        int rows) {
        int viewEnd = Math.min(viewStart + 3 * rows, visible.size());
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int z = viewStart; z < viewEnd; z++) {
            var stack = visible.get(z);
            if (stack == null || index.cyclesWith(stack)
                .isEmpty()) continue;
            int x = (z - viewStart) % 3 * 68 + 57;
            int y = (z - viewStart) / 3 * 23 + 22;
            if (index.blocks(stack)) GL11.glColor4f(1, 0.4f, 0.4f, 1);
            else GL11.glColor4f(1, 1, 1, 1);
            RecursivePatternButton.drawIcon(mc, x - 5, y - 5, 8, 300);
        }
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopAttrib();
    }

    public static String listTooltip(String text, IAEStack<?> stack, CraftingCycleIndex index) {
        if (stack == null || index == null) return text;
        var cycles = index.cyclesWith(stack);
        if (cycles.isEmpty()) return text;
        var lines = new StringBuilder(text);
        for (var cycle : cycles) {
            lines.append('\n')
                .append(
                    StatCollector.translateToLocalFormatted(
                        cycle.netOutput() ? "gtnl.gui.recursive_patterns.tooltip.net"
                            : "gtnl.gui.recursive_patterns.tooltip.no_net",
                        index.cycles()
                            .indexOf(cycle) + 1));
        }
        int machines = 0;
        for (var match : index.matchesFor(stack)) {
            machines += match.location()
                .machines()
                .size();
        }
        lines.append('\n')
            .append(
                StatCollector.translateToLocalFormatted(
                    "gtnl.gui.recursive_patterns.tooltip.machines",
                    machines,
                    RecursivePatternReport.highlightKeyName()))
            .append('\n')
            .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.open_list"));
        return lines.toString();
    }

    // The highlight key: the cycles under the mouse, else all cycles found, else asks the server for them.
    public static void highlight(Minecraft mc, CraftingCycleIndex index, List<CraftingCycleIndex.Match> hovered) {
        if (!hovered.isEmpty()) {
            RecursivePatternReport.highlight(mc, index.request(), hovered);
        } else if (index != null) {
            RecursivePatternReport.show(mc, index.request(), index.cycles());
        } else {
            ScienceNotLeisure.network.sendToServer(new RecursivePatternQueryPacket(true));
        }
    }
}
