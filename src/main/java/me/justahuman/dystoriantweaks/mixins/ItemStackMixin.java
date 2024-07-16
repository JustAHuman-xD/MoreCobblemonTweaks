package me.justahuman.dystoriantweaks.mixins;

import me.justahuman.dystoriantweaks.ModConfig;
import me.justahuman.dystoriantweaks.Utils;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.Formatting.*;

@Mixin(value = ItemStack.class, priority = 1000000)
public abstract class ItemStackMixin {
    @Shadow @Nullable
    public abstract NbtCompound getNbt();
    @Shadow
    public abstract Item getItem();


    @Inject(method = "getTooltip", at = @At(value = "RETURN"))
    public void changeTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        NbtCompound nbt = getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        final List<Text> lore = cir.getReturnValue();
        final List<Text> additionalLore = new ArrayList<>();
        final String polymerItem = nbt.get("Polymer$itemId") instanceof NbtString nbtString ? nbtString.asString() : null;
        final NbtCompound customData = nbt.get("Polymer$itemTag") instanceof NbtCompound compound ? compound : null;
        if (ModConfig.isEnabled("enhanced_egg_lore") && "huliscobblebreeding:pokemonegg".equals(polymerItem)) {
            if (customData != null) {
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
                    additionalLore.add(Text.literal("Egg Cycles Remaining: ").formatted(GREEN)
                            .append(Text.literal(String.valueOf(cycles)).formatted(WHITE)));
                    spacer = true;
                }
                if (steps != -1) {
                    additionalLore.add(Text.literal("Steps Left: ").formatted(AQUA)
                            .append(Text.literal(String.valueOf(Math.round(steps))).formatted(WHITE)));
                    spacer = true;
                }

                String nature = Utils.get(customData, "Nature", "");
                NbtCompound ability = customData.get("Ability") instanceof NbtCompound compound ? compound : null;
                String abilityName = Utils.get(ability, "AbilityName", "");
                String form = Utils.get(customData, "FormId", "");
                if ((!nature.isBlank() || !abilityName.isBlank() || !form.isBlank()) && spacer) {
                    additionalLore.add(Text.literal(" "));
                    spacer = false;
                }

                if (!nature.isBlank()) {
                    if (nature.contains(":")) {
                        nature = StringUtil.capitalize(nature.substring(nature.indexOf(':') + 1));
                    }
                    additionalLore.add(Text.literal("Nature: ").formatted(YELLOW)
                            .append(Text.literal(nature).formatted(WHITE)));
                    spacer = true;
                }

                if (!abilityName.isBlank()) {
                    additionalLore.add(Text.literal("Ability: ").formatted(GOLD)
                            .append(Text.literal(StringUtil.capitalize(abilityName)).formatted(WHITE)));
                    spacer = true;
                }

                if (!form.isBlank()) {
                    additionalLore.add(Text.literal("Form: ").formatted(WHITE)
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
                        additionalLore.add(Text.literal(" "));
                    }

                    if (hp != -1) {
                        additionalLore.add(Text.literal("HP: ").formatted(RED)
                                .append(Text.literal(String.valueOf(hp)).formatted(WHITE)));
                    }
                    if (attack != -1) {
                        additionalLore.add(Text.literal("Attack: ").formatted(BLUE)
                                .append(Text.literal(String.valueOf(attack)).formatted(WHITE)));
                    }
                    if (defense != -1) {
                        additionalLore.add(Text.literal("Defense: ").formatted(GRAY)
                                .append(Text.literal(String.valueOf(defense)).formatted(WHITE)));
                    }
                    if (spAttack != -1) {
                        additionalLore.add(Text.literal("Sp. Attack: ").formatted(AQUA)
                                .append(Text.literal(String.valueOf(spAttack)).formatted(WHITE)));
                    }
                    if (spDefense != -1) {
                        additionalLore.add(Text.literal("Sp. Defense: ").formatted(YELLOW)
                                .append(Text.literal(String.valueOf(spDefense)).formatted(WHITE)));
                    }
                    if (speed != -1) {
                        additionalLore.add(Text.literal("Speed: ").formatted(GREEN)
                                .append(Text.literal(String.valueOf(speed)).formatted(WHITE)));
                    }
                }
            }
        }

        final String key = Registries.ITEM.getId(getItem()).toString();
        final String cobbleKey = key.startsWith("cobblemon:") ? key.substring(10) : null;
        if (ModConfig.isEnabled("enhanced_berry_lore") && cobbleKey != null && cobbleKey.endsWith("_berry")) {
            switch(cobbleKey.substring(0, key.lastIndexOf('_'))) {
                case "tamato" -> evBerry(additionalLore, "Speed");
                case "grepa" -> evBerry(additionalLore, "Sp. Defense");
                case "hondew" -> evBerry(additionalLore, "Sp. Attack");
                case "qualot" -> evBerry(additionalLore, "Defense");
                case "pomeg" -> evBerry(additionalLore, "HP");
                case "kelpsey" -> evBerry(additionalLore, "Attack");
                case "hopo", "leppa"
                        -> additionalLore.add(Text.literal("Restores a selected move's PP when fed").formatted(GRAY));
            }
        }

        if (ModConfig.isEnabled("enhanced_consumable_lore") && cobbleKey != null) {
            if (cobbleKey.endsWith("_mint")) {
                String nature = cobbleKey.substring(0, key.indexOf('_'));
                additionalLore.add(Text.literal("When used on a Pokémon, it changes the effect of a Pokémon's Nature on its stats to that of the %s Nature.".formatted(nature)).formatted(GRAY));
                additionalLore.add(Text.literal("This does not change the Pokémon's actual Nature.").formatted(GRAY));
            } else {
                switch (cobbleKey) {
                    case "health_feather" -> evFeather(additionalLore, "HP");
                    case "muscle_feather" -> evFeather(additionalLore, "Attack");
                    case "resist_feather" -> evFeather(additionalLore, "Defense");
                    case "genius_feather" -> evFeather(additionalLore, "Sp. Attack");
                    case "clever_feather" -> evFeather(additionalLore, "Sp. Defense");
                    case "swift_feather" -> evFeather(additionalLore, "Speed");
                    case "hp_up" -> evMedicine(additionalLore, "HP");
                    case "protein" -> evMedicine(additionalLore, "Attack");
                    case "iron" -> evMedicine(additionalLore, "Defense");
                    case "calcium" -> evMedicine(additionalLore, "Sp. Attack");
                    case "zinc" -> evMedicine(additionalLore, "Sp. Defense");
                    case "carbos" -> evMedicine(additionalLore, "Speed");
                    case "pp_up" -> additionalLore.add(Text.literal("Increases the maximum PP of a selected move by 20% it's base PP. Can be stacked 3 times.").formatted(GRAY));
                    case "pp_max" -> additionalLore.add(Text.literal("Increases the maximum PP of a selected move to 160% it's base PP."));
                    case "exp_candy_xs" -> expCandy(additionalLore, 100);
                    case "exp_candy_s" -> expCandy(additionalLore, 800);
                    case "exp_candy_m" -> expCandy(additionalLore, 3000);
                    case "exp_candy_l" -> expCandy(additionalLore, 10000);
                    case "exp_candy_xl" -> expCandy(additionalLore, 30000);
                    case "rare_candy" -> additionalLore.add(Text.literal("Increases the Pokémon's level by 1.").formatted(GRAY));
                }
            }
        }

        if (ModConfig.isEnabled("enhanced_held_item_lore") && cobbleKey != null) {
            if (key.endsWith("_gem")) {
                String type = StringUtil.capitalize(cobbleKey.substring(0, key.lastIndexOf('_')));
                additionalLore.add(Text.literal("Increases the power of a" + type + " type move by 30%.").formatted(GRAY));
                additionalLore.add(Text.literal(" "));
                additionalLore.add(Text.literal("⇢ ").formatted(GRAY).append(Text.literal("Only activates once per battle.").formatted(RED)));
            } else {
                String line = switch(cobbleKey) {
                    case "ability_capsule" -> "Changes the ability of a Pokémon to it's alternative standard ability if possible.";
                    case "ability_patch" -> "Changes the ability of a Pokémon to it's hidden ability.";
                    case "eject_button" -> "Causes the holder to switch out if hit by a damaging move. Activates only once per battle.";
                    case "float_stone" -> "Halves the holder's weight to a minimum of 0.1kg.";
                    case "eviolite" -> "Boosts the holders Defense and Sp. Defense by 50% if they are not fully evolved.";
                    case "weakness_policy" -> "Raises the holder's Attack and Sp. Attack by two stages when hit by a super-effective move. Activates only once per battle.";
                    default -> null;
                };

                List<String> lines = switch (cobbleKey) {
                    case "sticky_barb" -> List.of("Damages the holder by 1/8 of the holder's maximum HP at the end of each turn.", "If a Pokémon with no held item hits the holder with a contact move, the Sticky Barb is transferred to the attacker.");
                    default -> null;
                };

                if (line != null) {
                    additionalLore.add(Text.literal(line).formatted(GRAY));
                } else if (lines != null) {
                    additionalLore.addAll(lines.stream().map(Text::literal).map(text -> text.formatted(GRAY)).toList());
                }
            }
        }

        lore.addAll(1, additionalLore);
    }

    @Unique
    public void evBerry(List<Text> lore, String ev) {
        lore.add(Text.literal("Decreases the Pokémon's %s EV by 10 (if possible), while raising friendship.".formatted(ev)).formatted(GRAY));
    }

    @Unique
    public void evFeather(List<Text> lore, String ev) {
        lore.add(Text.literal("Increases the Pokémon's %s by 1 if possible.".formatted(ev)).formatted(GRAY));
    }

    @Unique
    public void expCandy(List<Text> lore, int amount) {
        lore.add(Text.literal(String.format("Increases a Pokémon's experience by %.2f when used.", (double) amount)).formatted(GRAY));
    }

    @Unique
    public void evMedicine(List<Text> lore, String ev) {
        lore.add(Text.literal("Increases the Pokémon's %s EV by 10 if possible.".formatted(ev)).formatted(GRAY));
    }

    @Unique
    public void changeIdentifier(List<Text> lore, String newIdentifier, String newSource) {
        final Identifier identifier = Registries.ITEM.getId(getItem());
        final String idLine = identifier.toString();
        for (int i = 0; i < lore.size(); i++) {
            final String line = lore.get(i).getString();
            if (line.equals(idLine)) {
                lore.set(i, Text.literal(newIdentifier).formatted(DARK_GRAY));
            } else if (line.equals("Minecraft")) {
                lore.set(i, Text.literal(newSource).formatted(BLUE).formatted(ITALIC));
            }
        }
    }
}
