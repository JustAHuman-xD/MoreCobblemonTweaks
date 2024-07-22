package me.justahuman.dystoriantweaks;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityPool;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {
    public static final Map<String, Boolean> HIDDEN_CACHE = new HashMap<>();
    public static int currentBox = 0;
    public static boolean allBoxes = false;
    public static Search search = null;

    public static String get(NbtCompound nbt, String key, String def) {
        if (nbt != null && nbt.get(key) instanceof NbtString nbtString) {
            return nbtString.asString();
        }
        return def;
    }

    public static boolean get(NbtCompound nbt, String key, boolean def) {
        if (nbt != null && nbt.get(key) instanceof NbtByte nbtByte) {
            return nbtByte.byteValue() == 1;
        }
        return def;
    }

    public static int get(NbtCompound nbt, String key, int def) {
        if (nbt != null && nbt.get(key) instanceof NbtInt nbtInt) {
            return nbtInt.intValue();
        }
        return def;
    }

    public static short get(NbtCompound nbt, String key, short def) {
        if (nbt != null && nbt.get(key) instanceof NbtShort nbtShort) {
            return nbtShort.shortValue();
        }
        return def;
    }

    public static double get(NbtCompound nbt, String key, double def) {
        if (nbt != null && nbt.get(key) instanceof NbtDouble nbtDouble) {
            return nbtDouble.doubleValue();
        }
        return def;
    }

    public static boolean hasHiddenAbility(Pokemon pokemon) {
        Ability ability = pokemon.getAbility();
        String abilityName = ability.getName();
        Boolean cache = HIDDEN_CACHE.get(abilityName);
        if (cache != null) {
            return cache;
        }

        AbilityPool pool = pokemon.getSpecies().getAbilities();
        for (PotentialAbility potentialAbility : pool.getMapping().values().stream().flatMap(Collection::stream).collect(Collectors.toSet())) {
            DystorianTweaks.LOGGER.info(potentialAbility.getTemplate().getName());
            if (potentialAbility.getTemplate() == ability.getTemplate()) {

                if (potentialAbility.getType() == HiddenAbilityType.INSTANCE) {
                    HIDDEN_CACHE.put(abilityName, true);
                    return true;
                }
                HIDDEN_CACHE.put(abilityName, false);
                return false;
            }
        }

        if (!HIDDEN_CACHE.containsKey(abilityName)) {
            HIDDEN_CACHE.put(abilityName, false);
        }

        return false;
    }

    public static class Search {
        private final Set<SearchPredicate> options;
        private final Set<UUID> passedMons = new HashSet<>();
        private final Set<UUID> failedMons = new HashSet<>();

        protected Search(Set<SearchPredicate> options) {
            this.options = options;
        }

        public boolean passes(Pokemon pokemon) {
            UUID uuid = pokemon.getUuid();
            if (passedMons.contains(uuid)) {
                return true;
            } else if (failedMons.contains(uuid)) {
                return false;
            }

            for (SearchPredicate option : options) {
                if (!option.passes(pokemon)) {
                    failedMons.add(uuid);
                    return false;
                }
            }

            passedMons.add(uuid);
            return true;
        }

        public static Search of(String search) {
            search = search.toLowerCase(Locale.ROOT).trim();
            String[] options = search.split(" ");
            Set<SearchPredicate> searchOptions = new HashSet<>();
            for (String option : options) {
                boolean inverted = false;
                if (option.startsWith("!")) {
                    inverted = true;
                    option = option.substring(1);
                }

                final String query = option;
                SearchPredicate searchOption = switch (query) {
                    case "shiny" -> Pokemon::getShiny;
                    case "male" -> pokemon -> pokemon.getGender() != Gender.FEMALE;
                    case "female" -> pokemon -> pokemon.getGender() != Gender.MALE;
                    case "genderless" -> pokemon -> pokemon.getGender() == Gender.GENDERLESS;
                    case "bug" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getBUG()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getBUG();
                    case "dark" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getDARK()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getDARK();
                    case "dragon" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getDRAGON()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getDRAGON();
                    case "electric" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getELECTRIC()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getELECTRIC();
                    case "fairy" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getFAIRY()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getFAIRY();
                    case "fighting" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getFIGHTING()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getFIGHTING();
                    case "fire" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getFIRE()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getFIRE();
                    case "flying" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getFLYING()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getFLYING();
                    case "ghost" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getGHOST()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getGHOST();
                    case "grass" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getGRASS()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getGRASS();
                    case "ground" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getGROUND()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getGROUND();
                    case "ice" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getICE()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getICE();
                    case "normal" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getNORMAL()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getNORMAL();
                    case "poison" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getPOISON()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getPOISON();
                    case "psychic" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getPSYCHIC()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getPSYCHIC();
                    case "rock" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getROCK()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getROCK();
                    case "steel" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getSTEEL()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getSTEEL();
                    case "water" -> pokemon -> pokemon.getPrimaryType() == ElementalTypes.INSTANCE.getWATER()
                            || pokemon.getSecondaryType() == ElementalTypes.INSTANCE.getWATER();
                    case "holding" -> pokemon -> !pokemon.heldItem().isEmpty();
                    case "tradeable" -> Pokemon::getTradeable;
                    case "fainted" -> Pokemon::isFainted;
                    case "legendary" -> Pokemon::isLegendary;
                    case "mythical" -> Pokemon::isMythical;
                    case "ultrabeast", "ultra_beast" -> Pokemon::isUltraBeast;
                    case "ha", "hiddenabilty", "hidden_ability" -> Utils::hasHiddenAbility;
                    default -> pokemon -> pokemon.getSpecies().getName().toLowerCase(Locale.ROOT).startsWith(query);
                };

                if (searchOption != null) {
                    if (inverted) {
                        searchOptions.add(pokemon -> !searchOption.passes(pokemon));
                    } else {
                        searchOptions.add(searchOption);
                    }
                }
            }
            return new Search(searchOptions);
        }
    }

    @FunctionalInterface
    public interface SearchPredicate {
        boolean passes(Pokemon pokemon);
    }
}
