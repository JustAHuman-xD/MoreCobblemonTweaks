package me.justahuman.dystoriantweaks.features;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.util.Formatting.AQUA;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.WHITE;
import static net.minecraft.util.Formatting.YELLOW;

public class PcEnhancements {
    public static final Identifier IV_WIDGET_TEXTURE = new Identifier("dystoriantweaks", "textures/gui/pc/iv_display.png");
    public static final int IV_WIDGET_WIDTH = 52;
    public static final int IV_WIDGET_HEIGHT = 98;

    public static class IvWidget implements Drawable {
        protected final PCGUI gui;

        public IvWidget(PCGUI gui) {
            this.gui = gui;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = (gui.width - PCGUI.BASE_WIDTH) / 2;
            int y = (gui.height - PCGUI.BASE_HEIGHT) / 2;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            x -= IV_WIDGET_WIDTH;
            y += 31;

            context.drawTexture(IV_WIDGET_TEXTURE,
                    x, y,
                    IV_WIDGET_WIDTH,
                    IV_WIDGET_HEIGHT,
                    0, 0,
                    IV_WIDGET_WIDTH,
                    IV_WIDGET_HEIGHT,
                    IV_WIDGET_WIDTH,
                    IV_WIDGET_HEIGHT
            );

            Pokemon pokemon = gui.getPreviewPokemon$common();
            if (pokemon != null) {
                x += 6;
                y += 6;
                IVs iVs = pokemon.getIvs();
                drawText(context, Text.literal("HP: ").formatted(RED).append(Text.literal(String.valueOf(iVs.get(Stats.HP))).formatted(WHITE)), x, y, mouseX, mouseY);
                y += 20;
                drawText(context, Text.literal("Atk: ").formatted(BLUE).append(Text.literal(String.valueOf(iVs.get(Stats.ATTACK))).formatted(WHITE)), x, y, mouseX, mouseY);
                y += 20;
                drawText(context, Text.literal("Def: ").formatted(GRAY).append(Text.literal(String.valueOf(iVs.get(Stats.DEFENCE))).formatted(WHITE)), x, y, mouseX, mouseY);
                y += 20;
                drawText(context, Text.literal("Sp.Atk: ").formatted(AQUA).append(Text.literal(String.valueOf(iVs.get(Stats.SPECIAL_ATTACK))).formatted(WHITE)), x, y, mouseX, mouseY);
                y += 20;
                drawText(context, Text.literal("Sp.Def: ").formatted(YELLOW).append(Text.literal(String.valueOf(iVs.get(Stats.SPECIAL_DEFENCE))).formatted(WHITE)), x, y, mouseX, mouseY);
                y += 20;
                drawText(context, Text.literal("Speed: ").formatted(GREEN).append(Text.literal(String.valueOf(iVs.get(Stats.SPEED))).formatted(WHITE)), x, y, mouseX, mouseY);
                y += 20;
            }
        }

        public void drawText(DrawContext context, MutableText text, int x, int y, int mouseX, int mouseY) {
            RenderHelperKt.drawScaledText(context, null, text, x, y, PCGUI.SCALE, 1, Integer.MAX_VALUE, 0x00FFFFFF, false, true, mouseX, mouseY);
        }
    }
}
