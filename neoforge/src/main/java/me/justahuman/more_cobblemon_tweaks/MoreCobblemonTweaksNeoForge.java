package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.config.ConfigScreen;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.Keybinds;
import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(MoreCobblemonTweaks.MOD_ID)
public final class MoreCobblemonTweaksNeoForge {
    public MoreCobblemonTweaksNeoForge() {
        MoreCobblemonTweaks.initClient(FMLPaths.CONFIGDIR.get().resolve(MoreCobblemonTweaks.MOD_ID + ".json").toFile());
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInput(InputEvent.Key event) {
        if (Keybinds.OPEN_CONFIG.wasPressed() && ModList.get().isLoaded("cloth-config2")) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.setScreen(ConfigScreen.buildScreen(client.currentScreen));
        }
    }

    @SubscribeEvent
    public void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ModConfig.clearServerConfig();
    }
}
