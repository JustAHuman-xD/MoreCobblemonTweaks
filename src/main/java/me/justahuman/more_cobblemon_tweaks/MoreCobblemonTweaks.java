package me.justahuman.more_cobblemon_tweaks;

import com.mojang.logging.LogUtils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class MoreCobblemonTweaks implements ClientModInitializer {
    public static final String MOD_ID = "more_cobblemon_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
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
            }
        });
    }
}
