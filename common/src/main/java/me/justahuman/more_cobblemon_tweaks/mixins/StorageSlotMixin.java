package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.pc.BoxStorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StorageSlot.class, remap = false)
public abstract class StorageSlotMixin {
    @Shadow @Final private StorageWidget parent;
    @Shadow public abstract Pokemon getPokemon();

    @Inject(at = @At("HEAD"), method = "renderSlot", cancellable = true)
    public void renderSlotHead(GuiGraphics context, int posX, int posY, float partialTicks, CallbackInfo ci) {
        if (moreCobblemonTweaks$failSearch()) {
            if (ModConfig.isEnabled("pc_search_hide")) {
                ci.cancel();
            } else {
                RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 0.65f);
            }
        }

        MultiSelector selector = (MultiSelector) (Object) this.parent;
        if (((Object) this) instanceof BoxStorageSlot slot && selector.moreCobblemonTweaks$getSelectedPositions().contains(slot.getPosition())) {
            GuiUtilsKt.blitk(
                    context.pose(),
                    Textures.SELECTED_SLOT_OVERLAY,
                    posX,
                    posY,
                    StorageSlot.SIZE,
                    StorageSlot.SIZE
            );
        }
    }

    @Inject(at = @At("HEAD"), method = "isHovered", cancellable = true)
    public void isHovered(int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (!this.parent.visible || moreCobblemonTweaks$failSearch()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean moreCobblemonTweaks$failSearch() {
        if (Utils.search == null || !ModConfig.isEnabled("pc_search")) {
            return false;
        }
        Pokemon pokemon = this.getPokemon();
        return pokemon != null && !Utils.search.passes(pokemon);
    }
}
