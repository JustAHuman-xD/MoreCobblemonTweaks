package me.justahuman.more_cobblemon_tweaks.mixins.pc.moveall;

import com.cobblemon.mod.common.client.net.storage.pc.MoveClientPCPokemonHandler;
import com.cobblemon.mod.common.net.messages.client.storage.pc.MoveClientPCPokemonPacket;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(MoveClientPCPokemonHandler.class)
public class MoveClientPCPokemonHandlerMixin {
    @Inject(at = @At("TAIL"), method = "handle(Lcom/cobblemon/mod/common/net/messages/client/storage/pc/MoveClientPCPokemonPacket;Lnet/minecraft/client/Minecraft;)V")
    public void postHandle(MoveClientPCPokemonPacket packet, Minecraft client, CallbackInfo ci) {
        CompletableFuture<Void> moveAllPokemonFuture = Utils.moveAllPokemonFuture;
        if (moveAllPokemonFuture != null) {
            Utils.moveAllPokemonFuture = null;
            CompletableFuture.runAsync(Utils.moveAllCountUpdater);
            moveAllPokemonFuture.complete(null);
        }
    }
}
