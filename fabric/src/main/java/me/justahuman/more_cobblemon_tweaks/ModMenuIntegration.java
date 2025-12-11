package me.justahuman.more_cobblemon_tweaks;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.justahuman.more_cobblemon_tweaks.config.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (Hooks.clothConfig()) {
                return ConfigScreen.buildScreen(parent);
            } else {
                return null;
            }
        };
    }
}
