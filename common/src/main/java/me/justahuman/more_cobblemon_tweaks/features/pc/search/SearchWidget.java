package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.CustomTextField;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.ChatFormatting.GRAY;

public class SearchWidget extends CustomTextField {
    private static final CommandContext<?> DUMMY_CONTEXT = new CommandContext<>(null, null, null, null, null, null, null, null, null, false);
    private static final List<String> DEFAULT_SUGGESTIONS = List.of("holding", "helditem", "held_item", "fainted", "legendary", "mythical", "ultrabeast", "ultra_beast");
    private static final List<String> IGNORED_SUGGESTIONS = new ArrayList<>();
    static {
        for (Species species : PokemonSpecies.INSTANCE.getSpecies()) {
            ResourceLocation speciesId = species.getResourceIdentifier();
            String name = speciesId.getNamespace().equals("cobblemon") ? speciesId.getPath() : speciesId.toString();
            IGNORED_SUGGESTIONS.add(name);
        }
    }

    private String suggestion = "";

    public SearchWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setBordered(false);
        setHint(PcEnhancements.translate("pc_search.blank").withStyle(GRAY));
        setResponder(string -> {
            Utils.search = Search.of(string);
            if (!string.isBlank()) {
                int start = string.lastIndexOf(' ') + 1;
                if (string.charAt(start) == '!') {
                    start += 1;
                }

                SuggestionsBuilder builder = new SuggestionsBuilder(string, start);
                DEFAULT_SUGGESTIONS.forEach(builder::suggest);
                SuggestionsBuilder ignoredBuilder = new SuggestionsBuilder(string, start);
                IGNORED_SUGGESTIONS.forEach(ignoredBuilder::suggest);

                Suggestions suggestions = PokemonPropertiesArgumentType.Companion.properties().listSuggestions(DUMMY_CONTEXT, builder).join();
                suggestions.getList().removeAll(ignoredBuilder.build().getList());

                if (!suggestions.isEmpty()) {
                    for (Suggestion suggestion : suggestions.getList()) {
                        String applied = suggestion.apply(string);
                        if (applied.startsWith(string)) {
                            if (!applied.contains("=") && !DEFAULT_SUGGESTIONS.contains(suggestion.getText())) {
                                applied += "=";
                            }
                            this.suggestion = applied;
                            return;
                        }
                    }
                }
            }
            suggestion = "";
        });
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        String original = this.getValue();
        int cursorPosition = this.getCursorPosition();
        if (isFocused() && !suggestion.isBlank() && cursorPosition == original.length()) {
            // If there's a suggestion, render it as gray text
            setValue(suggestion);
            setTextColor(11184810);
            setFocused(false);
            super.renderWidget(context, mouseX, mouseY, delta);
            setValue(original);
            setTextColor(0xFFFFFF);
            setCursorPosition(cursorPosition);
            setFocused(true);
        }

        // Render normally
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}
