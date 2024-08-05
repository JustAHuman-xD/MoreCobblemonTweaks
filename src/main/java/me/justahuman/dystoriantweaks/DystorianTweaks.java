package me.justahuman.dystoriantweaks;

import com.mojang.logging.LogUtils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import me.justahuman.dystoriantweaks.features.PcEnhancements;
import me.justahuman.dystoriantweaks.utils.Textures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class DystorianTweaks implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static Consumer<String> chatConsumer = null;

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
                Textures.POSSIBLE_WALLPAPER_TEXTURES.clear();
                for (Identifier wallpaper : manager.findAllResources("textures/gui/pc/wallpapers", identifier -> true).keySet()) {
                    String path = wallpaper.getPath();
                    String shortName = path.substring(path.lastIndexOf('/') + 1, path.indexOf('.'));
                    Textures.POSSIBLE_WALLPAPER_TEXTURES.put(shortName, wallpaper);
                }
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            PcEnhancements.registerWallpaperCommand(dispatcher);
        });

        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (chatConsumer != null) {
                chatConsumer.accept(message);
                chatConsumer = null;
                return false;
            }
            return true;
        });
    }

    public static void addChatConsumer(Consumer<String> consumer) {
        chatConsumer = consumer;
    }
}
