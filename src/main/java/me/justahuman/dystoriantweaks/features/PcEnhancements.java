package me.justahuman.dystoriantweaks.features;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.justahuman.dystoriantweaks.utils.Textures;
import me.justahuman.dystoriantweaks.utils.Utils;
import me.justahuman.dystoriantweaks.config.ModConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import static net.minecraft.util.Formatting.*;

public class PcEnhancements {
    public static final List<Integer> BOX_ORDER = new ArrayList<>();

    public static void registerWallpaperCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("setWallpaper")
                .then(argument("wallpaper", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            for (String shortName : Textures.POSSIBLE_WALLPAPER_TEXTURES.keySet()) {
                                builder.suggest(shortName);
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String shortName = StringArgumentType.getString(context, "wallpaper");
                            Identifier wallpaper = Textures.POSSIBLE_WALLPAPER_TEXTURES.get(shortName);
                            if (wallpaper == null) {
                                context.getSource().sendFeedback(Text.literal("Not a valid wallpaper identifier!").formatted(RED));
                                return 1;
                            }
                            context.getSource().sendFeedback(Text.literal("Updated box texture!").formatted(GREEN));
                            if (Utils.allBoxes) {
                                try {
                                    int boxes = Cobblemon.INSTANCE.getStorage().getPC(context.getSource().getPlayer().getUuid()).getBoxes().size();
                                    for (int i = 0; i < boxes; i++) {
                                        ModConfig.setBoxTexture(i, wallpaper);
                                    }
                                } catch (NoPokemonStoreException ignored) {}
                                return 1;
                            }
                            ModConfig.setBoxTexture(Utils.currentBox, wallpaper);
                            return 1;
                        })));
    }

    public static int getBoxIndex(int box) {
        if (BOX_ORDER.size() <= box) {
            for (int i = BOX_ORDER.size(); i <= box; i++) {
                BOX_ORDER.add(i);
            }
        }
        return BOX_ORDER.indexOf(box);
    }
}
