package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.gui.pc.ReleaseButton;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReleaseButton.class)
public class ReleaseButtonMixin {
    @Final @Shadow(remap = false) private StorageWidget parent;

    @Inject(method = "isHovered", at = @At("HEAD"), cancellable = true)
    public void onlyHoveredWhenRendering(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (!parent.canDeleteSelected() || parent.getDisplayConfirmRelease()) {
            cir.setReturnValue(false);
        }
    }
}
