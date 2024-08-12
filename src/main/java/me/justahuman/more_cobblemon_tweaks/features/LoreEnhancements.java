package me.justahuman.more_cobblemon_tweaks.features;

import com.cobblemon.mod.common.item.berry.FriendshipRaisingBerryItem;
import com.cobblemon.mod.common.item.berry.PPRestoringBerryItem;
import com.cobblemon.mod.common.item.interactive.CandyItem;
import com.cobblemon.mod.common.item.interactive.FeatherItem;
import com.cobblemon.mod.common.item.interactive.MintItem;
import com.cobblemon.mod.common.item.interactive.PPUpItem;
import com.cobblemon.mod.common.item.interactive.VitaminItem;
import com.cobblemon.mod.common.item.interactive.ability.AbilityChangeItem;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

import static com.cobblemon.mod.common.CobblemonItems.*;
import static net.minecraft.util.Formatting.*;

public class LoreEnhancements {
    private static final Set<Item> TYPE_GEMS = Set.of(NORMAL_GEM, FIRE_GEM, WATER_GEM, GRASS_GEM, ELECTRIC_GEM, ICE_GEM, FIGHTING_GEM,
            POISON_GEM, GROUND_GEM, FLYING_GEM, PSYCHIC_GEM, BUG_GEM, ROCK_GEM, GHOST_GEM, DRAGON_GEM, DARK_GEM, STEEL_GEM, FAIRY_GEM);

    public static void enhanceEggLore(List<Text> lore, List<Text> newLore, NbtCompound customData) {
        Text name = lore.get(0);
        final boolean shiny = Utils.get(customData, "Shiny", false);
        if (shiny) {
            name = name.copy().append(Text.literal(" ★").formatted(YELLOW, BOLD));
        }
        final String gender = Utils.get(customData, "Gender", "NONE");
        if (gender.equals("MALE") || gender.equals("FEMALE")) {
            boolean male = gender.equals("MALE");
            name = name.copy().append(Text.literal(male ? " ♂" : " ♀")
                    .styled(style -> style.withColor(male ? 0x32CBFF : 0xFC5454)));
        }
        lore.set(0, name);

        final int cycles = Utils.get(customData, "currentEggCycle", -1);
        final double steps = Utils.get(customData, "stepsLeftInCycle", -1d);
        boolean spacer = false;

        if (cycles != -1) {
            newLore.add(Text.literal("Egg Cycles Remaining: ").formatted(GREEN)
                    .append(Text.literal(String.valueOf(cycles)).formatted(WHITE)));
            spacer = true;
        }
        if (steps != -1) {
            newLore.add(Text.literal("Steps Left: ").formatted(AQUA)
                    .append(Text.literal(String.valueOf(Math.round(steps))).formatted(WHITE)));
            spacer = true;
        }

        String nature = Utils.get(customData, "Nature", "");
        NbtCompound ability = customData.get("Ability") instanceof NbtCompound compound ? compound : null;
        String abilityName = Utils.get(ability, "AbilityName", "");
        String form = Utils.get(customData, "FormId", "");
        if ((!nature.isBlank() || !abilityName.isBlank() || !form.isBlank()) && spacer) {
            newLore.add(Text.literal(" "));
            spacer = false;
        }

        if (!nature.isBlank()) {
            if (nature.contains(":")) {
                nature = StringUtil.capitalize(nature.substring(nature.indexOf(':') + 1));
            }
            newLore.add(Text.literal("Nature: ").formatted(YELLOW)
                    .append(Text.literal(nature).formatted(WHITE)));
            spacer = true;
        }

        if (!abilityName.isBlank()) {
            newLore.add(Text.literal("Ability: ").formatted(GOLD)
                    .append(Text.literal(StringUtil.capitalize(abilityName)).formatted(WHITE)));
            spacer = true;
        }

        if (!form.isBlank()) {
            newLore.add(Text.literal("Form: ").formatted(WHITE)
                    .append(Text.literal(StringUtil.capitalize(form))));
            spacer = true;
        }

        NbtCompound ivs = customData.get("IVs") instanceof NbtCompound compound ? compound : null;
        if (ivs != null) {
            short hp = Utils.get(ivs, "hp", (short) -1);
            short attack = Utils.get(ivs, "attack", (short) -1);
            short defense = Utils.get(ivs, "defence", (short) -1);
            short spAttack = Utils.get(ivs, "special_attack", (short) -1);
            short spDefense = Utils.get(ivs, "special_defence", (short) -1);
            short speed = Utils.get(ivs, "speed", (short) -1);

            if (spacer) {
                newLore.add(Text.literal(" "));
            }

            if (hp != -1) {
                newLore.add(Text.literal("HP: ").formatted(RED)
                        .append(Text.literal(String.valueOf(hp)).formatted(WHITE)));
            }
            if (attack != -1) {
                newLore.add(Text.literal("Attack: ").formatted(BLUE)
                        .append(Text.literal(String.valueOf(attack)).formatted(WHITE)));
            }
            if (defense != -1) {
                newLore.add(Text.literal("Defense: ").formatted(GRAY)
                        .append(Text.literal(String.valueOf(defense)).formatted(WHITE)));
            }
            if (spAttack != -1) {
                newLore.add(Text.literal("Sp. Attack: ").formatted(AQUA)
                        .append(Text.literal(String.valueOf(spAttack)).formatted(WHITE)));
            }
            if (spDefense != -1) {
                newLore.add(Text.literal("Sp. Defense: ").formatted(YELLOW)
                        .append(Text.literal(String.valueOf(spDefense)).formatted(WHITE)));
            }
            if (speed != -1) {
                newLore.add(Text.literal("Speed: ").formatted(GREEN)
                        .append(Text.literal(String.valueOf(speed)).formatted(WHITE)));
            }
        }
    }

