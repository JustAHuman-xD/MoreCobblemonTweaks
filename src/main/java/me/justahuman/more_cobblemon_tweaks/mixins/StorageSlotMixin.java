package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StorageSlot.class)
public abstract class StorageSlotMixin {
    @Shadow(remap = false) @Final private StorageWidget parent;
    @Shadow(remap = false) public abstract Pokemon getPokemon();

    @Inject(at = @At("HEAD"), method = "render")
    public void renderSlotHead(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Pokemon pokemon = this.getPokemon();
        if (pokemon == null) {
            return;
        }

        if (Utils.search != null && ModConfig.isEnabled("pc_search") && !Utils.search.passes(pokemon)) {
            RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 0.65f);
        }
    }

    @Inject(at = @At("HEAD"), method = "isHovered", remap = false, cancellable = true)
    public void isHovered(int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (!this.parent.visible) {
            cir.setReturnValue(false);
        }
    }
}
