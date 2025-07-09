package me.justahuman.more_cobblemon_tweaks.mixins;

import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Button.class)
public interface ButtonAccessor {
    @Accessor @Final Button.OnPress getOnPress();
    @Accessor @Final void setOnPress(Button.OnPress onPress);
}
