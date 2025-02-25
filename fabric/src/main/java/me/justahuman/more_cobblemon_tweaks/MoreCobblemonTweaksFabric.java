package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.config.ConfigScreen;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.Keybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public final class MoreCobblemonTweaksFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MoreCobblemonTweaks.initClient(FabricLoader.getInstance().getConfigDir().resolve(MoreCobblemonTweaks.MOD_ID + ".json").toFile(),
                id -> FabricLoader.getInstance().isModLoaded(id));

        KeyBindingHelper.registerKeyBinding(Keybinds.OPEN_CONFIG);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Keybinds.OPEN_CONFIG.consumeClick() && FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                client.setScreen(ConfigScreen.buildScreen(client.screen));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ModConfig.clearServerConfig());

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                MoreCobblemonTweaks.onReload(manager);
            }

            @Override
            public ResourceLocation getFabricId() {
                return MoreCobblemonTweaks.id("reload_listener");
            }
        });
    }
}
