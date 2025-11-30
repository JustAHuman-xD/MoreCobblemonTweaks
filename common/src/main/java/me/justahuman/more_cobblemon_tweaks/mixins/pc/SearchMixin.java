package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.api.storage.pc.search.PokemonFilter;
import com.cobblemon.mod.common.api.storage.pc.search.Search;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchPredicate;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates.EggGroupPredicate;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates.NameOrSpeciesPredicate;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Mixin(Search.Companion.class)
public class SearchMixin {
    @Unique private static final PokemonProperties moreCobblemonTweaks$NONE = new PokemonProperties();

    /**
     * @author JustAHuman
     * @reason This is the simplest way to add custom search predicates
     * TODO: PR an api for this to Cobblemon & the performance improvements (only parsing properties once)
     */
    @Overwrite
    public final @NotNull Search of(String search) {
        if (search == null || search.isBlank()) {
            return Search.Companion.getDEFAULT();
        }

        String[] options = search.toLowerCase(Locale.ROOT).trim().split(" ");
        Set<PokemonFilter> filters = new HashSet<>();
        for (String filter : options) {
            boolean inverted = false;
            if (filter.startsWith("!")) {
                inverted = true;
                filter = filter.substring(1);
            }

            PokemonFilter pokemonFilter = SearchPredicate.FIXED.get(filter);
            if (pokemonFilter == null) {
                String[] parts = filter.split("=", 2);
                if (parts.length == 2) {
                    if (EggGroupPredicate.KEY.equalsIgnoreCase(parts[0] + "=")) {
                        try {
                            EggGroup group = EggGroup.valueOf(parts[1].toUpperCase(Locale.ROOT));
                            pokemonFilter = new EggGroupPredicate(group);
                        } catch (IllegalArgumentException ignored) {}
                    } else {
                        pokemonFilter = SearchPredicate.FIXED.get(parts[0]);
                        if (parts[1].equals("false") || parts[1].equals("no")) {
                            inverted = !inverted;
                        }
                    }
                }

                if (pokemonFilter == null) {
                    PokemonProperties props = PokemonProperties.Companion.parse(filter);
                    if (!props.matches(moreCobblemonTweaks$NONE)) {
                        pokemonFilter = props::matches;
                    } else {
                        pokemonFilter = new NameOrSpeciesPredicate(filter);
                    }
                }
            }

            if (inverted) {
                filters.add(pokemonFilter.inverted());
            } else {
                filters.add(pokemonFilter);
            }
        }
        return new Search(filters, new HashSet<>(), new HashSet<>());
    }
}
