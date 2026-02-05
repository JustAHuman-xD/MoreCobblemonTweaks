package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.text.Text;
import com.cobblemon.mod.common.client.gui.pc.FilterWidget;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.pokemon.Species;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kotlin.jvm.functions.Function0;
import me.justahuman.more_cobblemon_tweaks.api.FilterSuggestable;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchPredicate;
import me.justahuman.more_cobblemon_tweaks.mixins.accessor.EditBoxAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(FilterWidget.class)
public abstract class FilterWidgetMixin extends EditBox implements FilterSuggestable {
    @Unique private static final CommandContext<?> DUMMY_CONTEXT = new CommandContext<>(null, null, null, null, null, null, null, null, null, false);
    @Unique private static final List<String> IGNORED_SUGGESTIONS = PokemonSpecies.getSpecies().stream()
            .map(Species::getResourceIdentifier).map(id -> id.getNamespace().equals("cobblemon") ? id.getPath() : id.toString()).toList();

    @Unique private static final int MAX_VISIBLE_CHARACTERS = 19;

    @Unique private String moreCobblemonTweaks$search = "";
    @Unique private String moreCobblemonTweaks$suggestion = "";

    @Unique private String moreCobblemonTweaks$value = "";

    private FilterWidgetMixin(Font font, int i, int j, Component component) {
        super(font, i, j, component);
    }

    @Inject(at = @At("TAIL"), method = "<init>(IILnet/minecraft/network/chat/Component;Lkotlin/jvm/functions/Function0;)V")
    private <R> void addSuggestions(int pX, int pY, Component text, Function0<R> update, CallbackInfo ci) {
        Consumer<String> originalResponder = ((EditBoxAccessor) this).getRawResponder();
        this.setResponder(input -> {
            originalResponder.accept(input);
            String trimmed = input.trim();
            this.moreCobblemonTweaks$search = trimmed.toLowerCase();
            this.moreCobblemonTweaks$suggestion = "";
            if (input.isEmpty()) {
                return;
            }

            int start = moreCobblemonTweaks$search.lastIndexOf(' ') + 1;
            if (moreCobblemonTweaks$search.length() > start && moreCobblemonTweaks$search.charAt(start) == '!') {
                start += 1;
            }

            SuggestionsBuilder builder = new SuggestionsBuilder(moreCobblemonTweaks$search, start);
            SuggestionsBuilder additionalBuilder = new SuggestionsBuilder(moreCobblemonTweaks$search, start);
            SuggestionsBuilder ignoredBuilder = new SuggestionsBuilder(moreCobblemonTweaks$search, start);
            for (SearchPredicate predicate : SearchPredicate.ALL) {
                predicate.suggest(additionalBuilder);
            }
            for (String ignored : IGNORED_SUGGESTIONS) {
                ignoredBuilder.suggest(ignored);
            }

            CompletableFuture.supplyAsync(additionalBuilder::build).thenAcceptAsync(suggestions -> {
                if (!moreCobblemonTweaks$trySetSuggestion(trimmed, suggestions)) {
                    CompletableFuture.supplyAsync(() -> PokemonPropertiesArgumentType.Companion.properties().listSuggestions(DUMMY_CONTEXT, builder).join())
                            .thenComposeAsync(propertiesSuggestions -> CompletableFuture.supplyAsync(ignoredBuilder::build)
                                    .thenAcceptAsync(ignored -> {
                                        propertiesSuggestions.getList().removeAll(ignored.getList());
                                        moreCobblemonTweaks$trySetSuggestion(trimmed, propertiesSuggestions);
                                    }));
                }
            });
        });
    }

    @Override
    public void moreCobblemonTweaks$fillSuggestion() {
        setValue(moreCobblemonTweaks$suggestion);
    }

    @Unique
    private boolean moreCobblemonTweaks$trySetSuggestion(String trimmed, Suggestions suggestions) {
        for (Suggestion suggestion : suggestions.getList()) {
            String text = suggestion.getText().trim();
            String applied = suggestion.apply(moreCobblemonTweaks$search).trim();
            if (applied.length() > moreCobblemonTweaks$search.length() && applied.startsWith(moreCobblemonTweaks$search)) {
                if (!text.contains("=") && !SearchPredicate.FIXED.containsKey(text)) {
                    applied += "=";
                }
                applied = trimmed + applied.substring(moreCobblemonTweaks$search.length());
                this.moreCobblemonTweaks$suggestion = applied;
                return true;
            }
        }
        return false;
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/gui/GuiUtilsKt;blitk$default(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;ZFILjava/lang/Object;)V"))
    private void tempTrimValue(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        moreCobblemonTweaks$value = getValue();
        String trimmedValue = moreCobblemonTweaks$value;
        if (trimmedValue.length() > MAX_VISIBLE_CHARACTERS) {
            int cursorPos = this.getCursorPosition();
            if (cursorPos <= MAX_VISIBLE_CHARACTERS) {
                trimmedValue = trimmedValue.substring(0, MAX_VISIBLE_CHARACTERS);
            } else if (cursorPos >= trimmedValue.length() - MAX_VISIBLE_CHARACTERS) {
                trimmedValue = trimmedValue.substring(trimmedValue.length() - MAX_VISIBLE_CHARACTERS);
            } else {
                trimmedValue = trimmedValue.substring(cursorPos - MAX_VISIBLE_CHARACTERS, cursorPos);
            }
        }
        ((EditBoxAccessor) this).setDirectValue(trimmedValue);
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/client/gui/pc/FilterWidget;renderCursor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/network/chat/MutableComponent;)V"))
    private void restoreValue(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ((EditBoxAccessor) this).setDirectValue(moreCobblemonTweaks$value);
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/client/render/RenderHelperKt;drawScaledText$default(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/chat/MutableComponent;Ljava/lang/Number;Ljava/lang/Number;FLjava/lang/Number;IIZZLjava/lang/Integer;Ljava/lang/Integer;ILjava/lang/Object;)V"), method = "renderWidget")
    private void renderSuggestion(GuiGraphics context, ResourceLocation font, MutableComponent text, Number x, Number y, float scale, Number opacity, int maxCharacterWidth, int colour, boolean centered, boolean shadow, Integer pMouseX, Integer pMouseY, int i, Object o, Operation<Void> original) {
        int cursorPos = this.getCursorPosition();
        int suggestionLength = moreCobblemonTweaks$suggestion.length();
        if (isFocused() && !moreCobblemonTweaks$suggestion.isBlank() && cursorPos == moreCobblemonTweaks$search.length() && suggestionLength <= MAX_VISIBLE_CHARACTERS) {
            RenderHelperKt.drawScaledText(context, font, new Text().parse(moreCobblemonTweaks$suggestion).withStyle(ChatFormatting.BOLD), x, y, 1F, 1F, Integer.MAX_VALUE, 11184810, false, false, null, null);
        }
        original.call(context, font, text, x, y, scale, opacity, maxCharacterWidth, colour, centered, shadow, pMouseX, pMouseY, i, o);
    }
}
