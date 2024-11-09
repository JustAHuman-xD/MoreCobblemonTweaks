package me.justahuman.more_cobblemon_tweaks.features;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PcEnhancements {
    private static final String BASE_KEY = "more_cobblemon_tweaks.pc_enhancements.";

    public static Tooltip tooltip(String key, Object... args) {
        return Tooltip.create(translate(key, args));
    }

    public static MutableComponent translate(String key, Object... args) {
        return Component.translatable(BASE_KEY + key, args);
    }
}
