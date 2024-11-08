package me.justahuman.more_cobblemon_tweaks.features.pc.box_name;

import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperButton;
import me.justahuman.more_cobblemon_tweaks.utils.CustomButton;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.client.gui.Drawable;

import java.util.Set;

public class CancelButton extends CustomButton {
    public CancelButton(int x, int y, Set<Drawable> siblings) {
        super(x, y, Textures.CONTROL_BUTTON_WIDTH, Textures.BUTTON_HEIGHT, Textures.CANCEL_BUTTON_TEXTURE, siblings);
        setVisible(false);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        handleSibling(RenameWidget.class, widget -> {
            widget.setVisible(false);
            widget.setText("");
        });

        setVisible(false);
        handleSibling(ConfirmButton.class, button -> button.setVisible(false));
        handleSibling(RenameButton.class, button -> button.setVisible(true));
        handleSibling(WallpaperButton.class, button -> button.setVisible(true));
        handleSibling(SearchButton.class, button -> button.setVisible(true));
    }
}
