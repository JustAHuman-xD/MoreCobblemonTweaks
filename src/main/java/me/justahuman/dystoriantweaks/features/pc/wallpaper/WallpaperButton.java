package me.justahuman.dystoriantweaks.features.pc.wallpaper;

import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import me.justahuman.dystoriantweaks.mixins.StorageWidgetAccessor;
import me.justahuman.dystoriantweaks.utils.CustomButton;
import me.justahuman.dystoriantweaks.utils.Textures;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import java.util.Set;

public class WallpaperButton extends CustomButton {
    public WallpaperButton(int x, int y, Set<Drawable> siblings) {
        super(x, y, Textures.WALLPAPER_BUTTON_WIDTH, Textures.BUTTON_HEIGHT, Textures.WALLPAPER_BUTTON_TEXTURE, siblings);
        setTooltip(Tooltip.of(Text.literal("Click to change the wallpaper of the current box! (Hold Control to change all boxes)")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        handleSibling(WallpaperWidget.class, widget -> {
            handleSibling(StorageWidget.class, storageWidget -> {
                storageWidget.visible = widget.visible;
                storageWidget.active = widget.visible;
            });
            handleSibling(StorageWidgetAccessor.class, StorageWidgetAccessor::resetSelectedInvoker);
            widget.setVisible(!widget.visible);
        });
    }
}