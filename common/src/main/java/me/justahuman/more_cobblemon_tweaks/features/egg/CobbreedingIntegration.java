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
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CobbreedingIntegration extends EnhancedEggLore {
    private final PokemonProperties properties;
    private final IVs ivs;

    public CobbreedingIntegration(String eggInfo) {
        this(PokemonProperties.Companion.parse(eggInfo));
    }

    public CobbreedingIntegration(PokemonProperties properties) {
        this.properties = properties;
        this.ivs = properties.getIvs();
    }

    @Override
    public Component getName(List<Component> lore) {
        if (lore.size() > 2) {
            String name = lore.remove(1).getString();
            if (name.equals("Bad egg")) {
                return Component.literal("Bad Egg");
            }
            return Component.literal(name + " Egg");
        }
        return super.getName(lore);
    }

    @Override
    public boolean isShiny() {
        return Boolean.TRUE.equals(properties.getShiny());
    }

    @Override
    public String getGender() {
        Gender gender = properties.getGender();
        return gender == null ? null : gender.name();
    }

    @Override
    public List<Component> getHatchProgress(List<Component> lore) {
        if (lore.size() > 1) {
            String hatchProgress = lore.get(1).getString();
            String[] parts = hatchProgress.split(":");
            if (parts.length == 2) {
                try {
                    int minutes = Integer.parseInt(parts[0].trim());
                    int seconds = Integer.parseInt(parts[1].trim());
                    lore.remove(1);
                    return List.of(LoreEnhancements.translate("egg.cobbreeding.hatch_progress", minutes, seconds));
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    @Override
    public String getNature() {
        String natureId = properties.getNature();
        if (natureId == null) {
            return null;
        }
        ResourceLocation id = natureId.contains(":") ? ResourceLocation.tryParse(natureId) : MiscUtilsKt.cobblemonResource(natureId);
        Nature nature = id == null ? null : Natures.getNature(id);
        return nature == null ? null : Component.translatable(nature.getDisplayName()).getString();
    }

    @Override
    public String getAbility() {
        String abilityId = properties.getAbility();
        AbilityTemplate ability = abilityId == null ? null : Abilities.get(abilityId);
        return ability == null ? null : Component.translatable(ability.getDisplayName()).getString();
    }

    @Override
    public String getForm() {
        // Cobbreeding does this themselves
        return null;
    }

    @Override
    public String getPokeBall() {
        String pokeBallId = properties.getPokeball();
        return pokeBallId == null ? null : pokeBallFromId(pokeBallId);
    }

    @Override
    public boolean hasIVs() {
        if (ivs == null) {
            return false;
        }
        return getHpIV() != null
                || getAtkIV() != null
                || getDefIV() != null
                || getSpAtkIV() != null
                || getSpDefIV() != null
                || getSpeedIV() != null;
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

    public static EnhancedEggLore get(ItemStack itemStack) {
        if (itemStack.getItem() instanceof PokemonEgg) {
            try {
                if (itemStack.has(PokemonEgg.Companion.getVERSION())) {
                    // 2.0.x
                    if (itemStack.has(PokemonEgg.Companion.getPOKEMON_PROPERTIES())) {
                        return new CobbreedingIntegration(itemStack.get(PokemonEgg.Companion.getPOKEMON_PROPERTIES()));
                    } else if (itemStack.has(PokemonEgg.Companion.getEGG_INFO()) && ModConfig.isEnabled("egg_encryption_warning")) {
                        return new EncryptedEggLore(List.of(LoreEnhancements.translate("egg.cobbreeding_encryption_warning"
                                + (Utils.isSinglePlayer() ? "_singleplayer" : "")).withStyle(ChatFormatting.RED)));
                    }
                }
            } catch (NoSuchMethodError e) {
                // 1.8.8+ legacy
                if (itemStack.has(PokemonEgg.Companion.getEGG_INFO()) && isString(itemStack.get(PokemonEgg.Companion.getEGG_INFO()))) {
                    return new CobbreedingIntegration(itemStack.get(PokemonEgg.Companion.getEGG_INFO()));
                } else if (itemStack.has(PokemonEgg.Companion.getPOKEMON_PROPERTIES())) {
                    return new CobbreedingIntegration(itemStack.get(PokemonEgg.Companion.getPOKEMON_PROPERTIES()));
                }
            }
        }
        return null;
    }

    private static boolean isString(Object value) {
        return value instanceof String;
    }
}
