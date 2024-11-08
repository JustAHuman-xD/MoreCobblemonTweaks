package me.justahuman.more_cobblemon_tweaks.features;

import com.cobblemon.mod.common.item.berry.FriendshipRaisingBerryItem;
import com.cobblemon.mod.common.item.berry.PPRestoringBerryItem;
import com.cobblemon.mod.common.item.interactive.CandyItem;
import com.cobblemon.mod.common.item.interactive.FeatherItem;
import com.cobblemon.mod.common.item.interactive.MintItem;
import com.cobblemon.mod.common.item.interactive.PPUpItem;
import com.cobblemon.mod.common.item.interactive.VitaminItem;
import com.cobblemon.mod.common.item.interactive.ability.AbilityChangeItem;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.egg.EnhancedEggLore;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

import static com.cobblemon.mod.common.CobblemonItems.*;
import static net.minecraft.util.Formatting.*;

public class LoreEnhancements {
    private static final String BASE_KEY = "more_cobblemon_tweaks.lore_enhancements.";
    private static final Set<Item> TYPE_GEMS = Set.of(NORMAL_GEM, FIRE_GEM, WATER_GEM, GRASS_GEM, ELECTRIC_GEM, ICE_GEM, FIGHTING_GEM,
            POISON_GEM, GROUND_GEM, FLYING_GEM, PSYCHIC_GEM, BUG_GEM, ROCK_GEM, GHOST_GEM, DRAGON_GEM, DARK_GEM, STEEL_GEM, FAIRY_GEM);

    public static void enhanceEggLore(List<Text> lore, List<Text> newLore, EnhancedEggLore enhancedEggLore) {
        Text name = enhancedEggLore.getName(lore);
        final boolean shiny = enhancedEggLore.isShiny();
        if (ModConfig.isEnabled("shiny_egg_indicator") && shiny) {
            name = name.copy().append(Text.literal(" ★").formatted(YELLOW, BOLD));
        }

        final String gender = enhancedEggLore.getGender();
        if (gender.equals("MALE") || gender.equals("FEMALE")) {
            boolean male = gender.equals("MALE");
            name = name.copy().append(Text.literal(male ? " ♂" : " ♀")
                    .styled(style -> style.withColor(male ? 0x32CBFF : 0xFC5454)));
        }
        lore.set(0, name);

        final List<Text> hatchProgress = enhancedEggLore.getHatchProgress();
        boolean spacer = false;

        if (hatchProgress != null && !hatchProgress.isEmpty()) {
            newLore.addAll(hatchProgress);
            spacer = true;
        }

        String nature = enhancedEggLore.getNature();
        String abilityName = enhancedEggLore.getAbility();
        String form = enhancedEggLore.getForm();
        if ((!nature.isBlank() || !abilityName.isBlank() || !form.isBlank()) && spacer) {
            newLore.add(Text.literal(" "));
            spacer = false;
        }

        if (!nature.isBlank()) {
            if (nature.contains(":")) {
                nature = StringUtils.capitalize(nature.substring(nature.indexOf(':') + 1));
            }
            newLore.add(translate("egg.nature").formatted(YELLOW)
                    .append(Text.literal(nature).formatted(WHITE)));
            spacer = true;
        }

        if (!abilityName.isBlank()) {
            newLore.add(translate("egg.ability").formatted(GOLD)
                    .append(Text.literal(StringUtils.capitalize(abilityName)).formatted(WHITE)));
            spacer = true;
        }

        if (!form.isBlank()) {
            newLore.add(translate("egg.form").formatted(WHITE)
                    .append(Text.literal(StringUtils.capitalize(form))));
            spacer = true;
        }

        if (enhancedEggLore.hasIVs()) {
            short hp = enhancedEggLore.getHpIV();
            short attack = enhancedEggLore.getAtkIV();
            short defense = enhancedEggLore.getDefIV();
            short spAttack = enhancedEggLore.getSpAtkIV();
            short spDefense = enhancedEggLore.getSpDefIV();
            short speed = enhancedEggLore.getSpeedIV();

            if (spacer) {
                newLore.add(Text.literal(" "));
            }

            if (hp != -1) {
                newLore.add(translate("egg.iv.hp").formatted(RED)
                        .append(Text.literal(String.valueOf(hp)).formatted(WHITE)));
            }
            if (attack != -1) {
                newLore.add(translate("egg.iv.attack").formatted(BLUE)
                        .append(Text.literal(String.valueOf(attack)).formatted(WHITE)));
            }
            if (defense != -1) {
                newLore.add(translate("egg.iv.defense").formatted(GRAY)
                        .append(Text.literal(String.valueOf(defense)).formatted(WHITE)));
            }
            if (spAttack != -1) {
                newLore.add(translate("egg.iv.sp_attack").formatted(AQUA)
                        .append(Text.literal(String.valueOf(spAttack)).formatted(WHITE)));
            }
            if (spDefense != -1) {
                newLore.add(translate("egg.iv.sp_defense").formatted(YELLOW)
                        .append(Text.literal(String.valueOf(spDefense)).formatted(WHITE)));
            }
            if (speed != -1) {
                newLore.add(translate("egg.iv.speed").formatted(GREEN)
                        .append(Text.literal(String.valueOf(speed)).formatted(WHITE)));
            }
        }
    }

