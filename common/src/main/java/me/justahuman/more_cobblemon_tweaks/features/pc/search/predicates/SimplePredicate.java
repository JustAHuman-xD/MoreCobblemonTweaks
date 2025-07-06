package me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchPredicate;

import java.util.List;
import java.util.function.Predicate;

public class SimplePredicate implements SearchPredicate {
    private final List<String> names;
    private final Predicate<Pokemon> predicate;

    public SimplePredicate(List<String> names, Predicate<Pokemon> predicate) {
        this.names = names;
        this.predicate = predicate;
        SearchPredicate.register(this, names);
    }

    @Override
    public void suggest(SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String name : names) {
            if (name.startsWith(remaining)) {
                builder.suggest(name);
            }
        }
    }

    @Override
    public boolean passes(Pokemon pokemon) {
        return predicate.test(pokemon);
    }
}
