package me.justahuman.more_cobblemon_tweaks.features.pc.multiselect;

import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.Minecraft;

public record SlotPosition(int x, int y) {
    public static final SlotPosition ZERO = new SlotPosition(0, 0);

    public static final int BOX_ROWS = 5;
    public static final int BOX_COLUMNS = 6;

    public static final int MAX_INDEX = BOX_ROWS * BOX_COLUMNS - 1;

    public int index() {
        return y * BOX_COLUMNS + x;
    }

    public SlotPosition plus(SlotPosition offset) {
        return new SlotPosition(this.x + offset.x, this.y + offset.y);
    }

    public SlotPosition minus(SlotPosition offset) {
        return new SlotPosition(this.x - offset.x, this.y - offset.y);
    }

    public static SlotPosition of(Pokemon pokemon) {
        if (!(Minecraft.getInstance().screen instanceof PCGUI pcgui)) {
            return ZERO;
        }

        PCPosition position = pcgui.getPc().getPosition(pokemon);
        return position != null ? of(position) : ZERO;
    }

    public static SlotPosition of(PCPosition position) {
        return of(position.getSlot());
    }

    public static SlotPosition of(int index) {
        int x = index % BOX_COLUMNS;
        int y = index / BOX_COLUMNS;
        return new SlotPosition(x, y);
    }
}
