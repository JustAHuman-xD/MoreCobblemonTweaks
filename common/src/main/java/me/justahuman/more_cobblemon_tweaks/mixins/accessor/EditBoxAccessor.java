package me.justahuman.more_cobblemon_tweaks.mixins.accessor;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor(value = "value") void setDirectValue(String value);
    @Accessor(value = "responder") Consumer<String> getRawResponder();
}
