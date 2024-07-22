package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.dystoriantweaks.Utils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageSlot.class)
public abstract class StorageSlotMixin {
    @Shadow(remap = false) public abstract Pokemon getPokemon();

    @Inject(at = @At("HEAD"), method = "render")
    public void renderSlotHead(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Pokemon pokemon = this.getPokemon();
        if (pokemon == null || Utils.search == null || !ModConfig.isEnabled("pc_search")) {
            return;
        }

        if (!Utils.search.passes(pokemon)) {
            RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 0.65f);
        }
    }
}
