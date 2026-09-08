package me.justahuman.more_cobblemon_tweaks.features.pc;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import me.justahuman.more_cobblemon_tweaks.config.ModConfig;
import me.justahuman.more_cobblemon_tweaks.features.PcEnhancements;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;

import static net.minecraft.ChatFormatting.*;

public class IvWidget implements Renderable {
    protected final PCGUI gui;

    public IvWidget(PCGUI gui) {
        this.gui = gui;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Pokemon pokemon = gui.getPreviewPokemon$common();
        if (pokemon == null) {
            return;
        }

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
        x += 9.5;
        y += 9.5;

        IVs ivs = pokemon.getIvs();
        y = drawStat(context, ivs, Stats.HP, GREEN, x, y, mouseX, mouseY);
        y = drawStat(context, ivs, Stats.ATTACK, RED, x, y, mouseX, mouseY);
        y = drawStat(context, ivs, Stats.DEFENCE, GOLD, x, y, mouseX, mouseY);
        y = drawStat(context, ivs, Stats.SPECIAL_ATTACK, LIGHT_PURPLE, x, y, mouseX, mouseY);
        y = drawStat(context, ivs, Stats.SPECIAL_DEFENCE, YELLOW, x, y, mouseX, mouseY);
        y = drawStat(context, ivs, Stats.SPEED, AQUA, x, y, mouseX, mouseY);

        double baseAverage = Stats.Companion.getPERMANENT().stream().mapToInt(ivs::getOrDefault).average().orElse(0.0);
        double effectiveAverage = Stats.Companion.getPERMANENT().stream().mapToDouble(ivs::getEffectiveBattleIV).average().orElse(0.0);
        drawStat(context, "average", baseAverage, effectiveAverage, WHITE, x, y, mouseX, mouseY);
    }

    public double drawStat(GuiGraphics context, IVs ivs, Stats stat, ChatFormatting color, double x, double y, int mouseX, int mouseY) {
        return drawStat(context, stat.name().toLowerCase(), ivs.getOrDefault(stat), ivs.isHyperTrained(stat) ? (double) ivs.getEffectiveBattleIV(stat) : null, color, x, y, mouseX, mouseY);
    }

    public double drawStat(GuiGraphics context, String stat, double baseValue, Double effectiveValue, ChatFormatting color, double x, double y, int mouseX, int mouseY) {
        boolean colored = ModConfig.isEnabled("pc_colored_ivs");
        RenderHelperKt.drawScaledText(context, null, PcEnhancements.translate("iv_display." + stat).withStyle(colored ? color : WHITE), x, y, PCGUI.SCALE, 1, Integer.MAX_VALUE, 0x00FFFFFF, false, true, mouseX, mouseY);
        String value = Screen.hasShiftDown() ? Utils.ivPercent(effectiveValue != null ? effectiveValue : baseValue) : (int) baseValue + (effectiveValue != null ? " (" + effectiveValue.intValue() + ")" : "");
        int width = Minecraft.getInstance().font.width(value);
        double valueX = x + 45 - (width * PCGUI.SCALE);
        RenderHelperKt.drawScaledText(context, null, Component.literal(value).withStyle(WHITE), valueX, y, PCGUI.SCALE, 1, Integer.MAX_VALUE, 0x00FFFFFF, false, true, mouseX, mouseY);
        return y + 15;
    }
}