package me.justahuman.dystoriantweaks.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public abstract class CustomTextField extends TextFieldWidget {
    protected CustomTextField(int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x + 4, y + 3, Textures.TEXT_FIELD_WIDTH - 8, Textures.TEXT_FIELD_HEIGHT - 8, Text.empty());
        setVisible(false);
        setDrawsBackground(false);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(Textures.TEXT_FIELD_TEXTURE,
                getX() - 4, getY() - 4,
                Textures.TEXT_FIELD_WIDTH,
                Textures.TEXT_FIELD_HEIGHT,
                0, 0,
                Textures.TEXT_FIELD_WIDTH,
                Textures.TEXT_FIELD_HEIGHT,
                Textures.TEXT_FIELD_WIDTH,
                Textures.TEXT_FIELD_HEIGHT
        );
        super.renderButton(context, mouseX, mouseY, delta);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int x = this.getX() - 4;
        int y = this.getY() - 4;
        return this.visible && mouseX >= x && mouseX < (x + Textures.TEXT_FIELD_WIDTH) && mouseY >= y && mouseY < (y + Textures.TEXT_FIELD_HEIGHT);
    }
}
