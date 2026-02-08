package me.justahuman.more_cobblemon_tweaks.features.egg;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import net.allthemods.allthemons.registry.PokemonDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class AllTheMonsEggLore extends EnhancedEggLore {
    private final ItemStack eggStack;

    protected AllTheMonsEggLore(ItemStack eggStack) {
        this.eggStack = eggStack;
    }

    @Override
    public Component getName(List<Component> lore) {
        if (eggStack.has(PokemonDataComponents.SPECIES.get())) {
            ResourceLocation speciesId = eggStack.get(PokemonDataComponents.SPECIES.get());
            Species species = PokemonSpecies.getByIdentifier(speciesId);
            if (species != null) {
                return LoreEnhancements.translate("egg.allthemons.name", species.getTranslatedName());
            }
        }
        return super.getName(lore);
    }

    @Override
    public ChatFormatting getShinyColor() {
        return null;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public List<Component> getHatchProgress(List<Component> lore) {
        if (!eggStack.has(PokemonDataComponents.EGG_TIME.get())) {
            return List.of();
        }

        int ticks = eggStack.getOrDefault(PokemonDataComponents.EGG_TIME.get(), 0);
        int minutes = ticks / 1200;
        int seconds = (ticks % 1200) / 20;
        return List.of(LoreEnhancements.translate("egg.allthemons.hatch_progress", minutes, seconds));
    }

    @Override
    public String getNature() {
        String natureId = eggStack.get(PokemonDataComponents.NATURE.get());
        if (natureId == null) {
            return null;
        }
        ResourceLocation id = natureId.contains(":") ? ResourceLocation.tryParse(natureId) : MiscUtilsKt.cobblemonResource(natureId);
        Nature nature = id == null ? null : Natures.getNature(id);
        return nature == null ? null : Component.translatable(nature.getDisplayName()).getString();
    }

    @Override
    public String getAbility() {
        return null;
    }

    @Override
    public String getForm() {
        return null;
    }

    @Override
    public String getPokeBall() {
        return null;
    }

    @Override
    public boolean hasIVs() {
        return eggStack.has(PokemonDataComponents.IVS.get());
    }

    @Override
    public Integer getHpIV() {
        Map<String, Integer> ivs = eggStack.getOrDefault(PokemonDataComponents.IVS.get(), Map.of());
        return ivs.getOrDefault(Stats.HP.getIdentifier().getPath(), 0);
    }

    @Override
    public Integer getAtkIV() {
        Map<String, Integer> ivs = eggStack.getOrDefault(PokemonDataComponents.IVS.get(), Map.of());
        return ivs.getOrDefault(Stats.ATTACK.getIdentifier().getPath(), 0);
    }

    @Override
    public Integer getDefIV() {
        Map<String, Integer> ivs = eggStack.getOrDefault(PokemonDataComponents.IVS.get(), Map.of());
        return ivs.getOrDefault(Stats.DEFENCE.getIdentifier().getPath(), 0);
    }

    @Override
    public Integer getSpAtkIV() {
        Map<String, Integer> ivs = eggStack.getOrDefault(PokemonDataComponents.IVS.get(), Map.of());
        return ivs.getOrDefault(Stats.SPECIAL_ATTACK.getIdentifier().getPath(), 0);
    }

    @Override
    public Integer getSpDefIV() {
        Map<String, Integer> ivs = eggStack.getOrDefault(PokemonDataComponents.IVS.get(), Map.of());
        return ivs.getOrDefault(Stats.SPECIAL_DEFENCE.getIdentifier().getPath(), 0);
    }

    @Override
    public Integer getSpeedIV() {
        Map<String, Integer> ivs = eggStack.getOrDefault(PokemonDataComponents.IVS.get(), Map.of());
        return ivs.getOrDefault(Stats.SPEED.getIdentifier().getPath(), 0);
    }

    @Override
    public void finalize(List<Component> lore, List<Component> newLore) {
        Component name = lore.getFirst();
        lore.clear();
        lore.add(name);
    }

    public static AllTheMonsEggLore get(ItemStack eggStack) {
        return new AllTheMonsEggLore(eggStack);
    }
}
