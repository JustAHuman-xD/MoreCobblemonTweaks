package me.justahuman.more_cobblemon_tweaks.features.pc.box_name;

import me.justahuman.more_cobblemon_tweaks.utils.CustomTextField;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static net.minecraft.ChatFormatting.GRAY;

public class RenameWidget extends CustomTextField {
    int boxPlaceholder = Utils.currentBox;

    public RenameWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setBordered(false);
        setHint(Component.translatable("cobblemon.ui.pc.box.title", Utils.currentBox + 1).withStyle(GRAY));
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (boxPlaceholder != Utils.currentBox) {
            setHint(Component.translatable("cobblemon.ui.pc.box.title", Utils.currentBox + 1).withStyle(GRAY));
            boxPlaceholder = Utils.currentBox;
        }
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}