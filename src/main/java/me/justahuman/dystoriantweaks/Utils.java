package me.justahuman.dystoriantweaks;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final Map<String, Boolean> HIDDEN_CACHE = new HashMap<>();
    public static int currentBox = 0;
    public static boolean allBoxes = false;
    public static String search = "serp";

    public static String get(NbtCompound nbt, String key, String def) {
        if (nbt != null && nbt.get(key) instanceof NbtString nbtString) {
            return nbtString.asString();
        }
        return def;
    }

    public static boolean get(NbtCompound nbt, String key, boolean def) {
        if (nbt != null && nbt.get(key) instanceof NbtByte nbtByte) {
            return nbtByte.byteValue() == 1;
        }
        return def;
    }

    public static int get(NbtCompound nbt, String key, int def) {
        if (nbt != null && nbt.get(key) instanceof NbtInt nbtInt) {
            return nbtInt.intValue();
        }
        return def;
    }

    public static short get(NbtCompound nbt, String key, short def) {
        if (nbt != null && nbt.get(key) instanceof NbtShort nbtShort) {
            return nbtShort.shortValue();
        }
        return def;
    }

    public static double get(NbtCompound nbt, String key, double def) {
        if (nbt != null && nbt.get(key) instanceof NbtDouble nbtDouble) {
            return nbtDouble.doubleValue();
        }
        return def;
    }
}
