package me.justahuman.more_cobblemon_tweaks.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public abstract class CustomTextField extends TextFieldWidget {
    protected CustomTextField(int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x + 6, y + 7, Textures.TEXT_FIELD_WIDTH - 8, Textures.BUTTON_HEIGHT - 8, Text.empty());
        setVisible(false);
        setDrawsBackground(false);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(Textures.TEXT_FIELD_TEXTURE,
                getX() - 6, getY() - 7,
                Textures.TEXT_FIELD_WIDTH,
                Textures.BUTTON_HEIGHT,
                0, 0,
                Textures.TEXT_FIELD_WIDTH,
                Textures.BUTTON_HEIGHT,
                Textures.TEXT_FIELD_WIDTH,
                Textures.BUTTON_HEIGHT
        );
        super.renderButton(context, mouseX, mouseY, delta);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int x = this.getX() - 6;
        int y = this.getY() - 7;
        return this.visible && mouseX >= x && mouseX < (x + Textures.TEXT_FIELD_WIDTH) && mouseY >= y && mouseY < (y + Textures.BUTTON_HEIGHT);
    }
}
