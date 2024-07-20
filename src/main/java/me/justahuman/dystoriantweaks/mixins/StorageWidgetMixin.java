package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import me.justahuman.dystoriantweaks.Utils;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageWidget.class)
public abstract class StorageWidgetMixin {
    @Shadow(remap = false) private int box;

    @Inject(at = @At("HEAD"), method = "renderButton")
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Utils.currentBox = this.box;
    }
}
