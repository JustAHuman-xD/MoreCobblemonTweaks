package me.justahuman.more_cobblemon_tweaks.features.egg;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.util.ResourceLocationExtensionsKt;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public abstract class EnhancedEggLore {
    public Component getName(List<Component> lore) {
        return lore.getFirst();
    }
    public abstract boolean isShiny();
    public abstract String getGender();
    public abstract List<Component> getHatchProgress(List<Component> lore);
    public abstract String getNature();
    public abstract String getAbility();
    public abstract String getForm();
    public abstract String getPokeBall();
    protected String pokeBallFromId(String pokeBallId) {
        PokeBall pokeBall = PokeBalls.getPokeBall(ResourceLocationExtensionsKt.asIdentifierDefaultingNamespace(pokeBallId, Cobblemon.MODID));
        return pokeBall == null ? pokeBallId : pokeBall.item.getDefaultInstance().getHoverName().getString();
    }
    public abstract boolean hasIVs();
    public boolean hasIVs(int iv) {
        return hasIVs()
                && Objects.equals(iv, getHpIV())
                && Objects.equals(iv, getAtkIV())
                && Objects.equals(iv, getDefIV())
                && Objects.equals(iv, getSpAtkIV())
                && Objects.equals(iv, getSpDefIV())
                && Objects.equals(iv, getSpeedIV());
    }
    public abstract Integer getHpIV();
    public abstract Integer getAtkIV();
    public abstract Integer getDefIV();
    public abstract Integer getSpAtkIV();
    public abstract Integer getSpDefIV();
    public abstract Integer getSpeedIV();
}
