package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.client.gui.pasture.PasturePCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.PCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelectorState;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.pc.IvWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.CancelButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.ConfirmButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.RenameButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.RenameWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.MultiSelectButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.Search;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperWidget;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
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
    @Shadow(remap = false) private StorageWidget storageWidget;
    @Shadow(remap = false) private Pokemon previewPokemon;

    @Unique private MultiSelectButton moreCobblemonTweaks$multiSelectButton;

    @Unique private RenameWidget moreCobblemonTweaks$renameWidget;
    @Unique private WallpaperWidget moreCobblemonTweaks$wallpaperWidget;
    @Unique private SearchWidget moreCobblemonTweaks$searchWidget;

    protected PcGuiMixin(Component title) {
        super(title);
    }

    @ModifyVariable(at = @At("HEAD"), index = 4, name = "openOnBox", method = "<init>(Lcom/cobblemon/mod/common/client/storage/ClientPC;Lcom/cobblemon/mod/common/client/storage/ClientParty;Lcom/cobblemon/mod/common/client/gui/pc/PCGUIConfiguration;I)V", argsOnly = true, remap = false)
    private static int fixOpenOnBox(int value) {
        return value <= 1 ? Utils.currentBox : value;
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void onInit(CallbackInfo ci) {
        Search.instance = null;

        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        if (ModConfig.isEnabled("pc_iv_display")) {
            this.addRenderableOnly(new IvWidget(cast()));
        }

        Set<Renderable> siblings = new HashSet<>(this.children().stream().filter(Renderable.class::isInstance).map(Renderable.class::cast).toList());
        if (!(configuration instanceof PasturePCGUIConfiguration) && ModConfig.isEnabled("pc_multi_select")) {
            siblings.add(this.addRenderableWidget(moreCobblemonTweaks$multiSelectButton = new MultiSelectButton(x + 271, y + 179, siblings)));
        }

        boolean wallpapers = ModConfig.isEnabled("custom_pc_wallpapers");
        if (wallpapers) {
            WallpaperButton button = this.addRenderableWidget(new WallpaperButton(x + 243, y - 13, siblings));
            siblings.add(this.addRenderableWidget(moreCobblemonTweaks$wallpaperWidget = new WallpaperWidget(pc, button, x + 85, y + 27)));
            siblings.add(button);
        }

        if (ModConfig.isEnabled("custom_pc_box_names")) {
            siblings.add(this.addRenderableWidget(new CancelButton(x + 243, y - 13, siblings)));
            siblings.add(this.addRenderableWidget(new ConfirmButton(x + 222, y - 13, siblings)));
            siblings.add(this.addRenderableWidget(moreCobblemonTweaks$renameWidget = new RenameWidget(x + 106, y - 13)));
            siblings.add(this.addRenderableWidget(new RenameButton(x + (wallpapers ? 220 : 241), y - 13, siblings)));
        }

        if (ModConfig.isEnabled("pc_search")) {
            siblings.add(this.addRenderableWidget(new SearchButton(x + 82, y - 13, siblings)));
            siblings.add(this.addRenderableWidget(moreCobblemonTweaks$searchWidget = new SearchWidget(x + 104, y - 13)));
        }
    }

    @ModifyArg(method = "render", index = 2, at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/client/render/RenderHelperKt;drawScaledText$default(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/chat/MutableComponent;Ljava/lang/Number;Ljava/lang/Number;FLjava/lang/Number;IIZZLjava/lang/Integer;Ljava/lang/Integer;ILjava/lang/Object;)V", ordinal = 12))
    public MutableComponent overrideBoxTitle(MutableComponent text) {
        if (ModConfig.isEnabled("custom_pc_box_names")) {
            Component newTitle = ModConfig.getBoxName(Utils.currentBox);
            if (newTitle instanceof MutableComponent mutable && newTitle != CommonComponents.EMPTY) {
                return mutable;
            }
        }
        return text;
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/util/MiscUtilsKt;isInventoryKeyPressed(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;II)Z"))
    public boolean preventClosing(Screen $this$isInventoryKeyPressed, Minecraft client, int keyCode, int scanCode) {
        boolean renameSelected = moreCobblemonTweaks$renameWidget != null && moreCobblemonTweaks$renameWidget.isFocused();
        boolean searchSelected = moreCobblemonTweaks$searchWidget != null && moreCobblemonTweaks$searchWidget.isFocused();
        return MiscUtilsKt.isInventoryKeyPressed($this$isInventoryKeyPressed, client, keyCode, scanCode) && !renameSelected && !searchSelected;
    }

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    public void suggestionAndSummary(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        boolean renameSelected = moreCobblemonTweaks$renameWidget != null && moreCobblemonTweaks$renameWidget.isFocused();
        boolean searchSelected = moreCobblemonTweaks$searchWidget != null && moreCobblemonTweaks$searchWidget.isFocused();
        if (searchSelected && keyCode == GLFW.GLFW_KEY_TAB) {
            moreCobblemonTweaks$searchWidget.fillSuggestion();
            cir.setReturnValue(true);
        } else if (!renameSelected && !searchSelected && CobblemonKeyBinds.INSTANCE.getSUMMARY().matches(keyCode, scanCode)) {
            Utils.currentBox = this.storageWidget.getBox();
            Utils.summaryPC = this.pc;
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
            Summary.Companion.open(summaryPokemon, false, 0);
            cir.setReturnValue(true);
        }
    }

    /**
     * @author JustAHuman
     * @reason This method will be redone in Cobblemon 1.7 but right now box scrolling is broken Cobblemon UI Tweaks, this will be removed when Cobblemon 1.7 is released.
     */
    @Overwrite
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (moreCobblemonTweaks$wallpaperWidget != null && moreCobblemonTweaks$wallpaperWidget.visible) {
            return moreCobblemonTweaks$wallpaperWidget.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
        } else if (storageWidget.getPastureWidget() != null && storageWidget.getPastureWidget().getPastureScrollList().isHovered(mouseX, mouseY)) {
            return storageWidget.getPastureWidget().getPastureScrollList().mouseScrolled(mouseX, mouseY, deltaX, deltaY);
        } else if (storageWidget.isHovered() && mouseX < (storageWidget.getX() + Textures.STORAGE_WIDGET_SCREEN_WIDTH)) {
            this.storageWidget.setBox(this.storageWidget.getBox() - (int) deltaY);
            return true;
        } else {
            return this.getChildAt(mouseX, mouseY).filter(guiEventListener -> guiEventListener.mouseScrolled(mouseX, mouseY, deltaX, deltaY)).isPresent();
        }
    }

    @Override
    public boolean moreCobblemonTweaks$isMultiSelecting() {
        return moreCobblemonTweaks$multiSelectButton != null && moreCobblemonTweaks$multiSelectButton.isToggled();
    }

    @Unique
    public PCGUI cast() {
        return (PCGUI) (Object) this;
    }
}
