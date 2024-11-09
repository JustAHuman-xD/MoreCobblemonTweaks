package me.justahuman.more_cobblemon_tweaks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public abstract class CustomTextField extends EditBox {
    protected CustomTextField(int x, int y) {
        super(Minecraft.getInstance().font, x + 6, y + 7, Textures.TEXT_FIELD_WIDTH - 8, Textures.BUTTON_HEIGHT - 8, Component.empty());
        setVisible(false);
        setBordered(false);
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.blit(Textures.TEXT_FIELD_TEXTURE,
                getX() - 6, getY() - 7,
                Textures.TEXT_FIELD_WIDTH,
                Textures.BUTTON_HEIGHT,
                0, 0,
                Textures.TEXT_FIELD_WIDTH,
                Textures.BUTTON_HEIGHT,
                Textures.TEXT_FIELD_WIDTH,
                Textures.BUTTON_HEIGHT
        );
        super.renderWidget(context, mouseX, mouseY, delta);
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
