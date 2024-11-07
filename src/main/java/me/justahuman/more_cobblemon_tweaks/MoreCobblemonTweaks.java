package me.justahuman.more_cobblemon_tweaks;

import com.mojang.logging.LogUtils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class MoreCobblemonTweaks implements ClientModInitializer {
    public static final String MOD_ID = "more_cobblemon_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ModConfig.clearServerConfig());

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MOD_ID, "reload_listener");
            }

            @Override
            public void reload(ResourceManager manager) {
                ModConfig.loadFromFile();
                Textures.POSSIBLE_WALLPAPER_TEXTURES.clear();
                for (Identifier wallpaper : manager.findAllResources("textures/gui/pc/wallpapers", identifier -> true).keySet()) {
                    String path = wallpaper.getPath();
                    String shortName = path.substring(path.lastIndexOf('/') + 1, path.indexOf('.'));
                    Textures.POSSIBLE_WALLPAPER_TEXTURES.put(shortName, wallpaper);
                }

                ResourcePackManager resourcePackManager = MinecraftClient.getInstance().getResourcePackManager();
                List<String> serverPackIds = resourcePackManager.getProfiles().stream().filter(profile -> profile.getSource() == ResourcePackSource.SERVER).map(ResourcePackProfile::getName).toList();
                for (Map.Entry<Identifier, List<Resource>> entry : manager.findAllResources("config", identifier -> identifier.getPath().equals("config/" + MOD_ID + ".json")).entrySet()) {
                    for (Resource resource : entry.getValue()) {
                        if (!serverPackIds.contains(resource.getResourcePackName())) {
                            continue;
                        }

                        try {
                            ModConfig.loadServerConfig(resource.getReader());
                        } catch (Exception e) {
                            LOGGER.error("Failed to load server config file \"{}\" from pack \"{}\"",
                                    entry.getKey(), resource.getResourcePackName());
                            LOGGER.error("Error: ", e);
                        }
                    }
                }
            }
        });
    }
}
