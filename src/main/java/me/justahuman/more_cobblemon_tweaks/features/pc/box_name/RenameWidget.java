package me.justahuman.more_cobblemon_tweaks.features.pc.box_name;

import me.justahuman.more_cobblemon_tweaks.utils.CustomTextField;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import static net.minecraft.util.Formatting.GRAY;

public class RenameWidget extends CustomTextField {
    int boxPlaceholder = Utils.currentBox;

    public RenameWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setDrawsBackground(false);
        setPlaceholder(Text.translatable("cobblemon.ui.pc.box.title", Utils.currentBox + 1).formatted(GRAY));
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (boxPlaceholder != Utils.currentBox) {
            setPlaceholder(Text.translatable("cobblemon.ui.pc.box.title", Utils.currentBox + 1).formatted(GRAY));
            boxPlaceholder = Utils.currentBox;
        }
        super.renderButton(context, mouseX, mouseY, delta);
    }
}