package me.justahuman.more_cobblemon_tweaks.api;

import java.util.function.Supplier;

public interface ConditionalIconButton {
    void setCondition(Supplier<Boolean> condition);
}