    public static void enhanceBerryLore(Item item, List<Text> newLore) {
        if (item instanceof FriendshipRaisingBerryItem friendshipBerry) {
            newLore.add(translate("berry.ev", friendshipBerry.getStat().getDisplayName().getString()).formatted(GRAY));
        } else if (item instanceof PPRestoringBerryItem) {
            newLore.add(translate("berry.pp").formatted(GRAY));
        }
    }

    public static void enhanceConsumablesLore(Item item, List<Text> newLore) {
        if (item instanceof MintItem mint) {
            newLore.add(translate("consumable.mint", Text.translatable(mint.getNature().getDisplayName()).getString()).formatted(GRAY));
            newLore.add(translate("consumable.mint_note").formatted(GRAY));
        } else if (item instanceof FeatherItem feather) {
            newLore.add(translate("consumable.feather", feather.getStat().getDisplayName().getString()).formatted(GRAY));
        } else if (item instanceof VitaminItem vitamin) {
            newLore.add(translate("consumable.vitamin", vitamin.getStat().getDisplayName().getString()).formatted(GRAY));
        } else if (item instanceof PPUpItem) {
            if (item == PP_UP) {
                newLore.add(translate("consumable.pp_up").formatted(GRAY));
            } else if (item == PP_MAX) {
                newLore.add(translate("consumable.pp_max"));
            }
        } else if (item instanceof CandyItem) {
            String amount = null;
            if (item == EXPERIENCE_CANDY_XS) {
                amount = "100";
            } else if (item == EXPERIENCE_CANDY_S) {
                amount = "800";
            } else if (item == EXPERIENCE_CANDY_M) {
                amount = "3,000";
            } else if (item == EXPERIENCE_CANDY_L) {
                amount = "10,000";
            } else if (item == EXPERIENCE_CANDY_XL) {
                amount = "30,000";
            } else if (item == RARE_CANDY) {
                newLore.add(translate("consumable.rare_candy").formatted(GRAY));
            }

            if (amount != null) {
                newLore.add(translate("consumable.candy", amount).formatted(GRAY));
            }
        }
    }

    public static void enhanceHeldItemLore(Item item, List<Text> newLore) {
        if (TYPE_GEMS.contains(item)) {
            String type = StringUtils.capitalize(item.toString().replace("cobblemon:", "").replace("_gem", ""));
            newLore.add(translate("held_item.type_gem", type).formatted(GRAY));
            newLore.add(Text.literal(" "));
            newLore.add(Text.literal("⇢ ").formatted(GRAY).append(translate("held_item.type_gem_note").formatted(RED)));
        } else if (item instanceof AbilityChangeItem<?>) {
            if (item == ABILITY_CAPSULE) {
                newLore.add(translate("held_item.ability_capsule").formatted(GRAY));
            } else if (item == ABILITY_PATCH) {
                newLore.add(translate("held_item.ability_patch").formatted(GRAY));
            }
        } else {
            String key = null;
            if (item == EJECT_BUTTON) {
                key = "held_item.eject_button";
            } else if (item == FLOAT_STONE) {
                key = "held_item.float_stone";
            } else if (item == EVIOLITE) {
                key = "held_item.eviolite";
            } else if (item == WEAKNESS_POLICY) {
                key = "held_item.weakness_policy";
            }

            List<String> keys = null;
            if (item == STICKY_BARB) {
                keys = List.of("held_item.sticky_barb", "held_item.sticky_barb_secondary");
            }

            if (key != null) {
                newLore.add(translate(key).formatted(GRAY));
            } else if (keys != null) {
                newLore.addAll(keys.stream().map(LoreEnhancements::translate).map(text -> text.formatted(GRAY)).toList());
            }
        }
    }

    public static MutableText translate(String key, Object... args) {
        return Text.translatable(BASE_KEY + key, args);
    }
}
