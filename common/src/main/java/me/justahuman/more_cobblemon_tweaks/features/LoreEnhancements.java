package me.justahuman.more_cobblemon_tweaks.features;

import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.egg.EnhancedEggLore;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.List;

import static net.minecraft.ChatFormatting.*;

public class LoreEnhancements {
    private static final String BASE_KEY = "more_cobblemon_tweaks.lore_enhancements.";

    public static void enhanceEggLore(List<Component> lore, List<Component> newLore, EnhancedEggLore enhancedEggLore) {
        Component name = enhancedEggLore.getName(lore);
        final boolean shiny = ModConfig.isEnabled("shiny_egg_indicator") && enhancedEggLore.isShiny();
        final boolean perfect = ModConfig.isEnabled("perfect_iv_egg_indicator") && enhancedEggLore.hasIVs(31);
        final boolean minimum = ModConfig.isEnabled("minimum_iv_egg_indicator") && enhancedEggLore.hasIVs(0);
        final boolean textIndicators = ModConfig.isEnabled("text_egg_indicators");
        if (shiny && !textIndicators) {
            name = name.copy().append(translate("egg.shiny.symbol").withStyle(YELLOW));
        }
        if (perfect && !textIndicators) {
            name = name.copy().append(translate("egg.perfect.symbol").withStyle(AQUA));
        } else if (minimum && !textIndicators) {
            name = name.copy().append(translate("egg.minimum.symbol").withStyle(RED));
        }

        final String gender = enhancedEggLore.getGender();
        if (gender != null && (gender.equals("MALE") || gender.equals("FEMALE"))) {
            boolean male = gender.equals("MALE");
            name = name.copy().append(Component.literal(male ? " ♂" : " ♀")
                    .withStyle(style -> style.withColor(male ? 0x32CBFF : 0xFC5454)));
        }
        lore.set(0, name);

        if (shiny && textIndicators) {
            newLore.add(translate("egg.shiny.text").withStyle(YELLOW));
            newLore.add(Component.literal(" "));
        }

        final List<Component> hatchProgress = enhancedEggLore.getHatchProgress(lore);
        boolean spacer = false;

        if (hatchProgress != null && !hatchProgress.isEmpty()) {
            newLore.addAll(hatchProgress);
            spacer = true;
        }

        String nature = enhancedEggLore.getNature();
        String abilityName = enhancedEggLore.getAbility();
        String form = enhancedEggLore.getForm();
        String pokeBall = enhancedEggLore.getPokeBall();
        if ((nature != null || abilityName != null || form != null || pokeBall != null) && spacer) {
            newLore.add(Component.literal(" "));
            spacer = false;
        }

        if (nature != null) {
            if (nature.contains(":")) {
                nature = StringUtils.capitalize(nature.substring(nature.indexOf(':') + 1));
            }
            newLore.add(translate("egg.nature").withStyle(YELLOW)
                    .append(Component.literal(nature).withStyle(WHITE)));
            spacer = true;
        }

        if (abilityName != null) {
            newLore.add(translate("egg.ability").withColor(new Color(0xFFD800).getRGB())
                    .append(Component.literal(StringUtils.capitalize(abilityName)).withStyle(WHITE)));
            spacer = true;
        }

        if (form != null) {
            newLore.add(translate("egg.form").withColor(new Color(0xFFBB00).getRGB())
                    .append(Component.literal(StringUtils.capitalize(form)).withStyle(WHITE)));
            spacer = true;
        }

        if (pokeBall != null) {
            newLore.add(translate("egg.pokeball").withStyle(GOLD)
                    .append(Component.literal(StringUtils.capitalize(pokeBall)).withStyle(WHITE)));
            spacer = true;
        }

        if (enhancedEggLore.hasIVs()) {
            Integer hp = enhancedEggLore.getHpIV();
            Integer attack = enhancedEggLore.getAtkIV();
            Integer defense = enhancedEggLore.getDefIV();
            Integer spAttack = enhancedEggLore.getSpAtkIV();
            Integer spDefense = enhancedEggLore.getSpDefIV();
            Integer speed = enhancedEggLore.getSpeedIV();

            if (spacer) {
                newLore.add(Component.literal(" "));
            }

            Integer sum = null;
            if (hp != null && hp != -1) {
                newLore.add(iv("hp", GREEN, hp));
                sum = hp;
            }
            if (attack != null && attack != -1) {
                newLore.add(iv("attack", RED, attack));
                sum = (sum == null ? attack : sum + attack);
            }
            if (defense != null && defense != -1) {
                newLore.add(iv("defense", GOLD, defense));
                sum = (sum == null ? defense : sum + defense);
            }
            if (spAttack != null && spAttack != -1) {
                newLore.add(iv("sp_attack", LIGHT_PURPLE, spAttack));
                sum = (sum == null ? spAttack : sum + spAttack);
            }
            if (spDefense != null && spDefense != -1) {
                newLore.add(iv("sp_defense", YELLOW, spDefense));
                sum = (sum == null ? spDefense : sum + spDefense);
            }
            if (speed != null && speed != -1) {
                newLore.add(iv("speed", AQUA, speed));
                sum = (sum == null ? speed : sum + speed);
            }
            if (sum != null) {
                int average = (int) (sum / 6.0);
                newLore.add(iv("average", WHITE, average));
            }
            if (perfect && textIndicators) {
                newLore.add(translate("egg.perfect.text").withStyle(WHITE));
            } else if (minimum && textIndicators) {
                newLore.add(translate("egg.minimum.text").withStyle(WHITE));
            }
        }
    }

    public static MutableComponent iv(String stat, ChatFormatting color, int iv) {
        return translate("egg.iv." + stat).withStyle(color)
                .append(Component.literal(Screen.hasShiftDown() ? Math.round(iv / 31.0 * 100) + "%" : Integer.toString(iv)).withStyle(WHITE));
    }

    public static MutableComponent translate(String key, Object... args) {
        return Component.translatable(BASE_KEY + key, args);
    }
}
