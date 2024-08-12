package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import me.justahuman.more_cobblemon_tweaks.features.pc.IvWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.CancelButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.ConfirmButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.RenameButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.RenameWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperWidget;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(PCGUI.class)
public abstract class PcGuiMixin extends Screen {
    @Shadow(remap = false) @Final public static int BASE_WIDTH;
    @Shadow(remap = false) @Final public static int BASE_HEIGHT;

    protected PcGuiMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void onInit(CallbackInfo ci) {
        Utils.search = null;

        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        if (ModConfig.isEnabled("pc_iv_display")) {
            this.addDrawable(new IvWidget(cast()));
        }

        Set<Drawable> siblings = new HashSet<>(this.children().stream().filter(Drawable.class::isInstance).map(Drawable.class::cast).toList());
        boolean wallpapers = ModConfig.isEnabled("custom_pc_wallpapers");

        if (wallpapers) {
            WallpaperButton button = this.addDrawableChild(new WallpaperButton(x + 243, y - 13, siblings));
            siblings.add(this.addDrawableChild(new WallpaperWidget(button, x + 85, y + 27)));
            siblings.add(button);
        }

        if (ModConfig.isEnabled("custom_pc_box_names")) {
            siblings.add(this.addDrawableChild(new CancelButton(x + 243, y - 13, siblings)));
            siblings.add(this.addDrawableChild(new ConfirmButton(x + 222, y - 13, siblings)));
            siblings.add(this.addDrawableChild(new RenameWidget(x + 106, y - 13)));
            siblings.add(this.addDrawableChild(new RenameButton(x + (wallpapers ? 220 : 241), y - 13, siblings)));
        }

        if (ModConfig.isEnabled("pc_search")) {
            siblings.add(this.addDrawableChild(new SearchButton(x + 82, y - 13, siblings)));
            siblings.add(this.addDrawableChild(new SearchWidget(x + 104, y - 13)));
        }
    }

    @Unique
    public PCGUI cast() {
        return (PCGUI) (Object) this;
    }
}
