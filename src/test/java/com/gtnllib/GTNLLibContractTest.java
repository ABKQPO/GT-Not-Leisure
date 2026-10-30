package com.gtnllib;

import static com.gtnltest.ContractAssertions.finish;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.gtnllib.client.GTNLLibClient;
import com.gtnllib.gui.widget.StepSlider;

public class GTNLLibContractTest {

    public static final Path SOURCES = Paths.get("src/main/java/com/gtnllib");
    public static final Path RESOURCES = Paths.get("src/main/resources/assets/gtnllib");

    public static void main(String[] args) throws Exception {
        run();
        finish("GTNLLib");
    }

    public static void run() throws Exception {
        GTNLLibClient.connectConfig();
        HudContracts.run();
        WheelContracts.run();
        LayoutContracts.run();
        ThemeContracts.run();
        BoundaryContracts.run();
        FxContracts.run();
        RegistryContracts.run();
    }

    public static boolean compactDrawable(String text) throws ReflectiveOperationException {
        Field chars = StepSlider.class.getDeclaredField("COMPACT_CHARS");
        chars.setAccessible(true);
        String charset = (String) chars.get(null);
        return text.chars()
            .allMatch(c -> charset.indexOf(c) >= 0);
    }

}
