package me.justahuman.more_cobblemon_tweaks.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModConfig {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final JsonObject INTERNAL_CONFIG = new JsonObject();
    private static final JsonObject SERVER_CONFIG = new JsonObject();
    private static final JsonObject DEFAULT_CONFIG = new JsonObject();
    private static final ServerData SINGLE_PLAYER = new ServerData("", "singleplayer", ServerData.Type.OTHER);
    private static final Map<Integer, Component> BOX_NAME_CACHE = new HashMap<>();
    private static final Map<Integer, ResourceLocation> WALLPAPER_CACHE = new HashMap<>();
    static {
        DEFAULT_CONFIG.addProperty("enhanced_egg_lore", true);
        DEFAULT_CONFIG.addProperty("shiny_egg_indicator", true);
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

    public static void clearServerConfig() {
        SERVER_CONFIG.asMap().clear();
    }

    public static void loadServerConfig(BufferedReader reader) {
        if (JsonParser.parseReader(reader) instanceof JsonObject jsonObject) {
            jsonObject.entrySet().forEach(entry -> SERVER_CONFIG.add(entry.getKey(), entry.getValue()));
        }
    }

    public static boolean serverOverride(String option) {
        return SERVER_CONFIG.get(option) instanceof JsonPrimitive primitive && primitive.isBoolean();
    }

    public static boolean isEnabled(String option) {
        if (SERVER_CONFIG.get(option) instanceof JsonPrimitive primitive && primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }

        return INTERNAL_CONFIG.get(option) instanceof JsonPrimitive primitive && primitive.isBoolean()
                ? primitive.getAsBoolean()
                : DEFAULT_CONFIG.get(option).getAsBoolean();
    }

    public static void setEnabled(String key, boolean value) {
        INTERNAL_CONFIG.addProperty(key, value);
    }

    public static Component getBoxName(int box) {
        Component cache = BOX_NAME_CACHE.get(box);
        if (cache != null) {
            return cache;
        }

        JsonObject serverBoxes = INTERNAL_CONFIG.get("pc_box_names") instanceof JsonObject object ? object : null;
        if (serverBoxes == null) {
            BOX_NAME_CACHE.put(box, CommonComponents.EMPTY);
            return null;
        }

        ServerData data = Minecraft.getInstance().getCurrentServer();
        if (data == null) {
            data = SINGLE_PLAYER;
        }

        JsonObject boxNames = serverBoxes.get(data.ip) instanceof JsonObject object ? object : null;
        if (boxNames == null) {
            BOX_NAME_CACHE.put(box, CommonComponents.EMPTY);
            return null;
        }

        Component name = boxNames.get(String.valueOf(box)) instanceof JsonPrimitive primitive && primitive.isString()
                ? Component.literal(primitive.getAsString()).withStyle(ChatFormatting.BOLD)
                : CommonComponents.EMPTY;
        BOX_NAME_CACHE.put(box, name);
        return name;
    }

    public static void setBoxName(int box, String name) {
        ServerData data = Minecraft.getInstance().getCurrentServer();
        if (data == null) {
            data = SINGLE_PLAYER;
        }

        JsonObject serverBoxes = INTERNAL_CONFIG.get("pc_box_names") instanceof JsonObject object ? object : new JsonObject();
        JsonObject boxNames = serverBoxes.get(data.ip) instanceof JsonObject object ? object : new JsonObject();
        if (name == null || name.isBlank()) {
            boxNames.remove(String.valueOf(box));
            BOX_NAME_CACHE.put(box, CommonComponents.EMPTY);
        } else {
            boxNames.addProperty(String.valueOf(box), name);
            BOX_NAME_CACHE.put(box, Component.literal(name).withStyle(ChatFormatting.BOLD));
        }
        serverBoxes.add(data.ip, boxNames);
        INTERNAL_CONFIG.add("pc_box_names", serverBoxes);
        saveConfig(false);
    }

    public static ResourceLocation getBoxTexture(int box) {
        ResourceLocation cache = WALLPAPER_CACHE.get(box);
        if (cache != null) {
            return cache;
        }

        JsonObject pcWallpapers = INTERNAL_CONFIG.get("pc_wallpapers") instanceof JsonObject object ? object : null;
        if (pcWallpapers == null) {
            WALLPAPER_CACHE.put(box, Textures.WALLPAPER_DEFAULT_TEXTURE);
            return Textures.WALLPAPER_DEFAULT_TEXTURE;
        }

        ServerData data = Minecraft.getInstance().getCurrentServer();
        if (data == null) {
            data = SINGLE_PLAYER;
        }

        JsonObject wallpapers = pcWallpapers.get(data.ip) instanceof JsonObject object ? object : null;
        if (wallpapers == null) {
            WALLPAPER_CACHE.put(box, Textures.WALLPAPER_DEFAULT_TEXTURE);
            return Textures.WALLPAPER_DEFAULT_TEXTURE;
        }

        ResourceLocation wallpaper = wallpapers.get(String.valueOf(box)) instanceof JsonPrimitive primitive && primitive.isString()
                ? ResourceLocation.parse(primitive.getAsString())
                : Textures.WALLPAPER_DEFAULT_TEXTURE;
        WALLPAPER_CACHE.put(box, wallpaper);
        return wallpaper;
    }

    public static void setBoxTexture(int box, ResourceLocation texture) {
        ServerData data = Minecraft.getInstance().getCurrentServer();
        if (data == null) {
            data = SINGLE_PLAYER;
        }

        JsonObject pcWallpapers = INTERNAL_CONFIG.get("pc_wallpapers") instanceof JsonObject object ? object : new JsonObject();
        JsonObject wallpapers = pcWallpapers.get(data.ip) instanceof JsonObject object ? object : new JsonObject();
        wallpapers.addProperty(String.valueOf(box), texture.toString());
        pcWallpapers.add(data.ip, wallpapers);
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
        final File configFile = MoreCobblemonTweaks.getConfigFile();
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                if (!configFile.createNewFile()) {
                    throw new IOException();
                }
            } catch(IOException | SecurityException e) {
                MoreCobblemonTweaks.LOGGER.warn("Failed to create config file!");
                MoreCobblemonTweaks.LOGGER.warn(e.getMessage());
            }
        }
        return configFile;
    }
}
