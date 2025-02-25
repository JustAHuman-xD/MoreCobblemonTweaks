package me.justahuman.more_cobblemon_tweaks.mixins;

import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import me.justahuman.more_cobblemon_tweaks.features.egg.BetterBreedingIntegration;
import me.justahuman.more_cobblemon_tweaks.features.egg.CobbreedingIntegration;
import me.justahuman.more_cobblemon_tweaks.features.egg.EnhancedEggLore;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ItemStack.class, priority = 1000000)
public abstract class ItemStackMixin {
    @Inject(method = "getTooltipLines", at = @At(value = "RETURN"), cancellable = true)
    public void changeTooltip(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        final List<Component> lore = new ArrayList<>(cir.getReturnValue());
        if (lore.isEmpty()) {
            return;
        }

        final ItemStack itemStack = moreCobblemonTweaks$cast();
        final List<Component> newLore = new ArrayList<>();

        if (ModConfig.isEnabled("enhanced_egg_lore")) {
            EnhancedEggLore wrapper = null;
            if (Utils.modEnabled("cobbreeding")) {
                wrapper = CobbreedingIntegration.get(itemStack);
            }
            if (wrapper == null) {
                wrapper = BetterBreedingIntegration.get(itemStack);
            }
            if (wrapper != null) {
                LoreEnhancements.enhanceEggLore(lore, newLore, wrapper);
            }
        }

        lore.addAll(1, newLore);
        cir.setReturnValue(lore);
    }

    @Unique
    private ItemStack moreCobblemonTweaks$cast() {
        return (ItemStack) (Object) this;
    }
}
