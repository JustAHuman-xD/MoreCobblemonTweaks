package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.WallpapersScrollingWidget;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.net.messages.server.storage.pc.RequestChangePCBoxWallpaperPacket;
import com.llamalad7.mixinextras.sugar.Local;
import me.justahuman.more_cobblemon_tweaks.api.BoxViewHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallpapersScrollingWidget.WallpaperEntry.class)
public class WallpaperEntryMixin {
    @Shadow @Final private ResourceLocation wallpaper;
    @Shadow private ResourceLocation altWallpaper;

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    public void setAllVisibleBoxWallpapers(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir, @Local(name = "appliedWallpaper") ResourceLocation appliedWallpaper) {
        if (!(Minecraft.getInstance().screen instanceof PCGUI pcgui)
                || pcgui.getStorage() == null
                || !((BoxViewHolder) (Object) pcgui.getStorage()).moreCobblemonTweaks$isBoxListOpen()) {
            return;
        }

        ClientPC pc = pcgui.getPc();
        int boxIndex = pcgui.getStorage().getBox();
        int startBox = boxIndex * 30;
        int endBox = Math.min(startBox + 29, pc.getBoxes().size() - 1);
        for (int box = startBox; box <= endBox; box++) {
            if (box == boxIndex) continue; // Already set by normal behavior
            new RequestChangePCBoxWallpaperPacket(pc.getUuid(), box, this.wallpaper, Screen.hasShiftDown() ? this.altWallpaper : null).sendToServer();
            pc.getBoxes().get(box).setWallpaper(appliedWallpaper);
        }
    }
}
