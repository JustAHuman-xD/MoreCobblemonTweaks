package me.justahuman.more_cobblemon_tweaks.api;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.SlotPosition;

import java.util.List;

public interface MultiSelector {
    boolean moreCobblemonTweaks$isMultiSelecting();
    boolean moreCobblemonTweaks$isSelected(PCPosition position);
    List<Pokemon> moreCobblemonTweaks$getSelectedPokemon();
    SlotPosition moreCobblemonTweaks$getSelectionOrigin();
    SlotPosition moreCobblemonTweaks$getHoveredSlot(int mouseX, int mouseY);
    void moreCobblemonTweaks$clearMultiSelection();
}
