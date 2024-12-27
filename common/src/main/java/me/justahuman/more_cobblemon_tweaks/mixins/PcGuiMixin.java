package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.pc.IvWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.CancelButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.ConfirmButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.RenameButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.box_name.RenameWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.search.SearchWidget;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper.WallpaperWidget;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(PCGUI.class)
public abstract class PcGuiMixin extends Screen {
    @Shadow(remap = false) @Final public static int BASE_WIDTH;
    @Shadow(remap = false) @Final public static int BASE_HEIGHT;

    @Unique private RenameWidget moreCobblemonTweaks$renameWidget;
    @Unique private SearchWidget moreCobblemonTweaks$searchWidget;

    protected PcGuiMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void onInit(CallbackInfo ci) {
        Utils.search = null;

        int x = (width - BASE_WIDTH) / 2;
        int y = (height - BASE_HEIGHT) / 2;

        if (ModConfig.isEnabled("pc_iv_display")) {
            this.addRenderableOnly(new IvWidget(cast()));
        }

        Set<Renderable> siblings = new HashSet<>(this.children().stream().filter(Renderable.class::isInstance).map(Renderable.class::cast).toList());
        boolean wallpapers = ModConfig.isEnabled("custom_pc_wallpapers");

        if (wallpapers) {
            WallpaperButton button = this.addRenderableWidget(new WallpaperButton(x + 243, y - 13, siblings));
            siblings.add(this.addRenderableWidget(new WallpaperWidget(button, x + 85, y + 27)));
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

    @Unique
    public PCGUI cast() {
        return (PCGUI) (Object) this;
    }
}
