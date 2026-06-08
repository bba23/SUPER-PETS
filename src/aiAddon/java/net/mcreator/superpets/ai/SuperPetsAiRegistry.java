package net.mcreator.superpets.ai;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public final class SuperPetsAiRegistry {
	public static Item CREATOR_STONE;

	private static boolean loaded;

	private SuperPetsAiRegistry() {
	}

	public static void load() {
		if (loaded)
			return;
		loaded = true;
		CREATOR_STONE = registerItem("creator_stone", properties -> new CreatorStoneItem(properties.stacksTo(1).rarity(Rarity.EPIC).fireResistant()));
	}

	private static <I extends Item> I registerItem(String name, Function<Item.Properties, ? extends I> supplier) {
		return (I) Items.registerItem(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SuperPetsAiMod.MODID, name)), (Function<Item.Properties, Item>) supplier);
	}
}
