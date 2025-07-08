package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.mixins.EditBoxAccessor;
import me.justahuman.more_cobblemon_tweaks.utils.CustomTextField;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.ChatFormatting.GRAY;

public class SearchWidget extends CustomTextField {
    private static final CommandContext<?> DUMMY_CONTEXT = new CommandContext<>(null, null, null, null, null, null, null, null, null, false);
    private static final List<String> IGNORED_SUGGESTIONS = PokemonSpecies.INSTANCE.getSpecies().stream()
            .map(Species::getResourceIdentifier).map(id -> id.getNamespace().equals("cobblemon") ? id.getPath() : id.toString()).toList();

    private String search = "";
    private String suggestion = "";

    public SearchWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setBordered(false);
        setHint(PcEnhancements.translate("pc_search.blank").withStyle(GRAY));
        setResponder(string -> {
            this.search = string.trim().toLowerCase();;
            this.suggestion = "";
            Utils.search = null;
            if (search.isBlank()) {
                return;
            }

            Utils.search = Search.of(this.search);
            int start = search.lastIndexOf(' ') + 1;
            if (search.length() > start && search.charAt(start) == '!') {
                start += 1;
            }

            SuggestionsBuilder builder = new SuggestionsBuilder(search, start);
            SuggestionsBuilder additionalBuilder = new SuggestionsBuilder(search, start);
            SuggestionsBuilder ignoredBuilder = new SuggestionsBuilder(search, start);
            for (SearchPredicate predicate : SearchPredicate.ALL) {
                predicate.suggest(additionalBuilder);
            }
            for (String ignored : IGNORED_SUGGESTIONS) {
                ignoredBuilder.suggest(ignored);
            }

            CompletableFuture.supplyAsync(additionalBuilder::build).thenAcceptAsync(suggestions -> {
                if (!trySetSuggestion(suggestions)) {
                    CompletableFuture.supplyAsync(() -> PokemonPropertiesArgumentType.Companion.properties().listSuggestions(DUMMY_CONTEXT, builder).join())
                            .thenComposeAsync(propertiesSuggestions -> CompletableFuture.supplyAsync(ignoredBuilder::build)
                                    .thenAcceptAsync(ignored -> {
                                        propertiesSuggestions.getList().removeAll(ignored.getList());
                                        trySetSuggestion(propertiesSuggestions);
                                    }));
                }
            });
        });
    }

    public boolean trySetSuggestion(Suggestions suggestions) {
        for (Suggestion suggestion : suggestions.getList()) {
            String text = suggestion.getText().trim();
            String applied = suggestion.apply(search).trim();
            if (applied.length() > search.length() && applied.startsWith(search)) {
                if (!text.contains("=") && !SearchPredicate.FIXED.containsKey(text)) {
                    applied += "=";
                }
                this.suggestion = applied;
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int cursorPosition = this.getCursorPosition();
        if (isFocused() && !suggestion.isBlank() && cursorPosition == search.length()) {
            // If there's a suggestion, render it as gray text
            ((EditBoxAccessor) this).setDirectValue(suggestion);
            setTextColor(11184810);
            setFocused(false);
            super.renderWidget(context, mouseX, mouseY, delta);
            ((EditBoxAccessor) this).setDirectValue(search);
            setTextColor(0xFFFFFF);
            setCursorPosition(cursorPosition);
            setFocused(true);
        }

        // Render normally
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    public void fillSuggestion() {
        setValue(suggestion);
    }
}
