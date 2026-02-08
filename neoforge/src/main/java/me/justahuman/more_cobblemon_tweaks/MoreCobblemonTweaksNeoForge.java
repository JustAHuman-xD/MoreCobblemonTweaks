package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.config.ConfigScreen;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.Keybinds;
import me.justahuman.more_cobblemon_tweaks.features.egg.AllTheMonsEggLoreFactory;
import me.justahuman.more_cobblemon_tweaks.features.egg.EnhancedEggLore;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = MoreCobblemonTweaks.MOD_ID, dist = Dist.CLIENT)
public final class MoreCobblemonTweaksNeoForge {
    public MoreCobblemonTweaksNeoForge() {
        MoreCobblemonTweaks.initClient(
                FMLPaths.CONFIGDIR.get().resolve(MoreCobblemonTweaks.MOD_ID + ".json").toFile(),
                id -> ModList.get().isLoaded(id),
                id -> ModList.get().getModFileById(id).versionString()
        );

        NeoForge.EVENT_BUS.register(this);
        if (Hooks.clothConfig()) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                    () -> (modContainer, arg) -> ConfigScreen.buildScreen(arg));
        }
        if (Hooks.allTheMonsPresent()) {
            if (Hooks.allTheMonsCompat().enabled()) {
                MoreCobblemonTweaks.LOGGER.info(">> AllTheMons support enabled!{}", Hooks.allTheMonsCompat() == Hooks.SupportStatus.UNTESTED ? " (**UNTESTED** AllTheMons version)" : "");
                EnhancedEggLore.registerFactory(AllTheMonsEggLoreFactory::get);
            } else {
                MoreCobblemonTweaks.LOGGER.warn(">> AllTheMons version is not compatible! AllTheMons support disabled.");
            }
        }
    }

    @SubscribeEvent
    public void onInput(InputEvent.Key event) {
        if (Keybinds.OPEN_CONFIG.consumeClick() && Hooks.clothConfig()) {
            Minecraft client = Minecraft.getInstance();
            client.setScreen(ConfigScreen.buildScreen(client.screen));
        }
    }

    @SubscribeEvent
    public void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ModConfig.clearServerConfig();
    }
}
