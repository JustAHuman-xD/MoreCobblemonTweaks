package me.justahuman.dystoriantweaks.features.pc.wallpaper;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.dystoriantweaks.config.ModConfig;
import me.justahuman.dystoriantweaks.utils.Textures;
import me.justahuman.dystoriantweaks.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class WallpaperWidget extends AlwaysSelectedEntryListWidget<WallpaperWidget.Entry> {
    protected static final int ENTRY_WIDTH = 156;
    protected static final int ENTRY_HEIGHT = 142;
    protected final WallpaperButton button;
    protected boolean visible = false;

    public WallpaperWidget(WallpaperButton button, int x, int y) {
        super(MinecraftClient.getInstance(), 174, 155, y, y + 155, ENTRY_HEIGHT);
        this.button = button;
        centerListVertically = false;
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setRenderHorizontalShadows(false);
        setLeftPos(x);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            for (Map.Entry<String, Identifier> entry : Textures.POSSIBLE_WALLPAPER_TEXTURES.entrySet()) {
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
        if (isMouseOver(mouseX, mouseY) && getHoveredEntry() != null) {
            Utils.playSound(CobblemonSounds.PC_CLICK);
            getHoveredEntry().mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (visible) {
            super.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        for (int i = 0; i < getEntryCount(); i++) {
            int top = getRowTop(i) + 2;
            int bottom = top + ENTRY_HEIGHT;
            if (bottom >= this.top && top <= this.bottom - 2) {
                this.renderEntry(context, mouseX, mouseY, delta, i, this.left + (this.width / 2) - (ENTRY_WIDTH / 2), top, ENTRY_WIDTH, ENTRY_HEIGHT);
            }
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.left + this.width;
    }

    public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        protected final String name;
        protected final Identifier wallpaper;

        public Entry(String name, Identifier wallpaper) {
            this.name = name;
            this.wallpaper = wallpaper;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (hovered) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                if (y + textRenderer.fontHeight + 2 > WallpaperWidget.this.top) {
                    context.drawTooltip(textRenderer, Text.of(name), x, y + textRenderer.fontHeight + 2);
                }
                RenderSystem.disableBlend();
                context.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
            }

            context.drawTexture(wallpaper,
                    x, y,
                    entryWidth, entryHeight,
                    0, 0,
                    Textures.WALLPAPER_WIDTH,
                    Textures.WALLPAPER_HEIGHT,
                    Textures.WALLPAPER_WIDTH,
                    Textures.WALLPAPER_HEIGHT);

            RenderSystem.enableBlend();
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ModConfig.setBoxTexture(Utils.currentBox, this.wallpaper);
            ClientPlayerEntity player = WallpaperWidget.this.client.player;
            if (Utils.allBoxes && player != null) {
                try {
                    int boxes = Cobblemon.INSTANCE.getStorage().getPC(player.getUuid()).getBoxes().size();
                    for (int i = 0; i < boxes; i++) {
                        ModConfig.setBoxTexture(i, wallpaper);
                    }
                } catch (NoPokemonStoreException ignored) {}
            }
            WallpaperWidget.this.button.onClick(mouseX, mouseY);
            return false;
        }

        @Override
        public Text getNarration() {
            return Text.empty();
        }
    }
}
