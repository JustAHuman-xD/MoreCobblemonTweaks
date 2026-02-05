package me.justahuman.more_cobblemon_tweaks.features.pc.boxes;

import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.pasture.PasturePCGUIConfiguration;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.mixins.accessor.PcGuiAccessor;
import me.justahuman.more_cobblemon_tweaks.utils.CustomButton;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class BoxListButton extends CustomButton {
    private final StorageWidget parent;

    private boolean toggled = false;

    private int originalBoxIndex = 0;
    private int originalStatIndex = 0;

    public BoxListButton(StorageWidget parent, int x, int y) {
        super(x, y, Textures.BOX_LIST_BUTTON_WIDTH, Textures.BOX_LIST_BUTTON_HEIGHT, Textures.BOX_LIST_BUTTON_TEXTURE, Set.of());
        this.parent = parent;
    }

    private boolean canToggle() {
        if (!ModConfig.isEnabled("pc_box_view") || parent.getPcGui().getConfiguration() instanceof PasturePCGUIConfiguration) return false;
        PcGuiAccessor pcGui = (PcGuiAccessor) (Object) parent.getPcGui();
        return !pcGui.getWallpaperWidget().visible && !parent.canDeleteSelected();
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (!canToggle()) {
            return;
        }

        context.blit(texture,
                getX(), getY(),
                width, height,
                0, isHovered() ? height : 0,
                width, height,
                width, height * 2
        );

        RenderHelperKt.drawScaledText(
                context,
                CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                Component.translatable("more_cobblemon_tweaks.pc_enhancements.box_list.button." + (toggled ? "enabled" : "disabled")).withStyle(ChatFormatting.BOLD),
                getX() + (width / 2),
                getY() + 3.5,
                1F,
                1F,
                Integer.MAX_VALUE,
                0xFFFFFFFF,
                true,
                true,
                null,
                null
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        PcGuiAccessor pcGui = (PcGuiAccessor) (Object) parent.getPcGui();
        if (!toggled) {
            originalStatIndex = pcGui.getCurrentStatIndex();
            originalBoxIndex = parent.getBox();
            pcGui.setCurrentStatIndex(4); // Invalid index so it doesn't show any stats
            parent.setBox(0); // Move to first box, first "box" of boxes
            toggled = true;
        } else {
            toggled = false;
            pcGui.setCurrentStatIndex(originalStatIndex);
            parent.setBox(originalBoxIndex);
        }
        parent.resetSelected();
    }

    public boolean isToggled() {
        return toggled;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return canToggle() && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    protected boolean isValidClickButton(int i) {
        return i == 0 || i == 1;
    }
}
