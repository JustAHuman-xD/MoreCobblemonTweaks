package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.gui.pc.BoxStorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.utils.Textures;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import static com.cobblemon.mod.common.client.gui.pc.StorageSlot.SIZE;

@Mixin(value = StorageSlot.class, remap = false)
public abstract class StorageSlotMixin {
    @Shadow @Final private StorageWidget parent;

    @Inject(at = @At("HEAD"), method = "renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", cancellable = true)
    public void renderSlotHead(GuiGraphics context, int posX, int posY, float partialTicks, CallbackInfo ci) {
        MultiSelector selector = (MultiSelector) (Object) this.parent;
        if (((Object) this) instanceof BoxStorageSlot slot && selector.moreCobblemonTweaks$isSelected(slot.getPosition())) {
            GuiUtilsKt.blitk(
                    context.pose(),
                    Textures.SELECTED_SLOT_OVERLAY,
                    posX,
                    posY,
                    SIZE,
                    SIZE
            );

            Pokemon pokemon = getPokemon();
            if (pokemon == null) {
                return;
            }

            PoseStack matrices = context.pose();
            context.enableScissor(
                    posX - 2,
                    posY + 2,
                    posX + SIZE + 4,
                    posY + SIZE + 4
            );

            matrices.pushPose();
            matrices.translate(posX + (SIZE / 2.0), posY + 1.0, 0.0);
            matrices.scale(2.5F, 2.5F, 1F);

            float[] original = Arrays.copyOf(RenderSystem.getShaderColor(), 4);
            RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 0.65f);
            PokemonGuiUtilsKt.drawProfilePokemon(
                    pokemon.asRenderablePokemon(),
                    matrices,
                    QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, 35F, 0F)),
                    PoseType.PROFILE,
                    getState(),
                    0F,
                    4.5F,
                    true,
                    false,
                    1F,
                    1F,
                    1F,
                    0.33F,
                    0f,
                    0f
            );
            RenderSystem.setShaderColor(original[0], original[1], original[2], original[3]);
            matrices.popPose();

            context.disableScissor();

            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isHovered", cancellable = true)
    public void isHovered(int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (!this.parent.visible) {
            cir.setReturnValue(false);
        }
    }

    @Shadow public abstract Pokemon getPokemon();
    @Shadow public abstract FloatingState getState();
}
