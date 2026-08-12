package com.science.gtnl.common.gui.modularui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.api.stellar.StellarIrisNodeDisplay;
import com.science.gtnl.api.stellar.StellarIrisUpgradeBranch;
import com.science.gtnl.api.stellar.StellarIrisUpgradeDefinition;
import com.science.gtnl.api.stellar.StellarIrisUpgradeRegistry;
import com.science.gtnl.utils.world.stellar.StellarIrisTeamSnapshot;
import com.science.gtnl.utils.world.stellar.StellarIrisUpgradeManager;

import lombok.Getter;
import lombok.Setter;

public class StellarIrisUpgradeTreeWidget extends Widget<StellarIrisUpgradeTreeWidget> implements Interactable {

    private static final float MIN_ZOOM = 0.3F;
    private static final float MAX_ZOOM = 1.8F;
    private static final int PARTICLE_COUNT = 60;
    private static final int CIRCLE_CACHE_RADIUS = 96;
    private static final int[][] CIRCLE_HALF_WIDTHS = createCircleHalfWidths();
    private static final int[][] RING_OFFSETS = createRingOffsets();

    private final List<CosmicParticle> particles = new ArrayList<>();
    private final Map<String, NodePosition> nodePositions = new HashMap<>();
    private final Map<String, NodePosition> baseNodeOffsets = new HashMap<>();
    private final Map<String, List<StellarIrisUpgradeDefinition>> definitionsByBranch = new HashMap<>();
    private final Map<String, List<StellarIrisUpgradeDefinition>> definitionsByBranchAndRow = new HashMap<>();
    private final Random random = new Random();

    private List<StellarIrisUpgradeDefinition> definitions = new ArrayList<>();
    private StellarIrisTeamSnapshot snapshot = new StellarIrisTeamSnapshot();
    @Setter
    private StellarIrisUpgradeActionSyncHandler actionHandler;
    private String hoveredUpgradeId;
    private String selectedUpgradeId;
    private float fadeAlpha;
    private float panelSlide;
    private float viewOffsetX;
    private float viewOffsetY;
    private float zoom = 1.0F;
    private float targetZoom = 1.0F;
    private float zoomAnchorX;
    private float zoomAnchorY;
    private float panX;
    private float panY;
    private float targetPanX;
    private float targetPanY;
    private boolean centeringCamera;
    private int ticks;
    private int particleWidth = -1;
    private int particleHeight = -1;
    private int lastMouseX;
    private int lastMouseY;
    private int cachedDefinitionCount = -1;

    public StellarIrisUpgradeTreeWidget() {
        tooltipDynamic(this::buildTooltip).tooltipPos(RichTooltip.Pos.NEXT_TO_MOUSE)
            .tooltipShowUpTimer(0)
            .tooltipAutoUpdate(true);
    }

    public void setSnapshot(StellarIrisTeamSnapshot snapshot) {
        this.snapshot = snapshot == null ? new StellarIrisTeamSnapshot() : snapshot;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        ticks++;
        fadeAlpha = Math.min(1.0F, fadeAlpha + 0.08F);
        for (CosmicParticle particle : particles) {
            particle.tick();
            if (particle.isDead()) {
                particle.reset(particleWidth, particleHeight, random);
            }
        }
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int width = getArea().width;
        int height = getArea().height;
        updatePanelAnimation();
        updateZoomAnimation();
        updatePanAnimation();
        ensureDefinitionCache();
        ensureParticles(width, height);
        boolean renderedGalaxy = renderGalaxy(width, height);
        if (fadeAlpha < 0.1F) {
            return;
        }
        beginRectangleBatch();
        if (!renderedGalaxy) {
            renderGalaxyFallback(width, height);
        }
        renderParticles();
        renderVignette(width, height);
        updateNodePositions(width, height);
        renderConnections();
        renderCentralCore(width / 2.0F, height / 2.0F);
        renderNodes();
        endRectangleBatch();
        renderNodeDisplays(context, widgetTheme);
        renderNodeLabels();
        renderHeader(width);
        renderDetails(width, height);
        renderControlsHint(width, height);
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        lastMouseX = getContext().getAbsMouseX();
        lastMouseY = getContext().getAbsMouseY();
        if (mouseButton == 0 && hoveredUpgradeId != null) {
            if (hoveredUpgradeId.equals(selectedUpgradeId) && tryRequestUnlock(hoveredUpgradeId)) {
                Interactable.playButtonClickSound();
            } else {
                selectedUpgradeId = hoveredUpgradeId;
            }
            return Result.SUCCESS;
        }
        if (mouseButton == 0) {
            selectedUpgradeId = null;
            return Result.SUCCESS;
        }
        if (mouseButton == 1) {
            return Result.SUCCESS;
        }
        if (mouseButton == 2) {
            centeringCamera = true;
            return Result.SUCCESS;
        }
        return Result.IGNORE;
    }

