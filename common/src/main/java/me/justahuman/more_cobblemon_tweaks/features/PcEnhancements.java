package me.justahuman.more_cobblemon_tweaks.features;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class PcEnhancements {
    private static final String BASE_KEY = "more_cobblemon_tweaks.pc_enhancements.";

    public static Tooltip tooltip(String key, Object... args) {
        return Tooltip.of(translate(key, args));
    }

    public static MutableText translate(String key, Object... args) {
        return Text.translatable(BASE_KEY + key, args);
    }
}
