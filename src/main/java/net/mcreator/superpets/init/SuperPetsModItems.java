/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.superpets.init;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;

import net.mcreator.superpets.item.StonebutwirdItem;
import net.mcreator.superpets.block.AncientresienredBlock;
import net.mcreator.superpets.block.AncientresienpurpleBlock;
import net.mcreator.superpets.block.AncientresienpinkBlock;
import net.mcreator.superpets.block.AncientresiengreenBlock;
import net.mcreator.superpets.SuperPetsMod;

import java.util.function.Function;

public class SuperPetsModItems {
	public static Item ANCIENTRESIENPINK;
	public static Item ANCIENTRESIENPURPLE;
	public static Item ANCIENTRESIENGREEN;
	public static Item ANCIENTRESIENRED;
	public static Item STONEBUTWIRD;

	public static void load() {
		ANCIENTRESIENPINK = register("ancientresienpink", properties -> new AncientresienpinkBlock.Item(properties.stacksTo(6).rarity(Rarity.UNCOMMON)));
		ANCIENTRESIENPURPLE = register("ancientresienpurple", properties -> new AncientresienpurpleBlock.Item(properties.stacksTo(6).rarity(Rarity.UNCOMMON)));
		ANCIENTRESIENGREEN = register("ancientresiengreen", properties -> new AncientresiengreenBlock.Item(properties.stacksTo(6).rarity(Rarity.UNCOMMON)));
		ANCIENTRESIENRED = register("ancientresienred", properties -> new AncientresienredBlock.Item(properties.stacksTo(6).rarity(Rarity.UNCOMMON)));
		STONEBUTWIRD = register("stonebutwird", StonebutwirdItem::new);
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static <I extends Item> I register(String name, Function<Item.Properties, ? extends I> supplier) {
		return (I) Items.registerItem(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SuperPetsMod.MODID, name)), (Function<Item.Properties, Item>) supplier);
	}
}