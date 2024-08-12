package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiUtilsKt.class)
public abstract class GuiUtilsMixin {
    @Unique
    private static final String BOX_OLD_TEXTURE = "textures/gui/pc/pc_screen_overlay.png";
    @Unique
    private static final Identifier DEFAULT_TEXTURE = new Identifier("dystoriantweaks", "textures/gui/pc/wallpapers/default_wallpaper.png");

    @Inject(at = @At("HEAD"), method = "blitk", cancellable = true)
    private static void blitk(MatrixStack matrixStack, Identifier texture, Number x, Number y, Number height, Number width, Number uOffset, Number vOffset, Number textureWidth, Number textureHeight, Number blitOffset, Number red, Number green, Number blue, Number alpha, boolean blend, float scale, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof PCGUI && ModConfig.isEnabled("custom_pc_wallpapers") && texture.getPath().equals(BOX_OLD_TEXTURE)) {
            Identifier newTexture = ModConfig.getBoxTexture(Utils.currentBox);
            if (newTexture == null) {
                newTexture = DEFAULT_TEXTURE;
            }
            GuiUtilsKt.blitk(matrixStack, newTexture, x, y, height, width, uOffset, vOffset, textureWidth, textureHeight, blitOffset, red, green, blue, alpha, blend, scale);
            ci.cancel();
        }
    }
}
