package me.justahuman.more_cobblemon_tweaks.features.egg;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class EnhancedEggLore {
    protected final CompoundTag nbt;

    protected EnhancedEggLore(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public Component getName(List<Component> lore) {
        return lore.get(0);
    }

    public abstract boolean isShiny();
    public abstract String getGender();
    public abstract List<Component> getHatchProgress();
    public abstract String getNature();
    public abstract String getAbility();
    public abstract String getForm();
    public abstract boolean hasIVs();
    public abstract short getHpIV();
    public abstract short getAtkIV();
    public abstract short getDefIV();
    public abstract short getSpAtkIV();
    public abstract short getSpDefIV();
    public abstract short getSpeedIV();
}
