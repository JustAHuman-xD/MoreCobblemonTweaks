package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Search {
    private static final PokemonProperties NONE = new PokemonProperties();

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

            SearchPredicate searchOption = fixed(option);
            if (searchOption == null) {
                if (option.contains("=") && !option.endsWith("=")) {
                    String before = option.substring(0, option.indexOf('='));
                    String after = option.substring(option.indexOf('=') + 1);
                    searchOption = fixed(before);
                    if (after.equals("false") || after.equals("no")) {
                        inverted = !inverted;
                    }
                }

                if (searchOption == null) {
                    PokemonProperties filter = PokemonProperties.Companion.parse(option);
                    if (!filter.matches(NONE)) {
                        searchOption = filter::matches;
                    } else {
                        var nameFilter = option.toLowerCase(Locale.ROOT);
                        searchOption = pokemon -> pokemon.getSpecies().resourceIdentifier.getPath().contains(nameFilter)
                                || pokemon.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(nameFilter);
                    }
                }
            }

            if (inverted) {
                searchOptions.add(searchOption.invert());
            } else {
                searchOptions.add(searchOption);
            }
        }
        return new Search(searchOptions);
    }

    private static SearchPredicate fixed(String option) {
        return switch(option) {
            case "holding", "helditem", "held_item" -> pokemon -> !pokemon.heldItem().isEmpty();
            case "fainted" -> Pokemon::isFainted;
            case "legendary" -> Pokemon::isLegendary;
            case "mythical" -> Pokemon::isMythical;
            case "ultrabeast", "ultra_beast" -> Pokemon::isUltraBeast;
            default -> null;
        };
    }
}
