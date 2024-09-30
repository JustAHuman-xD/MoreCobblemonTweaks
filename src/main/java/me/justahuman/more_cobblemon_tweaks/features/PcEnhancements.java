package me.justahuman.more_cobblemon_tweaks.features;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PcEnhancements {
    private static final String BASE_KEY = "more_cobblemon_tweaks.pc_enhancements.";
    public static final List<Integer> BOX_ORDER = new ArrayList<>();

    public static int getBoxIndex(int box) {
        if (BOX_ORDER.size() <= box) {
            for (int i = BOX_ORDER.size(); i <= box; i++) {
                BOX_ORDER.add(i);
            }
        }
        return BOX_ORDER.indexOf(box);
    }

    public static Tooltip tooltip(String key, Object... args) {
        return Tooltip.of(translate(key, args));
    }

    public static MutableText translate(String key, Object... args) {
        return Text.translatable(BASE_KEY + key, args);
    }
}