    @Override
    public boolean onMouseScroll(UpOrDown direction, int amount) {
        float previousTargetZoom = targetZoom;
        targetZoom = MathHelper.clamp_float(targetZoom + direction.modifier * 0.15F, MIN_ZOOM, MAX_ZOOM);
        if (targetZoom != previousTargetZoom) {
            zoomAnchorX = getContext().getAbsMouseX() - getArea().x - getArea().width / 2.0F;
            zoomAnchorY = getContext().getAbsMouseY() - getArea().y - getArea().height / 2.0F;
            return true;
        }
        return false;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        int mouseX = getContext().getAbsMouseX();
        int mouseY = getContext().getAbsMouseY();
        if (mouseButton == 0 || mouseButton == 1) {
            centeringCamera = false;
            targetPanX += (mouseX - lastMouseX) / zoom;
            targetPanY += (mouseY - lastMouseY) / zoom;
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    private void ensureParticles(int width, int height) {
        if (width == particleWidth && height == particleHeight) {
            return;
        }
        particleWidth = width;
        particleHeight = height;
        particles.clear();
        for (int index = 0; index < PARTICLE_COUNT; index++) {
            particles.add(new CosmicParticle(width, height, random));
        }
    }

    private void updatePanelAnimation() {
        float target = selectedUpgradeId == null ? 0.0F : 1.0F;
        panelSlide += (target - panelSlide) * 0.22F;
        if (Math.abs(target - panelSlide) < 0.002F) {
            panelSlide = target;
        }
    }

    private void updateZoomAnimation() {
        if (zoom == targetZoom) {
            return;
        }
        float previousZoom = zoom;
        zoom += (targetZoom - zoom) * 0.24F;
        if (Math.abs(targetZoom - zoom) < 0.002F) {
            zoom = targetZoom;
        }
        float ratio = zoom / previousZoom;
        viewOffsetX = (viewOffsetX - zoomAnchorX) * ratio + zoomAnchorX;
        viewOffsetY = (viewOffsetY - zoomAnchorY) * ratio + zoomAnchorY;
    }

    private void updatePanAnimation() {
        if (centeringCamera) {
            targetPanX = 0.0F;
            targetPanY = 0.0F;
        }
        panX += (targetPanX - panX) * 0.42F;
        panY += (targetPanY - panY) * 0.42F;
        if (Math.abs(targetPanX - panX) < 0.002F) {
            panX = targetPanX;
        }
        if (Math.abs(targetPanY - panY) < 0.002F) {
            panY = targetPanY;
        }
        if (centeringCamera) {
            viewOffsetX += (0.0F - viewOffsetX) * 0.24F;
            viewOffsetY += (0.0F - viewOffsetY) * 0.24F;
            if (Math.abs(viewOffsetX) < 0.01F && Math.abs(viewOffsetY) < 0.01F && panX == 0.0F && panY == 0.0F) {
                viewOffsetX = 0.0F;
                viewOffsetY = 0.0F;
                centeringCamera = false;
            }
        }
    }

    private boolean renderGalaxy(int width, int height) {
        return StellarIrisGalaxyRenderer.render(width, height, fadeAlpha, ticks);
    }

    private void renderGalaxyFallback(int width, int height) {
        drawBatchRect(0, 0, width, height, alphaColor(0xFF03040C, fadeAlpha));
        int bandHeight = Math.max(1, height / 18);
        for (int band = 0; band < 18; band++) {
            float wave = (MathHelper.sin((ticks + band * 13) * 0.025F) + 1.0F) * 0.5F;
            int red = 7 + (int) (wave * 9.0F);
            int green = 9 + (int) (wave * 11.0F);
            int blue = 27 + (int) (wave * 34.0F);
            drawBatchRect(
                0,
                band * bandHeight,
                width,
                Math.min(height, (band + 1) * bandHeight),
                alphaColor((red << 16) | (green << 8) | blue, fadeAlpha * 0.35F));
        }
        renderNebula(width * 0.28F, height * 0.25F, Math.min(width, height) * 0.32F, 0x4F57368A);
        renderNebula(width * 0.72F, height * 0.72F, Math.min(width, height) * 0.28F, 0x4F174D75);
    }

    private void renderNebula(float centerX, float centerY, float radius, int color) {
        for (int ring = 6; ring > 0; ring--) {
            float progress = ring / 6.0F;
            int alpha = (int) (((color >>> 24) & 0xFF) * (1.0F - progress) * fadeAlpha * 0.4F);
            drawCircle(centerX, centerY, radius * progress, (alpha << 24) | (color & 0xFFFFFF));
        }
    }

    private void renderParticles() {
        for (CosmicParticle particle : particles) {
            float alpha = particle.getAlpha() * fadeAlpha;
            int size = particle.getSize();
            int color = alphaColor(particle.getColor(), alpha);
            drawBatchRect(
                (int) particle.getX(),
                (int) particle.getY(),
                (int) particle.getX() + size,
                (int) particle.getY() + size,
                color);
        }
    }

    private void renderVignette(int width, int height) {
        int strength = (int) (fadeAlpha * 120.0F);
        for (int index = 0; index < 40; index += 2) {
            int alpha = (int) (strength * (1.0F - index / 40.0F));
            int color = alpha << 24;
            drawBatchRect(0, index, width, index + 2, color);
            drawBatchRect(0, height - index - 2, width, height - index, color);
        }
    }

    private void ensureDefinitionCache() {
        int definitionCount = StellarIrisUpgradeRegistry.getUpgradeCount();
        if (cachedDefinitionCount == definitionCount) {
            return;
        }
        definitions = new ArrayList<>(StellarIrisUpgradeRegistry.getUpgrades());
        definitionsByBranch.clear();
        definitionsByBranchAndRow.clear();
        baseNodeOffsets.clear();
        for (StellarIrisUpgradeDefinition definition : definitions) {
            String branchId = definition.getBranchId();
            definitionsByBranch.computeIfAbsent(branchId, key -> new ArrayList<>())
                .add(definition);
            definitionsByBranchAndRow
                .computeIfAbsent(getNodeGroupKey(branchId, definition.getRow()), key -> new ArrayList<>())
                .add(definition);
        }
        for (StellarIrisUpgradeDefinition definition : definitions) {
            baseNodeOffsets.put(definition.getId(), calculateBaseNodeOffset(definition));
        }
        cachedDefinitionCount = definitionCount;
    }

    private void updateNodePositions(int width, int height) {
        nodePositions.clear();
        hoveredUpgradeId = null;
        float centerX = width / 2.0F;
        float centerY = height / 2.0F;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        int mouseY = getContext().getAbsMouseY() - getArea().y;
        for (StellarIrisUpgradeDefinition definition : definitions) {
            NodePosition baseOffset = baseNodeOffsets.get(definition.getId());
            NodePosition position = nodePositions.get(definition.getId());
            if (position == null) {
                position = new NodePosition();
                nodePositions.put(definition.getId(), position);
            }
            position.set(
                baseOffset.x * zoom + centerX + getCameraOffsetX(),
                baseOffset.y * zoom + centerY + getCameraOffsetY());
            float radius = getNodeRadius(definition);
            if (distance(mouseX, mouseY, position.x, position.y) <= radius * 1.2247449F) {
                hoveredUpgradeId = definition.getId();
            }
        }
    }

    private NodePosition calculateBaseNodeOffset(StellarIrisUpgradeDefinition definition) {
        StellarIrisUpgradeBranch branch = StellarIrisUpgradeRegistry.getBranch(definition.getBranchId());
        List<StellarIrisUpgradeDefinition> matchingDefinitions = branch.isRepeatable()
            ? definitionsByBranch.get(definition.getBranchId())
            : definitionsByBranchAndRow.get(getNodeGroupKey(definition.getBranchId(), definition.getRow()));
        int index = matchingDefinitions.indexOf(definition);
        int count = matchingDefinitions.size();
        float baseX;
        float baseY;
        if (branch.isRepeatable()) {
            float angle = index * ((float) Math.PI * 2.0F / count) - (float) Math.PI / 2.0F;
            baseX = MathHelper.cos(angle) * 55.0F;
            baseY = MathHelper.sin(angle) * 55.0F;
        } else {
            float angle = branch.getTreeAngle() + definition.getRow() * 0.05F;
            if (count > 1) {
                angle += (index - (count - 1) / 2.0F) * 0.38F;
            }
            float radius = 95.0F + definition.getRow() * 50.0F;
            baseX = MathHelper.cos(angle) * radius;
            baseY = MathHelper.sin(angle) * radius;
        }
        return new NodePosition(baseX, baseY);
    }

    private void renderConnections() {
        for (StellarIrisUpgradeDefinition definition : definitions) {
            NodePosition end = nodePositions.get(definition.getId());
            if (end == null) {
                continue;
            }
            for (String prerequisiteId : definition.getPrerequisiteIds()) {
                NodePosition start = nodePositions.get(prerequisiteId);
                if (start == null) {
                    continue;
                }
                boolean prerequisiteOwned = snapshot.getLevel(prerequisiteId) > 0;
                boolean bothOwned = prerequisiteOwned && snapshot.getLevel(definition.getId()) > 0;
                int alpha = bothOwned ? (int) (fadeAlpha * 180.0F)
                    : prerequisiteOwned ? (int) (fadeAlpha * 80.0F) : (int) (fadeAlpha * 30.0F);
                drawConnection(start, end, alphaColor(getBranchColor(definition), alpha / 255.0F), bothOwned);
            }
        }
    }

    private void renderCentralCore(float centerX, float centerY) {
        float coreX = centerX + getCameraOffsetX();
        float coreY = centerY + getCameraOffsetY();
        int radius = (int) (45.0F * zoom * (MathHelper.sin(ticks * 0.06F) * 0.1F + 1.0F));
        for (int currentRadius = radius + 25; currentRadius > radius; currentRadius -= 3) {
            float progress = (currentRadius - radius) / 25.0F;
            drawCircle(
                coreX,
                coreY,
                currentRadius,
                alphaColor(0xFFCC44, (1.0F - progress) * 40.0F / 255.0F * fadeAlpha));
        }
        for (int currentRadius = radius; currentRadius > 0; currentRadius -= 2) {
            float progress = currentRadius / (float) radius;
            int red = (int) (255.0F * progress + 200.0F * (1.0F - progress));
            int green = (int) (200.0F * progress + 150.0F * (1.0F - progress));
            int blue = (int) (100.0F * progress + 50.0F * (1.0F - progress));
            drawCircle(
                coreX,
                coreY,
                currentRadius,
                alphaColor((red << 16) | (green << 8) | blue, (0.5F + 0.5F * progress) * fadeAlpha));
        }
    }

    private void renderNodes() {
        for (StellarIrisUpgradeDefinition definition : definitions) {
            NodePosition position = nodePositions.get(definition.getId());
            if (position == null) {
                continue;
            }
            int level = snapshot.getLevel(definition.getId());
            boolean owned = level > 0;
            boolean tierLocked = snapshot.getTier() < getRequiredTier(definition);
            boolean available = StellarIrisUpgradeManager.canUnlock(snapshot, definition)
                && snapshot.getSpendablePoints() >= definition.getCostForLevel(level + 1);
            boolean hovered = definition.getId()
                .equals(hoveredUpgradeId);
            boolean selected = definition.getId()
                .equals(selectedUpgradeId);
            float stateAlpha = owned ? 1.0F : available ? hovered ? 0.9F : 0.6F : tierLocked ? 0.25F : 0.4F;
            float pulse = owned || available || hovered || selected ? 1.0F + MathHelper.sin(
                ticks * 0.08F + definition.getId()
                    .hashCode())
                * 0.12F : 1.0F;
            int radius = (int) (getNodeRadius(definition) * pulse);
            int branchColor = getBranchColor(definition);
            if (owned || available || selected) {
                for (int glowRadius = radius + 14; glowRadius > radius; glowRadius -= 3) {
                    float progress = (glowRadius - radius) / 14.0F;
                    drawNodeCircle(
                        position.x,
                        position.y,
                        glowRadius,
                        alphaColor(branchColor, (1.0F - progress) * 60.0F / 255.0F * fadeAlpha * stateAlpha));
                }
            }
            int coreColor = tierLocked ? 0x282828 : owned ? branchColor : darken(branchColor, 0.4F);
            drawNodeCircle(position.x, position.y, radius, alphaColor(coreColor, fadeAlpha * stateAlpha));
            int borderColor = owned ? branchColor
                : available ? darken(
                    branchColor,
                    MathHelper.sin(
                        ticks * 0.08F + definition.getId()
                            .hashCode())
                        * 0.3F + 0.7F)
                    : 0x404050;
            drawNodeRing(
                position.x,
                position.y,
                radius,
                alphaColor(borderColor, fadeAlpha * (hovered || selected ? 1.0F : 150.0F / 255.0F)));
            if (selected) {
                float rotation = ticks * 0.05F;
                for (int index = 0; index < 8; index++) {
                    float angle = rotation + index * (float) Math.PI / 4.0F;
                    float ringX = position.x + MathHelper.cos(angle) * (radius + 6);
                    float ringY = position.y + MathHelper.sin(angle) * (radius + 6);
                    drawBatchRect(
                        (int) ringX - 1,
                        (int) ringY - 1,
                        (int) ringX + 2,
                        (int) ringY + 2,
                        alphaColor(0xFFFFFF, fadeAlpha * 200.0F / 255.0F));
                }
            }
        }
    }

    private void renderNodeLabels() {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        for (StellarIrisUpgradeDefinition definition : definitions) {
            NodePosition position = nodePositions.get(definition.getId());
            if (position == null) {
                continue;
            }
            StellarIrisNodeDisplay nodeDisplay = definition.getNodeDisplay();
            if (nodeDisplay != null && !nodeDisplay.isText()) {
                continue;
            }
            int level = snapshot.getLevel(definition.getId());
            boolean owned = level > 0;
            boolean tierLocked = snapshot.getTier() < getRequiredTier(definition);
            boolean available = StellarIrisUpgradeManager.canUnlock(snapshot, definition)
                && snapshot.getSpendablePoints() >= definition.getCostForLevel(level + 1);
            float stateAlpha = owned ? 1.0F
                : available ? definition.getId()
                    .equals(hoveredUpgradeId) ? 0.9F : 0.6F : tierLocked ? 0.25F : 0.4F;
            String label = nodeDisplay == null
                ? owned ? "\u2713"
                    : tierLocked ? "T" + getRequiredTier(definition)
                        : Integer.toString(definition.getCostForLevel(level + 1))
                : nodeDisplay.getText();
            int textColor = owned ? 0xFFFFFF : tierLocked ? 0x505050 : available ? 0xFFCC44 : 0x606060;
            font.drawString(
                label,
                (int) position.x - font.getStringWidth(label) / 2,
                (int) position.y - (owned ? 4 : 3),
                alphaColor(textColor, fadeAlpha * stateAlpha));
        }
    }

    private void renderNodeDisplays(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (StellarIrisUpgradeDefinition definition : definitions) {
            StellarIrisNodeDisplay nodeDisplay = definition.getNodeDisplay();
            NodePosition position = nodePositions.get(definition.getId());
            if (nodeDisplay == null || nodeDisplay.isText() || position == null) {
                continue;
            }
            IDrawable icon = nodeDisplay.getIcon();
            if (icon == null) {
                continue;
            }
            int level = snapshot.getLevel(definition.getId());
            boolean tierLocked = snapshot.getTier() < getRequiredTier(definition);
            boolean available = StellarIrisUpgradeManager.canUnlock(snapshot, definition)
                && snapshot.getSpendablePoints() >= definition.getCostForLevel(level + 1);
            float stateAlpha = level > 0 ? 1.0F : available ? 0.7F : tierLocked ? 0.25F : 0.4F;
            int size = Math.max(8, Math.min(16, (int) getNodeRadius(definition)));
            int x = (int) position.x - size / 2;
            int y = (int) position.y - size / 2;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, fadeAlpha * stateAlpha);
            icon.draw(context, x, y, size, size, widgetTheme.getTheme());
        }
        GL11.glPopAttrib();
    }

    private void renderHeader(int width) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String points = StatCollector
            .translateToLocalFormatted("gtnl.stellar_iris.points", snapshot.getSpendablePoints());
        String tier = StatCollector.translateToLocalFormatted("gtnl.stellar_iris.tier", snapshot.getTier());
        String title = StatCollector.translateToLocal("gtnl.stellar_iris.title");
        font.drawString(points, 20, 20, alphaColor(snapshot.getSpendablePoints() > 0 ? 0xFFCC44 : 0x808080, fadeAlpha));
        font.drawString(tier, 20, 34, alphaColor(0xAAAAAA, fadeAlpha));
        font.drawString(title, (width - font.getStringWidth(title)) / 2, 15, alphaColor(0xE0E0F0, fadeAlpha));
    }

