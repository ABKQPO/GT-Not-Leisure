package com.science.gtnl;

import static com.gtnltest.TestSupport.check;

import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.client.Minecraft;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.gtnllib.network.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Overrides do not inherit SideOnly from the packet base class. */
@Tag("architecture")
public final class PacketSideTest {

    @Test
    void removesClientPacketHandlersFromTheServerClasspath() throws Exception {
        int checked = 0;
        try (var sources = Files.list(Path.of("src/main/java/com/science/gtnl/common/packet"))) {
            for (Path source : sources.filter(
                file -> file.toString()
                    .endsWith(".java"))
                .toList()) {
                String name = source.getFileName()
                    .toString()
                    .replace(".java", "");
                Class<?> type = Class
                    .forName("com.science.gtnl.common.packet." + name, false, PacketSideTest.class.getClassLoader());
                checked += checkHandlers(type);
            }
        }
        check(checked > 0, "clientbound packet side checks discover actual packet classes");
    }

    private static int checkHandlers(Class<?> type) throws NoSuchMethodException {
        int checked = 0;
        if (ClientboundPacket.class.isAssignableFrom(type)) {
            var handler = type.getMethod("handleClient", Minecraft.class);
            SideOnly side = handler.getDeclaredAnnotation(SideOnly.class);
            check(
                side != null && side.value() == Side.CLIENT,
                type.getName() + ".handleClient must be stripped from a dedicated server, including overrides");
            checked++;
        }
        // A serverbound packet may declare its clientbound reply as a nested class.
        for (Class<?> nested : type.getDeclaredClasses()) checked += checkHandlers(nested);
        return checked;
    }
}
