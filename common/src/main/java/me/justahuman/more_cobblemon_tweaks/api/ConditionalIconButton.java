package me.justahuman.more_cobblemon_tweaks.api;

import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public interface ConditionalIconButton {
    void moreCobblemonTweaks$setCondition(Supplier<Boolean> condition);
    void moreCobblemonTweaks$setTooltip(Supplier<Component> tooltip);
}
