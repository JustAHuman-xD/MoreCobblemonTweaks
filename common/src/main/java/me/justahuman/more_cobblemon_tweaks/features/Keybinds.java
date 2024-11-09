package me.justahuman.more_cobblemon_tweaks.features;

import com.mojang.blaze3d.platform.InputConstants;
import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import net.minecraft.client.KeyMapping;

public class Keybinds {
    private static final String KEYBIND_PREFIX = "key." + MoreCobblemonTweaks.MOD_ID + ".";
    private static final String KEYBINDS_CATEGORY = "key.categories." + MoreCobblemonTweaks.MOD_ID;

    public static final KeyMapping OPEN_CONFIG = new KeyMapping(KEYBIND_PREFIX + "open_config", InputConstants.KEY_F9, KEYBINDS_CATEGORY);
}
