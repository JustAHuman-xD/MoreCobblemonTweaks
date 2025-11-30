package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.CobblemonNetwork;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.net.NetworkPacket;
import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.client.gui.pc.BoxStorageSlot;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.ReleaseConfirmButton;
import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.net.messages.server.storage.pc.MovePCPokemonPacket;
import com.cobblemon.mod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket;
import com.cobblemon.mod.common.net.messages.server.storage.pc.SwapPCPokemonPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelectorState;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.MultiGrabbedStorageSlot;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.SlotPosition;
import me.justahuman.more_cobblemon_tweaks.mixins.accessor.ButtonAccessor;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(value = StorageWidget.class, remap = false)
public abstract class StorageWidgetMixin extends SoundlessWidget implements MultiSelector {
    @Final @Shadow private PCGUI pcGui;
    @Final @Shadow private ReleaseConfirmButton releaseYesButton;

    @Unique private SlotPosition moreCobblemonTweaks$lastHoveredSlot = SlotPosition.ZERO;

    @Unique private PCPosition moreCobblemonTweaks$lastPosition = null;
    @Unique private int moreCobblemonTweaks$selectedBox = -1;
    @Unique private SlotPosition moreCobblemonTweaks$selectionOrigin = null;
    @Unique private final List<Pokemon> moreCobblemonTweaks$selection = new ArrayList<>();
    @Unique private final Map<UUID, MultiGrabbedStorageSlot> moreCobblemonTweaks$grabbedSlots = new HashMap<>();

