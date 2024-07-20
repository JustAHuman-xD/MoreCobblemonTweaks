package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType;
import me.justahuman.dystoriantweaks.Utils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.justahuman.dystoriantweaks.Utils.HIDDEN_CACHE;

@Mixin(Text.class)
public interface TextMixin {
    @Inject(at = @At("HEAD"), method = "translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;", cancellable = true)
    private static void translatable(String key, CallbackInfoReturnable<MutableText> cir) {
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

        if (pokemon == null) {
            return;
        }

        Ability ability = pokemon.getAbility();
        String abilityName = ability.getName();

        if (ability.getDisplayName().equals(key)) {
            Boolean cache = HIDDEN_CACHE.get(abilityName);
            if (cache != null) {
                if (Boolean.TRUE.equals(cache)) {
                    cir.setReturnValue(Text.literal("(HA) ").append(MutableText.of(new TranslatableTextContent(key, null, TranslatableTextContent.EMPTY_ARGUMENTS))));
                }
                return;
            }

            for (PotentialAbility potentialAbility : pokemon.getForm().getAbilities()) {
                if (potentialAbility.getTemplate() == ability.getTemplate()) {
                    if (potentialAbility.getType() == HiddenAbilityType.INSTANCE) {
                        HIDDEN_CACHE.put(abilityName, true);
                        cir.setReturnValue(Text.literal("(HA) ").append(MutableText.of(new TranslatableTextContent(key, null, TranslatableTextContent.EMPTY_ARGUMENTS))));
                        return;
                    }
                    HIDDEN_CACHE.put(abilityName, false);
                    return;
                }
            }

            HIDDEN_CACHE.put(abilityName, false);
        }
    }

    @Inject(at = @At("HEAD"), method = "translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;", cancellable = true)
    private static void translatableArgs(String key, Object[] args, CallbackInfoReturnable<MutableText> cir) {
        if (key.equals("cobblemon.ui.pc.box.title") && ModConfig.getBoxName(Utils.currentBox) instanceof MutableText text) {
            cir.setReturnValue(text);
        }
    }
}
