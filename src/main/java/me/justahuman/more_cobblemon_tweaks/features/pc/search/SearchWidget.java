package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import me.justahuman.more_cobblemon_tweaks.utils.CustomTextField;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.text.Text;

import static net.minecraft.util.Formatting.GRAY;

public class SearchWidget extends CustomTextField {
    public SearchWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setDrawsBackground(false);
        setPlaceholder(Text.literal("No Search Provided").formatted(GRAY));
        setChangedListener(string -> Utils.search = Search.of(string));
    }
}
