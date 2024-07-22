package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import me.justahuman.dystoriantweaks.config.ModConfig;
import me.justahuman.dystoriantweaks.features.PcEnhancements;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PCGUI.class)
public abstract class PcGuiMixin extends Screen {
    @Shadow(remap = false) @Final public static int BASE_WIDTH;
    @Shadow(remap = false) @Final public static int BASE_HEIGHT;

    protected PcGuiMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void onInit(CallbackInfo ci) {
        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        if (ModConfig.isEnabled("pc_iv_display")) {
            this.addDrawable(new PcEnhancements.IvWidget(cast()));
        }

        boolean wallpapers = ModConfig.isEnabled("custom_pc_wallpapers");
        if (wallpapers) {
            this.addDrawableChild(new PcEnhancements.WallpaperButton(x + 243, y - 13));
        }

        if (ModConfig.isEnabled("custom_pc_box_names")) {
            this.addDrawableChild(new PcEnhancements.RenameButton(x + (wallpapers ? 220 : 241), y - 13));
        }

        if (ModConfig.isEnabled("pc_search")) {
            PcEnhancements.SearchWidget widget = this.addDrawableChild(new PcEnhancements.SearchWidget(x + 104, y - 13));
            this.addDrawableChild(new PcEnhancements.SearchButton(widget, x + 82, y - 7));
        }
    }

    @Unique
    public PCGUI cast() {
        return (PCGUI) (Object) this;
    }
}
