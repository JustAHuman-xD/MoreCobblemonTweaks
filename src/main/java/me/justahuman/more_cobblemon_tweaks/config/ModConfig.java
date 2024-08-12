package me.justahuman.more_cobblemon_tweaks.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks.MOD_ID;

public class ModConfig {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    public static final JsonObject INTERNAL_CONFIG = new JsonObject();
    public static final JsonObject DEFAULT_CONFIG = new JsonObject();
    private static final Map<Integer, Text> BOX_NAME_CACHE = new HashMap<>();
    private static final Map<Integer, Identifier> WALLPAPER_CACHE = new HashMap<>();
    static {
        DEFAULT_CONFIG.addProperty("enhanced_egg_lore", true);
        DEFAULT_CONFIG.addProperty("enhanced_berry_lore", true);
        DEFAULT_CONFIG.addProperty("enhanced_consumable_lore", true);
        DEFAULT_CONFIG.addProperty("enhanced_held_item_lore", true);
        DEFAULT_CONFIG.addProperty("wt_compact_lore", true);
        DEFAULT_CONFIG.addProperty("pc_iv_display", true);
        DEFAULT_CONFIG.addProperty("open_box_history", true);
        DEFAULT_CONFIG.addProperty("pc_search", true);
        DEFAULT_CONFIG.addProperty("custom_pc_wallpapers", true);
        DEFAULT_CONFIG.addProperty("custom_pc_box_names", true);
        DEFAULT_CONFIG.add("pc_wallpapers", new JsonObject());
        DEFAULT_CONFIG.add("pc_box_names", new JsonObject());
    }

    public static void loadFromFile() {
        INTERNAL_CONFIG.asMap().clear();
        WALLPAPER_CACHE.clear();
        BOX_NAME_CACHE.clear();

        try (final FileReader reader = new FileReader(getConfigFile())) {
            if (JsonParser.parseReader(reader) instanceof JsonObject jsonObject) {
                jsonObject.entrySet().forEach(entry -> INTERNAL_CONFIG.add(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            MoreCobblemonTweaks.LOGGER.warn("Error occurred while loading Config!");
            MoreCobblemonTweaks.LOGGER.warn(e.getMessage());
        }
    }

    public static boolean isEnabled(String option) {
        return INTERNAL_CONFIG.get(option) instanceof JsonPrimitive primitive && primitive.isBoolean()
                ? primitive.getAsBoolean()
                : DEFAULT_CONFIG.get(option).getAsBoolean();
    }

    public static void setEnabled(String key, boolean value) {
        INTERNAL_CONFIG.addProperty(key, value);
    }

    public static Text getBoxName(int box) {
        Text cache = BOX_NAME_CACHE.get(box);
        if (cache != null) {
            return cache;
        }

        JsonObject serverBoxes = INTERNAL_CONFIG.get("pc_box_names") instanceof JsonObject object ? object : null;
        if (serverBoxes == null) {
            BOX_NAME_CACHE.put(box, ScreenTexts.EMPTY);
            return null;
        }

        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null) {
            info = new ServerInfo("", "singleplayer", true);
        }

        JsonObject boxNames = serverBoxes.get(info.address) instanceof JsonObject object ? object : null;
        if (boxNames == null) {
            BOX_NAME_CACHE.put(box, ScreenTexts.EMPTY);
            return null;
        }

        Text name = boxNames.get(String.valueOf(box)) instanceof JsonPrimitive primitive && primitive.isString()
                ? Text.literal(primitive.getAsString()).formatted(Formatting.BOLD)
                : ScreenTexts.EMPTY;
        BOX_NAME_CACHE.put(box, name);
        return name;
    }

    public static void setBoxName(int box, String name) {
        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null) {
            info = new ServerInfo("", "singleplayer", true);
        }

        JsonObject serverBoxes = INTERNAL_CONFIG.get("pc_box_names") instanceof JsonObject object ? object : new JsonObject();
        JsonObject boxNames = serverBoxes.get(info.address) instanceof JsonObject object ? object : new JsonObject();
        if (name == null || name.isBlank()) {
            boxNames.remove(String.valueOf(box));
        } else {
            boxNames.addProperty(String.valueOf(box), name);
        }
        serverBoxes.add(info.address, boxNames);
        INTERNAL_CONFIG.add("pc_box_names", serverBoxes);
        BOX_NAME_CACHE.put(box, Text.literal(name).formatted(Formatting.BOLD));
        saveConfig(false);
    }

    public static Identifier getBoxTexture(int box) {
        Identifier cache = WALLPAPER_CACHE.get(box);
        if (cache != null) {
            return cache;
        }

        JsonObject pcWallpapers = INTERNAL_CONFIG.get("pc_wallpapers") instanceof JsonObject object ? object : null;
        if (pcWallpapers == null) {
            WALLPAPER_CACHE.put(box, Textures.WALLPAPER_DEFAULT_TEXTURE);
            return Textures.WALLPAPER_DEFAULT_TEXTURE;
        }

        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null) {
            info = new ServerInfo("", "singleplayer", true);
        }

        JsonObject wallpapers = pcWallpapers.get(info.address) instanceof JsonObject object ? object : null;
        if (wallpapers == null) {
            WALLPAPER_CACHE.put(box, Textures.WALLPAPER_DEFAULT_TEXTURE);
            return Textures.WALLPAPER_DEFAULT_TEXTURE;
        }

        Identifier wallpaper = wallpapers.get(String.valueOf(box)) instanceof JsonPrimitive primitive && primitive.isString()
                ? new Identifier(primitive.getAsString().replace("dystoriantweaks:", "more_cobblemon_tweaks:"))
                : Textures.WALLPAPER_DEFAULT_TEXTURE;
        WALLPAPER_CACHE.put(box, wallpaper);
        return wallpaper;
    }

