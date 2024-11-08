package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.CustomButton;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.Drawable;

import java.util.Set;

public class SearchButton extends CustomButton {
    public SearchButton(int x, int y, Set<Drawable> siblings) {
        super(x, y, Textures.SEARCH_BUTTON_WIDTH, Textures.BUTTON_HEIGHT, Textures.SEARCH_BUTTON_TEXTURE, siblings);
        setTooltip(PcEnhancements.tooltip("pc_search.tooltip"));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        handleSibling(SearchWidget.class, widget -> {
            if (widget.isVisible()) {
                widget.setVisible(false);
                Utils.search = null;
                return;
            }
            widget.setVisible(true);
            Utils.search = Search.of(widget.getText());
        });
    }
}