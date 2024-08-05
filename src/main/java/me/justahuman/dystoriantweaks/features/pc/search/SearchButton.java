package me.justahuman.dystoriantweaks.features.pc.search;

import me.justahuman.dystoriantweaks.utils.CustomButton;
import me.justahuman.dystoriantweaks.utils.Textures;
import me.justahuman.dystoriantweaks.utils.Utils;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import java.util.Set;

public class SearchButton extends CustomButton {
    public SearchButton(int x, int y, Set<Drawable> siblings) {
        super(x, y, Textures.SEARCH_BUTTON_WIDTH, Textures.BUTTON_HEIGHT, Textures.SEARCH_BUTTON_TEXTURE, siblings);
        setTooltip(Tooltip.of(Text.literal("Click to open/close the search")));
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
            Utils.search = Utils.Search.of(widget.getText());
        });
    }
}