    private void renderDetails(int width, int height) {
        if (selectedUpgradeId == null || panelSlide <= 0.01F) {
            return;
        }
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(selectedUpgradeId);
        if (definition == null) {
            return;
        }
        int panelWidth = 208;
        int panelHeight = 172;
        int panelX = width - (int) (panelSlide * (panelWidth + 15));
        int panelY = (height - panelHeight) / 2;
        int branchColor = getBranchColor(definition);
        Gui.drawRect(
            panelX,
            panelY,
            panelX + panelWidth,
            panelY + panelHeight,
            alphaColor(0x0C0C14, fadeAlpha * panelSlide * 230.0F / 255.0F));
        drawPanelBorder(panelX, panelY, panelWidth, panelHeight, alphaColor(branchColor, fadeAlpha * panelSlide));
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int textX = panelX + 10;
        int textY = panelY + 10;
        String name = StatCollector.translateToLocal(definition.getTranslationKey());
        font.drawString(name, textX, textY, alphaColor(branchColor, fadeAlpha * panelSlide));
        int level = snapshot.getLevel(definition.getId());
        String description = StatCollector.translateToLocal(definition.getDescriptionKey());
        int infoY = panelY + 34;
        if (!description.equals(definition.getDescriptionKey()) && !description.isEmpty()) {
            List<String> lines = font.listFormattedStringToWidth(description, panelWidth - 20);
            for (int index = 0; index < lines.size(); index++) {
                font.drawString(
                    lines.get(index),
                    textX,
                    panelY + 28 + index * 10,
                    alphaColor(0x9090A0, fadeAlpha * panelSlide));
            }
            infoY = panelY + 34 + lines.size() * 10;
        }
        if (definition.isRepeatable()) {
            renderRepeatableDetails(definition, level, textX, infoY, panelY + panelHeight - 24, fadeAlpha * panelSlide);
            return;
        }
        int cost = definition.getCostForLevel(level + 1);
        boolean canAfford = snapshot.getSpendablePoints() >= cost;
        font.drawString(
            StatCollector.translateToLocalFormatted("gtnl.stellar_iris.cost", cost),
            textX,
            infoY,
            alphaColor(canAfford ? 0xFFCC44 : 0xFF5544, fadeAlpha * panelSlide));
        boolean tierMet = snapshot.getTier() >= getRequiredTier(definition);
        font.drawString(
            StatCollector.translateToLocalFormatted("gtnl.stellar_iris.tier_requirement", getRequiredTier(definition)),
            textX,
            infoY + 14,
            alphaColor(tierMet ? 0x707080 : 0xFF5544, fadeAlpha * panelSlide));
        font.drawString(
            getStatus(definition, level),
            textX,
            panelY + panelHeight - 24,
            alphaColor(getStatusColor(definition, level), fadeAlpha * panelSlide));
    }

