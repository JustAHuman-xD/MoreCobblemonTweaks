package me.justahuman.more_cobblemon_tweaks.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen buildScreen(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("More Cobblemon Tweaks"));

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        final ConfigCategory pcCategory = builder.getOrCreateCategory(Text.literal("Pc Enhancements"));
        final ConfigCategory loreCategory = builder.getOrCreateCategory(Text.literal("Lore Enhancements"));
        //final ConfigCategory otherCategory = builder.getOrCreateCategory(Text.literal("Other Tweaks"));

        /* Pc Config Options */

        pcCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Box Search"), ModConfig.isEnabled("pc_search"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Adds a button to open/close a search bar"))
                .setSaveConsumer(value -> ModConfig.setEnabled("pc_search", value))
                .build());

        pcCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Open Box History"), ModConfig.isEnabled("open_box_history"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Should the PC remember and open to the last box you had open?"))
                .setSaveConsumer(value -> ModConfig.setEnabled("open_box_history", value))
                .build());

        pcCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("IV Display"), ModConfig.isEnabled("pc_iv_display"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Adds a small widget to the pc menu showing the iv's of the previewed pokemon."))
                .setSaveConsumer(value -> ModConfig.setEnabled("pc_iv_display", value))
                .build());

        pcCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Custom Box Names"), ModConfig.isEnabled("custom_pc_box_names"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Allows the player to change PC box names."))
                .setSaveConsumer(value -> ModConfig.setEnabled("custom_pc_box_names", value))
                .build());

        pcCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Box Wallpapers"), ModConfig.isEnabled("custom_pc_wallpapers"))
                .setDefaultValue(true)
                .setTooltip(Text.literal("Allows the player to change PC wallpaper."))
                .setSaveConsumer(value -> ModConfig.setEnabled("custom_pc_wallpapers", value))
                .build());

        /* Lore Config Options */

        loreCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enhanced Egg Lore (Hulis)"), ModConfig.isEnabled("enhanced_egg_lore"))
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

        /* Other Tweaks */

        // todo

        builder.setSavingRunnable(ModConfig::saveConfig);
        return builder.build();
    }
}
