package me.justahuman.more_cobblemon_tweaks.mixins;

import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.egg.BetterBreedingIntegration;
import me.justahuman.more_cobblemon_tweaks.features.egg.CobBreedingIntegration;
import me.justahuman.more_cobblemon_tweaks.features.egg.HulisIntegration;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

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

        final Item item = getItem();
        final String itemId = Registries.ITEM.getId(item).toString();
        final List<Text> lore = cir.getReturnValue();
        final List<Text> newLore = new ArrayList<>();
        final String polymerItem = nbt.get("Polymer$itemId") instanceof NbtString nbtString ? nbtString.asString() : null;
        final NbtCompound customData = nbt.get("Polymer$itemTag") instanceof NbtCompound compound ? compound : null;
        if (ModConfig.isEnabled("enhanced_egg_lore")) {
            if (HulisIntegration.POLYMER_ID.equals(polymerItem) && customData != null) {
                LoreEnhancements.enhanceEggLore(lore, newLore, new HulisIntegration(nbt));
            } else if (CobBreedingIntegration.ITEM_ID.equals(itemId)) {
                LoreEnhancements.enhanceEggLore(lore, newLore, new CobBreedingIntegration(nbt));
            } else if (BetterBreedingIntegration.isEgg(nbt)) {
                LoreEnhancements.enhanceEggLore(lore, newLore, new BetterBreedingIntegration(nbt));
            }
        }

        if (ModConfig.isEnabled("enhanced_berry_lore")) {
            LoreEnhancements.enhanceBerryLore(item, newLore);
        }

        if (ModConfig.isEnabled("enhanced_consumable_lore")) {
            LoreEnhancements.enhanceConsumablesLore(item, newLore);
        }

        if (ModConfig.isEnabled("enhanced_held_item_lore")) {
            LoreEnhancements.enhanceHeldItemLore(item, newLore);
        }

        if (ModConfig.isEnabled("wt_compact_lore")) {
            // TODO
        }

        lore.addAll(1, newLore);
    }
}