    private void renderRepeatableDetails(StellarIrisUpgradeDefinition definition, int level, int textX, int infoY,
        int statusY, float alpha) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        boolean maxed = level >= definition.getMaxLevel();
        String levelText = StatCollector
            .translateToLocalFormatted("gtnl.stellar_iris.level", level, definition.getMaxLevel());
        if (maxed) {
            levelText = levelText + " (" + StatCollector.translateToLocal("gtnl.stellar_iris.max_level") + ")";
        }
        font.drawString(levelText, textX, infoY, alphaColor(maxed ? 0x44FF44 : 0xAAAAAA, alpha));
        if (maxed) {
            font.drawString(
                StatCollector.translateToLocal("gtnl.stellar_iris.max_level"),
                textX,
                statusY,
                alphaColor(0x44FF44, alpha));
            return;
        }
        int nextCost = definition.getCostForLevel(level + 1);
        boolean canAfford = snapshot.getSpendablePoints() >= nextCost;
        font.drawString(
            StatCollector.translateToLocalFormatted("gtnl.stellar_iris.next_level", nextCost),
            textX,
            infoY + 14,
            alphaColor(canAfford ? 0xFFCC44 : 0xFF5544, alpha));
        font.drawString(
            StatCollector
                .translateToLocal(canAfford ? "gtnl.stellar_iris.available" : "gtnl.stellar_iris.insufficient_points"),
            textX,
            statusY,
            alphaColor(canAfford ? 0x88FF88 : 0xFF8844, alpha));
    }

    private String getStatus(StellarIrisUpgradeDefinition definition, int level) {
        if (level > 0) {
            return StatCollector.translateToLocal("gtnl.stellar_iris.unlocked");
        }
        if (snapshot.getTier() < getRequiredTier(definition)) {
            return StatCollector.translateToLocal("gtnl.stellar_iris.tier_locked");
        }
        if (snapshot.getSpendablePoints() < definition.getCostForLevel(level + 1)) {
            return StatCollector.translateToLocal("gtnl.stellar_iris.insufficient_points");
        }
        if (StellarIrisUpgradeManager.canUnlock(snapshot, definition)) {
            return StatCollector.translateToLocal("gtnl.stellar_iris.available");
        }
        return StatCollector.translateToLocal("gtnl.stellar_iris.prerequisites_needed");
    }

    private boolean tryRequestUnlock(String upgradeId) {
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(upgradeId);
        if (definition == null || actionHandler == null || !StellarIrisUpgradeManager.canUnlock(snapshot, definition)) {
            return false;
        }
        int level = snapshot.getLevel(upgradeId);
        if (snapshot.getSpendablePoints() < definition.getCostForLevel(level + 1)) {
            return false;
        }
        actionHandler.requestUnlock(upgradeId);
        return true;
    }

    private void buildTooltip(RichTooltip tooltip) {
        if (hoveredUpgradeId == null || hoveredUpgradeId.equals(selectedUpgradeId)) {
            return;
        }
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(hoveredUpgradeId);
        if (definition != null) {
            tooltip.add(getTooltipColor(definition) + StatCollector.translateToLocal(definition.getTranslationKey()));
        }
    }

    private void renderControlsHint(int width, int height) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int color = alphaColor(0x606070, fadeAlpha * 100.0F / 255.0F);
        String panHint = StatCollector.translateToLocal("gtnl.stellar_iris.controls.pan_zoom");
        font.drawString(StatCollector.translateToLocal("gtnl.stellar_iris.controls.close"), 20, height - 25, color);
        font.drawString(panHint, width - font.getStringWidth(panHint) - 20, height - 25, color);
    }

    private void drawPanelBorder(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + 2, color);
        Gui.drawRect(x, y, x + 2, y + height, color);
        Gui.drawRect(x + width - 2, y, x + width, y + height, color);
        Gui.drawRect(x, y + height - 2, x + width, y + height, color);
    }

    private void beginRectangleBatch() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator.instance.startDrawingQuads();
    }

    private void endRectangleBatch() {
        Tessellator.instance.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawBatchRect(int left, int top, int right, int bottom, int color) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorRGBA((color >> 16) & 255, (color >> 8) & 255, color & 255, color >>> 24);
        tessellator.addVertex(right, top, 0.0D);
        tessellator.addVertex(left, top, 0.0D);
        tessellator.addVertex(left, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
    }

    private int getStatusColor(StellarIrisUpgradeDefinition definition, int level) {
        if (level > 0) {
            return 0x44FF44;
        }
        if (snapshot.getTier() < getRequiredTier(definition)) {
            return 0xFF5544;
        }
        if (StellarIrisUpgradeManager.canUnlock(snapshot, definition)) {
            return snapshot.getSpendablePoints() >= definition.getCostForLevel(level + 1) ? 0x88FF88 : 0xFF8844;
        }
        return 0x888888;
    }

    private int getRequiredTier(StellarIrisUpgradeDefinition definition) {
        return definition.isRepeatable() ? 0 : Math.max(0, definition.getRow() - 1);
    }

    private String getNodeGroupKey(String branchId, int row) {
        return branchId + ':' + row;
    }

    private int getBranchColor(StellarIrisUpgradeDefinition definition) {
        String branchId = definition.getBranchId();
        if ("ignition".equals(branchId)) {
            return 0xFF783C;
        }
        if ("fusion".equals(branchId)) {
            return 0x50B4FF;
        }
        if ("collapse".equals(branchId)) {
            return 0xB450DC;
        }
        if ("void".equals(branchId)) {
            return 0x50FFB4;
        }
        return 0xDCC878;
    }

    private EnumChatFormatting getTooltipColor(StellarIrisUpgradeDefinition definition) {
        String branchId = definition.getBranchId();
        if ("ignition".equals(branchId)) {
            return EnumChatFormatting.GOLD;
        }
        if ("fusion".equals(branchId)) {
            return EnumChatFormatting.AQUA;
        }
        if ("collapse".equals(branchId)) {
            return EnumChatFormatting.LIGHT_PURPLE;
        }
        if ("void".equals(branchId)) {
            return EnumChatFormatting.GREEN;
        }
        return EnumChatFormatting.YELLOW;
    }

    private int darken(int color, float factor) {
        int red = (int) (((color >> 16) & 255) * factor);
        int green = (int) (((color >> 8) & 255) * factor);
        int blue = (int) ((color & 255) * factor);
        return (red << 16) | (green << 8) | blue;
    }

    private float getCameraOffsetX() {
        return viewOffsetX + panX * zoom;
    }

    private float getCameraOffsetY() {
        return viewOffsetY + panY * zoom;
    }

    private float getNodeRadius(StellarIrisUpgradeDefinition definition) {
        if (definition.isRepeatable()) {
            return 12.0F * zoom;
        }
        if (definition.getRow() == 11) {
            return 18.0F * zoom;
        }
        return (definition.getRow() == 5 ? 15.0F : 14.0F) * zoom;
    }

    private float distance(float firstX, float firstY, float secondX, float secondY) {
        float deltaX = firstX - secondX;
        float deltaY = firstY - secondY;
        return MathHelper.sqrt_float(deltaX * deltaX + deltaY * deltaY);
    }

    private void drawConnection(NodePosition from, NodePosition to, int color, boolean animated) {
        int startX = (int) from.x;
        int startY = (int) from.y;
        int endX = (int) to.x;
        int endY = (int) to.y;
        int steps = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
        if (steps == 0) {
            return;
        }
        for (int index = 0; index <= steps; index += 6) {
            int x = startX + (endX - startX) * index / steps;
            int y = startY + (endY - startY) * index / steps;
            drawBatchRect(x, y, x + 2, y + 2, color);
        }
        if (animated) {
            float progress = (ticks % 40) / 40.0F;
            int pulseX = (int) (startX + (endX - startX) * progress);
            int pulseY = (int) (startY + (endY - startY) * progress);
            drawBatchRect(
                pulseX - 2,
                pulseY - 2,
                pulseX + 3,
                pulseY + 3,
                alphaColor(0xFFFFFF, fadeAlpha * 220.0F / 255.0F));
        }
    }

    private void drawCircle(float centerX, float centerY, float radius, int color) {
        if (radius <= 0.0F) {
            return;
        }
        int integerRadius = (int) radius;
        if (radius == integerRadius && integerRadius <= CIRCLE_CACHE_RADIUS) {
            for (int y = -integerRadius; y <= integerRadius; y += 2) {
                int halfWidth = CIRCLE_HALF_WIDTHS[integerRadius][y + integerRadius];
                drawBatchRect(
                    (int) centerX - halfWidth,
                    (int) centerY + y,
                    (int) centerX + halfWidth + 1,
                    (int) centerY + y + 2,
                    color);
            }
            return;
        }
        for (int y = -integerRadius; y <= radius; y += 2) {
            int halfWidth = (int) Math.sqrt(radius * radius - y * y);
            drawBatchRect(
                (int) centerX - halfWidth,
                (int) centerY + y,
                (int) centerX + halfWidth + 1,
                (int) centerY + y + 2,
                color);
        }
    }

    private void drawNodeCircle(float centerX, float centerY, int radius, int color) {
        if (radius <= 0) {
            return;
        }
        for (int y = -radius; y <= radius; y++) {
            int halfWidth = radius <= CIRCLE_CACHE_RADIUS ? CIRCLE_HALF_WIDTHS[radius][y + radius]
                : (int) Math.sqrt(radius * radius - y * y);
            drawBatchRect(
                (int) centerX - halfWidth,
                (int) centerY + y,
                (int) centerX + halfWidth + 1,
                (int) centerY + y + 1,
                color);
        }
    }

    private void drawNodeRing(float centerX, float centerY, int radius, int color) {
        if (radius <= CIRCLE_CACHE_RADIUS) {
            int[] offsets = RING_OFFSETS[radius];
            for (int index = 0; index < offsets.length; index += 2) {
                int x = (int) centerX + offsets[index];
                int y = (int) centerY + offsets[index + 1];
                drawBatchRect(x, y, x + 1, y + 1, color);
            }
            return;
        }
        for (int angleIndex = 0; angleIndex < 360; angleIndex += 8) {
            float angle = angleIndex * (float) Math.PI / 180.0F;
            int x = (int) centerX + (int) (MathHelper.cos(angle) * radius);
            int y = (int) centerY + (int) (MathHelper.sin(angle) * radius);
            drawBatchRect(x, y, x + 1, y + 1, color);
        }
    }

    private int alphaColor(int rgb, float alpha) {
        int clampedAlpha = Math.max(0, Math.min(255, (int) (alpha * 255.0F)));
        return (clampedAlpha << 24) | (rgb & 0xFFFFFF);
    }

    private static int[][] createCircleHalfWidths() {
        int[][] halfWidths = new int[CIRCLE_CACHE_RADIUS + 1][];
        for (int radius = 0; radius <= CIRCLE_CACHE_RADIUS; radius++) {
            halfWidths[radius] = new int[radius * 2 + 1];
            for (int y = -radius; y <= radius; y++) {
                halfWidths[radius][y + radius] = (int) Math.sqrt(radius * radius - y * y);
            }
        }
        return halfWidths;
    }

    private static int[][] createRingOffsets() {
        int[][] offsetsByRadius = new int[CIRCLE_CACHE_RADIUS + 1][];
        for (int radius = 0; radius <= CIRCLE_CACHE_RADIUS; radius++) {
            int[] offsets = new int[90];
            for (int angleIndex = 0, index = 0; angleIndex < 360; angleIndex += 8) {
                float angle = angleIndex * (float) Math.PI / 180.0F;
                offsets[index++] = (int) (MathHelper.cos(angle) * radius);
                offsets[index++] = (int) (MathHelper.sin(angle) * radius);
            }
            offsetsByRadius[radius] = offsets;
        }
        return offsetsByRadius;
    }

    public static class NodePosition {

        private float x;
        private float y;

        public NodePosition() {}

        public NodePosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class CosmicParticle {

        @Getter
        private float x;
        @Getter
        private float y;
        private float velocityX;
        private float velocityY;
        private int age;
        private int maximumAge;
        @Getter
        private int color;
        @Getter
        private int size;

        public CosmicParticle(int width, int height, Random random) {
            reset(width, height, random);
        }

        public void tick() {
            x += velocityX;
            y += velocityY;
            age++;
        }

        public boolean isDead() {
            return age >= maximumAge || x < -4.0F || y < -4.0F || x > 8196.0F || y > 8196.0F;
        }

        public void reset(int width, int height, Random random) {
            x = random.nextFloat() * Math.max(1, width);
            y = random.nextFloat() * Math.max(1, height);
            velocityX = (random.nextFloat() - 0.5F) * 0.25F;
            velocityY = (random.nextFloat() - 0.5F) * 0.25F;
            age = 0;
            maximumAge = 100 + random.nextInt(160);
            color = random.nextBoolean() ? 0xB7DFFF : 0x9B8FFF;
            size = random.nextInt(3) + 1;
        }

        public float getAlpha() {
            float progress = age / (float) maximumAge;
            return Math.min(1.0F, progress * 6.0F) * Math.min(1.0F, (1.0F - progress) * 6.0F);
        }

    }
}
