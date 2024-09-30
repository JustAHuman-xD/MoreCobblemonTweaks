package me.justahuman.more_cobblemon_tweaks.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

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

        pcCategory.addEntry(basicToggle(entryBuilder, "pc_search"));
        pcCategory.addEntry(basicToggle(entryBuilder, "open_box_history"));
        pcCategory.addEntry(basicToggle(entryBuilder, "pc_iv_display"));
        pcCategory.addEntry(basicToggle(entryBuilder, "custom_pc_box_names"));
        pcCategory.addEntry(basicToggle(entryBuilder, "custom_pc_wallpapers"));

        /* Lore Config Options */

        loreCategory.addEntry(basicToggle(entryBuilder, "enhanced_egg_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "enhanced_berry_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "enhanced_consumable_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "enhanced_held_item_lore"));

        /* Other Tweaks */

        // todo

        builder.setSavingRunnable(ModConfig::saveConfig);
        return builder.build();
    }

    private static AbstractConfigListEntry<?> basicToggle(ConfigEntryBuilder builder, String key) {
        return builder.startBooleanToggle(Text.translatable("more_cobblemon_tweaks.config.option." + key), ModConfig.isEnabled(key))
                .setRequirement(() -> !ModConfig.serverOverride(key))
                .setDefaultValue(true)
                .setTooltipSupplier(tooltip(key))
                .setSaveConsumer(value -> ModConfig.setEnabled(key, value))
                .build();
    }

    private static Supplier<Optional<Text[]>> tooltip(String key) {
        return () -> {
            if (ModConfig.serverOverride(key)) {
                return Optional.of(new Text[] { Text.translatable("more_cobblemon_tweaks.config.option.overridden_tooltip") });
            }
            return Optional.of(new Text[] { Text.translatable("more_cobblemon_tweaks.config.option." + key + ".tooltip") });
        };
    }
}
