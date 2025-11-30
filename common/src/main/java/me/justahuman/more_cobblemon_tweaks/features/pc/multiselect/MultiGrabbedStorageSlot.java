package me.justahuman.more_cobblemon_tweaks.features.pc.multiselect;

import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiGrabbedStorageSlot extends StorageSlot {
    private final MultiSelector parent;
    private final Pokemon pokemon;
    public final SlotPosition slot;
    public SlotPosition localSlot;

    public MultiGrabbedStorageSlot(int x, int y, StorageWidget parent, Pokemon pokemon, SlotPosition slot, SlotPosition localSlot) {
        super(x, y, parent, ignored -> {});
        this.parent = (MultiSelector) (Object) parent;
        this.pokemon = pokemon;
        this.slot = slot;
        this.localSlot = localSlot;
        setSlotSelected(true);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        SlotPosition pos = localSlot.minus(parent.moreCobblemonTweaks$getSelectionOrigin()).plus(getPlacingOffset(mouseX, mouseY));
        renderSlot(
                context,
                mouseX - (width / 2) + (pos.x() * width) + (pos.x() != 0 ? StorageWidget.BOX_SLOT_PADDING * (pos.x() - 1) : 0),
                mouseY - (height / 2) + (pos.y() * height) + (pos.y() != 0 ? StorageWidget.BOX_SLOT_PADDING * (pos.y() - 1) : 0),
                delta
        );
    }

    public SlotPosition getPlacingOffset(int mouseX, int mouseY) {
        return getPlacingOffset(parent.moreCobblemonTweaks$getHoveredSlot(mouseX, mouseY));
    }

    public SlotPosition getPlacingOffset(SlotPosition root) {
        SlotPosition target = root.plus(localSlot.minus(parent.moreCobblemonTweaks$getSelectionOrigin()));
        int offsetX = 0;
        int offsetY = 0;

        if (target.x() >= SlotPosition.BOX_COLUMNS) {
            offsetX = -SlotPosition.BOX_COLUMNS;
            offsetY += 1;
        }

        if (target.y() + offsetY >= SlotPosition.BOX_ROWS) {
            offsetY = -SlotPosition.BOX_ROWS + offsetY;
        }
        return new SlotPosition(offsetX, offsetY);
    }

    @Override
    public @Nullable Pokemon getPokemon() {
        return this.pokemon;
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public boolean isHoveredOrFocused() {
        return localSlot.equals(parent.moreCobblemonTweaks$getSelectionOrigin());
    }

    @Override
    public boolean isStationary() {
        return false;
    }
}
