package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import com.cobblemon.mod.common.pokemon.Pokemon;

@FunctionalInterface
public interface SearchPredicate {
    boolean passes(Pokemon pokemon);
}