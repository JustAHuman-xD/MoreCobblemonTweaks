package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import me.justahuman.dystoriantweaks.features.PcEnhancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PCPosition.class)
public abstract class PcPositionMixin {
    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(int box, int slot, CallbackInfo ci) {
        ((PcPositionAccessor) this).setBox(PcEnhancements.getBoxIndex(box));
    }
}