    private StorageWidgetMixin(int pX, int pY, int pWidth, int pHeight, @NotNull Component component) {
        super(pX, pY, pWidth, pHeight, component);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void releaseMultiselect(CallbackInfo ci) {
        Button.OnPress original = ((ButtonAccessor) (Object) this.releaseYesButton).getOnPress();
        ((ButtonAccessor) (Object) this.releaseYesButton).setOnPress(button -> {
            if (!moreCobblemonTweaks$isMultiSelecting()) {
                original.onPress(button);
                return;
            }

            for (Pokemon pokemon : moreCobblemonTweaks$selection) {
                PCPosition position = pcGui.getPc().getPosition(pokemon);
                if (position != null) {
                    CobblemonNetwork.sendToServer(new ReleasePCPokemonPacket(pokemon.getUuid(), position));
                }
            }
            playSound(CobblemonSounds.PC_RELEASE);
            resetSelected();
            setDisplayConfirmRelease(false);
            moreCobblemonTweaks$clearMultiSelection();
        });
    }

    @Inject(at = @At("TAIL"), method = "renderWidget")
    public void renderGrabbedMultiSelection(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        for (MultiGrabbedStorageSlot slot : moreCobblemonTweaks$grabbedSlots.values()) {
            slot.render(context, mouseX,  mouseY, delta);
        }
    }

    @Inject(at = @At("HEAD"), method = "onStorageSlotClicked", cancellable = true)
    public void onSlotClicked(Button button, CallbackInfo ci) {
        if (!button.isHovered()) {
            ci.cancel();
            return;
        } else if (!moreCobblemonTweaks$isMultiSelecting()) {
            return;
        }

        ci.cancel();
        if (!(button instanceof BoxStorageSlot slot)) {
            return;
        } else if (slot.getPokemon() == null || Screen.hasShiftDown() || slot.getPosition().getBox() != moreCobblemonTweaks$selectedBox && moreCobblemonTweaks$selectedBox != -1) {
            boolean emptyOnly = slot.getPokemon() == null;

            ClientPC pc = this.pcGui.getPc();
            PCPosition clickedPos = slot.getPosition();
            int box = clickedPos.getBox();
            SlotPosition clicked = SlotPosition.of(clickedPos);

            Map<Pokemon, PCPosition> moves = new HashMap<>();
            for (Pokemon pokemon : moreCobblemonTweaks$selection) {
                PCPosition position = pc.getPosition(pokemon);
                if (position == null) {
                    continue;
                }

                MultiGrabbedStorageSlot grabbed = moreCobblemonTweaks$grabbedSlots.get(pokemon.getUuid());
                SlotPosition offset = grabbed.localSlot.minus(moreCobblemonTweaks$selectionOrigin).plus(grabbed.getPlacingOffset(clicked));
                int index = clicked.plus(offset).index();
                PCPosition target = new PCPosition(box, index++);
                while (emptyOnly && pc.get(target) != null && !moreCobblemonTweaks$isSelected(target)) {
                    if (index > SlotPosition.MAX_INDEX) {
                        break;
                    }
                    target = new PCPosition(box, index++);
                }

                if (emptyOnly && pc.get(target) != null && !moreCobblemonTweaks$isSelected(target)) {
                    break;
                }

                moves.put(pokemon, target);
            }

            if (moves.isEmpty()) {
                return;
            }

            CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
            for (Map.Entry<Pokemon, PCPosition> entry : moves.entrySet()) {
                Pokemon pokemon = entry.getKey();
                PCPosition target = entry.getValue();

                chain = chain.thenComposeAsync($ -> {
                    PCPosition source = pc.getPosition(pokemon);
                    if (source == null) {
                        return CompletableFuture.completedFuture(null);
                    }

                    Pokemon targetPokemon = pc.get(target);
                    NetworkPacket<?> packet = targetPokemon != null
                            ? new SwapPCPokemonPacket(pokemon.getUuid(), source, targetPokemon.getUuid(), target)
                            : new MovePCPokemonPacket(pokemon.getUuid(), source, target);

                    CompletableFuture<Void> packetFuture = new CompletableFuture<>();
                    Utils.moveAllPokemonFuture = packetFuture;
                    CobblemonNetwork.sendToServer(packet);
                    return packetFuture;
                });
            }

            chain.whenComplete(($, t) -> {
                if (t == null) {
                    for (Pokemon pokemon : moves.keySet()) {
                        PCPosition position = pc.getPosition(pokemon);
                        if (position != null) {
                            moreCobblemonTweaks$select(position, false);
                        }
                    }
                    playSound(CobblemonSounds.PC_DROP);
                } else {
                    MoreCobblemonTweaks.LOGGER.error("An error occurred while moving multiple Pokémon in the PC.", t);
                }
                Utils.moveAllPokemonFuture = null;
            });

            return;
        }

        PCPosition lastPosition = moreCobblemonTweaks$lastPosition;
        PCPosition clickedPosition = slot.getPosition();
        boolean selecting = !moreCobblemonTweaks$isSelected(clickedPosition);

        if (lastPosition != null && Screen.hasShiftDown()) {
            int from = Math.min(lastPosition.getSlot(), clickedPosition.getSlot());
            int to = Math.max(lastPosition.getSlot(), clickedPosition.getSlot());
            for (int i = from; i <= to; i++) {
                PCPosition position = new PCPosition(clickedPosition.getBox(), i);
                moreCobblemonTweaks$select(position, selecting);
            }
        } else {
            moreCobblemonTweaks$select(clickedPosition, selecting);
            moreCobblemonTweaks$lastPosition = clickedPosition;
        }

        playSound(selecting ? CobblemonSounds.PC_GRAB : CobblemonSounds.PC_DROP);
    }

    @Unique
    private void moreCobblemonTweaks$select(PCPosition position, boolean select) {
        Pokemon pokemon = this.pcGui.getPc().get(position);
        if (pokemon == null) {
            return;
        }

        if (select) {
            if (moreCobblemonTweaks$isSelected(position)) {
                return;
            }

            SlotPosition slot = SlotPosition.of(position);
            SlotPosition localSlot = SlotPosition.of(moreCobblemonTweaks$selection.size());

            int boxStartX = getX() + StorageWidget.BOX_SLOT_START_OFFSET_X;
            int boxStartY = getY() + StorageWidget.BOX_SLOT_START_OFFSET_Y + (pcGui.getDisplayOptions() ? 5 : 0);
            MultiGrabbedStorageSlot grabbedSlot = new MultiGrabbedStorageSlot(
                    boxStartX + (slot.x() * (StorageSlot.SIZE + StorageWidget.BOX_SLOT_PADDING)),
                    boxStartY + (slot.y() * (StorageSlot.SIZE + StorageWidget.BOX_SLOT_PADDING)),
                    (StorageWidget) (Object) this,
                    pokemon,
                    slot,
                    localSlot
            );
            if (moreCobblemonTweaks$selection.isEmpty()) {
                moreCobblemonTweaks$selectionOrigin = localSlot;
            }
            moreCobblemonTweaks$selectedBox = position.getBox();
            moreCobblemonTweaks$selection.add(pokemon);
            moreCobblemonTweaks$grabbedSlots.put(pokemon.getUuid(), grabbedSlot);
        } else {
            moreCobblemonTweaks$selection.remove(pokemon);
            moreCobblemonTweaks$grabbedSlots.remove(pokemon.getUuid());
            if (moreCobblemonTweaks$selection.isEmpty()) {
                moreCobblemonTweaks$selectedBox = -1;
                moreCobblemonTweaks$selectionOrigin = null;
            } else {
                moreCobblemonTweaks$selectionOrigin = moreCobblemonTweaks$grabbedSlots.values().iterator().next().localSlot;
                for (int i = 0; i < moreCobblemonTweaks$selection.size(); i++) {
                    Pokemon selected = moreCobblemonTweaks$selection.get(i);
                    MultiGrabbedStorageSlot grabbed = moreCobblemonTweaks$grabbedSlots.get(selected.getUuid());
                    grabbed.localSlot = SlotPosition.of(i);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "canDeleteSelected", cancellable = true)
    public void canDelete(CallbackInfoReturnable<Boolean> cir) {
        if (moreCobblemonTweaks$isMultiSelecting()) {
            cir.setReturnValue(!moreCobblemonTweaks$selection.isEmpty());
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true, remap = true)
    public void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (!this.visible) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean moreCobblemonTweaks$isMultiSelecting() {
        return ((MultiSelectorState) (Object) this.pcGui).moreCobblemonTweaks$isMultiSelecting();
    }

    @Override
    public boolean moreCobblemonTweaks$isSelected(PCPosition position) {
        Pokemon pokemon = this.pcGui.getPc().get(position);
        return pokemon != null && moreCobblemonTweaks$selection.contains(pokemon);
    }

    @Override
    public SlotPosition moreCobblemonTweaks$getSelectionOrigin() {
        return moreCobblemonTweaks$selectionOrigin;
    }

    @Override
    public SlotPosition moreCobblemonTweaks$getHoveredSlot(int mouseX, int mouseY) {
        for (GuiEventListener child : getChildren()) {
            if (child instanceof BoxStorageSlot boxSlot && boxSlot.isHovered(mouseX, mouseY)) {
                moreCobblemonTweaks$lastHoveredSlot = SlotPosition.of(boxSlot.getPosition());
                return moreCobblemonTweaks$lastHoveredSlot;
            }
        }
        return moreCobblemonTweaks$lastHoveredSlot;
    }

    @Override
    public List<Pokemon> moreCobblemonTweaks$getSelectedPokemon() {
        return moreCobblemonTweaks$selection;
    }

    @Override
    public void moreCobblemonTweaks$clearMultiSelection() {
        moreCobblemonTweaks$selectedBox = -1;
        moreCobblemonTweaks$selection.clear();
        moreCobblemonTweaks$grabbedSlots.clear();
        moreCobblemonTweaks$lastPosition = null;
    }

    @Shadow public abstract void setDisplayConfirmRelease(boolean b);
    @Shadow protected abstract void playSound(SoundEvent soundEvent);
    @Shadow public abstract void setupStorageSlots();
    @Shadow public abstract void resetSelected();
}
