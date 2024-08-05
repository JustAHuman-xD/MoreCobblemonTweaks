package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import me.justahuman.dystoriantweaks.features.PcEnhancements;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PCPosition.class)
public abstract class PcPositionMixin {
    @Mutable
    @Shadow(remap = false) @Final private int box;

    @Inject(at = @At("TAIL"), method = "<init>", remap = false)
    public void init(int box, int slot, CallbackInfo ci) {
        this.box = PcEnhancements.getBoxIndex(box);
    }
}
