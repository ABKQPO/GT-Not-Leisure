package com.science.gtnl.common.wireless;

import static com.gtnltest.TestSupport.check;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

@Tag("wireless")
public class WirelessCardViewTest {

    @TestFactory
    Stream<DynamicTest> scenarios() {
        return Stream.of(
            DynamicTest.dynamicTest("visualisationModes", WirelessCardViewTest::visualisationModes),
            DynamicTest.dynamicTest("layout", WirelessCardViewTest::layout));
    }

    private static void visualisationModes() {
        int last = WirelessCardVisualisation.MODE_COUNT - 1;
        check(
            WirelessCardVisualisation.step(0, -1) == last && WirelessCardVisualisation.step(last, 1) == 0
                && WirelessCardVisualisation.step(3, 1) == 4,
            "the view mode steps both ways and wraps between off and the last mode");
        check(
            WirelessCardVisualisation.validMode(0) && WirelessCardVisualisation.validMode(last)
                && !WirelessCardVisualisation.validMode(WirelessCardVisualisation.MODE_COUNT)
                && !WirelessCardVisualisation.validMode(-1),
            "a view mode picked from the menu is accepted only inside the known modes");
    }

    private static void layout() {
        var narrow = WirelessCardLayout.forScreen(332, 244);
        var wide = WirelessCardLayout.forScreen(640, 360);
        check(
            narrow.width() == 320 && narrow.visualisationWidth() == 70
                && narrow.visualisationX() + narrow.visualisationWidth() == narrow.width() - 34,
            "the narrowest window keeps the view dropdown beside the refresh icon");
        check(
            wide.width() == 520 && wide.rows() == WirelessCardLayout.MAX_ROWS && wide.visualisationWidth() == 120,
            "a 1080p screen at GUI scale 3 shows the widest window with every row");
    }
}
