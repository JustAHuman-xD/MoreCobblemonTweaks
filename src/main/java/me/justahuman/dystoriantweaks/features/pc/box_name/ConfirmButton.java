package me.justahuman.dystoriantweaks.features.pc.box_name;

import com.cobblemon.mod.common.CobblemonSounds;
import me.justahuman.dystoriantweaks.config.ModConfig;
import me.justahuman.dystoriantweaks.features.pc.search.SearchButton;
import me.justahuman.dystoriantweaks.features.pc.wallpaper.WallpaperButton;
import me.justahuman.dystoriantweaks.utils.CustomButton;
import me.justahuman.dystoriantweaks.utils.Textures;
import me.justahuman.dystoriantweaks.utils.Utils;
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
            ModConfig.setBoxName(Utils.currentBox, widget.getText().replace('&', '§'));
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
