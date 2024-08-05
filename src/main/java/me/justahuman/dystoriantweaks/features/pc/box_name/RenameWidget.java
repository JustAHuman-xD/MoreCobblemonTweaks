package me.justahuman.dystoriantweaks.features.pc.box_name;

import me.justahuman.dystoriantweaks.utils.CustomTextField;
import me.justahuman.dystoriantweaks.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import static net.minecraft.util.Formatting.GRAY;

public class RenameWidget extends CustomTextField {
    int boxPlaceholder = Utils.currentBox;

    public RenameWidget(int x, int y) {
        super(x, y);
        setVisible(false);
        setDrawsBackground(false);
        setPlaceholder(Text.literal("Box " + (Utils.currentBox + 1)).formatted(GRAY));
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (boxPlaceholder != Utils.currentBox) {
            setPlaceholder(Text.literal("Box " + (Utils.currentBox + 1)).formatted(GRAY));
            boxPlaceholder = Utils.currentBox;
        }
        super.renderButton(context, mouseX, mouseY, delta);
    }
}