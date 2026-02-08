package me.justahuman.more_cobblemon_tweaks.features.egg;

import net.allthemods.allthemons.registry.AllTheMonsRegistrator;
import net.minecraft.world.item.ItemStack;

public class AllTheMonsEggLoreFactory {
    public static AllTheMonsEggLore get(ItemStack eggStack) {
        if (eggStack.getItem() == AllTheMonsRegistrator.POKEMON_EGG_ITEM.value()) {
            return new AllTheMonsEggLore(eggStack);
        }
        return null;
    }
}
