package me.andromedov.mythicskript.classes;

import org.bukkit.DyeColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorDataTest {

    @Test
    void parsesRgbHexWithOptionalHash() {
        assertColor(ColorData.parse("#33AAFF"), 255, 51, 170, 255);
        assertColor(ColorData.parse("33aaff"), 255, 51, 170, 255);
    }

    @Test
    void parsesArgbHex() {
        assertColor(ColorData.parse("8033AAFF"), 128, 51, 170, 255);
    }

    @Test
    void parsesCommaSeparatedChannelsWithWhitespace() {
        assertColor(ColorData.parse(" 12, 34, 56 "), 255, 12, 34, 56);
    }

    @Test
    void parsesDyeColorNamesCaseInsensitively() {
        org.bukkit.Color expected = DyeColor.LIGHT_BLUE.getColor();
        assertColor(
                ColorData.parse("light_blue"),
                expected.getAlpha(),
                expected.getRed(),
                expected.getGreen(),
                expected.getBlue()
        );
    }

    @Test
    void rejectsInvalidColorsAndOutOfRangeChannels() {
        assertThrows(IllegalArgumentException.class, () -> ColorData.parse(null));
        assertThrows(IllegalArgumentException.class, () -> ColorData.parse("not-a-color"));
        assertThrows(IllegalArgumentException.class, () -> ColorData.parse("256,0,0"));
        assertThrows(IllegalArgumentException.class, () -> ColorData.parse("-1,0,0"));
    }

    private static void assertColor(ColorData color, int alpha, int red, int green, int blue) {
        assertEquals(alpha, color.getAlpha());
        assertEquals(red, color.getRed());
        assertEquals(green, color.getGreen());
        assertEquals(blue, color.getBlue());
    }
}
