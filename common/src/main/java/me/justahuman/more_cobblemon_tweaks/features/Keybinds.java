package me.justahuman.more_cobblemon_tweaks.features;

import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Keybinds {
    private static final String KEYBIND_PREFIX = "key." + MoreCobblemonTweaks.MOD_ID + ".";
    private static final String KEYBINDS_CATEGORY = "key.categories." + MoreCobblemonTweaks.MOD_ID;

    public static final KeyBinding OPEN_CONFIG = new KeyBinding(KEYBIND_PREFIX + "open_config", InputUtil.GLFW_KEY_F9, KEYBINDS_CATEGORY);
}