    public static void enhanceBerryLore(Item item, List<Text> newLore) {
        if (item instanceof FriendshipRaisingBerryItem friendshipBerry) {
            evBerry(newLore, friendshipBerry.getStat().getDisplayName().getString());
        } else if (item instanceof PPRestoringBerryItem) {
            newLore.add(Text.literal("Restores a selected move's PP when fed").formatted(GRAY));
        }
    }

    public static void enhanceConsumablesLore(Item item, List<Text> newLore) {
        if (item instanceof MintItem mint) {
            newLore.add(Text.literal("Changes the stat effect of a Pokémon's Nature to %s.".formatted(Text.translatable(mint.getNature().getDisplayName()).getString())).formatted(GRAY));
            newLore.add(Text.literal("Note: This does not change the Pokémon's actual Nature.").formatted(GRAY));
        } else if (item instanceof FeatherItem feather) {
            evFeather(newLore, feather.getStat().getDisplayName().getString());
        } else if (item instanceof VitaminItem vitamin) {
            evMedicine(newLore, vitamin.getStat().getDisplayName().getString());
        } else if (item instanceof PPUpItem) {
            if (item == PP_UP) {
                newLore.add(Text.literal("Increases the maximum PP of a selected move by 20% it's base PP. Can be stacked 3 times.").formatted(GRAY));
            } else if (item == PP_MAX) {
                newLore.add(Text.literal("Increases the maximum PP of a selected move to 160% it's base PP."));
            }
        } else if (item instanceof CandyItem) {
            if (item == EXPERIENCE_CANDY_XS) {
                expCandy(newLore, "100");
            } else if (item == EXPERIENCE_CANDY_S) {
                expCandy(newLore, "800");
            } else if (item == EXPERIENCE_CANDY_M) {
                expCandy(newLore, "3,000");
            } else if (item == EXPERIENCE_CANDY_L) {
                expCandy(newLore, "10,000");
            } else if (item == EXPERIENCE_CANDY_XL) {
                expCandy(newLore, "30,000");
            } else if (item == RARE_CANDY) {
                newLore.add(Text.literal("Increases the Pokémon's level by 1.").formatted(GRAY));
            }
        }
    }

    public static void enhanceHeldItemLore(Item item, List<Text> newLore) {
        if (TYPE_GEMS.contains(item)) {
            String type = StringUtil.capitalize(item.toString().replace("_gem", ""));
            newLore.add(Text.literal("Increases the power of a " + type + " type move by 30%.").formatted(GRAY));
            newLore.add(Text.literal(" "));
            newLore.add(Text.literal("⇢ ").formatted(GRAY).append(Text.literal("Only activates once per battle.").formatted(RED)));
        } else if (item instanceof AbilityChangeItem<?>) {
            if (item == ABILITY_CAPSULE) {
                newLore.add(Text.literal("Changes the ability of a Pokémon to it's alternative standard ability if possible.").formatted(GRAY));
            } else if (item == ABILITY_PATCH) {
                newLore.add(Text.literal("Changes the ability of a Pokémon to it's hidden ability.").formatted(GRAY));
            }
        } else {
            String line = null;
            if (item == EJECT_BUTTON) {
                line = "Causes the holder to switch out if hit by a damaging move. Activates only once per battle.";
            } else if (item == FLOAT_STONE) {
                line = "Halves the holder's weight to a minimum of 0.1kg.";
            } else if (item == EVIOLITE) {
                line = "Boosts the holders Defense and Sp. Defense by 50% if they are not fully evolved.";
            } else if (item == WEAKNESS_POLICY) {
                line = "Raises the holder's Attack and Sp. Attack by two stages when hit by a super-effective move. Activates only once per battle.";
            }

            List<String> lines = null;
            if (item == STICKY_BARB) {
                lines = List.of("Damages the holder by 1/8 of the holder's maximum HP at the end of each turn.",
                        "If a Pokémon with no held item hits the holder with a contact move, the Sticky Barb is transferred to the attacker.");
            }

            if (line != null) {
                newLore.add(Text.literal(line).formatted(GRAY));
            } else if (lines != null) {
                newLore.addAll(lines.stream().map(Text::literal).map(text -> text.formatted(GRAY)).toList());
            }
        }
    }

    private static void evBerry(List<Text> lore, String ev) {
        lore.add(Text.literal("Decreases the Pokémon's %s EV by 10 (if possible), while raising friendship.".formatted(ev)).formatted(GRAY));
    }

    private static void evFeather(List<Text> lore, String ev) {
        lore.add(Text.literal("Increases the Pokémon's %s by 1 if possible.".formatted(ev)).formatted(GRAY));
    }

    private static void expCandy(List<Text> lore, String amount) {
        lore.add(Text.literal("Increases a Pokémon's experience by %s when used.".formatted(amount)).formatted(GRAY));
    }

    private static void evMedicine(List<Text> lore, String ev) {
        lore.add(Text.literal("Increases the Pokémon's %s EV by 10 if possible.".formatted(ev)).formatted(GRAY));
    }
}
