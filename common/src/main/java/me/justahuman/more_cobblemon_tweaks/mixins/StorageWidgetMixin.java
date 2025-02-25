package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StorageWidget.class)
public abstract class StorageWidgetMixin extends SoundlessWidget {
    @Shadow(remap = false) private int box;

    private StorageWidgetMixin(int pX, int pY, int pWidth, int pHeight, @NotNull Component component) {
        super(pX, pY, pWidth, pHeight, component);
    }

    @Inject(at = @At("TAIL"), method = "setBox", remap = false)
    public void setBox(int value, CallbackInfo ci) {
        Utils.currentBox = box;
    }

    @ModifyArg(method = "renderWidget", index = 1, at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/gui/GuiUtilsKt;blitk$default(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;ZFILjava/lang/Object;)V", ordinal = 1))
    public ResourceLocation overrideOverlayTexture(ResourceLocation texture) {
        if (ModConfig.isEnabled("custom_pc_wallpapers")) {
            ResourceLocation newTexture = ModConfig.getBoxTexture(Utils.currentBox);
            if (newTexture != null) {
                return newTexture;
            }
        }
        return texture;
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    public void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (!this.visible) {
            cir.setReturnValue(false);
        }
    }
}
