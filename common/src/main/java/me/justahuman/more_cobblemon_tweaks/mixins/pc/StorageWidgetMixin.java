package me.justahuman.more_cobblemon_tweaks.mixins.pc;

import com.cobblemon.mod.common.CobblemonNetwork;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.net.NetworkPacket;
import com.cobblemon.mod.common.api.storage.pc.PCPosition;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.gui.ProfileTransformType;
import com.cobblemon.mod.common.client.gui.pc.BoxStorageSlot;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.PartyStorageSlot;
import com.cobblemon.mod.common.client.gui.pc.ReleaseConfirmButton;
import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.client.render.gui.PCBoxWallpaperRepository;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.client.storage.ClientBox;
import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.net.messages.server.storage.pc.MovePCPokemonPacket;
import com.cobblemon.mod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket;
import com.cobblemon.mod.common.net.messages.server.storage.pc.RequestChangePCBoxWallpaperPacket;
import com.cobblemon.mod.common.net.messages.server.storage.pc.RequestRenamePCBoxPacket;
import com.cobblemon.mod.common.net.messages.server.storage.pc.SwapPCPokemonPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.justahuman.more_cobblemon_tweaks.MoreCobblemonTweaks;
import me.justahuman.more_cobblemon_tweaks.api.BoxViewHolder;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelector;
import me.justahuman.more_cobblemon_tweaks.api.MultiSelectorState;
import me.justahuman.more_cobblemon_tweaks.features.pc.boxes.BoxListButton;
import me.justahuman.more_cobblemon_tweaks.features.pc.boxes.BoxListSlot;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.MultiGrabbedStorageSlot;
import me.justahuman.more_cobblemon_tweaks.features.pc.multiselect.SlotPosition;
import me.justahuman.more_cobblemon_tweaks.mixins.accessor.ButtonAccessor;
import me.justahuman.more_cobblemon_tweaks.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(value = StorageWidget.class, remap = false)
public abstract class StorageWidgetMixin extends SoundlessWidget implements MultiSelector, BoxViewHolder {
    @Unique private static final int BOX_PREVIEW_SIZE = 9;
    @Unique private static final FloatingState BOX_PREVIEW_STATE = new FloatingState();

    @Shadow @Final private static ResourceLocation screenOverlayResource;
    @Shadow @Final private static ResourceLocation screenGridResource;
    @Shadow @Final private static ResourceLocation partyPanelResource;

    @Shadow @Final private PCGUI pcGui;
    @Shadow private int box;

    @Shadow @Final private ArrayList<PartyStorageSlot> partySlots;
    @Shadow @Final private ReleaseConfirmButton releaseYesButton;

    @Unique private SlotPosition moreCobblemonTweaks$lastHoveredSlot = SlotPosition.ZERO;

    @Unique private PCPosition moreCobblemonTweaks$lastPosition = null;
    @Unique private int moreCobblemonTweaks$selectedBox = -1;
    @Unique private SlotPosition moreCobblemonTweaks$selectionOrigin = null;
    @Unique private final List<Pokemon> moreCobblemonTweaks$selection = new ArrayList<>();
    @Unique private final Map<UUID, MultiGrabbedStorageSlot> moreCobblemonTweaks$grabbedSlots = new HashMap<>();

    @Unique private BoxListButton moreCobblemonTweaks$boxListButton;
    @Unique private Integer moreCobblemonTweaks$boxListSlotIndex = 0;
    @Unique private final List<BoxListSlot> moreCobblemonTweaks$boxListSlots = new ArrayList<>();
    @Unique private BoxListSlot moreCobblemonTweaks$selectedBoxListSlot = null;

