package me.justahuman.more_cobblemon_tweaks.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final JsonObject INTERNAL_CONFIG = new JsonObject();
    private static final JsonObject SERVER_CONFIG = new JsonObject();
    private static final JsonObject DEFAULT_CONFIG = new JsonObject();
    static {
        DEFAULT_CONFIG.addProperty("enhanced_egg_lore", true);
        DEFAULT_CONFIG.addProperty("egg_encryption_warning", true);
        DEFAULT_CONFIG.addProperty("shiny_egg_indicator", true);
        DEFAULT_CONFIG.addProperty("perfect_iv_egg_indicator", true);
        DEFAULT_CONFIG.addProperty("minimum_iv_egg_indicator", true);
        DEFAULT_CONFIG.addProperty("text_egg_indicators", false);
        DEFAULT_CONFIG.addProperty("pc_multi_select", true);
        DEFAULT_CONFIG.addProperty("pc_iv_display", true);
        DEFAULT_CONFIG.addProperty("pc_colored_ivs", true);
        DEFAULT_CONFIG.addProperty("open_box_history", true);
    }

    public static void loadFromFile() {
        INTERNAL_CONFIG.asMap().clear();

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

    public static void saveConfig() {
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

    public static boolean getDefault(String key) {
        return DEFAULT_CONFIG.get(key) instanceof JsonPrimitive primitive && primitive.isBoolean() && primitive.getAsBoolean();
    }
}
