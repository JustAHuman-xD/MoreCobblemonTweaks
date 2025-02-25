package me.justahuman.more_cobblemon_tweaks.features.egg;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import ludichat.cobbreeding.PokemonEgg;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CobbreedingIntegration extends EnhancedEggLore {
    private final PokemonProperties properties;
    private final IVs ivs;

    public CobbreedingIntegration(PokemonProperties properties) {
        this.properties = properties;
        this.ivs = properties.getIvs();
    }

    @Override
    public boolean isShiny() {
        return Boolean.TRUE.equals(properties.getShiny());
    }

    @Override
    public String getGender() {
        Gender gender = properties.getGender();
        return gender == null ? "NONE" : gender.name();
    }

    @Override
    public List<Component> getHatchProgress() {
        // Cobb Breeding does this themselves
        return null;
    }

    @Override
    public String getNature() {
        String natureId = properties.getNature();
        if (natureId == null) {
            return null;
        }
        ResourceLocation id = natureId.contains(":") ? ResourceLocation.tryParse(natureId) : MiscUtilsKt.cobblemonResource(natureId);
        Nature nature = id == null ? null : Natures.INSTANCE.getNature(id);
        return nature == null ? null : Component.translatable(nature.getDisplayName()).getString();
    }

    @Override
    public String getAbility() {
        String abilityId = properties.getAbility();
        AbilityTemplate ability = abilityId == null ? null : Abilities.INSTANCE.get(abilityId);
        return ability == null ? null : Component.translatable(ability.getDisplayName()).getString();
    }

    @Override
    public String getForm() {
        // Cobb Breeding does this themselves
        return null;
    }

    @Override
    public boolean hasIVs() {
        return ivs != null;
    }

    @Override
    public Integer getHpIV() {
        return ivs.get(Stats.HP);
    }

    @Override
    public Integer getAtkIV() {
        return ivs.get(Stats.ATTACK);
    }

    @Override
    public Integer getDefIV() {
        return ivs.get(Stats.DEFENCE);
    }

    @Override
    public Integer getSpAtkIV() {
        return ivs.get(Stats.SPECIAL_ATTACK);
    }

    @Override
    public Integer getSpDefIV() {
        return ivs.get(Stats.SPECIAL_DEFENCE);
    }

    @Override
    public Integer getSpeedIV() {
        return ivs.get(Stats.SPEED);
    }

    public static CobbreedingIntegration get(ItemStack itemStack) {
        if (itemStack.getItem() instanceof PokemonEgg && itemStack.has(PokemonEgg.Companion.getPOKEMON_PROPERTIES())) {
            return new CobbreedingIntegration(itemStack.get(PokemonEgg.Companion.getPOKEMON_PROPERTIES()));
        }
        return null;
    }
}
