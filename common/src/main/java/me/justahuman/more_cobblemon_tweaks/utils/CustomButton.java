package me.justahuman.more_cobblemon_tweaks.utils;

import com.cobblemon.mod.common.CobblemonSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.function.Consumer;

public abstract class CustomButton extends AbstractWidget {
    protected final Set<Renderable> siblings;
    protected final ResourceLocation texture;

    public CustomButton(int x, int y, int width, int height, ResourceLocation texture, Set<Renderable> siblings) {
        super(x, y, width, height, Component.empty());
        this.texture = texture;
        this.siblings = siblings;
    }

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.blit(texture,
                getX(), getY(),
                width, height,
                0, 0,
                width, height,
                width, height
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(CobblemonSounds.PC_CLICK, 1.0F));
    }

    protected <T> void handleSibling(Class<T> clazz, Consumer<T> consumer) {
        for (Renderable sibling : siblings) {
            if (clazz.isInstance(sibling)) {
                consumer.accept(clazz.cast(sibling));
            }
        }
    }
}