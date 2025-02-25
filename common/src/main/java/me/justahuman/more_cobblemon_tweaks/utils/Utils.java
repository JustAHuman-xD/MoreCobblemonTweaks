package me.justahuman.more_cobblemon_tweaks.utils;

import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.Search;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Utils {
    private static final Map<String, Boolean> MOD_ENABLED_CACHE = new HashMap<>();
    private static Function<String, Boolean> modEnabledFunction = id -> false;
    public static int currentBox = 0;
    public static boolean allBoxes = false;
    public static Search search = null;

    public static void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    public static String get(CompoundTag nbt, String key, String def) {
        if (nbt != null && nbt.get(key) instanceof StringTag nbtString) {
            return nbtString.getAsString();
        }
        return def;
    }

    public static boolean get(CompoundTag nbt, String key, boolean def) {
        if (nbt != null && nbt.get(key) instanceof ByteTag nbtByte) {
            return nbtByte.getAsByte() == 1;
        }
        return def;
    }

    public static int get(CompoundTag nbt, String key, int def) {
        if (nbt != null && nbt.get(key) instanceof IntTag nbtInt) {
            return nbtInt.getAsInt();
        }
        return def;
    }

    public static short get(CompoundTag nbt, String key, short def) {
        if (nbt != null && nbt.get(key) instanceof ShortTag nbtShort) {
            return nbtShort.getAsShort();
        }
        return def;
    }

    public static double get(CompoundTag nbt, String key, double def) {
        if (nbt != null && nbt.get(key) instanceof DoubleTag nbtDouble) {
            return nbtDouble.getAsDouble();
        }
        return def;
    }

    public static boolean modEnabled(String id) {
        Boolean cached = MOD_ENABLED_CACHE.get(id);
        if (cached != null) {
            return cached;
        }
        boolean enabled = modEnabledFunction.apply(id);
        MOD_ENABLED_CACHE.put(id, enabled);
        return enabled;
    }

    public static void setModEnabledFunction(Function<String, Boolean> function) {
        modEnabledFunction = function;
    }
}
