package me.justahuman.more_cobblemon_tweaks.mixins;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor(value = "value") void setDirectValue(String value);
}