    private StorageWidgetMixin(int pX, int pY, int pWidth, int pHeight, @NotNull Component component) {
        super(pX, pY, pWidth, pHeight, component);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void addCustom(CallbackInfo ci) {
        StorageWidget self = (StorageWidget) (Object) this;

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

        moreCobblemonTweaks$boxListButton = new BoxListButton(self, getX() + 194, getY() + 124);
    }

    @Inject(at = @At("HEAD"), method = "onStorageSlotClicked(Lnet/minecraft/client/gui/components/Button;)V", cancellable = true)
    public void onStorageSlotClicked(Button button, CallbackInfo ci) {
        if (!button.isHovered()) {
            ci.cancel();
            return;
        } else if (!moreCobblemonTweaks$isMultiSelecting() && !moreCobblemonTweaks$isBoxListOpen()) {
            return;
        }

        ci.cancel();
        if (moreCobblemonTweaks$isMultiSelecting()) {
            moreCobblemonTweaks$multiSelectSlotClicked(button);
        }
    }

    @Unique
    private CompletableFuture<Void> moreCobblemonTweaks$handleMultiPokemonMoves(Map<Pokemon, PCPosition> moves) {
        return moreCobblemonTweaks$handleMultiPokemonMoves(moves, false);
    }

    @Unique
    private CompletableFuture<Void> moreCobblemonTweaks$handleMultiPokemonMoves(Map<Pokemon, PCPosition> moves, boolean nested) {
        if (moves.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        if (!nested) {
            Utils.moveAllCompleted = false;
            Utils.moveAllTimeouts = 0;
            Utils.moveAllCount = 0;
            Utils.moveAllCountUpdater = () -> {
                int count = 0;
                for (Map.Entry<Pokemon, PCPosition> e : moves.entrySet()) {
                    Pokemon p = e.getKey();
                    PCPosition pos = pcGui.getPc().getPosition(p);
                    if (pos != null && pos.equals(e.getValue())) {
                        count++;
                    }
                }
                Utils.moveAllCount = count;
            };
            Utils.moveAllTotal = moves.size();
        }

        ClientPC pc = pcGui.getPc();
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (Map.Entry<Pokemon, PCPosition> entry : moves.entrySet()) {
            Pokemon pokemon = entry.getKey();
            PCPosition target = entry.getValue();

            future = future.thenComposeAsync($ -> {
                PCPosition source = pc.getPosition(pokemon);
                if (source == null || source.equals(target)) {
                    return CompletableFuture.completedFuture(null);
                }

                moreCobblemonTweaks$select(source, false);

                Pokemon targetPokemon = pc.get(target);
                NetworkPacket<?> packet = targetPokemon != null
                        ? new SwapPCPokemonPacket(pokemon.getUuid(), source, targetPokemon.getUuid(), target)
                        : new MovePCPokemonPacket(pokemon.getUuid(), source, target);

                CompletableFuture<Void> packetFuture = new CompletableFuture<>();
                Utils.moveAllPokemonFuture = packetFuture;
                CompletableFuture.delayedExecutor(50L, TimeUnit.MILLISECONDS)
                        .execute(() -> CobblemonNetwork.sendToServer(packet));
                CompletableFuture.delayedExecutor(5L, TimeUnit.SECONDS)
                        .execute(() -> {
                            if (!packetFuture.isDone()) {
                                Utils.moveAllTimeouts += 1;
                                packetFuture.complete(null);
                                MoreCobblemonTweaks.LOGGER.warn("Timeouted {} times while moving Pokémon {} in the PC. Will retry.", Utils.moveAllTimeouts, pokemon.getDisplayName(false).getString());
                            }
                        });
                return packetFuture;
            });
        }

        future = future.thenComposeAsync($ -> {
            Map<Pokemon, PCPosition> incompleteMoves = new LinkedHashMap<>();
            for (Map.Entry<Pokemon, PCPosition> entry : moves.entrySet()) {
                Pokemon pokemon = entry.getKey();
                PCPosition target = entry.getValue();
                PCPosition currentPosition = pc.getPosition(pokemon);
                if (!target.equals(currentPosition)) {
                    incompleteMoves.put(pokemon, target);
                }
            }
            return !incompleteMoves.isEmpty() && Utils.moveAllTimeouts <= 5 ? moreCobblemonTweaks$handleMultiPokemonMoves(incompleteMoves, true) : CompletableFuture.completedFuture(null);
        });

        if (!nested) {
            future = future.whenComplete(($, t) -> {
                if (t == null) {
                    playSound(CobblemonSounds.PC_DROP);
                } else {
                    MoreCobblemonTweaks.LOGGER.error("An error occurred while moving multiple Pokémon in the PC.", t);
                }
                Utils.moveAllPokemonFuture = null;
                Utils.moveAllCompleted = true;
                Utils.moveAllCount = 0;
                Utils.moveAllCountUpdater = () -> {};
                Utils.moveAllTimeouts = 0;
            });
        }
        return future;
    }

    // Multi-Selection Start

    @Inject(at = @At("TAIL"), method = "renderWidget")
    public void renderGrabbedMultiSelection(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.pose().pushPose();
        context.pose().translate(0.0F, 0.0F, 400.0F);
        for (MultiGrabbedStorageSlot slot : moreCobblemonTweaks$grabbedSlots.values()) {
            slot.render(context, mouseX,  mouseY, delta);
        }
        context.pose().popPose();
    }

    @Unique
    private void moreCobblemonTweaks$multiSelectSlotClicked(Button button) {
        if (!(button instanceof BoxStorageSlot slot)) {
            return;
        } else if (slot.getPokemon() == null || Screen.hasControlDown() || slot.getPosition().getBox() != moreCobblemonTweaks$selectedBox && moreCobblemonTweaks$selectedBox != -1) {
            boolean emptyOnly = slot.getPokemon() == null;

            ClientPC pc = this.pcGui.getPc();
            PCPosition clickedPos = slot.getPosition();
            int box = clickedPos.getBox();
            SlotPosition clicked = SlotPosition.of(clickedPos);

            Map<Pokemon, PCPosition> moves = new LinkedHashMap<>();
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

            moreCobblemonTweaks$clearMultiSelection();
            moreCobblemonTweaks$handleMultiPokemonMoves(moves);
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
                for (int i = 0; i < moreCobblemonTweaks$selection.size(); i++) {
                    Pokemon p = moreCobblemonTweaks$selection.get(i);
                    MultiGrabbedStorageSlot slot = moreCobblemonTweaks$grabbedSlots.get(p.getUuid());
                    slot.localSlot = SlotPosition.of(i);
                    if (i == 0) {
                        moreCobblemonTweaks$selectionOrigin = slot.localSlot;
                    }
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

    // Multi-Selection End

    // Box List Start

    @Inject(method = "renderWidget", at = @At("TAIL"))
    public void renderBoxListButton(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (moreCobblemonTweaks$isBoxListOpen()) {
            return;
        }

        moreCobblemonTweaks$boxListButton.render(context, mouseX, mouseY, delta);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void boxListButtonClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (moreCobblemonTweaks$boxListButton.isMouseOver(pMouseX, pMouseY)) {
            moreCobblemonTweaks$boxListButton.mouseClicked(pMouseX, pMouseY, pButton);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setBox", at = @At("HEAD"), cancellable = true)
    public void setBoxRestriction(int value, CallbackInfo ci) {
        if (!moreCobblemonTweaks$isBoxListOpen()) {
            return;
        }

        int startingBox = value * 30;
        if (startingBox >= pcGui.getPc().getBoxes().size()) {
            ((StorageWidget) (Object) this).setBox(0);
            ci.cancel();
        } else if (startingBox < 0) {
            int lastBoxIndex = (pcGui.getPc().getBoxes().size() - 1) / 30;
            ((StorageWidget) (Object) this).setBox(lastBoxIndex);
            ci.cancel();
        }
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    public void renderBoxList(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!moreCobblemonTweaks$isBoxListOpen()) {
            return;
        }

        ci.cancel();
        PoseStack matrices = context.pose();
        if (pcGui.getConfiguration().getShowParty()) {
            GuiUtilsKt.blitk(
                    matrices,
                    partyPanelResource,
                    getX() + 182,
                    getY() - 19,
                    PCGUI.RIGHT_PANEL_HEIGHT,
                    PCGUI.RIGHT_PANEL_WIDTH
            );

            drawScaledText(
                    context,
                    CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                    LocalizationUtilsKt.lang("ui.party").withStyle(ChatFormatting.BOLD),
                    getX() + 213,
                    getY() - 15.5,
                    true,
                    true
            );
        }

        GuiUtilsKt.blitk(
                matrices,
                screenGridResource,
                getX() + 7,
                getY() + (pcGui.getDisplayOptions() ? 16 : 11),
                133,
                160
        );

        GuiUtilsKt.blitk(
                matrices,
                screenOverlayResource,
                getX(),
                getY(),
                StorageWidget.SCREEN_HEIGHT,
                StorageWidget.SCREEN_WIDTH
        );

        for (BoxListSlot boxSlot : moreCobblemonTweaks$boxListSlots) {
            boxSlot.render(context, mouseX, mouseY, delta);
        }

        BoxListSlot previewed = moreCobblemonTweaks$getPreviewedBoxListSlot(mouseX, mouseY);
        if (previewed != null) {
            ClientBox box = previewed.getBox();
            if (box != null) {
                int pcGuiX = ((pcGui.width - PCGUI.BASE_WIDTH) / 2);
                int pcGuiY = ((pcGui.height - PCGUI.BASE_HEIGHT) / 2);
                MutableComponent boxName = Component.translatable("cobblemon.ui.pc.box.title", previewed.boxIndex + 1).withStyle(ChatFormatting.BOLD);
                if (box.getName() != null) {
                    String clipped = box.getName().getString();
                    if (clipped.length() > 13) {
                        clipped = clipped.substring(0, 13) + "...";
                    }
                    boxName = Component.literal(clipped).withStyle(ChatFormatting.BOLD);
                }
                drawScaledText(
                        context,
                        CobblemonResources.INSTANCE.getDEFAULT_LARGE(),
                        boxName,
                        pcGuiX + 5,
                        pcGuiY + 11.5,
                        false,
                        true
                );

                int previewX = pcGuiX + 6;
                int previewY = pcGuiY  + 27;
                List<Pokemon> slots = box.getSlots();
                int index = 0;
                for (int row = 1; row <= 5; row++) {
                    for (int col = 1; col <= 6; col++) {
                        Pokemon pokemon = slots.get(index++);
                        if (pokemon == null) {
                            continue;
                        }

                        // x + offset + (col - 1) * (slot size + spacing) + extra pixel space between 3rd and 4th column
                        int pokemonX = previewX + 3 + (col - 1) * (BOX_PREVIEW_SIZE + 1) + (col > 3 ? 1 : 0);
                        // y + offset + (row - 1) * (slot size + spacing)
                        int pokemonY = previewY + 6 + (row - 1) * (BOX_PREVIEW_SIZE + 2);

                        context.enableScissor(
                                pokemonX - 2,
                                pokemonY + 2,
                                pokemonX + BOX_PREVIEW_SIZE + 4,
                                pokemonY + BOX_PREVIEW_SIZE + 4
                        );

                        matrices.pushPose();
                        matrices.translate(pokemonX + (BOX_PREVIEW_SIZE / 2.0), pokemonY + 1, 0);
                        matrices.scale(2.5f, 2.5f, 1F);

                        PokemonGuiUtilsKt.drawProfilePokemon(
                                pokemon.asRenderablePokemon(),
                                matrices,
                                QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, 35F, 0F)),
                                PoseType.PROFILE,
                                BOX_PREVIEW_STATE,
                                0F,
                                1.62F,
                                ProfileTransformType.PROFILE,
                                false,
                                1f,
                                1f,
                                1f,
                                1f,
                                0f,
                                0f,
                                13
                        );
                        matrices.popPose();
                        context.disableScissor();
                    }
                }
            }
        }

        if (pcGui.getConfiguration().getShowParty()) {
            for (PartyStorageSlot partySlot : partySlots) {
                partySlot.render(context, mouseX, mouseY, delta);
            }
        }

        moreCobblemonTweaks$boxListButton.render(context, mouseX, mouseY, delta);
    }

    @Unique
    private void moreCobblemonTweaks$boxListSlotClicked(BoxListSlot slot, int button) {
        if (!slot.shouldRender()) {
            return;
        }

        if (button == 1 || Screen.hasShiftDown()) {
            moreCobblemonTweaks$boxListButton.mouseClicked(moreCobblemonTweaks$boxListButton.getX() + 1, moreCobblemonTweaks$boxListButton.getY() + 1, button);
            ((StorageWidget) (Object) this).setBox(slot.boxIndex);
            moreCobblemonTweaks$selectedBoxListSlot = null;
            return;
        }

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(CobblemonSounds.PC_CLICK, 1.0F));
        if (moreCobblemonTweaks$selectedBoxListSlot != null && moreCobblemonTweaks$selectedBoxListSlot.shouldRender()) {
            if (moreCobblemonTweaks$selectedBoxListSlot != slot) {
                int fromBoxIndex = moreCobblemonTweaks$selectedBoxListSlot.boxIndex;
                int toBoxIndex = slot.boxIndex;
                ClientPC pc = this.pcGui.getPc();
                ClientBox fromBox = moreCobblemonTweaks$selectedBoxListSlot.getBox();
                ClientBox toBox = slot.getBox();
                if (fromBox == null || toBox == null) {
                    moreCobblemonTweaks$selectedBoxListSlot = null;
                    return;
                }

                String fromBoxName = fromBox.getName() != null ? fromBox.getName().getString() : null;
                String toBoxName = toBox.getName() != null ? toBox.getName().getString() : null;
                if (!Objects.equals(fromBoxName, toBoxName)) {
                    new RequestRenamePCBoxPacket(pc.getUuid(), fromBoxIndex, toBoxName).sendToServer();
                    new RequestRenamePCBoxPacket(pc.getUuid(), toBoxIndex, fromBoxName).sendToServer();
                }

                ResourceLocation fromBoxWallpaper = fromBox.getWallpaper();
                ResourceLocation toBoxWallpaper = toBox.getWallpaper();
                if (!Objects.equals(fromBoxWallpaper, toBoxWallpaper)) {
                    ResourceLocation altFromBoxWallpaper = null;
                    ResourceLocation altToBoxWallpaper = null;
                    for (var wallpaperData : PCBoxWallpaperRepository.allWallpapers) {
                        if (fromBoxWallpaper.equals(wallpaperData.getFirst())) {
                            altFromBoxWallpaper = wallpaperData.getSecond();
                        } else if (toBoxWallpaper.equals(wallpaperData.getSecond())) {
                            altToBoxWallpaper = wallpaperData.getFirst();
                        }
                    }
                    new RequestChangePCBoxWallpaperPacket(pc.getUuid(), fromBoxIndex, toBoxWallpaper, altToBoxWallpaper).sendToServer();
                    new RequestChangePCBoxWallpaperPacket(pc.getUuid(), toBoxIndex, fromBoxWallpaper, altFromBoxWallpaper).sendToServer();
                }

                Map<Pokemon, PCPosition> moves = new LinkedHashMap<>();
                List<Pokemon> fromSlots = fromBox.getSlots();
                List<Pokemon> toSlots = toBox.getSlots();
                for (int i = 0; i < fromSlots.size(); i++) {
                    Pokemon pokemon = fromSlots.get(i);
                    if (pokemon != null) {
                        moves.put(pokemon, new PCPosition(toBoxIndex, i));
                    }
                }
                for (int i = 0; i < toSlots.size(); i++) {
                    Pokemon pokemon = toSlots.get(i);
                    if (pokemon != null) {
                        moves.put(pokemon, new PCPosition(fromBoxIndex, i));
                    }
                }
                moreCobblemonTweaks$handleMultiPokemonMoves(moves);
            }
            moreCobblemonTweaks$selectedBoxListSlot = null;
            return;
        }

        moreCobblemonTweaks$selectedBoxListSlot = slot;
    }

    @WrapOperation(method = "setupStorageSlots", at = @At(value = "NEW", target = "com/cobblemon/mod/common/client/gui/pc/BoxStorageSlot"))
    public BoxStorageSlot setupBoxListSlotForEachBoxSlot(int x, int y, StorageWidget parent, ClientPC pc, PCPosition position, Button.OnPress onPress, Operation<BoxStorageSlot> original) {
        BoxListSlot boxListSlot = new BoxListSlot(
                parent,
                moreCobblemonTweaks$boxListSlotIndex++,
                x,
                y,
                (slot, button) -> moreCobblemonTweaks$boxListSlotClicked(slot, button)
        );
        addWidget(boxListSlot);
        moreCobblemonTweaks$boxListSlots.add(boxListSlot);
        return original.call(x, y, parent, pc, position, onPress);
    }

    @Inject(method = "resetStorageSlots", at = @At("TAIL"))
    public void resetBoxListSlots(CallbackInfo ci) {
        this.moreCobblemonTweaks$boxListSlots.forEach(widget -> removeWidget(widget));
        this.moreCobblemonTweaks$boxListSlots.clear();
        moreCobblemonTweaks$boxListSlotIndex = 30 * box;
    }

    @Override
    public boolean moreCobblemonTweaks$isBoxListOpen() {
        return moreCobblemonTweaks$boxListButton.isToggled();
    }

    @Override
    public BoxListSlot moreCobblemonTweaks$getSelectedBoxListSlot() {
        return moreCobblemonTweaks$selectedBoxListSlot;
    }

    @Override
    public BoxListSlot moreCobblemonTweaks$getPreviewedBoxListSlot(double mouseX, double mouseY) {
        BoxListSlot previewed = moreCobblemonTweaks$getSelectedBoxListSlot();
        for (BoxListSlot boxSlot : moreCobblemonTweaks$boxListSlots) {
            if (boxSlot.isHovered(mouseX, mouseY) && boxSlot.shouldRender()) {
                previewed = boxSlot;
            }
        }
        return previewed;
    }

    // Box List End

    private void drawScaledText(GuiGraphics context, ResourceLocation font, MutableComponent text, double x, double y, boolean centered, boolean shadow) {
        RenderHelperKt.drawScaledText(
                context,
                font,
                text,
                x,
                y,
                1F,
                1F,
                Integer.MAX_VALUE,
                0xFFFFFFFF,
                centered,
                shadow,
                null,
                null
        );
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true, remap = true)
    public void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (!this.visible) {
            cir.setReturnValue(false);
        }
    }

    @Shadow public abstract void setDisplayConfirmRelease(boolean b);
    @Shadow protected abstract void playSound(SoundEvent soundEvent);
    @Shadow public abstract void setupStorageSlots();
    @Shadow public abstract void resetSelected();
    @Shadow public abstract boolean canDeleteSelected();
}
