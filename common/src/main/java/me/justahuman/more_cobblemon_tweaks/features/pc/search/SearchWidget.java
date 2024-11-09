package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.CustomTextField;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;

import static net.minecraft.ChatFormatting.GRAY;

public class SearchWidget extends CustomTextField {
    public SearchWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setBordered(false);
        setHint(PcEnhancements.translate("pc_search.blank").withStyle(GRAY));
        setResponder(string -> Utils.search = Search.of(string));
    }
}
