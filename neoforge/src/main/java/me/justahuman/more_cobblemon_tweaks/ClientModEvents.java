package me.justahuman.more_cobblemon_tweaks;

import me.justahuman.more_cobblemon_tweaks.features.Keybinds;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@EventBusSubscriber(modid = MoreCobblemonTweaks.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
//        event.registerReloadListener(new ContextAwareReloadListener() {
//            @Override
//            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
//                return CompletableFuture.runAsync(() -> {
//                    MoreCobblemonTweaks.onReload(manager);
//                }, applyExecutor);
//            }
//        });
    }

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(Keybinds.OPEN_CONFIG);
    }
}
