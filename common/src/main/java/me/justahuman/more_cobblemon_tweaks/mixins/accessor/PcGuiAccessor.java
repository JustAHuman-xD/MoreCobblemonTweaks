package me.justahuman.more_cobblemon_tweaks.mixins.accessor;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.WallpapersScrollingWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PCGUI.class, remap = false)
public interface PcGuiAccessor {
    @Accessor void setCurrentStatIndex(int currentStatIndex);
    @Accessor int getCurrentStatIndex();
    @Accessor WallpapersScrollingWidget getWallpaperWidget();
}
