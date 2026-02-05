package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.gui.pc.BoxNameWidget;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import me.justahuman.more_cobblemon_tweaks.api.BoxViewHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoxNameWidget.class)
public class BoxNameWidgetMixin {
    @Shadow(remap = false) @Final private PCGUI pcGui;
    @Shadow(remap = false) @Final private StorageWidget storageWidget;

    @ModifyVariable(method = "renderWidget", at = @At("STORE"), name = "label")
    public MutableComponent replaceLabelIfBoxList(MutableComponent value) {
        if (((BoxViewHolder) (Object) storageWidget).moreCobblemonTweaks$isBoxListOpen()) {
            int boxIndex = storageWidget.getBox();
            int start = boxIndex * 30;
            int end = Math.min(start + 29, pcGui.getPc().getBoxes().size() - 1);
            return Component.translatable("more_cobblemon_tweaks.pc_enhancements.box_list.title", start + 1, end + 1);
        }
        return value;
    }

    @Inject(method = "setFocused", at = @At("HEAD"), cancellable = true, remap = false)
    public void preventFocusIfBoxList(boolean focused, CallbackInfo ci) {
        if (((BoxViewHolder) (Object) storageWidget).moreCobblemonTweaks$isBoxListOpen()) {
            ci.cancel();
        }
    }
}
