package me.justahuman.more_cobblemon_tweaks.features.egg;

import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class EnhancedEggLore {
    public Component getName(List<Component> lore) {
        return lore.getFirst();
    }
    public abstract boolean isShiny();
    public abstract String getGender();
    public abstract List<Component> getHatchProgress();
    public abstract String getNature();
    public abstract String getAbility();
    public abstract String getForm();
    public abstract boolean hasIVs();
    public abstract Integer getHpIV();
    public abstract Integer getAtkIV();
    public abstract Integer getDefIV();
    public abstract Integer getSpAtkIV();
    public abstract Integer getSpDefIV();
    public abstract Integer getSpeedIV();
}
