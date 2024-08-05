package me.justahuman.dystoriantweaks.utils;

import me.justahuman.dystoriantweaks.features.pc.search.Search;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.sound.SoundEvent;

public class Utils {
    public static int currentBox = 0;
    public static boolean allBoxes = false;
    public static Search search = null;

    public static void playSound(SoundEvent sound) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(sound, 1.0F));
    }

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
