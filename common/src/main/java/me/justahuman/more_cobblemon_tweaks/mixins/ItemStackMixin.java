package me.justahuman.more_cobblemon_tweaks.mixins;

import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import me.justahuman.more_cobblemon_tweaks.features.egg.EnhancedEggLore;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ItemStack.class, priority = 1000000)
public abstract class ItemStackMixin {
    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    public void changeTooltip(Item instance, ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        instance.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);

        final List<Component> lore = new ArrayList<>(list);
        if (lore.isEmpty()) {
            return;
        }

        final List<Component> newLore = new ArrayList<>();
        if (ModConfig.isEnabled("enhanced_egg_lore")) {
            EnhancedEggLore wrapper = EnhancedEggLore.get(itemStack);
            if (wrapper != null) {
                LoreEnhancements.enhanceEggLore(wrapper, lore, newLore);
            }
        }

        lore.addAll(1, newLore);
        list.clear();
        list.addAll(lore);
    }
}
