package me.justahuman.more_cobblemon_tweaks.mixins;

import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StorageWidget.class)
public interface StorageWidgetAccessor {
    @Invoker(value = "resetSelected", remap = false)
    void resetSelectedInvoker();
}
