package me.justahuman.dystoriantweaks;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class DystorianTweaks implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("dystoriantweaks", "reload_listener");
            }

            @Override
            public void reload(ResourceManager manager) {
                ModConfig.loadFromFile();
            }
        });
    }
}
