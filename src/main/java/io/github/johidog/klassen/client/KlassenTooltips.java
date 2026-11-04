package io.github.johidog.klassen.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class KlassenTooltips implements ItemTooltipCallback {

    private static final TagKey<Item> BOEGEN = TagKey.create(Registries.ITEM, new ResourceLocation("klassen", "boegen"));
    private static final TagKey<Item> SCHWERTER = TagKey.create(Registries.ITEM, new ResourceLocation("klassen", "schwerter"));
    private static final TagKey<Item> MAGIE_WAFFEN = TagKey.create(Registries.ITEM, new ResourceLocation("klassen", "magie_waffen"));
    private static final TagKey<Item> FARMERS_DELIGHT = TagKey.create(Registries.ITEM, new ResourceLocation("klassen", "farmers_delight"));

    @Override
    public void getTooltip(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        String reqKey = null;
        if (stack.is(BOEGEN)) reqKey = "tooltip.klassen.req.bogen";
        else if (stack.is(SCHWERTER)) reqKey = "tooltip.klassen.req.schwerter";
        else if (stack.is(MAGIE_WAFFEN)) reqKey = "tooltip.klassen.req.magie_waffen";
        else if (stack.is(FARMERS_DELIGHT)) reqKey = "tooltip.klassen.req.farmers_delight";
        else if (stack.is(Items.DIAMOND_SWORD)) reqKey = "tooltip.klassen.req.diamond_sword";

        if (reqKey == null) return;

        boolean isBow = stack.is(BOEGEN);
        if (isBow) {
            lines.add(Component.translatable("tooltip.klassen.subtitle.bow").withStyle(ChatFormatting.AQUA));
        }
        lines.add(Component.literal("──────────────").withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.translatable("tooltip.klassen.req.header")
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        lines.add(Component.translatable(reqKey).withStyle(ChatFormatting.RED));
        if (isBow) {
            lines.add(Component.empty());
            lines.add(Component.translatable("tooltip.klassen.effect.header").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            lines.add(Component.translatable("tooltip.klassen.effect.bow1").withStyle(ChatFormatting.WHITE));
            lines.add(Component.translatable("tooltip.klassen.effect.bow2").withStyle(ChatFormatting.WHITE));
            lines.add(Component.translatable("tooltip.klassen.effect.bow3").withStyle(ChatFormatting.WHITE));
            lines.add(Component.translatable("tooltip.klassen.effect.bow.cd").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        lines.add(Component.literal("――――――――――――――").withStyle(ChatFormatting.DARK_GRAY));
    }
}
