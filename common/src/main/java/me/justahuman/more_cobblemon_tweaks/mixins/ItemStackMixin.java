package me.justahuman.more_cobblemon_tweaks.mixins;

import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
    @Shadow public abstract DataComponentMap getComponents();

    @Inject(method = "getTooltipLines", at = @At(value = "RETURN"), cancellable = true)
    public void changeTooltip(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        final List<Component> lore = new ArrayList<>(cir.getReturnValue());
        if (lore.isEmpty()) {
            return;
        }

        final Item item = getItem();
        final DataComponentMap components = getComponents();
        final String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
        final List<Component> newLore = new ArrayList<>();

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

        lore.addAll(1, newLore);
        cir.setReturnValue(lore);
    }
}
