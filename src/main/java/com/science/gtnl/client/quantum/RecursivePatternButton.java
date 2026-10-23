package com.science.gtnl.client.quantum;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.science.gtnl.ScienceNotLeisure;

import appeng.client.gui.ScreenColor;
import appeng.client.gui.widgets.ITooltip;
import appeng.client.texture.ExtraBlockTextures;

/**
 * Asks where the crafting cycles of a request are. It pulses while the calculation reports a cycle without net output.
 */
public class RecursivePatternButton extends GuiButton implements ITooltip {

    public static final ResourceLocation ICON = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        "textures/gui/crafting_ring.png");

    private boolean attention;

    public RecursivePatternButton(int x, int y) {
        super(0, x, y, 16, 16, "");
    }

    public void setAttention(boolean attention) {
        this.attention = attention;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.visible) return;
        this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
            && mouseX < this.xPosition + this.width
            && mouseY < this.yPosition + this.height;

        if (this.attention && System.currentTimeMillis() / 400 % 2 == 0) {
            drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + 17, this.yPosition + 17, 0xFFFF5050);
        }
        ScreenColor.setGuiColor();
        minecraft.renderEngine.bindTexture(ExtraBlockTextures.GuiTexture("guis/states.png"));
        this.drawTexturedModalRect(this.xPosition, this.yPosition, 256 - 16, 256 - 16, 16, 16);
        ScreenColor.resetGuiColor();
        drawIcon(minecraft, this.xPosition, this.yPosition, 16, this.zLevel);
        this.mouseDragged(minecraft, mouseX, mouseY);
    }

    public static void drawIcon(Minecraft minecraft, double x, double y, double size, double z) {
        minecraft.renderEngine.bindTexture(ICON);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + size, z, 0, 1);
        tessellator.addVertexWithUV(x + size, y + size, z, 1, 1);
        tessellator.addVertexWithUV(x + size, y, z, 1, 0);
        tessellator.addVertexWithUV(x, y, z, 0, 0);
        tessellator.draw();
    }

    @Override
    public String getMessage() {
        final String hint = StatCollector.translateToLocal("gtnl.gui.recursive_patterns.hint");
        final List<String> lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(hint, 180);
        return StatCollector.translateToLocal("gtnl.gui.recursive_patterns") + '\n' + String.join("\n", lines);
    }

    @Override
    public int xPos() {
        return this.xPosition;
    }

    @Override
    public int yPos() {
        return this.yPosition;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }
}
