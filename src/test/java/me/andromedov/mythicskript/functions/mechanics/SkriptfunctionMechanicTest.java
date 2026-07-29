package me.andromedov.mythicskript.functions.mechanics;

import me.andromedov.mythicskript.classes.ColorData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SkriptfunctionMechanicTest {

    @Test
    void preservesTextButTrimsTypedValues() {
        assertEquals("  text  ", SkriptfunctionMechanic.convert("  text  ", String.class));
        assertEquals(true, SkriptfunctionMechanic.convert(" true ", Boolean.class));
        assertEquals(42.5D, SkriptfunctionMechanic.convert(" 42.5 ", Number.class));
        assertEquals(8, SkriptfunctionMechanic.convert(" 8 ", Integer.class));
        assertInstanceOf(ColorData.class, SkriptfunctionMechanic.convert(" #33AAFF ", ColorData.class));
    }

    @Test
    void rejectsInvalidTypedValues() {
        assertThrows(IllegalArgumentException.class,
                () -> SkriptfunctionMechanic.convert("yes", Boolean.class));
        assertThrows(IllegalArgumentException.class,
                () -> SkriptfunctionMechanic.convert("many", Number.class));
    }
}
