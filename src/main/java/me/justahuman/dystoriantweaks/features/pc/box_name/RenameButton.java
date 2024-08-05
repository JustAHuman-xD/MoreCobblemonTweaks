package me.justahuman.dystoriantweaks.features.pc.box_name;

import me.justahuman.dystoriantweaks.features.pc.search.SearchButton;
import me.justahuman.dystoriantweaks.features.pc.search.SearchWidget;
import me.justahuman.dystoriantweaks.features.pc.wallpaper.WallpaperButton;
import me.justahuman.dystoriantweaks.utils.CustomButton;
import me.justahuman.dystoriantweaks.utils.Textures;
import me.justahuman.dystoriantweaks.utils.Utils;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import java.util.Set;

public class RenameButton extends CustomButton {
    public RenameButton(int x, int y, Set<Drawable> siblings) {
        super(x, y, Textures.RENAME_BUTTON_WIDTH, Textures.BUTTON_HEIGHT, Textures.RENAME_BUTTON_TEXTURE, siblings);
        setTooltip(Tooltip.of(Text.literal("Click to rename the current box!")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        handleSibling(RenameWidget.class, widget -> widget.setVisible(true));
        handleSibling(ConfirmButton.class, button -> button.setVisible(true));
        handleSibling(CancelButton.class, button -> button.setVisible(true));

        setVisible(false);
        handleSibling(WallpaperButton.class, button -> button.setVisible(false));
        handleSibling(SearchButton.class, button -> button.setVisible(false));
        handleSibling(SearchWidget.class, widget -> {
            widget.setVisible(false);
            Utils.search = null;
        });
    }
}