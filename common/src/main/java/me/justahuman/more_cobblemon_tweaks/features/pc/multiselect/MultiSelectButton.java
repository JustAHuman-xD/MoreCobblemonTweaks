package me.justahuman.more_cobblemon_tweaks.features.pc.multiselect;

import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.utils.CustomButton;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

import java.util.Set;

public class MultiSelectButton extends CustomButton {
    protected boolean toggled = false;

    public MultiSelectButton(int x, int y, Set<Renderable> siblings) {
        super(x, y, Textures.MULTI_SELECT_BUTTON_WIDTH, Textures.MULTI_SELECT_BUTTON_HEIGHT, Textures.MULTI_SELECT_BUTTON_TEXTURE, siblings);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int v = isHovered ? Textures.MULTI_SELECT_BUTTON_HEIGHT : 0;
        if (toggled) {
            v = Textures.MULTI_SELECT_BUTTON_HEIGHT * 2;
        }

        context.blit(texture,
                getX(), getY(),
                width, height,
                0, v,
                width, height,
                width, height * 3
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        toggled = !toggled;
        if (!toggled) {
            handleSibling(MultiSelector.class, MultiSelector::moreCobblemonTweaks$clearSelection);
        }
    }

    public boolean isToggled() {
        return toggled;
    }
}
