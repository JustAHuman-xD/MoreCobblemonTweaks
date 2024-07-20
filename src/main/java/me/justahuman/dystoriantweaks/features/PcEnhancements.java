package me.justahuman.dystoriantweaks.features;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.justahuman.dystoriantweaks.DystorianTweaks;
import me.justahuman.dystoriantweaks.Utils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import me.justahuman.dystoriantweaks.mixins.ChatScreenAccessor;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import static net.minecraft.util.Formatting.*;

public class PcEnhancements {
    public static final List<Identifier> POSSIBLE_WALLPAPER_TEXTURES = new ArrayList<>();
    public static final Identifier IV_WIDGET_TEXTURE = new Identifier("dystoriantweaks", "textures/gui/pc/iv_display.png");
    public static final Identifier RENAME_BUTTON_TEXTURE = new Identifier("dystoriantweaks", "textures/gui/pc/rename_button.png");
    public static final Identifier WALLPAPER_BUTTON_TEXTURE = new Identifier("dystoriantweaks", "textures/gui/pc/wallpaper_button.png");
    public static final int IV_WIDGET_WIDTH = 52;
    public static final int IV_WIDGET_HEIGHT = 98;
    public static final int RENAME_BUTTON_WIDTH = 21;
    public static final int RENAME_BUTTON_HEIGHT = 18;
    public static final int WALLPAPER_BUTTON_WIDTH = 19;
    public static final int WALLPAPER_BUTTON_HEIGHT = 18;

    public static class IvWidget implements Drawable {
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

            x -= IV_WIDGET_WIDTH;
            y += 31;

            context.drawTexture(IV_WIDGET_TEXTURE,
                    (int) x, (int) y,
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
                x += 9.5;
                y += 9.5;
                IVs iVs = pokemon.getIvs();
                drawText(context, Text.literal("HP:").formatted(RED), x, y, mouseX, mouseY);
                String hp = iVs.get(Stats.HP).toString();
                drawText(context, Text.literal(hp).formatted(WHITE), x + (hp.length() == 1 ? 30 : 27), y, mouseX, mouseY);
                y += 15;
                drawText(context, Text.literal("Atk:").formatted(BLUE), x, y, mouseX, mouseY);
                String attack = iVs.get(Stats.ATTACK).toString();
                drawText(context, Text.literal(attack).formatted(WHITE), x + (attack.length() == 1 ? 30 : 27), y, mouseX, mouseY);
                y += 15;
                drawText(context, Text.literal("Def:").formatted(GRAY), x, y, mouseX, mouseY);
                String defense = iVs.get(Stats.DEFENCE).toString();
                drawText(context, Text.literal(defense).formatted(WHITE), x + (defense.length() == 1 ? 30 : 27), y, mouseX, mouseY);
                y += 15;
                drawText(context, Text.literal("Sp.Atk:").formatted(AQUA), x, y, mouseX, mouseY);
                String spAttack = iVs.get(Stats.SPECIAL_ATTACK).toString();
                drawText(context, Text.literal(spAttack).formatted(WHITE), x + (spAttack.length() == 1 ? 30 : 27), y, mouseX, mouseY);
                y += 15;
                drawText(context, Text.literal("Sp.Def:").formatted(YELLOW), x, y, mouseX, mouseY);
                String spDef = iVs.get(Stats.SPECIAL_DEFENCE).toString();
                drawText(context, Text.literal(spDef).formatted(WHITE), x + (spDef.length() == 1 ? 30 : 27), y, mouseX, mouseY);
                y += 15;
                drawText(context, Text.literal("Speed:").formatted(GREEN), x, y, mouseX, mouseY);
                String speed = iVs.get(Stats.SPEED).toString();
                drawText(context, Text.literal(speed).formatted(WHITE), x + (speed.length() == 1 ? 30 : 27), y, mouseX, mouseY);
            }
        }

        public void drawText(DrawContext context, MutableText text, double x, double y, int mouseX, int mouseY) {
            RenderHelperKt.drawScaledText(context, null, text, x, y, PCGUI.SCALE, 1, Integer.MAX_VALUE, 0x00FFFFFF, false, true, mouseX, mouseY);
        }
    }

    public static class RenameButton extends ClickableWidget {
        public RenameButton(int x, int y) {
            super(x, y, RENAME_BUTTON_WIDTH, RENAME_BUTTON_HEIGHT, Text.empty());
            setTooltip(Tooltip.of(Text.literal("Click to rename the current box!")));
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawTexture(RENAME_BUTTON_TEXTURE,
                    getX(), getY(),
                    RENAME_BUTTON_WIDTH,
                    RENAME_BUTTON_HEIGHT,
                    0, 0,
                    RENAME_BUTTON_WIDTH,
                    RENAME_BUTTON_HEIGHT,
                    RENAME_BUTTON_WIDTH,
                    RENAME_BUTTON_HEIGHT
            );
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

        @Override
        public void onClick(double mouseX, double mouseY) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null) {
                client.setScreen(null);
                player.sendMessage(Text.literal("Enter new box name: ").formatted(YELLOW, BOLD));
                DystorianTweaks.addChatConsumer(boxName -> ModConfig.setBoxName(Utils.currentBox, boxName));
            }
        }
    }

    public static class WallpaperButton extends ClickableWidget {
        public WallpaperButton(int x, int y) {
            super(x, y, WALLPAPER_BUTTON_WIDTH, WALLPAPER_BUTTON_HEIGHT, Text.empty());
            setTooltip(Tooltip.of(Text.literal("Click to change the wallpaper of the current box!")));
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawTexture(WALLPAPER_BUTTON_TEXTURE,
                    getX(), getY(),
                    WALLPAPER_BUTTON_WIDTH,
                    WALLPAPER_BUTTON_HEIGHT,
                    0, 0,
                    WALLPAPER_BUTTON_WIDTH,
                    WALLPAPER_BUTTON_HEIGHT,
                    WALLPAPER_BUTTON_WIDTH,
                    WALLPAPER_BUTTON_HEIGHT
            );
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

        @Override
        public void onClick(double mouseX, double mouseY) {
            ChatScreen chatScreen = new ChatScreen("");
            MinecraftClient.getInstance().setScreen(chatScreen);
            ((ChatScreenAccessor) chatScreen).getChatField().setText("/setWallpaper ");
        }
    }

    public static void registerWallpaperCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("setWallpaper")
                .then(argument("wallpaper", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            for (Identifier wallpaper : POSSIBLE_WALLPAPER_TEXTURES) {
                                builder.suggest(wallpaper.toString());
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String identifier = StringArgumentType.getString(context, "wallpaper");
                            Identifier wallpaper = new Identifier(identifier);
                            if (!POSSIBLE_WALLPAPER_TEXTURES.contains(wallpaper)) {
                                context.getSource().sendFeedback(Text.literal("Not a valid wallpaper identifier!").formatted(RED));
                                return 1;
                            }
                            context.getSource().sendFeedback(Text.literal("Updated box texture!").formatted(GREEN));
                            ModConfig.setBoxTexture(Utils.currentBox, wallpaper);
                            return 1;
                        })));
    }
}
