package com.science.gtnl.client.celestweave;

import static com.gtnltest.TestSupport.check;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

import com.cleanroommc.modularui.drawable.DrawableSerialization;
import com.gtnllib.client.GTNLLibClient;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import gregtech.api.modularui2.GTGuiThemes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class EquipmentTestSupport {

    public static final Path RESOURCES = Paths.get("src/main/resources/assets/sciencenotleisure");
    public static final Path SOURCES = Paths.get("src/main/java/com/science/gtnl");

    @BeforeAll
    public static void initializeEquipment() throws Exception {
        check(
            "gtnlEquipment".equals(EquipmentTheme.COLORS.getFullName()) && GTGuiThemes.STANDARD.getId() != null,
            "the equipment and GT widget theme keys register before any theme loads, as in preInit");
        GTNLLibClient.connectConfig();
        // Production registers these during MUI initialization; do not rely on ThemeTest running first.
        DrawableSerialization.init();
    }

    public static <T> T roundTrip(T packet, Constructor<T> empty) throws ReflectiveOperationException {
        ByteBuf buffer = Unpooled.buffer();
        try {
            ((IMessage) packet).toBytes(buffer);
            T copy = empty.newInstance();
            ((IMessage) copy).fromBytes(buffer);
            check(
                buffer.readableBytes() == 0,
                packet.getClass()
                    .getSimpleName() + " leaves no trailing bytes");
            return copy;
        } finally {
            buffer.release();
        }
    }
}
