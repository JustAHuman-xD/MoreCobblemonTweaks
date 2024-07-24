package me.justahuman.dystoriantweaks.mixins;

import me.justahuman.dystoriantweaks.Utils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Text.class)
public interface TextMixin {
    @Inject(at = @At("HEAD"), method = "translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;", cancellable = true)
    private static void translatableArgs(String key, Object[] args, CallbackInfoReturnable<MutableText> cir) {
        if (key.equals("cobblemon.ui.pc.box.title") && ModConfig.isEnabled("custom_pc_box_names") && ModConfig.getBoxName(Utils.currentBox) instanceof MutableText text) {
            cir.setReturnValue(text);
        }
    }
}
