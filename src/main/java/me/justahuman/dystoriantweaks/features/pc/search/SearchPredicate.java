package me.justahuman.dystoriantweaks.features.pc.search;

import com.cobblemon.mod.common.pokemon.Pokemon;

@FunctionalInterface
public interface SearchPredicate {
    boolean passes(Pokemon pokemon);
}