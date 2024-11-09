package me.justahuman.more_cobblemon_tweaks.features.pc;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static net.minecraft.ChatFormatting.*;

public class IvWidget implements Renderable {
    protected final PCGUI gui;

    public IvWidget(PCGUI gui) {
        this.gui = gui;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        double x = (gui.width - PCGUI.BASE_WIDTH) / 2d;
        double y = (gui.height - PCGUI.BASE_HEIGHT) / 2d;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        x -= Textures.IV_WIDGET_WIDTH;
        y += 31;

        context.blit(Textures.IV_WIDGET_TEXTURE,
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
            drawText(context, PcEnhancements.translate("iv_display.hp").withStyle(RED), x, y, mouseX, mouseY);
            String hp = iVs.get(Stats.HP).toString();
            drawText(context, Component.literal(hp).withStyle(WHITE), x + (hp.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.attack").withStyle(BLUE), x, y, mouseX, mouseY);
            String attack = iVs.get(Stats.ATTACK).toString();
            drawText(context, Component.literal(attack).withStyle(WHITE), x + (attack.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.defense").withStyle(GRAY), x, y, mouseX, mouseY);
            String defense = iVs.get(Stats.DEFENCE).toString();
            drawText(context, Component.literal(defense).withStyle(WHITE), x + (defense.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.sp_attack").withStyle(AQUA), x, y, mouseX, mouseY);
            String spAttack = iVs.get(Stats.SPECIAL_ATTACK).toString();
            drawText(context, Component.literal(spAttack).withStyle(WHITE), x + (spAttack.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.sp_defense").withStyle(YELLOW), x, y, mouseX, mouseY);
            String spDef = iVs.get(Stats.SPECIAL_DEFENCE).toString();
            drawText(context, Component.literal(spDef).withStyle(WHITE), x + (spDef.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            y += 15;
            drawText(context, PcEnhancements.translate("iv_display.speed").withStyle(GREEN), x, y, mouseX, mouseY);
            String speed = iVs.get(Stats.SPEED).toString();
            drawText(context, Component.literal(speed).withStyle(WHITE), x + (speed.length() == 1 ? 30 : 27), y, mouseX, mouseY);
        }
    }

    public void drawText(GuiGraphics context, MutableComponent text, double x, double y, int mouseX, int mouseY) {
        RenderHelperKt.drawScaledText(context, null, text, x, y, PCGUI.SCALE, 1, Integer.MAX_VALUE, 0x00FFFFFF, false, true, mouseX, mouseY);
    }
}