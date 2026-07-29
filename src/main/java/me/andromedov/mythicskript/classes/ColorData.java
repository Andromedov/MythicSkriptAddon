package me.andromedov.mythicskript.classes;

import ch.njol.skript.util.Color;
import ch.njol.yggdrasil.Fields;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.Locale;

/**
 * A Skript-compatible RGB color passed from a MythicMobs mechanic config.
 */
public final class ColorData implements Color {

    private int argb;

    public ColorData() {
        this(0xFFFFFFFF);
    }

    public ColorData(int argb) {
        this.argb = argb;
    }

    public static ColorData parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }

        String value = input.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }

        if (value.matches("(?i)[0-9a-f]{6}")) {
            return new ColorData(0xFF000000 | Integer.parseInt(value, 16));
        }
        if (value.matches("(?i)[0-9a-f]{8}")) {
            return new ColorData((int) Long.parseLong(value, 16));
        }

        String[] rgb = value.split(",");
        if (rgb.length == 3) {
            try {
                int red = parseChannel(rgb[0]);
                int green = parseChannel(rgb[1]);
                int blue = parseChannel(rgb[2]);
                return new ColorData(0xFF000000 | red << 16 | green << 8 | blue);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid RGB color '" + input + "'", exception);
            }
        }

        try {
            return fromBukkitColor(DyeColor.valueOf(value.toUpperCase(Locale.ROOT)).getColor());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid color '" + input + "'. Use #RRGGBB, #AARRGGBB, R,G,B, or a DyeColor name.",
                    exception
            );
        }
    }

    public static ColorData fromBukkitColor(org.bukkit.Color color) {
        return new ColorData(
                color.getAlpha() << 24
                        | color.getRed() << 16
                        | color.getGreen() << 8
                        | color.getBlue()
        );
    }

    private static int parseChannel(String value) {
        int channel = Integer.parseInt(value.trim());
        if (channel < 0 || channel > 255) {
            throw new NumberFormatException("RGB channels must be between 0 and 255");
        }
        return channel;
    }

    @Override
    public org.bukkit.Color asBukkitColor() {
        return org.bukkit.Color.fromARGB(argb);
    }

    @Override
    public int getAlpha() {
        return argb >>> 24;
    }

    @Override
    public int getRed() {
        return argb >> 16 & 0xFF;
    }

    @Override
    public int getGreen() {
        return argb >> 8 & 0xFF;
    }

    @Override
    public int getBlue() {
        return argb & 0xFF;
    }

    @Override
    public DyeColor asDyeColor() {
        DyeColor closest = DyeColor.WHITE;
        long closestDistance = Long.MAX_VALUE;

        for (DyeColor dyeColor : DyeColor.values()) {
            org.bukkit.Color candidate = dyeColor.getColor();
            long redDistance = getRed() - candidate.getRed();
            long greenDistance = getGreen() - candidate.getGreen();
            long blueDistance = getBlue() - candidate.getBlue();
            long distance = redDistance * redDistance
                    + greenDistance * greenDistance
                    + blueDistance * blueDistance;
            if (distance < closestDistance) {
                closest = dyeColor;
                closestDistance = distance;
            }
        }
        return closest;
    }

    @Override
    public String getName() {
        return toHexString();
    }

    @Override
    public Fields serialize() {
        return Fields.singletonPrimitive("argb", argb);
    }

    @Override
    public void deserialize(@NotNull Fields fields)
            throws StreamCorruptedException, NotSerializableException {
        argb = fields.getPrimitive("argb", Integer.class);
    }

    @Override
    public String toString() {
        return toHexString();
    }
}