    public static void setBoxTexture(int box, Identifier texture) {
        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null) {
            info = new ServerInfo("", "singleplayer", true);
        }

        JsonObject pcWallpapers = INTERNAL_CONFIG.get("pc_wallpapers") instanceof JsonObject object ? object : new JsonObject();
        JsonObject wallpapers = pcWallpapers.get(info.address) instanceof JsonObject object ? object : new JsonObject();
        wallpapers.addProperty(String.valueOf(box), texture.toString());
        pcWallpapers.add(info.address, wallpapers);
        INTERNAL_CONFIG.add("pc_wallpapers", pcWallpapers);
        WALLPAPER_CACHE.put(box, texture);
        saveConfig(false);
    }

    public static void saveConfig() {
        saveConfig(true);
    }

    public static void saveConfig(boolean resetCache) {
        if (resetCache) {
            BOX_NAME_CACHE.clear();
            WALLPAPER_CACHE.clear();
        }

        try (final FileWriter fileWriter = new FileWriter(getConfigFile())) {
            GSON.toJson(INTERNAL_CONFIG, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            MoreCobblemonTweaks.LOGGER.warn("Error occurred while saving config!");
            MoreCobblemonTweaks.LOGGER.warn(e.getMessage());
        }
    }

    public static File getConfigFile() {
        final File configFile = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json").toFile();
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                if (!configFile.createNewFile()) {
                    throw new IOException();
                }

                final File oldFile = FabricLoader.getInstance().getConfigDir().resolve("dystorian_tweaks.json").toFile();
                if (oldFile.isFile()) {
                    try (final FileReader reader = new FileReader(oldFile)) {
                        if (JsonParser.parseReader(reader) instanceof JsonObject jsonObject) {
                            jsonObject.entrySet().forEach(entry -> INTERNAL_CONFIG.add(entry.getKey(), entry.getValue()));
                        }
                        saveConfig();
                    } catch (Exception e) {
                        MoreCobblemonTweaks.LOGGER.warn("Error occurred while loading old Config!");
                        MoreCobblemonTweaks.LOGGER.warn(e.getMessage());
                    }
                } else {
                    DEFAULT_CONFIG.entrySet().forEach(entry -> INTERNAL_CONFIG.add(entry.getKey(), entry.getValue()));
                    saveConfig();
                }
            } catch(IOException | SecurityException e) {
                MoreCobblemonTweaks.LOGGER.warn("Failed to create config file!");
                MoreCobblemonTweaks.LOGGER.warn(e.getMessage());
            }
        }
        return configFile;
    }
}
