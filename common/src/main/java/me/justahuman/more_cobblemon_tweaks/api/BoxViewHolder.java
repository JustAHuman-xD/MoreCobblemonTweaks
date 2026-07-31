package me.justahuman.more_cobblemon_tweaks.api;

import com.cobblemon.mod.common.api.pokemon.PokemonSortMode;
import me.justahuman.more_cobblemon_tweaks.features.pc.boxes.BoxListSlot;

public interface BoxViewHolder {
    boolean moreCobblemonTweaks$isBoxListOpen();
    BoxListSlot moreCobblemonTweaks$getSelectedBoxListSlot();
    BoxListSlot moreCobblemonTweaks$getPreviewedBoxListSlot(double mouseX, double mouseY);
    void moreCobblemonTweaks$sortAllBoxes(PokemonSortMode sortMode, boolean descending);
}
