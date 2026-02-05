package me.justahuman.more_cobblemon_tweaks.features.pc.boxes;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.storage.pc.search.Search;
import com.cobblemon.mod.common.client.gui.CobblemonRenderable;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.storage.ClientBox;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import me.justahuman.more_cobblemon_tweaks.api.BoxViewHolder;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.List;
import java.util.Objects;

import static com.cobblemon.mod.common.client.gui.pc.StorageSlot.SIZE;

public class BoxListSlot extends Button implements CobblemonRenderable {
    private static final ResourceLocation POINTER = MiscUtilsKt.cobblemonResource("textures/gui/pc/pc_pointer.png");

    private final StorageWidget parent;
    private final BetterOnPress betterOnPress;
    public final int boxIndex;

    public BoxListSlot(StorageWidget parent, int boxIndex, int x, int y, BetterOnPress onPress) {
        super(x, y, SIZE, SIZE, Component.literal("BoxSlot"), button -> {}, DEFAULT_NARRATION);
        this.parent = parent;
        this.betterOnPress = onPress;
        this.boxIndex = boxIndex;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        ClientBox box = getBox();
        if (!shouldRender() || box == null) {
            return;
        }

        int wallpaperColor = PrimaryColorCache.getWallpaperColor(Utils.getUsedWallpaper(box));
        GuiUtilsKt.blitk(
                context.pose(),
                Textures.BOX_LIST_SLOT_TEXTURE,
                getX(),
                getY(),
                SIZE,
                SIZE,
                0,
                0,
                SIZE,
                SIZE,
                0,
                FastColor.ARGB32.red(wallpaperColor) / 255f,
                FastColor.ARGB32.green(wallpaperColor) / 255f,
                FastColor.ARGB32.blue(wallpaperColor) / 255f,
                0.75f
        );

        if (((BoxViewHolder) (Object) parent).moreCobblemonTweaks$getSelectedBoxListSlot() == this || isHovered(mouseX, mouseY)) {
            GuiUtilsKt.blitk(
                    context.pose(),
                    POINTER,
                    (getX() + 10) / PCGUI.SCALE,
                    ((getY() - 3) / PCGUI.SCALE) - parent.getPcGui().getSelectPointerOffsetY(),
                    8,
                    11,
                    0,
                    0,
                    11,
                    8,
                    0,
                    1,
                    1,
                    1,
                    1,
                    true,
                    PCGUI.SCALE
            );
        }

        int index = 0;
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 6; col++) {
                int pokemonIndex = index++;
                if (pokemonIndex >= box.getSlots().size()) {
                    continue;
                }

                Pokemon pokemon = box.getSlots().get(pokemonIndex);
                if (pokemon == null) {
                    continue;
                }

                //x + offset + (col - 1) * (slot size + spacing)
                int pokemonX = getX() + 1 + (col - 1) * (3 + 1);
                int pokemonY = getY() + 1 + (row - 1) * (3 + 2);
                int color = PrimaryColorCache.getPokemonColor(pokemon.getSpecies().resourceIdentifier);
                context.fill(pokemonX, pokemonY, pokemonX + 3, pokemonY + 3, color);
            }
        }
    }

    public ClientBox getBox() {
        List<ClientBox> boxes = parent.getPcGui().getPc().getBoxes();
        return boxes.size() > boxIndex ? boxes.get(boxIndex) : null;
    }

    public boolean clickable() {
        return getBox() != null && ((BoxViewHolder) (Object) parent).moreCobblemonTweaks$isBoxListOpen();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            this.betterOnPress.onPress(this, button);
            return true;
        } else {
            return false;
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return clickable() && mouseX >= getX() && mouseX <= getX() + SIZE && mouseY >= getY() && mouseY <= getY() + SIZE;
    }

    public boolean shouldRender() {
        ClientBox box = getBox();
        if (!clickable() || box == null) {
            return false;
        }

        Search search = parent.getPcGui().getSearch();
        boolean emptyBox = box.getSlots().stream().allMatch(Objects::isNull);
        return search == Search.Companion.getDEFAULT() || emptyBox || box.getSlots().stream().anyMatch(search::passes);
    }

    public interface BetterOnPress {
        void onPress(BoxListSlot slot, int button);
    }
}
