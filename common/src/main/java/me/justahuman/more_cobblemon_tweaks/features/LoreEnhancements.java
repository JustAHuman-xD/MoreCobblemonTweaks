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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

import static com.cobblemon.mod.common.CobblemonItems.*;
import static net.minecraft.ChatFormatting.*;

public class LoreEnhancements {
    private static final String BASE_KEY = "more_cobblemon_tweaks.lore_enhancements.";
    private static final Set<Item> TYPE_GEMS = Set.of(NORMAL_GEM, FIRE_GEM, WATER_GEM, GRASS_GEM, ELECTRIC_GEM, ICE_GEM, FIGHTING_GEM,
            POISON_GEM, GROUND_GEM, FLYING_GEM, PSYCHIC_GEM, BUG_GEM, ROCK_GEM, GHOST_GEM, DRAGON_GEM, DARK_GEM, STEEL_GEM, FAIRY_GEM);

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
        if ((!nature.isBlank() || !abilityName.isBlank() || !form.isBlank()) && spacer) {
            newLore.add(Component.literal(" "));
            spacer = false;
        }

        if (!nature.isBlank()) {
            if (nature.contains(":")) {
                nature = StringUtils.capitalize(nature.substring(nature.indexOf(':') + 1));
            }
            newLore.add(translate("egg.nature").withStyle(YELLOW)
                    .append(Component.literal(nature).withStyle(WHITE)));
            spacer = true;
        }

        if (!abilityName.isBlank()) {
            newLore.add(translate("egg.ability").withStyle(GOLD)
                    .append(Component.literal(StringUtils.capitalize(abilityName)).withStyle(WHITE)));
            spacer = true;
        }

        if (!form.isBlank()) {
            newLore.add(translate("egg.form").withStyle(WHITE)
                    .append(Component.literal(StringUtils.capitalize(form))));
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
                newLore.add(Component.literal(" "));
            }

            if (hp != -1) {
                newLore.add(translate("egg.iv.hp").withStyle(RED)
                        .append(Component.literal(String.valueOf(hp)).withStyle(WHITE)));
            }
            if (attack != -1) {
                newLore.add(translate("egg.iv.attack").withStyle(BLUE)
                        .append(Component.literal(String.valueOf(attack)).withStyle(WHITE)));
            }
            if (defense != -1) {
                newLore.add(translate("egg.iv.defense").withStyle(GRAY)
                        .append(Component.literal(String.valueOf(defense)).withStyle(WHITE)));
            }
            if (spAttack != -1) {
                newLore.add(translate("egg.iv.sp_attack").withStyle(AQUA)
                        .append(Component.literal(String.valueOf(spAttack)).withStyle(WHITE)));
            }
            if (spDefense != -1) {
                newLore.add(translate("egg.iv.sp_defense").withStyle(YELLOW)
                        .append(Component.literal(String.valueOf(spDefense)).withStyle(WHITE)));
            }
            if (speed != -1) {
                newLore.add(translate("egg.iv.speed").withStyle(GREEN)
                        .append(Component.literal(String.valueOf(speed)).withStyle(WHITE)));
            }
        }
    }

    public static MutableComponent translate(String key, Object... args) {
        return Component.translatable(BASE_KEY + key, args);
    }
}
