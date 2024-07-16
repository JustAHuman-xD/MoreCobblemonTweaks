package me.justahuman.dystoriantweaks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    public static final JsonObject INTERNAL_CONFIG = new JsonObject();
    public static final JsonObject DEFAULT_CONFIG = new JsonObject();
    static {
        DEFAULT_CONFIG.addProperty("enhanced_egg_lore", true);
        DEFAULT_CONFIG.addProperty("enhanced_berry_lore", true);
        DEFAULT_CONFIG.addProperty("enhanced_consumable_lore", true);
        DEFAULT_CONFIG.addProperty("enhanced_held_item_lore", true);
        DEFAULT_CONFIG.addProperty("wt_compact_lore", true);
    }

    public static void loadFromFile() {
        INTERNAL_CONFIG.asMap().clear();

        try (final FileReader reader = new FileReader(getConfigFile())) {
            if (JsonParser.parseReader(reader) instanceof JsonObject jsonObject) {
                jsonObject.entrySet().forEach(entry -> INTERNAL_CONFIG.add(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            DystorianTweaks.LOGGER.warn("Error occurred while loading Config!");
            DystorianTweaks.LOGGER.warn(e.getMessage());
        }
    }

    public static boolean isEnabled(String option) {
        return INTERNAL_CONFIG.get(option) instanceof JsonPrimitive primitive && primitive.isBoolean()
                ? primitive.getAsBoolean()
                : DEFAULT_CONFIG.get(option).getAsBoolean();
    }

    public static File getConfigFile() {
        final File configFile = FabricLoader.getInstance().getConfigDir().resolve("dystorian_tweaks.json").toFile();
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                if (!configFile.createNewFile()) {
                    throw new IOException();
                }

                try (final FileWriter fileWriter = new FileWriter(getConfigFile())) {
                    GSON.toJson(DEFAULT_CONFIG, fileWriter);
                    fileWriter.flush();
                } catch (IOException e) {
                    DystorianTweaks.LOGGER.warn("Error occurred while saving default config!");
                    DystorianTweaks.LOGGER.warn(e.getMessage());
                }
            } catch(IOException | SecurityException e) {
                DystorianTweaks.LOGGER.warn("Failed to create config file!");
                DystorianTweaks.LOGGER.warn(e.getMessage());
            }
        }
        return configFile;
    }
}
