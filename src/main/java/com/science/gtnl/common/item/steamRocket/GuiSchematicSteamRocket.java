package com.science.gtnl.common.item.steamRocket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import galaxyspace.GalaxySpace;
import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class GuiSchematicSteamRocket extends GuiContainerGC implements ISchematicResultPage {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(
        GalaxySpace.ASSET_PREFIX,
        "textures/gui/schematic_rocket_GS1_T1.png");
    private int pageIndex;

    public GuiSchematicSteamRocket(InventoryPlayer inventoryPlayer, int x, int y, int z) {
        super(new ContainerSchematicSteamRocket(inventoryPlayer, x, y, z));
        this.ySize = 221;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiButton backButton;
        this.buttonList.add(
            backButton = new GuiButton(
                0,
                this.width / 2 - 130,
                this.height / 2 - 30 + 27 - 12,
                40,
                20,
                GCCoreUtil.translate("gui.button.back.name")));
        this.buttonList.add(
            new GuiButton(
                1,
                this.width / 2 - 130,
                this.height / 2 - 30 + 27 + 12,
                40,
                20,
                GCCoreUtil.translate("gui.button.next.name")));
        backButton.enabled = true;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.enabled) {
            switch (button.id) {
                case 0: {
                    SchematicRegistry.flipToLastPage(this.pageIndex);
                    break;
                }
                case 1: {
                    SchematicRegistry.flipToNextPage(this.pageIndex);
                    break;
                }
            }
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj
            .drawString(EnumColor.WHITE + GCCoreUtil.translate("schematic.rocketSteam.name"), 7, 7, 0x404040);
        this.fontRendererObj
            .drawString(EnumColor.WHITE + GCCoreUtil.translate("container.inventory"), 14, 128, 0x404040);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiSchematicSteamRocket.GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void setPageIndex(int index) {
        this.pageIndex = index;
    }
}
