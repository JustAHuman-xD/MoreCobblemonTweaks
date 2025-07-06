package me.justahuman.more_cobblemon_tweaks.features.pc.search;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates.EggGroupPredicate;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates.NameOrSpeciesPredicate;

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

            SearchPredicate searchOption = SearchPredicate.FIXED.get(option);
            if (searchOption == null) {
                String[] parts = option.split("=", 2);
                if (parts.length == 2) {
                    if (EggGroupPredicate.NAMES.contains(parts[0].toLowerCase(Locale.ROOT) + "=")) {
                        try {
                            EggGroup group = EggGroup.valueOf(parts[1].toUpperCase(Locale.ROOT));
                            searchOption = new EggGroupPredicate(group);
                        } catch (IllegalArgumentException ignored) {}
                    } else {
                        searchOption = SearchPredicate.FIXED.get(parts[0]);
                        if (parts[1].equals("false") || parts[1].equals("no")) {
                            inverted = !inverted;
                        }
                    }
                }

                if (searchOption == null) {
                    PokemonProperties filter = PokemonProperties.Companion.parse(option);
                    if (!filter.matches(NONE)) {
                        searchOption = filter::matches;
                    } else {
                        searchOption = new NameOrSpeciesPredicate(option);
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
}
