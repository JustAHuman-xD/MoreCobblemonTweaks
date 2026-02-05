package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.gui.pc.IconButton;
import me.justahuman.more_cobblemon_tweaks.api.ConditionalIconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = IconButton.class)
public class IconButtonMixin extends Button implements ConditionalIconButton {
    @Unique private Supplier<Boolean> moreCobblemonTweaks$condition = () -> true;

    private IconButtonMixin(int i, int j, int k, int l, Component component, OnPress onPress, CreateNarration createNarration) {
        super(i, j, k, l, component, onPress, createNarration);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    public void hideIfNeeded(GuiGraphics context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!moreCobblemonTweaks$condition.get()) {
            ci.cancel();
        }
    }

    @Override
    public void onPress() {
        if (moreCobblemonTweaks$condition.get()) {
            super.onPress();
        }
    }

    @Override
    public void setCondition(Supplier<Boolean> condition) {
        this.moreCobblemonTweaks$condition = condition;
    }
}
