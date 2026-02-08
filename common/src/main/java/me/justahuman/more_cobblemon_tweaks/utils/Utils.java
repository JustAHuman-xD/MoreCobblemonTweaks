package me.justahuman.more_cobblemon_tweaks.utils;

import com.cobblemon.mod.common.client.gui.pc.PCGUIConfiguration;
import com.cobblemon.mod.common.client.render.gui.PCBoxWallpaperRepository;
import com.cobblemon.mod.common.client.storage.ClientBox;
import com.cobblemon.mod.common.client.storage.ClientPC;
import me.justahuman.more_cobblemon_tweaks.Hooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Utils {
    private static final Map<String, Boolean> MOD_ENABLED_CACHE = new HashMap<>();
    private static final Map<String, String> MOD_VERSION_CACHE = new HashMap<>();
    private static final DecimalFormat IV_FORMAT = new DecimalFormat("#.##");
    private static Function<String, Boolean> modEnabledFunction = id -> false;
    private static Function<String, String> modVersionFunction = id -> "unknown";

    public static CompletableFuture<Void> moveAllPokemonFuture = null;
    public static boolean moveAllCompleted = true;
    public static int moveAllCount = 0;
    public static Runnable moveAllCountUpdater = () -> {};
    public static int moveAllTotal = 0;
    public static int moveAllTimeouts = 0;

    public static ClientPC summaryPC = null;
    public static PCGUIConfiguration summaryConfig = null;
    public static Set<ResourceLocation> unseenWallpapers = null;
    public static boolean summaryFromPC = false;

    public static void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    public static boolean isSinglePlayer() {
        return Minecraft.getInstance().getSingleplayerServer() != null;
    }

    public static String ivPercent(double iv) {
        return IV_FORMAT.format(iv / 31.0 * 100.0) + "%";
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

    public static void forgetMod(String id) {
        MOD_ENABLED_CACHE.remove(id);
        MOD_VERSION_CACHE.remove(id);
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

    public static String modVersion(String id) {
        String cached = MOD_VERSION_CACHE.get(id);
        if (cached != null) {
            return cached;
        }
        String version = modVersionFunction.apply(id);
        MOD_VERSION_CACHE.put(id, version);
        return version;
    }

    public static void setModEnabledFunction(Function<String, Boolean> function) {
        modEnabledFunction = function;
        Hooks.reset();
        MOD_ENABLED_CACHE.clear();
    }

    public static void setModVersionFunction(Function<String, String> function) {
        modVersionFunction = function;
        Hooks.reset();
        MOD_VERSION_CACHE.clear();
    }

    public static ResourceLocation getUsedWallpaper(ClientBox box) {
        ResourceLocation boxWallpaper = box.getWallpaper();
        var wallpaperData = PCBoxWallpaperRepository.allWallpapers.stream().filter(it -> Objects.equals(it.getFirst(), boxWallpaper)).findFirst()
                .orElse(PCBoxWallpaperRepository.allWallpapers.stream().filter(it -> Objects.equals(it.getSecond(), boxWallpaper)).findFirst().orElse(null));
        return wallpaperData != null ? boxWallpaper : PCBoxWallpaperRepository.INSTANCE.getDefaultWallpaper();
    }
}
