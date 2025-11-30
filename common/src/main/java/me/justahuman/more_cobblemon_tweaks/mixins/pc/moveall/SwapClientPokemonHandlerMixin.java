package me.justahuman.more_cobblemon_tweaks.mixins.pc.moveall;

import com.cobblemon.mod.common.client.net.storage.SwapClientPokemonHandler;
import com.cobblemon.mod.common.net.messages.client.storage.SwapClientPokemonPacket;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwapClientPokemonHandler.class)
public class SwapClientPokemonHandlerMixin {
    @Inject(at = @At("TAIL"), method = "handle(Lcom/cobblemon/mod/common/net/messages/client/storage/SwapClientPokemonPacket;Lnet/minecraft/client/Minecraft;)V")
    public void postHandle(SwapClientPokemonPacket packet, Minecraft client, CallbackInfo ci) {
        if (Utils.moveAllPokemonFuture != null) {
            Utils.moveAllPokemonFuture.complete(null);
            Utils.moveAllPokemonFuture = null;
        }
    }
}
