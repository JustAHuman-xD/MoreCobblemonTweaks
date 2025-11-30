package me.justahuman.more_cobblemon_tweaks.features.pc.search.predicates;

import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EggGroupPredicate implements SearchPredicate {
    public static final String KEY = "egg_group=";
    private static final List<String> COMPLETION;
    static {
        List<String> completion = new ArrayList<>();
        for (EggGroup group : EggGroup.values()) {
            completion.add(KEY + group.name().toLowerCase(Locale.ROOT));
        }
        COMPLETION = List.copyOf(completion);
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
        if (remaining.startsWith(KEY)) {
            for (String completion : COMPLETION) {
                if (completion.startsWith(remaining)) {
                    builder.suggest(completion);
                }
            }
        } else if (KEY.startsWith(remaining)) {
            builder.suggest(KEY);
        }
    }

    @Override
    public boolean test(Pokemon pokemon) {
        return eggGroup != null && pokemon.getSpecies().getEggGroups().contains(eggGroup);
    }
}
