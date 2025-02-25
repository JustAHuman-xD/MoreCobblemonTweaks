package me.justahuman.more_cobblemon_tweaks.features;

import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.egg.EnhancedEggLore;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static net.minecraft.ChatFormatting.*;

public class LoreEnhancements {
    private static final String BASE_KEY = "more_cobblemon_tweaks.lore_enhancements.";

    public static void enhanceEggLore(List<Component> lore, List<Component> newLore, EnhancedEggLore enhancedEggLore) {
        Component name = enhancedEggLore.getName(lore);
        final boolean shiny = enhancedEggLore.isShiny();
        if (ModConfig.isEnabled("shiny_egg_indicator") && shiny) {
            name = name.copy().append(Component.literal(" ★").withStyle(YELLOW, BOLD));
        }

        final String gender = enhancedEggLore.getGender();
        if (gender.equals("MALE") || gender.equals("FEMALE")) {
            boolean male = gender.equals("MALE");
            name = name.copy().append(Component.literal(male ? " ♂" : " ♀")
                    .withStyle(style -> style.withColor(male ? 0x32CBFF : 0xFC5454)));
        }
        lore.set(0, name);

        final List<Component> hatchProgress = enhancedEggLore.getHatchProgress();
        boolean spacer = false;

        if (hatchProgress != null && !hatchProgress.isEmpty()) {
            newLore.addAll(hatchProgress);
            spacer = true;
        }

        String nature = enhancedEggLore.getNature();
        String abilityName = enhancedEggLore.getAbility();
        String form = enhancedEggLore.getForm();
        if ((nature != null || abilityName != null || form != null) && spacer) {
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
            newLore.add(translate("egg.ability").withStyle(GOLD)
                    .append(Component.literal(StringUtils.capitalize(abilityName)).withStyle(WHITE)));
            spacer = true;
        }

        if (form != null) {
            newLore.add(translate("egg.form").withStyle(WHITE)
                    .append(Component.literal(StringUtils.capitalize(form))));
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

            if (hp != null && hp != -1) {
                newLore.add(translate("egg.iv.hp").withStyle(RED)
                        .append(Component.literal(String.valueOf(hp)).withStyle(WHITE)));
            }
            if (attack != null && attack != -1) {
                newLore.add(translate("egg.iv.attack").withStyle(BLUE)
                        .append(Component.literal(String.valueOf(attack)).withStyle(WHITE)));
            }
            if (defense != null && defense != -1) {
                newLore.add(translate("egg.iv.defense").withStyle(GRAY)
                        .append(Component.literal(String.valueOf(defense)).withStyle(WHITE)));
            }
            if (spAttack != null && spAttack != -1) {
                newLore.add(translate("egg.iv.sp_attack").withStyle(AQUA)
                        .append(Component.literal(String.valueOf(spAttack)).withStyle(WHITE)));
            }
            if (spDefense != null && spDefense != -1) {
                newLore.add(translate("egg.iv.sp_defense").withStyle(YELLOW)
                        .append(Component.literal(String.valueOf(spDefense)).withStyle(WHITE)));
            }
            if (speed != null && speed != -1) {
                newLore.add(translate("egg.iv.speed").withStyle(GREEN)
                        .append(Component.literal(String.valueOf(speed)).withStyle(WHITE)));
            }
        }
    }

    public static MutableComponent translate(String key, Object... args) {
        return Component.translatable(BASE_KEY + key, args);
    }
}
