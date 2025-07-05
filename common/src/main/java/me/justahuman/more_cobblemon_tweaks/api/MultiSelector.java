package me.justahuman.more_cobblemon_tweaks.api;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;

import java.util.List;

public interface MultiSelector {
    boolean moreCobblemonTweaks$isMultiSelecting();
    List<PCPosition> moreCobblemonTweaks$getSelectedPositions();
    void moreCobblemonTweaks$clearSelection();
}
