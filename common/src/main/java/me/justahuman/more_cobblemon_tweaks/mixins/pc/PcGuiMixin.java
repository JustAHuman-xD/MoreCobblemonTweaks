package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.gui.pasture.PasturePCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pc.BoxNameWidget;
import com.cobblemon.mod.common.client.gui.pc.FilterWidget;
import com.cobblemon.mod.common.client.gui.pc.IconButton;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.PCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds;
import com.cobblemon.mod.common.client.render.gui.PCBoxWallpaperRepository;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.justahuman.more_cobblemon_tweaks.api.BoxViewHolder;
import me.justahuman.more_cobblemon_tweaks.api.ConditionalIconButton;
import me.justahuman.more_cobblemon_tweaks.api.FilterSuggestable;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelectorState;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.pc.IvWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.boxes.BoxListSlot;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.MultiSelectButton;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mixin(value = PCGUI.class, priority = 2000)
public abstract class PcGuiMixin extends Screen implements MultiSelectorState {
    @Shadow(remap = false) @Final public static int BASE_WIDTH;
    @Shadow(remap = false) @Final public static int BASE_HEIGHT;

    @Shadow(remap = false) @Final private ClientPC pc;
    @Shadow(remap = false) @Final private PCGUIConfiguration configuration;
    @Shadow(remap = false) @Final private Set<ResourceLocation> unseenWallpapers;
    @Shadow(remap = false) private StorageWidget storageWidget;
    @Shadow(remap = false) private Pokemon previewPokemon;

    @Shadow(remap = false) private FilterWidget filterWidget;
    @Shadow(remap = false) private BoxNameWidget boxNameWidget;

    @Shadow
    @Final
    private static ResourceLocation portraitBackgroundResource;
    @Unique private MultiSelectButton moreCobblemonTweaks$multiSelectButton;

