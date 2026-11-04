package io.github.johidog.klassen.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record RequirementInfo(ItemStack stack, List<Component> lines) {
}
