package me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates;

import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchPredicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EggGroupPredicate implements SearchPredicate {
    public static final List<String> NAMES = List.of("egg=", "egg_group=");
    private static final Map<String, List<String>> COMPLETIONS;
    static {
        Map<String, List<String>> completions = new HashMap<>();
        for (String name : NAMES) {
            List<String> completion = new ArrayList<>();
            for (EggGroup group : EggGroup.values()) {
                completion.add(name + group.name().toLowerCase(Locale.ROOT));
            }
            completions.put(name, List.copyOf(completion));
        }
        COMPLETIONS = Map.copyOf(completions);
    }

    private final EggGroup eggGroup;

    public EggGroupPredicate() {
        this.eggGroup = null;
        SearchPredicate.register(this);
    }

    public EggGroupPredicate(EggGroup eggGroup) {
        this.eggGroup = eggGroup;
    }

    @Override
    public void suggest(SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String name : NAMES) {
            if (remaining.equals(name) || remaining.startsWith(name)) {
                for (String completion : COMPLETIONS.get(name)) {
                    if (completion.startsWith(remaining)) {
                        builder.suggest(completion);
                    }
                }
            } else if (name.startsWith(remaining)) {
                builder.suggest(name);
            }
        }
    }

    @Override
    public boolean passes(Pokemon pokemon) {
        return eggGroup != null && pokemon.getSpecies().getEggGroups().contains(eggGroup);
    }
}
