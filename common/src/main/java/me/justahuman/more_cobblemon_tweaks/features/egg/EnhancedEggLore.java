package me.justahuman.more_cobblemon_tweaks.features.egg;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.util.ResourceLocationExtensionsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class EnhancedEggLore {
    private static final List<Function<ItemStack, EnhancedEggLore>> EGG_LORE_FACTORIES = new ArrayList<>();

    public Component getName(List<Component> lore) {
        return lore.getFirst();
    }
    public abstract ChatFormatting getShinyColor();
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
    public final Integer hpIV() {
        Integer hpIV = getHpIV();
        return hpIV == null ? null : Math.max(0, Math.min(31, hpIV));
    }
    public final Integer atkIV() {
        Integer atkIV = getAtkIV();
        return atkIV == null ? null : Math.max(0, Math.min(31, atkIV));
    }
    public final Integer defIV() {
        Integer defIV = getDefIV();
        return defIV == null ? null : Math.max(0, Math.min(31, defIV));
    }
    public final Integer spAtkIV() {
        Integer spAtkIV = getSpAtkIV();
        return spAtkIV == null ? null : Math.max(0, Math.min(31, spAtkIV));
    }
    public final Integer spDefIV() {
        Integer spDefIV = getSpDefIV();
        return spDefIV == null ? null : Math.max(0, Math.min(31, spDefIV));
    }
    public final Integer speedIV() {
        Integer speedIV = getSpeedIV();
        return speedIV == null ? null : Math.max(0, Math.min(31, speedIV));
    }
    public void finalize(List<Component> lore, List<Component> newLore) {}

    public static void registerFactory(Function<ItemStack, EnhancedEggLore> supplier) {
        EGG_LORE_FACTORIES.add(supplier);
    }

    public static EnhancedEggLore get(ItemStack eggStack) {
        for (Function<ItemStack, EnhancedEggLore> supplier : EGG_LORE_FACTORIES) {
            EnhancedEggLore lore = supplier.apply(eggStack);
            if (lore != null) {
                return lore;
            }
        }
        return null;
    }
}
