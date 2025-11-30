package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import com.cobblemon.mod.common.api.storage.pc.search.PokemonFilter;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates.EggGroupPredicate;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates.SimplePredicate;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public interface SearchPredicate extends PokemonFilter {
    Set<SearchPredicate> ALL = new HashSet<>();
    Map<String, SearchPredicate> FIXED = new HashMap<>();

    SearchPredicate HOLDING = new SimplePredicate(
            List.of("holding", "helditem", "held_item"),
            pokemon -> !pokemon.heldItem().isEmpty()
    );

    SearchPredicate FAINTED = new SimplePredicate(
            List.of("fainted"),
            Pokemon::isFainted
    );

    SearchPredicate LEGENDARY = new SimplePredicate(
            List.of("legendary"),
            Pokemon::isLegendary
    );

    SearchPredicate MYTHICAL = new SimplePredicate(
            List.of("mythical"),
            Pokemon::isMythical
    );

    SearchPredicate ULTRA_BEAST = new SimplePredicate(
            List.of("ultrabeast", "ultra_beast"),
            Pokemon::isUltraBeast
    );

    SearchPredicate EGG_GROUP = new EggGroupPredicate();

    default void suggest(SuggestionsBuilder builder) {}

    @Override
    default @NotNull SearchPredicate inverted() {
        return pokemon -> !test(pokemon);
    }

    static void register(SearchPredicate predicate) {
        ALL.add(predicate);
    }

    static void register(SearchPredicate predicate, List<String> autocomplete) {
        register(predicate);
        for (String name : autocomplete) {
            FIXED.put(name, predicate);
        }
    }
}