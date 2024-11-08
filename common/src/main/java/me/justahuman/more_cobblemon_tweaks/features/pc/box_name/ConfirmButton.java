package me.justahuman.more_cobblemon_tweaks.features.pc.box_name;

import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperButton;
import me.justahuman.more_cobblemon_tweaks.utils.CustomButton;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.Drawable;

import java.util.Set;

public class ConfirmButton extends CustomButton {
    public ConfirmButton(int x, int y, Set<Drawable> siblings) {
        super(x, y, Textures.CONTROL_BUTTON_WIDTH, Textures.BUTTON_HEIGHT, Textures.CONFIRM_BUTTON_TEXTURE, siblings);
        setVisible(false);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        handleSibling(RenameWidget.class, widget -> {
            String newName = widget.getText();
            if (newName != null && !newName.isBlank()) {
                ModConfig.setBoxName(Utils.currentBox, newName);
            }

            widget.setText("");
            widget.setFocused(false);
            widget.setVisible(false);
        });

        setVisible(false);
        handleSibling(CancelButton.class, button -> button.setVisible(false));
        handleSibling(RenameButton.class, button -> button.setVisible(true));
        handleSibling(WallpaperButton.class, button -> button.setVisible(true));
        handleSibling(SearchButton.class, button -> button.setVisible(true));
    }
}
