package com.science.gtnl.client.celestweave;

import static com.gtnllib.GTNLLibContractTest.check;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.gtnllib.GTNLLibContractTest;
import com.science.gtnl.ArchitectureContracts;
import com.science.gtnl.client.portalgun.PortalGunContracts;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.wireless.WirelessAutoConnectContracts;
import com.science.gtnl.common.wireless.WirelessMatterManipulatorContracts;
import com.science.gtnl.common.yogsothoth.YogSothothContracts;
import com.science.gtnl.config.ConfigMigrationContracts;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import gregtech.api.modularui2.GTGuiThemes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CelestweaveContractTest {

    public static final Path RESOURCES = Paths.get("src/main/resources/assets/sciencenotleisure");
    public static final Path SOURCES = Paths.get("src/main/java/com/science/gtnl");

    private CelestweaveContractTest() {}

    public static void main(String[] args) throws Exception {
        check(
            "gtnlEquipment".equals(EquipmentTheme.COLORS.getFullName()) && GTGuiThemes.STANDARD.getId() != null,
            "the equipment and GT widget theme keys register before any theme loads, as in preInit");
        GTNLLibContractTest.run();
        CelestweaveArmorContracts.run();
        RailgunContracts.run();
        PortalGunContracts.run();
        YogSothothContracts.run();
        EquipmentRegressionContracts.run();
        EquipmentHudContracts.run();
        EquipmentResourceContracts.run();
        WirelessMatterManipulatorContracts.run();
        WirelessAutoConnectContracts.run();
        ArchitectureContracts.run();
        ConfigMigrationContracts.run();
        GTNLLibContractTest.finish("Celestweave");
    }

    public static <T> T roundTrip(T packet, Constructor<T> empty) throws ReflectiveOperationException {
        ByteBuf buffer = Unpooled.buffer();
        ((IMessage) packet).toBytes(buffer);
        T copy = empty.newInstance();
        ((IMessage) copy).fromBytes(buffer);
        check(
            buffer.readableBytes() == 0,
            packet.getClass()
                .getSimpleName() + " leaves no trailing bytes");
        return copy;
    }
}
