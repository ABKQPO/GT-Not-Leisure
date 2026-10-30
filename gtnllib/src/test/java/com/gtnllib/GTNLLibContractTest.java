package com.gtnllib;

import static com.gtnltest.ContractAssertions.check;
import static com.gtnltest.ContractAssertions.finish;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.gtnllib.client.GTNLLibClient;

public class GTNLLibContractTest {

    public static final Path SOURCES = Paths.get("src/main/java/com/gtnllib");
    public static final Path RESOURCES = Paths.get("src/main/resources/assets/gtnllib");

    public static void main(String[] args) throws Exception {
        run();
        finish("GTNLLib");
    }

    public static void run() throws Exception {
        ClassLoader loader = GTNLLibContractTest.class.getClassLoader();
        check(
            loader.getResource("com/science/gtnl/ScienceNotLeisure.class") == null,
            "library contracts run without GTNL classes");
        check(
            loader.getResource("gregtech/api/GregTechAPI.class") == null,
            "library contracts run without GregTech classes");
        GTNLLibClient.connectConfig();
        HudContracts.run();
        WheelContracts.run();
        LayoutContracts.run();
        ThemeContracts.run();
        BoundaryContracts.run();
        FxContracts.run();
        RegistryContracts.run();
    }

}
