package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.client.gui.pasture.PasturePCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pc.BoxNameWidget;
import com.cobblemon.mod.common.client.gui.pc.FilterWidget;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.PCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.more_cobblemon_tweaks.api.FilterSuggestable;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelectorState;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.pc.IvWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.MultiSelectButton;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
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

        Set<Renderable> siblings = new HashSet<>(this.children().stream().filter(Renderable.class::isInstance).map(Renderable.class::cast).toList());
        if (!(configuration instanceof PasturePCGUIConfiguration) && ModConfig.isEnabled("pc_multi_select")) {
            siblings.add(this.addRenderableWidget(moreCobblemonTweaks$multiSelectButton = new MultiSelectButton(x + 271, y + 179, siblings)));
        }
    }

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    public void suggestionAndSummary(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
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
