package me.justahuman.more_cobblemon_tweaks;

import com.mojang.logging.LogUtils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class MoreCobblemonTweaks {
    public static final String MOD_ID = "more_cobblemon_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static File configFile;

    public static void initClient(File configFile, Function<String, Boolean> modEnabledFunction) {
        LOGGER.info("Starting MoreCobblemonTweaks");
        MoreCobblemonTweaks.configFile = configFile;
        Utils.setModEnabledFunction(modEnabledFunction);
    }

    public static void onReload(ResourceManager manager) {
        ModConfig.loadFromFile();
        Textures.POSSIBLE_WALLPAPER_TEXTURES.clear();
        for (ResourceLocation wallpaper : manager.listResourceStacks("textures/gui/pc/wallpapers", identifier -> true).keySet()) {
            String path = wallpaper.getPath();
            String shortName = path.substring(path.lastIndexOf('/') + 1, path.indexOf('.'));
            Textures.POSSIBLE_WALLPAPER_TEXTURES.put(shortName, wallpaper);
        }

        PackRepository resourcePackManager = Minecraft.getInstance().getResourcePackRepository();
        List<String> serverPackIds = resourcePackManager.getSelectedPacks().stream().filter(pack -> pack.getPackSource() == PackSource.SERVER).map(Pack::getId).toList();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks("config", identifier -> identifier.getPath().equals("config/" + MOD_ID + ".json")).entrySet()) {
            for (Resource resource : entry.getValue()) {
                if (!serverPackIds.contains(resource.sourcePackId())) {
                    continue;
                }

                try {
                    ModConfig.loadServerConfig(resource.openAsReader());
                } catch (Exception e) {
                    LOGGER.error("Failed to load server config file \"{}\" from pack \"{}\"",
                            entry.getKey(), resource.sourcePackId());
                    LOGGER.error("Error: ", e);
                }
            }
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }

    public static File getConfigFile() {
        return configFile;
    }
}
