package me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates;

import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchPredicate;

import java.util.Locale;

public class NameOrSpeciesPredicate implements SearchPredicate {
    private final String filter;

    public NameOrSpeciesPredicate(String filter) {
        this.filter = filter.toLowerCase();
    }

    @Override
    public boolean test(Pokemon pokemon) {
        return pokemon.getSpecies().resourceIdentifier.getPath().contains(filter)
                || pokemon.getDisplayName(false).getString().toLowerCase(Locale.ROOT).contains(filter);
    }
}
