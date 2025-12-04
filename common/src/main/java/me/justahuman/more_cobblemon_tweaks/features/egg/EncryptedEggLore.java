package me.justahuman.more_cobblemon_tweaks.features.egg;

import me.justahuman.more_cobblemon_tweaks.features.LoreEnhancements;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EncryptedEggLore extends EnhancedEggLore {
    @Override
    public boolean isShiny() {
        return false;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public List<Component> getHatchProgress(List<Component> lore) {
        return List.of(LoreEnhancements.translate("egg.encryption_warning").withStyle(ChatFormatting.RED));
    }

    @Override
    public String getNature() {
        return null;
    }

    @Override
    public String getAbility() {
        return null;
    }

    @Override
    public String getForm() {
        return null;
    }

    @Override
    public String getPokeBall() {
        return null;
    }

    @Override
    public boolean hasIVs() {
        return false;
    }

    @Override
    public Integer getHpIV() {
        return null;
    }

    @Override
    public Integer getAtkIV() {
        return null;
    }

    @Override
    public Integer getDefIV() {
        return null;
    }

    @Override
    public Integer getSpAtkIV() {
        return null;
    }

    @Override
    public Integer getSpDefIV() {
        return null;
    }

    @Override
    public Integer getSpeedIV() {
        return null;
    }
}
