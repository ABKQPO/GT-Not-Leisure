package com.gtnllib;

import static com.gtnltest.TestSupport.check;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

import com.gtnllib.client.GTNLLibClient;

public abstract class LibraryTestSupport {

    public static final Path SOURCES = Paths.get("src/main/java/com/gtnllib");
    public static final Path RESOURCES = Paths.get("src/main/resources/assets/gtnllib");

    @BeforeAll
    public static void initializeLibrary() {
        ClassLoader loader = LibraryTestSupport.class.getClassLoader();
        check(
            loader.getResource("com/science/gtnl/ScienceNotLeisure.class") == null,
            "library tests must exclude GTNL");
        check(loader.getResource("gregtech/api/GregTechAPI.class") == null, "library tests must exclude GregTech");
        GTNLLibClient.connectConfig();
    }

}
