package me.justahuman.dystoriantweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.client.storage.ClientParty;
import me.justahuman.dystoriantweaks.Utils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageWidget.class)
public abstract class StorageWidgetMixin {
    @Shadow(remap = false) private int box;

    @Shadow(remap = false) public abstract void setBox(int value);

    @Inject(at = @At("TAIL"), method = "<init>", remap = false)
    public void init(int pX, int pY, PCGUI pcGui, ClientPC pc, ClientParty party, CallbackInfo ci) {
        if (pc.getBoxes().size() > Utils.currentBox && ModConfig.isEnabled("open_box_history")) {
            setBox(Utils.currentBox);
        }
    }

    @Inject(at = @At("HEAD"), method = "renderButton")
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Utils.currentBox = this.box;
    }
}
