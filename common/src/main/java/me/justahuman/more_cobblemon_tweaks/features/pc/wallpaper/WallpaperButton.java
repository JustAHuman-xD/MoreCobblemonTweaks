package me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper;

import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.mixins.StorageWidgetAccessor;
import me.justahuman.more_cobblemon_tweaks.utils.CustomButton;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;

import java.util.Set;

public class WallpaperButton extends CustomButton {
    public WallpaperButton(int x, int y, Set<Renderable> siblings) {
        super(x, y, Textures.WALLPAPER_BUTTON_WIDTH, Textures.BUTTON_HEIGHT, Textures.WALLPAPER_BUTTON_TEXTURE, siblings);
        setTooltip(PcEnhancements.tooltip("custom_pc_wallpapers.tooltip"));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Utils.allBoxes = Screen.hasControlDown();
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