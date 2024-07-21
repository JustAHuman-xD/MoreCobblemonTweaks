package me.justahuman.dystoriantweaks.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.justahuman.dystoriantweaks.DystorianTweaks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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
        DEFAULT_CONFIG.addProperty("pc_iv_display", true);
        DEFAULT_CONFIG.addProperty("open_box_history", true);
        DEFAULT_CONFIG.addProperty("custom_pc_wallpapers", true);
        DEFAULT_CONFIG.addProperty("custom_pc_box_names", true);
        DEFAULT_CONFIG.add("pc_wallpapers", new JsonObject());
        DEFAULT_CONFIG.add("pc_box_names", new JsonObject());
        DEFAULT_CONFIG.addProperty("hidden_ability_indicator", true);
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

    public static void setEnabled(String key, boolean value) {
        INTERNAL_CONFIG.addProperty(key, value);
    }

    public static Text getBoxName(int box) {
        JsonObject serverBoxes = INTERNAL_CONFIG.get("pc_box_names") instanceof JsonObject object ? object : null;
        if (serverBoxes == null) {
            return null;
        }

        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null) {
            info = new ServerInfo("", "singleplayer", true);
        }

        JsonObject boxNames = serverBoxes.get(info.address) instanceof JsonObject object ? object : null;
        if (boxNames == null) {
            return null;
        }

        return boxNames.get(String.valueOf(box)) instanceof JsonPrimitive primitive && primitive.isString()
                ? Text.literal(primitive.getAsString()).formatted(Formatting.BOLD)
                : null;
    }

    public static void setBoxName(int box, String name) {
        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null) {
            info = new ServerInfo("", "singleplayer", true);
        }

        JsonObject serverBoxes = INTERNAL_CONFIG.get("pc_box_names") instanceof JsonObject object ? object : new JsonObject();
        JsonObject boxNames = serverBoxes.get(info.address) instanceof JsonObject object ? object : new JsonObject();
        boxNames.addProperty(String.valueOf(box), name);
        serverBoxes.add(info.address, boxNames);
        INTERNAL_CONFIG.add("pc_box_names", serverBoxes);

        saveConfig();
    }

    public static Identifier getBoxTexture(int box) {
        JsonObject pcWallpapers = INTERNAL_CONFIG.get("pc_wallpapers") instanceof JsonObject object ? object : null;
        if (pcWallpapers == null) {
            return null;
        }

        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null) {
            info = new ServerInfo("", "singleplayer", true);
        }

        JsonObject wallpapers = pcWallpapers.get(info.address) instanceof JsonObject object ? object : null;
        if (wallpapers == null) {
            return null;
        }

        return wallpapers.get(String.valueOf(box)) instanceof JsonPrimitive primitive && primitive.isString()
                ? new Identifier(primitive.getAsString())
                : null;
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

        saveConfig();
    }

    public static void saveConfig() {
        try (final FileWriter fileWriter = new FileWriter(getConfigFile())) {
            GSON.toJson(INTERNAL_CONFIG, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            DystorianTweaks.LOGGER.warn("Error occurred while saving config!");
            DystorianTweaks.LOGGER.warn(e.getMessage());
        }
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
