package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.gui.pc.IconButton;
import me.justahuman.more_cobblemon_tweaks.api.ConditionalIconButton;
import net.minecraft.client.Minecraft;
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
    @Unique private Supplier<Component> moreCobblemonTweaks$tooltip = null;

    private IconButtonMixin(int i, int j, int k, int l, Component component, OnPress onPress, CreateNarration createNarration) {
        super(i, j, k, l, component, onPress, createNarration);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    public void hideIfNeeded(GuiGraphics context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!moreCobblemonTweaks$condition.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    public void renderCustomTooltip(GuiGraphics context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (moreCobblemonTweaks$tooltip != null && isHovered()) {
            context.renderTooltip(Minecraft.getInstance().font, moreCobblemonTweaks$tooltip.get(), mouseX, mouseY);
        }
    }

    @Override
    public void onPress() {
        if (moreCobblemonTweaks$condition.get()) {
            super.onPress();
        }
    }

    @Override
    public void moreCobblemonTweaks$setCondition(Supplier<Boolean> condition) {
        this.moreCobblemonTweaks$condition = condition;
    }

    @Override
    public void moreCobblemonTweaks$setTooltip(Supplier<Component> tooltip) {
        this.moreCobblemonTweaks$tooltip = tooltip;
    }
}
