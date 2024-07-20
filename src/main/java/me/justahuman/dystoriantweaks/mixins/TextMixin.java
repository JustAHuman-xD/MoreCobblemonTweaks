package me.justahuman.dystoriantweaks.mixins;

import me.justahuman.dystoriantweaks.DystorianTweaks;
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
    private static void translatable(String key, Object[] args, CallbackInfoReturnable<MutableText> cir) {
        if (key.equals("cobblemon.ui.pc.box.title") && args[0] instanceof String boxNum) {
            try {
                int box = Integer.parseInt(boxNum);
                MutableText text = ModConfig.getBoxName(box - 1);
                if (text != null) {
                    cir.setReturnValue(text);
                }
            } catch (Exception e) {
                DystorianTweaks.LOGGER.error("Couldn't replace the box name!", e);
            }
        }
    }
}
