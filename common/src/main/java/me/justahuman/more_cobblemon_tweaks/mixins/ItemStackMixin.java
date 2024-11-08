package me.justahuman.more_cobblemon_tweaks.mixins;

import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ItemStack.class, priority = 1000000)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();
    @Shadow public abstract ComponentMap getComponents();

    @Inject(method = "getTooltip", at = @At(value = "RETURN"))
    public void changeTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        final Item item = getItem();
        final ComponentMap components = getComponents();
        final String itemId = Registries.ITEM.getId(item).toString();
        final List<Text> lore = cir.getReturnValue();
        final List<Text> newLore = new ArrayList<>();

        if (ModConfig.isEnabled("enhanced_egg_lore")) {
//            TODO: Update this to use components once each mod is updated
//            if (HulisIntegration.POLYMER_ID.equals(polymerItem) && customData != null) {
//                LoreEnhancements.enhanceEggLore(lore, newLore, new HulisIntegration(nbt));
//            } else if (CobBreedingIntegration.ITEM_ID.equals(itemId)) {
//                LoreEnhancements.enhanceEggLore(lore, newLore, new CobBreedingIntegration(nbt));
//            } else if (BetterBreedingIntegration.isEgg(nbt)) {
//                LoreEnhancements.enhanceEggLore(lore, newLore, new BetterBreedingIntegration(nbt));
//            }
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

        lore.addAll(1, newLore);
    }
}
