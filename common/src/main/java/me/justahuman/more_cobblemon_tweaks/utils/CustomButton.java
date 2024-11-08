package me.justahuman.more_cobblemon_tweaks.utils;

import com.cobblemon.mod.common.CobblemonSounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.function.Consumer;

public abstract class CustomButton extends ClickableWidget {
    protected final Set<Drawable> siblings;
    protected final Identifier texture;

    public CustomButton(int x, int y, int width, int height, Identifier texture, Set<Drawable> siblings) {
        super(x, y, width, height, Text.empty());
        this.texture = texture;
        this.siblings = siblings;
    }

    @Override
    public abstract void onClick(double mouseX, double mouseY);

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(texture,
                getX(), getY(),
                width, height,
                0, 0,
                width, height,
                width, height
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.PC_CLICK, 1.0F));
    }

    protected <T> void handleSibling(Class<T> clazz, Consumer<T> consumer) {
        for (Drawable sibling : siblings) {
            if (clazz.isInstance(sibling)) {
                consumer.accept(clazz.cast(sibling));
            }
        }
    }
}