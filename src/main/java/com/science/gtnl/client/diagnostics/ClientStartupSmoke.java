package com.science.gtnl.client.diagnostics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/** Opt-in CI probe; ordinary client launches neither register it nor exit automatically. */
@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT)
public final class ClientStartupSmoke {

    private static Path report;
    private static int menuTicks;

    @EventBusSubscriber.Condition
    public static boolean requested() {
        String path = System.getenv("GTNL_CLIENT_SMOKE");
        if (path == null || path.isEmpty()) return false;
        report = Path.of(path);
        return true;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft client = Minecraft.getMinecraft();
        if (!(client.currentScreen instanceof GuiMainMenu)) {
            menuTicks = 0;
            return;
        }
        if (++menuTicks != 40) return;
        try {
            List<String> lines = new ObjectArrayList<>();
            inspect("blocks", client.getTextureMapBlocks(), lines);
            inspect(
                "items",
                (TextureMap) client.getTextureManager()
                    .getTexture(TextureMap.locationItemsTexture),
                lines);
            Collections.sort(lines);
            if (report.getParent() != null) Files.createDirectories(report.getParent());
            Files.write(report, lines, StandardCharsets.UTF_8);
            ScienceNotLeisure.LOG.info("GTNL_CLIENT_SMOKE_READY");
        } catch (IOException | RuntimeException exception) {
            ScienceNotLeisure.LOG.error("GTNL_CLIENT_SMOKE_FAILED", exception);
        } finally {
            client.shutdown();
        }
    }

    private static void inspect(String name, TextureMap atlas, List<String> lines) {
        Map<String, TextureAtlasSprite> registered = ReflectionHelper
            .getPrivateValue(TextureMap.class, atlas, "mapRegisteredSprites", "field_110574_e");
        TextureAtlasSprite missing = atlas.getAtlasSprite("missingno");
        int checked = 0;
        for (String sprite : registered.keySet()) {
            if (!sprite.startsWith("sciencenotleisure:") && !sprite.startsWith("gtnllib:")
                && !sprite.startsWith("reavaritia:")) continue;
            checked++;
            if (atlas.getAtlasSprite(sprite) == missing) lines.add("atlas " + name + " " + sprite);
        }
        if (checked == 0) throw new IllegalStateException("No owned sprites registered in " + name);
        lines.add("checked " + name + " " + checked);
    }
}
