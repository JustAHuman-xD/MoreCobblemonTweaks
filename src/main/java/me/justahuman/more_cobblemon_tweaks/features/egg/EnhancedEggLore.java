package me.justahuman.more_cobblemon_tweaks.features.egg;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.List;

public abstract class EnhancedEggLore {
    protected final NbtCompound nbt;

    protected EnhancedEggLore(NbtCompound nbt) {
        this.nbt = nbt;
    }

    public Text getName(List<Text> lore) {
        return lore.get(0);
    }

    public abstract boolean isShiny();
    public abstract String getGender();
    public abstract List<Text> getHatchProgress();
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
