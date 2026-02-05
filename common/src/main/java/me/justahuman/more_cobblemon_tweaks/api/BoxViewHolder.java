package me.justahuman.more_cobblemon_tweaks.api;

import me.justahuman.more_cobblemon_tweaks.features.pc.boxes.BoxListSlot;

public interface BoxViewHolder {
    boolean moreCobblemonTweaks$isBoxListOpen();
    BoxListSlot moreCobblemonTweaks$getSelectedBoxListSlot();
    BoxListSlot moreCobblemonTweaks$getPreviewedBoxListSlot(double mouseX, double mouseY);
}
