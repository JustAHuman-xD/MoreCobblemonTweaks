package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.client.storage.ClientParty;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StorageWidget.class)
public abstract class StorageWidgetMixin extends ClickableWidget {
    private StorageWidgetMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

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

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    public void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (!this.visible) {
            cir.setReturnValue(false);
        }
    }
}
