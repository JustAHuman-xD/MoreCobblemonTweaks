package me.justahuman.more_cobblemon_tweaks.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigScreen {
    public static Screen buildScreen(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("More Cobblemon Tweaks"));

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        final ConfigCategory pcCategory = builder.getOrCreateCategory(Component.literal("Pc Enhancements"));
        final ConfigCategory loreCategory = builder.getOrCreateCategory(Component.literal("Lore Enhancements"));

        /* Pc Config Options */

        pcCategory.addEntry(basicToggle(entryBuilder, "pc_iv_display"));
        pcCategory.addEntry(basicToggle(entryBuilder, "pc_colored_ivs", "pc_iv_display"));
        pcCategory.addEntry(basicToggle(entryBuilder, "pc_multi_select"));
        pcCategory.addEntry(basicToggle(entryBuilder, "pc_box_view"));

        /* Lore Config Options */

        loreCategory.addEntry(basicToggle(entryBuilder, "enhanced_egg_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "egg_encryption_warning", "enhanced_egg_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "shiny_egg_indicator", "enhanced_egg_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "perfect_iv_egg_indicator", "enhanced_egg_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "minimum_iv_egg_indicator", "enhanced_egg_lore"));
        loreCategory.addEntry(basicToggle(entryBuilder, "text_egg_indicators", "enhanced_egg_lore"));

        builder.setSavingRunnable(ModConfig::saveConfig);
        return builder.build();
    }

    private static AbstractConfigListEntry<?> basicToggle(ConfigEntryBuilder builder, String key) {
        return basicToggle(builder, key, entry -> {});
    }

    private static AbstractConfigListEntry<?> basicToggle(ConfigEntryBuilder builder, String key, String requires) {
        return basicToggle(builder, key, entry -> entry.setRequirement(() -> ModConfig.isEnabled(requires) && !ModConfig.serverOverride(key))
                .setTooltipSupplier(() -> {
                    if (ModConfig.serverOverride(key)) {
                        return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option.overridden_tooltip") });
                    } else if (!ModConfig.isEnabled(requires)) {
                        return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option." + key + ".requirements") });
                    }
                    return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option." + key + ".tooltip") });
                }));
    }

    private static AbstractConfigListEntry<?> basicToggle(ConfigEntryBuilder builder, String key, Consumer<BooleanToggleBuilder> unique) {
        BooleanToggleBuilder entry = builder.startBooleanToggle(Component.translatable("more_cobblemon_tweaks.config.option." + key), ModConfig.isEnabled(key))
                .setRequirement(() -> !ModConfig.serverOverride(key))
                .setDefaultValue(ModConfig.getDefault(key))
                .setTooltipSupplier(tooltip(key))
                .setSaveConsumer(value -> ModConfig.setEnabled(key, value));
        unique.accept(entry);
        return entry.build();
    }

    private static Supplier<Optional<Component[]>> tooltip(String key) {
        return () -> {
            if (ModConfig.serverOverride(key)) {
                return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option.overridden_tooltip") });
            }
            return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option." + key + ".tooltip") });
        };
    }
}
