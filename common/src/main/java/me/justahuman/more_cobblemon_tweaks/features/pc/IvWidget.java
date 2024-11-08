package me.justahuman.more_cobblemon_tweaks.features.pc;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static net.minecraft.util.Formatting.*;

public class IvWidget implements Drawable {
    protected final PCGUI gui;

    public IvWidget(PCGUI gui) {
        this.gui = gui;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        double x = (gui.width - PCGUI.BASE_WIDTH) / 2d;
        double y = (gui.height - PCGUI.BASE_HEIGHT) / 2d;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        x -= Textures.IV_WIDGET_WIDTH;
        y += 31;

        context.drawTexture(Textures.IV_WIDGET_TEXTURE,
                (int) x, (int) y,
                Textures.IV_WIDGET_WIDTH,
                Textures.IV_WIDGET_HEIGHT,
                0, 0,
                Textures.IV_WIDGET_WIDTH,
                Textures.IV_WIDGET_HEIGHT,
                Textures.IV_WIDGET_WIDTH,
                Textures.IV_WIDGET_HEIGHT
        );

        Pokemon pokemon = gui.getPreviewPokemon$common();
        if (pokemon != null) {
            x += 9.5;
            y += 9.5;
            IVs iVs = pokemon.getIvs();
            drawText(context, PcEnhancements.translate("iv_display.hp").formatted(RED), x, y, mouseX, mouseY);
            String hp = iVs.get(Stats.HP).toString();
            drawText(context, Text.literal(hp).formatted(WHITE), x + (hp.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.attack").formatted(BLUE), x, y, mouseX, mouseY);
            String attack = iVs.get(Stats.ATTACK).toString();
            drawText(context, Text.literal(attack).formatted(WHITE), x + (attack.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.defense").formatted(GRAY), x, y, mouseX, mouseY);
            String defense = iVs.get(Stats.DEFENCE).toString();
            drawText(context, Text.literal(defense).formatted(WHITE), x + (defense.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.sp_attack").formatted(AQUA), x, y, mouseX, mouseY);
            String spAttack = iVs.get(Stats.SPECIAL_ATTACK).toString();
            drawText(context, Text.literal(spAttack).formatted(WHITE), x + (spAttack.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.sp_defense").formatted(YELLOW), x, y, mouseX, mouseY);
            String spDef = iVs.get(Stats.SPECIAL_DEFENCE).toString();
            drawText(context, Text.literal(spDef).formatted(WHITE), x + (spDef.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.speed").formatted(GREEN), x, y, mouseX, mouseY);
            String speed = iVs.get(Stats.SPEED).toString();
            drawText(context, Text.literal(speed).formatted(WHITE), x + (speed.length() == 1 ? 30 : 27), y, mouseX, mouseY);
        }
    }

    public void drawText(DrawContext context, MutableText text, double x, double y, int mouseX, int mouseY) {
        RenderHelperKt.drawScaledText(context, null, text, x, y, PCGUI.SCALE, 1, Integer.MAX_VALUE, 0x00FFFFFF, false, true, mouseX, mouseY);
    }
}