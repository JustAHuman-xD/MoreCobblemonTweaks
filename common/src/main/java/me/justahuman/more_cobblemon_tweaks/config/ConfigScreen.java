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
        //final ConfigCategory otherCategory = builder.getOrCreateCategory(Component.literal("Other Tweaks"));

        /* Pc Config Options */

        pcCategory.addEntry(basicToggle(entryBuilder, "pc_search"));
        pcCategory.addEntry(basicToggle(entryBuilder, "open_box_history"));
        pcCategory.addEntry(basicToggle(entryBuilder, "pc_iv_display"));
        pcCategory.addEntry(basicToggle(entryBuilder, "custom_pc_box_names"));
        pcCategory.addEntry(basicToggle(entryBuilder, "custom_pc_wallpapers"));

        /* Lore Config Options */

        loreCategory.addEntry(basicToggle(entryBuilder, "enhanced_egg_lore", entry ->
                entry.setRequirement(() -> false).setTooltip(Component.translatable("more_cobblemon_tweaks.config.option.waiting_for_updates"))));
        loreCategory.addEntry(basicToggle(entryBuilder, "shiny_egg_indicator", entry ->
                entry.setRequirement(() -> false && ModConfig.isEnabled("enhanced_egg_lore") && !ModConfig.serverOverride("shiny_egg_indicator"))
                        .setTooltipSupplier(() -> {
                            if (true) {
                                return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option.waiting_for_updates") });
                            }
                            if (ModConfig.serverOverride("shiny_egg_indicator")) {
                                return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option.overridden_tooltip") });
                            } else if (!ModConfig.isEnabled("enhanced_egg_lore")) {
                                return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option.shiny_egg_indicator.requires_egg_lore") });
                            }
                            return Optional.of(new Component[] { Component.translatable("more_cobblemon_tweaks.config.option.shiny_egg_indicator.tooltip") });
                        })));

        /* Other Tweaks */

        // todo

        builder.setSavingRunnable(ModConfig::saveConfig);
        return builder.build();
    }

    private static AbstractConfigListEntry<?> basicToggle(ConfigEntryBuilder builder, String key) {
        return basicToggle(builder, key, entry -> {});
    }

    private static AbstractConfigListEntry<?> basicToggle(ConfigEntryBuilder builder, String key, Consumer<BooleanToggleBuilder> unique) {
        BooleanToggleBuilder entry = builder.startBooleanToggle(Component.translatable("more_cobblemon_tweaks.config.option." + key), ModConfig.isEnabled(key))
                .setRequirement(() -> !ModConfig.serverOverride(key))
                .setDefaultValue(true)
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
