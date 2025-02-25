package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.config.ConfigScreen;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.Keybinds;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.neoforged.neoforge.common.NeoForge;

@Mod(MoreCobblemonTweaks.MOD_ID)
public final class MoreCobblemonTweaksNeoForge {
    public MoreCobblemonTweaksNeoForge() {
        MoreCobblemonTweaks.initClient(FMLPaths.CONFIGDIR.get().resolve(MoreCobblemonTweaks.MOD_ID + ".json").toFile(),
                id -> ModList.get().isLoaded(id));
        NeoForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (modContainer, arg) -> ConfigScreen.buildScreen(arg));
    }

    @SubscribeEvent
    public void onInput(InputEvent.Key event) {
        if (Keybinds.OPEN_CONFIG.consumeClick() && Utils.modEnabled("cloth-config2")) {
            Minecraft client = Minecraft.getInstance();
            client.setScreen(ConfigScreen.buildScreen(client.screen));
        }
    }

    @SubscribeEvent
    public void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ModConfig.clearServerConfig();
    }
}
