package me.justahuman.more_cobblemon_tweaks.features.egg;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.pokemon.Nature;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class CobBreedingIntegration extends EnhancedEggLore {
    public static final String ITEM_ID = "cobbreeding:pokemon_egg";
    public CobBreedingIntegration(NbtCompound nbt) {
        super(nbt);
    }

    @Override
    public boolean isShiny() {
        return Utils.get(nbt, "shiny", false);
    }

    @Override
    public String getGender() {
        return "NONE";
    }

    @Override
    public List<Text> getHatchProgress() {
        // Cobb Breeding does this themselves
        return null;
    }

    @Override
    public String getNature() {
        Nature nature = Natures.INSTANCE.getNature(new Identifier(Utils.get(nbt, "nature", "")));
        if (nature != null) {
            return Text.translatable(nature.getDisplayName()).getString();
        }
        return "";
    }

    @Override
    public String getAbility() {
        String id = Utils.get(nbt, "ability", "");
        AbilityTemplate ability = Abilities.INSTANCE.get(id);
        if (ability != null) {
            return Text.translatable(ability.getDisplayName()).getString();
        }
        return id;
    }

    @Override
    public String getForm() {
        // Cobb Breeding does this themselves
        return "";
    }

    @Override
    public boolean hasIVs() {
        return nbt.contains("ivs", NbtElement.INT_ARRAY_TYPE);
    }

    @Override
    public short getHpIV() {
        return (short) nbt.getIntArray("ivs")[0];
    }

    @Override
    public short getAtkIV() {
        return (short) nbt.getIntArray("ivs")[1];
    }

    @Override
    public short getDefIV() {
        return (short) nbt.getIntArray("ivs")[2];
    }

    @Override
    public short getSpAtkIV() {
        return (short) nbt.getIntArray("ivs")[3];
    }

    @Override
    public short getSpDefIV() {
        return (short) nbt.getIntArray("ivs")[4];
    }

    @Override
    public short getSpeedIV() {
        return (short) nbt.getIntArray("ivs")[5];
    }
}
