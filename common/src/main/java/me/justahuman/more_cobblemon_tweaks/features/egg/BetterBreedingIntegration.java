package me.justahuman.more_cobblemon_tweaks.features.egg;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BetterBreedingIntegration extends CobBreedingIntegration {
    public BetterBreedingIntegration(CompoundTag nbt) {
        super(nbt);
    }

    @Override
    public Component getName(List<Component> lore) {
        String speciesName = Utils.get(nbt, "species", "");
        Species species = speciesName.isBlank()
                ? PokemonSpecies.INSTANCE.getByPokedexNumber(Utils.get(nbt, "species", -1), "cobblemon")
                : PokemonSpecies.INSTANCE.getByName(speciesName);
        if (species != null) {
            return species.getTranslatedName();
        }
        return lore.get(0);
    }

    @Override
    public String getGender() {
        return Utils.get(nbt, "gender", "NONE");
    }

    @Override
    public List<Component> getHatchProgress() {
        int ticks = Utils.get(nbt, "timer", -1);
        if (ticks != -1) {
            int minutes = (int) Math.floor(ticks / 1200d);
            int seconds = (int) Math.floor((ticks % 1200) / 20d);
            return List.of(LoreEnhancements.translate("egg.better_breeding.hatch_progress", minutes, seconds));
        }
        return null;
    }

    @Override
    public String getForm() {
        return Utils.get(nbt, "form", "");
    }

    public static boolean isEgg(CompoundTag nbt) {
        return nbt.contains("species", Tag.TAG_STRING) && nbt.contains("timer", Tag.TAG_INT);
    }
}
