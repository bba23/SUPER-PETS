package net.mcreator.superpets.friendship;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.mcreator.superpets.SuperPetsMod;

import java.util.function.Function;

public final class FriendshipRelicsRegistry {
	public static Block ANCIENT_RESIN_SLAB;
	public static Block ANCIENT_RESIN_SLAB_GREEN;
	public static Block ANCIENT_RESIN_SLAB_PURPLE;
	public static Block ANCIENT_RESIN_SLAB_PINK;
	public static Item ANCIENT_RESIN_SLAB_ITEM;
	public static Item ANCIENT_RESIN_SLAB_GREEN_ITEM;
	public static Item ANCIENT_RESIN_SLAB_PURPLE_ITEM;
	public static Item ANCIENT_RESIN_SLAB_PINK_ITEM;
	public static Item LINKED_FRIENDSHIP_STONE;
	public static Item FRIENDSHIP_NECKLACE;
	public static Item BONDING_SOUL;
	public static Item GIANT_SOUL_STONE;
	public static Item GIANT_LINKED_FRIENDSHIP_STONE;
	public static Item MASTER_STONE;

	private static boolean loaded;

	private FriendshipRelicsRegistry() {
	}

	public static void load() {
		if (loaded)
			return;
		loaded = true;
		FriendshipRelicsConfig.load();
		ANCIENT_RESIN_SLAB = registerAncientResinSlab("ancient_resin_slab");
		ANCIENT_RESIN_SLAB_GREEN = registerAncientResinSlab("ancient_resin_slab_green");
		ANCIENT_RESIN_SLAB_PURPLE = registerAncientResinSlab("ancient_resin_slab_purple");
		ANCIENT_RESIN_SLAB_PINK = registerAncientResinSlab("ancient_resin_slab_pink");
		ANCIENT_RESIN_SLAB_ITEM = registerAncientResinSlabItem("ancient_resin_slab", ANCIENT_RESIN_SLAB);
		ANCIENT_RESIN_SLAB_GREEN_ITEM = registerAncientResinSlabItem("ancient_resin_slab_green", ANCIENT_RESIN_SLAB_GREEN);
		ANCIENT_RESIN_SLAB_PURPLE_ITEM = registerAncientResinSlabItem("ancient_resin_slab_purple", ANCIENT_RESIN_SLAB_PURPLE);
		ANCIENT_RESIN_SLAB_PINK_ITEM = registerAncientResinSlabItem("ancient_resin_slab_pink", ANCIENT_RESIN_SLAB_PINK);
		LINKED_FRIENDSHIP_STONE = registerItem("linked_friendship_stone", properties -> new LinkedFriendshipStoneItem(properties.stacksTo(2).rarity(Rarity.RARE)));
		FRIENDSHIP_NECKLACE = registerItem("friendship_necklace", properties -> new FriendshipNecklaceItem(properties.stacksTo(1).rarity(Rarity.UNCOMMON)));
		BONDING_SOUL = registerItem("bonding_soul", properties -> new Item(properties.stacksTo(16).rarity(Rarity.RARE)));
		GIANT_SOUL_STONE = registerItem("giant_soul_stone", properties -> new Item(properties.stacksTo(16).rarity(Rarity.EPIC)));
		GIANT_LINKED_FRIENDSHIP_STONE = registerItem("giant_linked_friendship_stone", properties -> new GiantLinkedFriendshipStoneItem(properties.stacksTo(1).rarity(Rarity.EPIC)));
		MASTER_STONE = registerItem("master_stone", properties -> new MasterStoneItem(properties.stacksTo(1).rarity(Rarity.EPIC).fireResistant()));
	}

	private static AncientResinSlabBlock registerAncientResinSlab(String name) {
		return registerBlock(name, properties -> new AncientResinSlabBlock(properties.sound(SoundType.STONE).strength(2.2f, 8f).friction(FriendshipRelicsConfig.slabSlipperiness).speedFactor(FriendshipRelicsConfig.slabSpeedFactor)));
	}

	private static AncientResinSlabItem registerAncientResinSlabItem(String name, Block block) {
		return registerItem(name, properties -> new AncientResinSlabItem(block, properties.stacksTo(16).rarity(Rarity.UNCOMMON)));
	}

	private static <B extends Block> B registerBlock(String name, Function<BlockBehaviour.Properties, B> supplier) {
		return (B) Blocks.register(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(SuperPetsMod.MODID, name)), (Function<BlockBehaviour.Properties, Block>) supplier, BlockBehaviour.Properties.of());
	}

	private static <I extends Item> I registerItem(String name, Function<Item.Properties, ? extends I> supplier) {
		return (I) Items.registerItem(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SuperPetsMod.MODID, name)), (Function<Item.Properties, Item>) supplier);
	}
}
