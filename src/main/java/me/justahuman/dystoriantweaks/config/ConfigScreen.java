package me.justahuman.dystoriantweaks.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen buildScreen(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Dystorian Tweaks"));

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        final ConfigCategory pcCategory = builder.getOrCreateCategory(Text.literal("Pc Enhancements"));
        final ConfigCategory loreCategory = builder.getOrCreateCategory(Text.literal("Lore Enhancements"));

        /* Pc Config Options */

        pcCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("IV Display"), ModConfig.isEnabled("pc_iv_display"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Adds a small widget to the pc menu showing the iv's of the previewed pokemon."))
                .setSaveConsumer(value -> ModConfig.setEnabled("pc_iv_display", value))
                .build());

        /*pcCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Custom Box Names"), ModConfig.isEnabled("custom_pc_box_names"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Allows the player to change PC box names."))
                .setSaveConsumer(value -> ModConfig.setEnabled("custom_pc_box_names", value))
                .build());*/

        /* Lore Config Options */

        loreCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enhanced Egg Lore"), ModConfig.isEnabled("enhanced_egg_lore"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Adds the results of /eggstats directly to a Pokemon Egg's lore."))
                .setSaveConsumer(value -> ModConfig.setEnabled("enhanced_egg_lore", value))
                .build());

        loreCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enhanced Berry Lore"), ModConfig.isEnabled("enhanced_berry_lore"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Adds lore to berries that don't describe their behavior. (Tamato Berry, Pomeg Berry, etc)"))
                .setSaveConsumer(value -> ModConfig.setEnabled("enhanced_berry_lore", value))
                .build());

        loreCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enhanced Consumables Lore"), ModConfig.isEnabled("enhanced_consumable_lore"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Adds lore to various consumables without it. (Protein, Exp Candy, etc)"))
                .setSaveConsumer(value -> ModConfig.setEnabled("enhanced_consumable_lore", value))
                .build());

        loreCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enhanced Held Item Lore"), ModConfig.isEnabled("enhanced_held_item_lore"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Adds lore to held items without any (Type Gems, Eject button, etc)"))
                .setSaveConsumer(value -> ModConfig.setEnabled("enhanced_held_item_lore", value))
                .build());

        /*loreCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Compact Wonder Trade Lore"), ModConfig.isEnabled("wt_compact_lore"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Compacts the lore displayed when hovering over a WT pokemon in the menu."))
                .setSaveConsumer(value -> ModConfig.setEnabled("wt_compact_lore", value))
                .build());*/

        builder.setSavingRunnable(ModConfig::saveConfig);
        return builder.build();
    }
}
