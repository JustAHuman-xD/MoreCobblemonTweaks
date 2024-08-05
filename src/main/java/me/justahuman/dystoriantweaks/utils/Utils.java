package me.justahuman.dystoriantweaks.utils;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Utils {
    public static int currentBox = 0;
    public static boolean allBoxes = false;
    public static Search search = null;

    public static void playSound(SoundEvent sound) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(sound, 1.0F));
    }

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
            if (search == null || search.isBlank()) {
                return null;
            }

            search = search.toLowerCase(Locale.ROOT).trim();
            String[] options = search.split(" ");
            Set<SearchPredicate> searchOptions = new HashSet<>();
            for (String option : options) {
                boolean inverted = false;
                if (option.startsWith("!")) {
                    inverted = true;
                    option = option.substring(1);
                }

                String query = option;
                ElementalTypes types = ElementalTypes.INSTANCE;
                Natures natures = Natures.INSTANCE;
                SearchPredicate searchOption = switch (query) {
                    case "shiny" -> Pokemon::getShiny;
                    case "male" -> pokemon -> pokemon.getGender() != Gender.FEMALE;
                    case "female" -> pokemon -> pokemon.getGender() != Gender.MALE;
                    case "genderless" -> pokemon -> pokemon.getGender() == Gender.GENDERLESS;
                    case "holding" -> pokemon -> !pokemon.heldItem().isEmpty();
                    case "tradeable" -> Pokemon::getTradeable;
                    case "fainted" -> Pokemon::isFainted;
                    case "legendary" -> Pokemon::isLegendary;
                    case "mythical" -> Pokemon::isMythical;
                    case "ultrabeast", "ultra_beast" -> Pokemon::isUltraBeast;
                    default -> {
                        if (Identifier.isValid(query)) {
                            Nature nature = natures.getNature(query);
                            if (nature != null) {
                                yield pokemon -> pokemon.getNature() == nature || pokemon.getMintedNature() == nature;
                            }

                            ElementalType type = types.get(query);
                            if (type != null) {
                                yield pokemon -> {
                                    for (ElementalType pokemonType : pokemon.getTypes()) {
                                        if (pokemonType == type) {
                                            return true;
                                        }
                                    }
                                    return false;
                                };
                            }
                        }

                        if (query.startsWith("ability=")) {
                            String ability = query.substring(8);
                            yield pokemon -> Text.translatable(pokemon.getAbility().getDisplayName()).getString().toLowerCase(Locale.ROOT).startsWith(ability);
                        } else if (query.startsWith("form=")) {
                            String form = query.substring(5);
                            yield pokemon -> pokemon.getForm().getName().toLowerCase(Locale.ROOT).startsWith(form);
                        } else if (query.startsWith("knows=")) {
                            String move = query.substring(6);
                            yield pokemon -> pokemon.getMoveSet().getMoves().stream().anyMatch(pokemonMove
                                    -> pokemonMove.getDisplayName().getString().toLowerCase(Locale.ROOT).startsWith(move));
                        } else if (query.startsWith("learns=")) {
                            String move = query.substring(7);
                            yield pokemon -> pokemon.getAllAccessibleMoves().stream().anyMatch(pokemonMove
                                    -> pokemonMove.getDisplayName().getString().toLowerCase(Locale.ROOT).startsWith(move));
                        } else {
                            yield pokemon -> pokemon.getDisplayName().getString().toLowerCase(Locale.ROOT).startsWith(query);
                        }
                    }
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
