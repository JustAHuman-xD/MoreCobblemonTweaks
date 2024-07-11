package me.justahuman.dystoriantweaks;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

public class Utils {
    public static String get(NbtCompound nbt, String key, String def) {
        if (nbt.get(key) instanceof NbtString nbtString) {
            return nbtString.asString();
        }
        return def;
    }

    public static boolean get(NbtCompound nbt, String key, boolean def) {
        if (nbt.get(key) instanceof NbtByte nbtByte) {
            return nbtByte.byteValue() == 1;
        }
        return def;
    }

    public static int get(NbtCompound nbt, String key, int def) {
        if (nbt.get(key) instanceof NbtInt nbtInt) {
            return nbtInt.intValue();
        }
        return def;
    }

    public static short get(NbtCompound nbt, String key, short def) {
        if (nbt.get(key) instanceof NbtShort nbtShort) {
            return nbtShort.shortValue();
        }
        return def;
    }

    public static double get(NbtCompound nbt, String key, double def) {
        if (nbt.get(key) instanceof NbtDouble nbtDouble) {
            return nbtDouble.doubleValue();
        }
        return def;
    }
}
