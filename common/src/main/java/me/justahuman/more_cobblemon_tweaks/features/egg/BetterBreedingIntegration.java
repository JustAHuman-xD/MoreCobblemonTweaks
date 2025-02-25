package me.justahuman.more_cobblemon_tweaks.features.egg;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Species;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class BetterBreedingIntegration extends EnhancedEggLore {
    private final CompoundTag customData;
    public BetterBreedingIntegration(CompoundTag customData) {
        this.customData = customData;
    }

    @Override
    public Component getName(List<Component> lore) {
        String speciesName = Utils.get(customData, "species", "");
        Species species = speciesName.isBlank()
                ? PokemonSpecies.INSTANCE.getByPokedexNumber(Utils.get(customData, "species", -1), "cobblemon")
                : PokemonSpecies.INSTANCE.getByName(speciesName);
        if (species != null) {
            return species.getTranslatedName();
        }
        return lore.getFirst();
    }

    @Override
    public boolean isShiny() {
        return Utils.get(customData, "shiny", false);
    }

    @Override
    public String getGender() {
        return Utils.get(customData, "gender", "NONE");
    }

    @Override
    public List<Component> getHatchProgress() {
        int ticks = Utils.get(customData, "timer", -1);
        if (ticks != -1) {
            int minutes = (int) Math.floor(ticks / 1200d);
            int seconds = (int) Math.floor((ticks % 1200) / 20d);
            return List.of(LoreEnhancements.translate("egg.better_breeding.hatch_progress", minutes, seconds));
        }
        return null;
    }

    @Override
    public String getNature() {
        ResourceLocation identifier = ResourceLocation.tryParse(Utils.get(customData, "nature", ""));
        if (identifier == null) {
            return null;
        }
        Nature nature = Natures.INSTANCE.getNature(identifier);
        if (nature != null) {
            return Component.translatable(nature.getDisplayName()).getString();
        }
        return null;
    }

    @Override
    public String getAbility() {
        String id = Utils.get(customData, "ability", null);
        AbilityTemplate ability = id == null ? null : Abilities.INSTANCE.get(id);
        if (ability != null) {
            return Component.translatable(ability.getDisplayName()).getString();
        }
        return id;
    }

    @Override
    public String getForm() {
        return Utils.get(customData, "form", null);
    }

    @Override
    public boolean hasIVs() {
        return customData.contains("ivs", Tag.TAG_INT_ARRAY);
    }

    @Override
    public Integer getHpIV() {
        return customData.getIntArray("ivs")[0];
    }

    @Override
    public Integer getAtkIV() {
        return customData.getIntArray("ivs")[1];
    }

    @Override
    public Integer getDefIV() {
        return customData.getIntArray("ivs")[2];
    }

    @Override
    public Integer getSpAtkIV() {
        return customData.getIntArray("ivs")[3];
    }

    @Override
    public Integer getSpDefIV() {
        return customData.getIntArray("ivs")[4];
    }

    @Override
    public Integer getSpeedIV() {
        return customData.getIntArray("ivs")[5];
    }

    public static BetterBreedingIntegration get(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.getUnsafe();
            if (tag.contains("species", Tag.TAG_STRING) && tag.contains("timer", Tag.TAG_INT)) {
                return new BetterBreedingIntegration(tag);
            }
        }
        return null;
    }
}
