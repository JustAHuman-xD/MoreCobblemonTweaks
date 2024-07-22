package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.dystoriantweaks.Utils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Text.class)
public interface TextMixin {
    @Inject(at = @At("HEAD"), method = "translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;", cancellable = true)
    private static void translatable(String key, CallbackInfoReturnable<MutableText> cir) {
        if (!ModConfig.isEnabled("hidden_ability_indicator")) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.currentScreen == null) {
            return;
        }

        Pokemon pokemon = null;
        if (client.currentScreen instanceof PCGUI pcgui) {
            pokemon = pcgui.getPreviewPokemon$common();
        } else if (client.currentScreen instanceof Summary summary) {
            pokemon = summary.getSelectedPokemon$common();
        }

        if (pokemon != null && pokemon.getAbility().getDisplayName().equals(key) && Utils.hasHiddenAbility(pokemon)) {
            cir.setReturnValue(Text.literal("(HA) ").append(MutableText.of(new TranslatableTextContent(key, null, TranslatableTextContent.EMPTY_ARGUMENTS))));
        }
    }

    @Inject(at = @At("HEAD"), method = "translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;", cancellable = true)
    private static void translatableArgs(String key, Object[] args, CallbackInfoReturnable<MutableText> cir) {
        if (key.equals("cobblemon.ui.pc.box.title") && ModConfig.isEnabled("custom_pc_box_names") && ModConfig.getBoxName(Utils.currentBox) instanceof MutableText text) {
            cir.setReturnValue(text);
        }
    }
}