    protected PcGuiMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void onInit(CallbackInfo ci) {
        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        if (ModConfig.isEnabled("pc_iv_display")) {
            this.addRenderableOnly(new IvWidget(cast()));
        }

        Set<Renderable> siblings = new HashSet<>();
        if (!(configuration instanceof PasturePCGUIConfiguration)) {
            if (ModConfig.isEnabled("pc_multi_select")) {
                this.addRenderableWidget(moreCobblemonTweaks$multiSelectButton = new MultiSelectButton(x + 271, y + 179, siblings));
            }
        }
        siblings.addAll(this.children().stream().filter(Renderable.class::isInstance).map(Renderable.class::cast).toList());

        filterWidget.setMaxLength(100);

        for (Renderable renderable : siblings) {
            if (!(renderable instanceof IconButton iconButton)) {
                continue;
            }

            ConditionalIconButton conditional = (ConditionalIconButton) (Object) iconButton;
            String type = iconButton.getMessage().getString();
            BoxViewHolder boxViewHolder = (BoxViewHolder) (Object) storageWidget;
            if (type.startsWith("sort_")) {
                conditional.setCondition(() -> !boxViewHolder.moreCobblemonTweaks$isBoxListOpen());
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "HEAD"), cancellable = true)
    public void preventClickingWhenMultiMoving(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!Utils.moveAllCompleted) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseDragged", at = @At(value = "HEAD"), cancellable = true)
    public void preventDraggingWhenMultiMoving(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (!Utils.moveAllCompleted) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void preventScrollingWhenMultiMoving(double mouseX, double mouseY, double amount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (!Utils.moveAllCompleted) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    public void suggestionAndSummaryAndMultiMovingPrevention(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!Utils.moveAllCompleted) {
            cir.setReturnValue(true);
            return;
        }

        boolean renameSelected = boxNameWidget != null && boxNameWidget.isFocused();
        boolean searchSelected = filterWidget != null && filterWidget.isFocused();
        if (searchSelected && keyCode == GLFW.GLFW_KEY_TAB) {
            ((FilterSuggestable) (Object) filterWidget).moreCobblemonTweaks$fillSuggestion();
            cir.setReturnValue(true);
        } else if (!renameSelected && !searchSelected && CobblemonKeyBinds.INSTANCE.getSUMMARY().matches(keyCode, scanCode)) {
            Utils.summaryPC = this.pc;
            Utils.summaryConfig = this.configuration;
            Utils.unseenWallpapers = this.unseenWallpapers;
            Utils.summaryFromPC = true;
            List<Pokemon> summaryPokemon = new ArrayList<>();
            if (moreCobblemonTweaks$isMultiSelecting()) {
                List<Pokemon> selected = ((MultiSelector) (Object) storageWidget).moreCobblemonTweaks$getSelectedPokemon();
                for (int i = 0; i < Math.min(6, selected.size()); i++) {
                    summaryPokemon.add(selected.get(i));
                }
            } else {
                summaryPokemon.add(previewPokemon);
            }
            summaryPokemon.removeIf(Objects::isNull);

            if (!summaryPokemon.isEmpty()) {
                Summary.Companion.open(summaryPokemon, false, 0);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique private int moreCobblemonTweaks$mouseX;;
    @Unique private int moreCobblemonTweaks$mouseY;

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void captureMousePos(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        moreCobblemonTweaks$mouseX = mouseX;
        moreCobblemonTweaks$mouseY = mouseY;
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/gui/GuiUtilsKt;blitk$default(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;ZFILjava/lang/Object;)V"))
    public void renderPreviewedBoxWallpaper(PoseStack poseStack, ResourceLocation resource, Number x, Number y, Number height, Number width, Number uOffset, Number vOffset, Number textureWidth, Number textureHeight, Number blitOffset, Number red, Number green, Number blue, Number alpha, boolean blend, float scale, int i, Object o, Operation<Void> original) {
        if (!resource.equals(portraitBackgroundResource)) {
            original.call(poseStack, resource, x, y, height, width, uOffset, vOffset, textureWidth, textureHeight, blitOffset, red, green, blue, alpha, blend, scale, i, o);
            return;
        }

        BoxViewHolder boxViewHolder = (BoxViewHolder) (Object) storageWidget;
        if (!boxViewHolder.moreCobblemonTweaks$isBoxListOpen()) {
            original.call(poseStack, resource, x, y, height, width, uOffset, vOffset, textureWidth, textureHeight, blitOffset, red, green, blue, alpha, blend, scale, i, o);
            return;
        }

        BoxListSlot previewed = boxViewHolder.moreCobblemonTweaks$getPreviewedBoxListSlot(moreCobblemonTweaks$mouseX, moreCobblemonTweaks$mouseY);
        if (previewed != null && previewed.getBox() != null) {
            ResourceLocation wallpaper = Utils.getUsedWallpaper(previewed.getBox());
            if (wallpaper != null) {
                original.call(poseStack, wallpaper, x, y, height, width, uOffset, vOffset, textureWidth, textureHeight, blitOffset, red, green, blue, alpha, blend, scale, i, o);
                original.call(poseStack, Textures.PREVIEW_BOX_GRID, x, y, height, width, uOffset, vOffset, textureWidth, textureHeight, blitOffset, red, green, blue, alpha, blend, scale, i, o);
                return;
            }
        }
        original.call(poseStack, PCBoxWallpaperRepository.INSTANCE.getDefaultWallpaper(), x, y, height, width, uOffset, vOffset, textureWidth, textureHeight, blitOffset, red, green, blue, alpha, blend, scale, i, o);
        original.call(poseStack, Textures.PREVIEW_BOX_GRID, x, y, height, width, uOffset, vOffset, textureWidth, textureHeight, blitOffset, red, green, blue, alpha, blend, scale, i, o);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderMultiMoveInProgress(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Utils.moveAllCompleted) {
            return;
        }

        Component movingTitle = Component.translatable("more_cobblemon_tweaks.pc_enhancements.multi_move.title", Utils.moveAllCount, Utils.moveAllTotal);
        Component timeoutText = Component.translatable("more_cobblemon_tweaks.pc_enhancements.multi_move." + (Utils.moveAllTimeouts == 5 ? "last_attempt" : "timeout"), Utils.moveAllTimeouts);
        String progressText = switch ((int) (Util.getMillis() / 300L % 4L)) {
            case 1, 3 -> "o O o";
            case 2 -> "o o O";
            default -> "O o o";
        };
        int width = Math.max(font.width(progressText), font.width(movingTitle));
        int height = 20;
        if (Utils.moveAllTimeouts > 0) {
            width = Math.max(width, font.width(timeoutText));
            height = 30;
        }

        int x = this.width / 2 - width / 2;
        int k = x - 12;
        int l = this.height / 2 - height / 2 - 12;
        int m = width + 12 * 2;
        int n = height + 12 * 2;
        int o = this.isFocused() ? -1 : -6250336;
        PoseStack matrices = context.pose();
        matrices.pushPose();
        matrices.translate(0.0D, 0.0D, 400.0D);
        context.fill(k + 1, l, k + m, l + n, -16777216);
        context.renderOutline(k, l, m, n, o);
        context.drawCenteredString(this.font, movingTitle, this.width / 2, l + 12, 0xffffff);
        context.drawCenteredString(this.font, progressText, this.width / 2, l + 12 + 11, 0x808080);
        if (Utils.moveAllTimeouts > 0) {
            context.drawCenteredString(this.font, timeoutText, this.width / 2, l + 12 + 22, 0x808080);
        }
        matrices.popPose();
    }

    @Override
    public boolean moreCobblemonTweaks$isMultiSelecting() {
        return moreCobblemonTweaks$multiSelectButton != null && moreCobblemonTweaks$multiSelectButton.isToggled();
    }

    @Override
    public void onClose() {
        super.onClose();
        Utils.moveAllPokemonFuture = null;
    }

    @Unique
    public PCGUI cast() {
        return (PCGUI) (Object) this;
    }
}
