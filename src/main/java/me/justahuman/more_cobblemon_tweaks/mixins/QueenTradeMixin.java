package me.justahuman.more_cobblemon_tweaks.mixins;

import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.jei.JEIQueenTradesInfo;
import com.telepathicgrunt.the_bumblezone.modcompat.recipecategories.jei.QueenTradesJEICategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(QueenTradesJEICategory.class)
public abstract class QueenTradeMixin {
    @Inject(at = @At("HEAD"), method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/telepathicgrunt/the_bumblezone/modcompat/recipecategories/jei/JEIQueenTradesInfo;Lmezz/jei/api/recipe/IFocusGroup;)V", remap = false, cancellable = true)
    public void setRecipe(IRecipeLayoutBuilder builder, JEIQueenTradesInfo recipe, IFocusGroup focuses, CallbackInfo ci) {
        TagKey<Item> key = recipe.input.tagKey().orElse(null);
        RegistryEntryList.Named<Item> items = key != null ? Registries.ITEM.getEntryList(key).orElse(null) : null;
        if (items != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 6, 6).addItemStacks(items.stream().map(RegistryEntry::value).map(Item::getDefaultStack).toList());
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 6, 6).addItemStack(recipe.input.item().getDefaultStack());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 64, 6).addItemStacks(recipe.reward.getItems().stream().map(e -> new ItemStack(e, recipe.reward.count)).toList());
        recipe.outputFocused = !focuses.isEmpty() && focuses.getAllFocuses().get(0).getRole() == RecipeIngredientRole.OUTPUT;
        ci.cancel();
    }
}
