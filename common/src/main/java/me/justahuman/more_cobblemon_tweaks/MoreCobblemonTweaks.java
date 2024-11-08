package me.justahuman.more_cobblemon_tweaks;

import com.mojang.logging.LogUtils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class MoreCobblemonTweaks {
    public static final String MOD_ID = "more_cobblemon_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static File configFile;

    public static void initClient(File configFile) {
        LOGGER.info("Starting MoreCobblemonTweaks");
        MoreCobblemonTweaks.configFile = configFile;
    }

    public static void onReload(ResourceManager manager) {
        ModConfig.loadFromFile();
        Textures.POSSIBLE_WALLPAPER_TEXTURES.clear();
        for (Identifier wallpaper : manager.findAllResources("textures/gui/pc/wallpapers", identifier -> true).keySet()) {
            String path = wallpaper.getPath();
            String shortName = path.substring(path.lastIndexOf('/') + 1, path.indexOf('.'));
            Textures.POSSIBLE_WALLPAPER_TEXTURES.put(shortName, wallpaper);
        }

        ResourcePackManager resourcePackManager = MinecraftClient.getInstance().getResourcePackManager();
        List<String> serverPackIds = resourcePackManager.getProfiles().stream().filter(profile -> profile.getSource() == ResourcePackSource.SERVER).map(ResourcePackProfile::getId).toList();
        for (Map.Entry<Identifier, List<Resource>> entry : manager.findAllResources("config", identifier -> identifier.getPath().equals("config/" + MOD_ID + ".json")).entrySet()) {
            for (Resource resource : entry.getValue()) {
                if (!serverPackIds.contains(resource.getPackId())) {
                    continue;
                }

                try {
                    ModConfig.loadServerConfig(resource.getReader());
                } catch (Exception e) {
                    LOGGER.error("Failed to load server config file \"{}\" from pack \"{}\"",
                            entry.getKey(), resource.getPackId());
                    LOGGER.error("Error: ", e);
                }
            }
        }
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static File getConfigFile() {
        return configFile;
    }
}
