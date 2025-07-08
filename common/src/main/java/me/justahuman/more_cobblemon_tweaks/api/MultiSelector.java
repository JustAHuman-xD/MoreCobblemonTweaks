package me.justahuman.more_cobblemon_tweaks.api;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.List;

public interface MultiSelector {
    boolean moreCobblemonTweaks$isMultiSelecting();
    List<PCPosition> moreCobblemonTweaks$getSelectedPositions();
    List<Pokemon> moreCobblemonTweaks$getSelectedPokemon();
    void moreCobblemonTweaks$clearMultiSelection();
}
