package me.justahuman.dystoriantweaks.utils;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Gender;
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
                    case "bug" -> pokemon -> pokemon.getPrimaryType() == types.getBUG()
                            || pokemon.getSecondaryType() == types.getBUG();
                    case "dark" -> pokemon -> pokemon.getPrimaryType() == types.getDARK()
                            || pokemon.getSecondaryType() == types.getDARK();
                    case "dragon" -> pokemon -> pokemon.getPrimaryType() == types.getDRAGON()
                            || pokemon.getSecondaryType() == types.getDRAGON();
                    case "electric" -> pokemon -> pokemon.getPrimaryType() == types.getELECTRIC()
                            || pokemon.getSecondaryType() == types.getELECTRIC();
                    case "fairy" -> pokemon -> pokemon.getPrimaryType() == types.getFAIRY()
                            || pokemon.getSecondaryType() == types.getFAIRY();
                    case "fighting" -> pokemon -> pokemon.getPrimaryType() == types.getFIGHTING()
                            || pokemon.getSecondaryType() == types.getFIGHTING();
                    case "fire" -> pokemon -> pokemon.getPrimaryType() == types.getFIRE()
                            || pokemon.getSecondaryType() == types.getFIRE();
                    case "flying" -> pokemon -> pokemon.getPrimaryType() == types.getFLYING()
                            || pokemon.getSecondaryType() == types.getFLYING();
                    case "ghost" -> pokemon -> pokemon.getPrimaryType() == types.getGHOST()
                            || pokemon.getSecondaryType() == types.getGHOST();
                    case "grass" -> pokemon -> pokemon.getPrimaryType() == types.getGRASS()
                            || pokemon.getSecondaryType() == types.getGRASS();
                    case "ground" -> pokemon -> pokemon.getPrimaryType() == types.getGROUND()
                            || pokemon.getSecondaryType() == types.getGROUND();
                    case "ice" -> pokemon -> pokemon.getPrimaryType() == types.getICE()
                            || pokemon.getSecondaryType() == types.getICE();
                    case "normal" -> pokemon -> pokemon.getPrimaryType() == types.getNORMAL()
                            || pokemon.getSecondaryType() == types.getNORMAL();
                    case "poison" -> pokemon -> pokemon.getPrimaryType() == types.getPOISON()
                            || pokemon.getSecondaryType() == types.getPOISON();
                    case "psychic" -> pokemon -> pokemon.getPrimaryType() == types.getPSYCHIC()
                            || pokemon.getSecondaryType() == types.getPSYCHIC();
                    case "rock" -> pokemon -> pokemon.getPrimaryType() == types.getROCK()
                            || pokemon.getSecondaryType() == types.getROCK();
                    case "steel" -> pokemon -> pokemon.getPrimaryType() == types.getSTEEL()
                            || pokemon.getSecondaryType() == types.getSTEEL();
                    case "water" -> pokemon -> pokemon.getPrimaryType() == types.getWATER()
                            || pokemon.getSecondaryType() == types.getWATER();
                    case "holding" -> pokemon -> !pokemon.heldItem().isEmpty();
                    case "tradeable" -> Pokemon::getTradeable;
                    case "fainted" -> Pokemon::isFainted;
                    case "legendary" -> Pokemon::isLegendary;
                    case "mythical" -> Pokemon::isMythical;
                    case "ultrabeast", "ultra_beast" -> Pokemon::isUltraBeast;
                    case "adamant" -> pokemon -> pokemon.getNature() == natures.getADAMANT()
                            || pokemon.getMintedNature() == natures.getADAMANT();
                    case "bashful" -> pokemon -> pokemon.getNature() == natures.getBASHFUL()
                            || pokemon.getMintedNature() == natures.getBASHFUL();
                    case "bold" -> pokemon -> pokemon.getNature() == natures.getBOLD()
                            || pokemon.getMintedNature() == natures.getBOLD();
                    case "brave" -> pokemon -> pokemon.getNature() == natures.getBRAVE()
                            || pokemon.getMintedNature() == natures.getBRAVE();
                    case "calm" -> pokemon -> pokemon.getNature() == natures.getCALM()
                            || pokemon.getMintedNature() == natures.getCALM();
                    case "careful" -> pokemon -> pokemon.getNature() == natures.getCAREFUL()
                            || pokemon.getMintedNature() == natures.getCAREFUL();
                    case "docile" -> pokemon -> pokemon.getNature() == natures.getDOCILE()
                            || pokemon.getMintedNature() == natures.getDOCILE();
                    case "gentle" -> pokemon -> pokemon.getNature() == natures.getGENTLE()
                            || pokemon.getMintedNature() == natures.getGENTLE();
                    case "hardy" -> pokemon -> pokemon.getNature() == natures.getHARDY()
                            || pokemon.getMintedNature() == natures.getHARDY();
                    case "hasty" -> pokemon -> pokemon.getNature() == natures.getHASTY()
                            || pokemon.getMintedNature() == natures.getHASTY();
                    case "impish" -> pokemon -> pokemon.getNature() == natures.getIMPISH()
                            || pokemon.getMintedNature() == natures.getIMPISH();
                    case "jolly" -> pokemon -> pokemon.getNature() == natures.getJOLLY()
                            || pokemon.getMintedNature() == natures.getJOLLY();
                    case "lax" -> pokemon -> pokemon.getNature() == natures.getLAX()
                            || pokemon.getMintedNature() == natures.getLAX();
                    case "lonely" -> pokemon -> pokemon.getNature() == natures.getLONELY()
                            || pokemon.getMintedNature() == natures.getLONELY();
                    case "mild" -> pokemon -> pokemon.getNature() == natures.getMILD()
                            || pokemon.getMintedNature() == natures.getMILD();
                    case "modest" -> pokemon -> pokemon.getNature() == natures.getMODEST()
                            || pokemon.getMintedNature() == natures.getMODEST();
                    case "naive" -> pokemon -> pokemon.getNature() == natures.getNAIVE()
                            || pokemon.getMintedNature() == natures.getNAIVE();
                    case "naughty" -> pokemon -> pokemon.getNature() == natures.getNAUGHTY()
                            || pokemon.getMintedNature() == natures.getNAUGHTY();
                    case "quiet" -> pokemon -> pokemon.getNature() == natures.getQUIET()
                            || pokemon.getMintedNature() == natures.getQUIET();
                    case "quirky" -> pokemon -> pokemon.getNature() == natures.getQUIRKY()
                            || pokemon.getMintedNature() == natures.getQUIRKY();
                    case "rash" -> pokemon -> pokemon.getNature() == natures.getRASH()
                            || pokemon.getMintedNature() == natures.getRASH();
                    case "relaxed" -> pokemon -> pokemon.getNature() == natures.getRELAXED()
                            || pokemon.getMintedNature() == natures.getRELAXED();
                    case "sassy" -> pokemon -> pokemon.getNature() == natures.getSASSY()
                            || pokemon.getMintedNature() == natures.getSASSY();
                    case "serious" -> pokemon -> pokemon.getNature() == natures.getSERIOUS()
                            || pokemon.getMintedNature() == natures.getSERIOUS();
                    case "timid" -> pokemon -> pokemon.getNature() == natures.getTIMID()
                            || pokemon.getMintedNature() == natures.getTIMID();
                    default -> pokemon -> {
                        if (query.startsWith("ability=")) {
                            return Text.translatable(pokemon.getAbility().getDisplayName()).getString().toLowerCase(Locale.ROOT).startsWith(query.substring(8));
                        } else if (query.startsWith("form=")) {
                            return pokemon.getForm().getName().toLowerCase(Locale.ROOT).startsWith(query.substring(5));
                        } else if (query.startsWith("knows=")) {
                            return pokemon.getMoveSet().getMoves().stream().anyMatch(move -> move.getDisplayName().getString().toLowerCase(Locale.ROOT).startsWith(query.substring(6)));
                        } else if (query.startsWith("learns=")) {
                            return pokemon.getAllAccessibleMoves().stream().anyMatch(move -> move.getDisplayName().getString().toLowerCase(Locale.ROOT).startsWith(query.substring(7)));
                        } else {
                            return pokemon.getDisplayName().getString().toLowerCase(Locale.ROOT).startsWith(query);
                        }
                    };
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
