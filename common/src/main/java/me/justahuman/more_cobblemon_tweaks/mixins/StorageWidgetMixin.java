package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.CobblemonNetwork;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.client.gui.pc.BoxStorageSlot;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import com.cobblemon.mod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelectorState;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = StorageWidget.class, remap = false)
public abstract class StorageWidgetMixin extends SoundlessWidget implements MultiSelector {
    @Final @Shadow private PCGUI pcGui;
    @Shadow private int box;

    @Unique private PCPosition moreCobblemonTweaks$lastPosition = null;
    @Unique private final List<PCPosition> moreCobblemonTweaks$selectedPositions = new ArrayList<>();

    private StorageWidgetMixin(int pX, int pY, int pWidth, int pHeight, @NotNull Component component) {
        super(pX, pY, pWidth, pHeight, component);
    }

    @Inject(method = "_init_$lambda$1", at = @At(value = "HEAD"), cancellable = true)
    private static void onConfirmRelease(StorageWidget widget, Button it, CallbackInfo ci) {
        MultiSelector selector = (MultiSelector) (Object) widget;
        if (selector == null || !selector.moreCobblemonTweaks$isMultiSelecting()) {
            return;
        }

        ci.cancel();
        for (PCPosition position : selector.moreCobblemonTweaks$getSelectedPositions()) {
            Pokemon pokemon = widget.getPcGui().getPc().get(position);
            if (pokemon != null) {
                CobblemonNetwork.INSTANCE.sendToServer(new ReleasePCPokemonPacket(pokemon.getUuid(), position));
            }
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(CobblemonSounds.PC_RELEASE, 1.0F));
        widget.setDisplayConfirmRelease(false);
        selector.moreCobblemonTweaks$clearSelection();
    }

    @Inject(at = @At("TAIL"), method = "setBox")
    public void setBox(int value, CallbackInfo ci) {
        Utils.currentBox = box;
        moreCobblemonTweaks$clearSelection();
    }

    @Inject(at = @At("HEAD"), method = "onStorageSlotClicked", cancellable = true)
    public void onSlotClicked(Button button, CallbackInfo ci) {
        if (!button.isHovered()) {
            ci.cancel();
            return;
        } else if (!moreCobblemonTweaks$isMultiSelecting()) {
            return;
        }

        ci.cancel();
        if (!(button instanceof BoxStorageSlot slot)) {
            return;
        }

        PCPosition lastPosition = moreCobblemonTweaks$lastPosition;
        PCPosition clickedPosition = slot.getPosition();
        boolean enabling = !moreCobblemonTweaks$selectedPositions.contains(clickedPosition);

        if (lastPosition != null && Screen.hasShiftDown()) {
            int from = Math.min(lastPosition.getSlot(), clickedPosition.getSlot());
            int to = Math.max(lastPosition.getSlot(), clickedPosition.getSlot());
            for (int i = from; i <= to; i++) {
                PCPosition position = new PCPosition(clickedPosition.getBox(), i);
                moreCobblemonTweaks$selectedPositions.remove(position);
                if (enabling) {
                    moreCobblemonTweaks$selectedPositions.add(position);
                }
            }
        } else {
            moreCobblemonTweaks$toggleSelected(clickedPosition);
            moreCobblemonTweaks$lastPosition = clickedPosition;
        }
    }

    @Unique
    private void moreCobblemonTweaks$toggleSelected(PCPosition position) {
        if (!moreCobblemonTweaks$selectedPositions.remove(position)) {
            moreCobblemonTweaks$selectedPositions.add(position);
        }
    }

    @Inject(at = @At("HEAD"), method = "canDeleteSelected", cancellable = true)
    public void canDelete(CallbackInfoReturnable<Boolean> cir) {
        if (moreCobblemonTweaks$isMultiSelecting()) {
            cir.setReturnValue(!moreCobblemonTweaks$selectedPositions.isEmpty());
        }
    }

    @ModifyArg(method = "renderWidget", index = 1, at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/gui/GuiUtilsKt;blitk$default(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;ZFILjava/lang/Object;)V", ordinal = 1), remap = true)
    public ResourceLocation overrideOverlayTexture(ResourceLocation texture) {
        if (ModConfig.isEnabled("custom_pc_wallpapers")) {
            ResourceLocation newTexture = ModConfig.getBoxTexture(Utils.currentBox);
            if (newTexture != null) {
                return newTexture;
            }
        }
        return texture;
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true, remap = true)
    public void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (!this.visible) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean moreCobblemonTweaks$isMultiSelecting() {
        return ((MultiSelectorState) (Object) this.pcGui).moreCobblemonTweaks$isMultiSelecting();
    }

    @Override
    public List<PCPosition> moreCobblemonTweaks$getSelectedPositions() {
        return moreCobblemonTweaks$selectedPositions;
    }

    @Override
    public void moreCobblemonTweaks$clearSelection() {
        moreCobblemonTweaks$selectedPositions.clear();
        resetSelected();
    }

    @Shadow protected abstract void resetSelected();
}
