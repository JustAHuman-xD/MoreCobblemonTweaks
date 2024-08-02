package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PCPosition.class)
public interface PcPositionAccessor {
    @Accessor
    void setBox(int box);
}
