package me.justahuman.more_cobblemon_tweaks.features.pc.wallpaper;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class WallpaperWidget extends ObjectSelectionList<WallpaperWidget.Entry> {
    protected static final int ENTRY_WIDTH = 156;
    protected static final int ENTRY_HEIGHT = 142;
    protected final WallpaperButton button;

    public WallpaperWidget(WallpaperButton button, int x, int y) {
        super(Minecraft.getInstance(), 174, 155, y, ENTRY_HEIGHT);
        this.button = button;
        this.centerListVertically = false;
        this.visible = false;
        setRenderHeader(false, 0);
        setX(x);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            for (Map.Entry<String, ResourceLocation> entry : Textures.POSSIBLE_WALLPAPER_TEXTURES.entrySet()) {
                addEntry(new Entry(entry.getKey(), entry.getValue()));
            }
            this.visible = true;
        } else {
            clearEntries();
            this.visible = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && getHovered() != null) {
            Utils.playSound(CobblemonSounds.PC_CLICK);
            getHovered().mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Override
    protected void renderListItems(GuiGraphics context, int mouseX, int mouseY, float delta) {
        for (int i = 0; i < getItemCount(); i++) {
            int top = getRowTop(i) + 2;
            int bottom = top + ENTRY_HEIGHT;
            if (bottom >= this.getY() && top <= this.getBottom() - 2) {
                this.renderItem(context, mouseX, mouseY, delta, i, this.getX() + (this.width / 2) - (ENTRY_WIDTH / 2), top, ENTRY_WIDTH, ENTRY_HEIGHT);
            }
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight();
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        protected final String name;
        protected final ResourceLocation wallpaper;

        public Entry(String name, ResourceLocation wallpaper) {
            this.name = name;
            this.wallpaper = wallpaper;
        }

        @Override
        public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            RenderSystem.enableBlend();
            if (hovered) {
                Font font = Minecraft.getInstance().font;
                if (y + font.lineHeight + 2 > WallpaperWidget.this.getY()) {
                    graphics.renderTooltip(font, Component.literal(name), x, y + font.lineHeight + 2);
                }
                RenderSystem.disableBlend();
                graphics.setColor(1.0F, 1.0F, 1.0F, 0.5F);
            }

            graphics.blit(wallpaper,
                    x, y,
                    entryWidth, entryHeight,
                    0, 0,
                    Textures.WALLPAPER_WIDTH,
                    Textures.WALLPAPER_HEIGHT,
                    Textures.WALLPAPER_WIDTH,
                    Textures.WALLPAPER_HEIGHT);

            RenderSystem.enableBlend();
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ModConfig.setBoxTexture(Utils.currentBox, this.wallpaper);
            LocalPlayer player = WallpaperWidget.this.minecraft.player;
            if (Utils.allBoxes && player != null) {
                try {
                    int boxes = Cobblemon.INSTANCE.getStorage().getPC(player.getUUID(), player.registryAccess()).getBoxes().size();
                    for (int i = 0; i < boxes; i++) {
                        ModConfig.setBoxTexture(i, wallpaper);
                    }
                } catch (Exception ignored) {}
            }
            WallpaperWidget.this.button.onClick(mouseX, mouseY);
            return false;
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }
    }
}
