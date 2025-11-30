package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.ExitButton;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import me.justahuman.more_cobblemon_tweaks.mixins.accessor.ButtonAccessor;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Summary.class, priority = 2000)
public abstract class SummaryMixin extends Screen {

    protected SummaryMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void ensureBaseExit(CallbackInfo ci) {
        for (GuiEventListener child : this.children()) {
            if (child instanceof ExitButton exitButton) {
                Button.OnPress original = ((ButtonAccessor) (Object) exitButton).getOnPress();
                ((ButtonAccessor) (Object) exitButton).setOnPress(button -> {
                    CallbackInfo callbackInfo = new CallbackInfo("onPress", true);
                    moreCobblemonTweaks$handleFromPC(callbackInfo);
                    if (!callbackInfo.isCancelled()) {
                        original.onPress(button);
                    }
                });
            }
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    public void onClose(CallbackInfo ci) {
        moreCobblemonTweaks$handleFromPC(ci);
    }

    @Unique
    private static void moreCobblemonTweaks$handleFromPC(CallbackInfo ci) {
        if (Utils.summaryFromPC) {
            PCGUI pcGui = new PCGUI(Utils.summaryPC, CobblemonClient.INSTANCE.getStorage().getParty(), Utils.summaryConfig, CobblemonClient.INSTANCE.getLastPcBoxViewed(), Utils.unseenWallpapers);
            Utils.summaryPC = null;
            Utils.summaryConfig = null;
            Utils.unseenWallpapers = null;
            Utils.summaryFromPC = false;
            Minecraft.getInstance().setScreen(pcGui);
            ci.cancel();
        }
    }
}
