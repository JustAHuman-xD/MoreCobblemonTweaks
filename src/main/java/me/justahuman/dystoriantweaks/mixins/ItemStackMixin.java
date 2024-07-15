package me.justahuman.dystoriantweaks.mixins;

import me.justahuman.dystoriantweaks.Utils;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.Formatting.*;

@Mixin(value = ItemStack.class, priority = 1000000)
public abstract class ItemStackMixin {
    @Shadow @Nullable
    public abstract NbtCompound getNbt();
    @Shadow
    public abstract Item getItem();


    @Inject(method = "getTooltip", at = @At(value = "RETURN"))
    public void changeTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        NbtCompound nbt = getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        final List<Text> lore = cir.getReturnValue();
        final List<Text> additionalLore = new ArrayList<>();
        final String polymerItem = nbt.get("Polymer$itemId") instanceof NbtString nbtString ? nbtString.asString() : null;
        final NbtCompound customData = nbt.get("Polymer$itemTag") instanceof NbtCompound compound ? compound : null;
        if ("huliscobblebreeding:pokemonegg".equals(polymerItem)) {
            changeIdentifier(lore, "cobblemon:pokemon_egg", "Cobblemon");
            if (customData != null) {
                Text name = lore.get(0);
                final boolean shiny = Utils.get(customData, "Shiny", false);
                if (shiny) {
                    name = name.copy().append(Text.literal(" ★").formatted(YELLOW, BOLD));
                }
                final String gender = Utils.get(customData, "Gender", "NONE");
                if (gender.equals("MALE") || gender.equals("FEMALE")) {
                    boolean male = gender.equals("MALE");
                    name = name.copy().append(Text.literal(male ? " ♂" : " ♀")
                            .styled(style -> style.withColor(male ? 0x32CBFF : 0xFC5454)));
                }
                lore.set(0, name);

                final int cycles = Utils.get(customData, "currentEggCycle", -1);
                final double steps = Utils.get(customData, "stepsLeftInCycle", -1d);
                boolean spacer = false;

                if (cycles != -1) {
                    additionalLore.add(Text.literal("Egg Cycles Remaining: ").formatted(GREEN)
                            .append(Text.literal(String.valueOf(cycles)).formatted(WHITE)));
                    spacer = true;
                }
                if (steps != -1) {
                    additionalLore.add(Text.literal("Steps Left: ").formatted(AQUA)
                            .append(Text.literal(String.valueOf(Math.round(steps))).formatted(WHITE)));
                    spacer = true;
                }

                String nature = Utils.get(customData, "Nature", "");
                NbtCompound ability = customData.get("Ability") instanceof NbtCompound compound ? compound : null;
                String abilityName = Utils.get(ability, "AbilityName", "");
                String form = Utils.get(customData, "FormId", "");
                if ((!nature.isBlank() || !abilityName.isBlank() || !form.isBlank()) && spacer) {
                    additionalLore.add(Text.literal(" "));
                    spacer = false;
                }

                if (!nature.isBlank()) {
                    if (nature.contains(":")) {
                        nature = StringUtil.capitalize(nature.substring(nature.indexOf(':') + 1));
                    }
                    additionalLore.add(Text.literal("Nature: ").formatted(YELLOW)
                            .append(Text.literal(nature).formatted(WHITE)));
                    spacer = true;
                }

                if (!abilityName.isBlank()) {
                    additionalLore.add(Text.literal("Ability: ").formatted(GOLD)
                            .append(Text.literal(StringUtil.capitalize(abilityName)).formatted(WHITE)));
                    spacer = true;
                }

                if (!form.isBlank()) {
                    additionalLore.add(Text.literal("Form: ").formatted(WHITE)
                            .append(Text.literal(StringUtil.capitalize(form))));
                    spacer = true;
                }

                NbtCompound ivs = customData.get("IVs") instanceof NbtCompound compound ? compound : null;
                if (ivs != null) {
                    short hp = Utils.get(ivs, "hp", (short) -1);
                    short attack = Utils.get(ivs, "attack", (short) -1);
                    short defense = Utils.get(ivs, "defence", (short) -1);
                    short spAttack = Utils.get(ivs, "special_attack", (short) -1);
                    short spDefense = Utils.get(ivs, "special_defence", (short) -1);
                    short speed = Utils.get(ivs, "speed", (short) -1);

                    if (spacer) {
                        additionalLore.add(Text.literal(" "));
                    }

                    if (hp != -1) {
                        additionalLore.add(Text.literal("HP: ").formatted(RED)
                                .append(Text.literal(String.valueOf(hp)).formatted(WHITE)));
                    }
                    if (attack != -1) {
                        additionalLore.add(Text.literal("Attack: ").formatted(BLUE)
                                .append(Text.literal(String.valueOf(attack)).formatted(WHITE)));
                    }
                    if (defense != -1) {
                        additionalLore.add(Text.literal("Defense: ").formatted(GRAY)
                                .append(Text.literal(String.valueOf(defense)).formatted(WHITE)));
                    }
                    if (spAttack != -1) {
                        additionalLore.add(Text.literal("Sp. Attack: ").formatted(AQUA)
                                .append(Text.literal(String.valueOf(spAttack)).formatted(WHITE)));
                    }
                    if (spDefense != -1) {
                        additionalLore.add(Text.literal("Sp. Defense: ").formatted(YELLOW)
                                .append(Text.literal(String.valueOf(spDefense)).formatted(WHITE)));
                    }
                    if (speed != -1) {
                        additionalLore.add(Text.literal("Speed: ").formatted(GREEN)
                                .append(Text.literal(String.valueOf(speed)).formatted(WHITE)));
                    }
                }
            }
        }

        final String key = Registries.ITEM.getId(getItem()).toString();
        switch (key) {
            case "cobblemon:tamato_berry" -> friendStatBerry(additionalLore, "speed");
            case "cobblemon:grepa_berry" -> friendStatBerry(additionalLore, "sp. defense");
            case "cobblemon:hondew_berry" -> friendStatBerry(additionalLore, "sp. attack");
            case "cobblemon:qualot_berry" -> friendStatBerry(additionalLore, "defense");
            case "cobblemon:pomeg_berry" -> friendStatBerry(additionalLore, "hp");
            case "cobblemon:kelpsey_berry" -> friendStatBerry(additionalLore, "attack");
            case "cobblemon:hopo_berry", "cobblemon:leppa_berry"
                    -> additionalLore.add(Text.literal("Restores a selected move's PP when fed").formatted(GRAY));
        }

        lore.addAll(1, additionalLore);
    }

    @Unique
    public void friendStatBerry(List<Text> lore, String ev) {
        lore.add(Text.literal("Lowers %s EV when fed".formatted(ev)).formatted(GRAY));
        lore.add(Text.literal("Increases friendship when fed").formatted(GRAY));
    }

    @Unique
    public void changeIdentifier(List<Text> lore, String newIdentifier, String newSource) {
        final Identifier identifier = Registries.ITEM.getId(getItem());
        final String idLine = identifier.toString();
        for (int i = 0; i < lore.size(); i++) {
            final String line = lore.get(i).getString();
            if (line.equals(idLine)) {
                lore.set(i, Text.literal(newIdentifier).formatted(DARK_GRAY));
            } else if (line.equals("Minecraft")) {
                lore.set(i, Text.literal(newSource).formatted(BLUE).formatted(ITALIC));
            }
        }
    }
}
