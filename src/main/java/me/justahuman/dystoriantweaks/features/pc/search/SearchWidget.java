package me.justahuman.dystoriantweaks.features.pc.search;

import me.justahuman.dystoriantweaks.utils.CustomTextField;
import me.justahuman.dystoriantweaks.utils.Utils;
import net.minecraft.text.Text;

import static net.minecraft.util.Formatting.GRAY;

public class SearchWidget extends CustomTextField {
    public SearchWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setDrawsBackground(false);
        setPlaceholder(Text.literal("(Edit me)").formatted(GRAY));
        setChangedListener(string -> Utils.search = Utils.Search.of(string));
    }
}
