/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.superpets.init;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;

import net.mcreator.superpets.SuperPetsMod;
import net.mcreator.superpets.friendship.FriendshipRelicsRegistry;

public class SuperPetsModTabs {
	public static ResourceKey<CreativeModeTab> TAB_FRIENDS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(SuperPetsMod.MODID, "friends"));

	public static void load() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_FRIENDS,
				CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).title(Component.translatable("item_group.super_pets.friends")).icon(() -> new ItemStack(Items.AXOLOTL_BUCKET)).displayItems((parameters, tabData) -> {
					tabData.accept(SuperPetsModBlocks.ANCIENTRESIENPINK.asItem());
					tabData.accept(SuperPetsModBlocks.ANCIENTRESIENPURPLE.asItem());
					tabData.accept(SuperPetsModBlocks.ANCIENTRESIENGREEN.asItem());
					tabData.accept(SuperPetsModBlocks.ANCIENTRESIENRED.asItem());
					tabData.accept(SuperPetsModItems.STONEBUTWIRD);
					tabData.accept(FriendshipRelicsRegistry.ANCIENT_RESIN_SLAB_ITEM);
					tabData.accept(FriendshipRelicsRegistry.ANCIENT_RESIN_SLAB_GREEN_ITEM);
					tabData.accept(FriendshipRelicsRegistry.ANCIENT_RESIN_SLAB_PURPLE_ITEM);
					tabData.accept(FriendshipRelicsRegistry.ANCIENT_RESIN_SLAB_PINK_ITEM);
					tabData.accept(FriendshipRelicsRegistry.LINKED_FRIENDSHIP_STONE);
					tabData.accept(FriendshipRelicsRegistry.FRIENDSHIP_NECKLACE);
					tabData.accept(FriendshipRelicsRegistry.BONDING_SOUL);
					tabData.accept(FriendshipRelicsRegistry.GIANT_SOUL_STONE);
					tabData.accept(FriendshipRelicsRegistry.GIANT_LINKED_FRIENDSHIP_STONE);
					tabData.accept(FriendshipRelicsRegistry.MASTER_STONE);
					tabData.accept(FriendshipRelicsRegistry.CREATOR_STONE);
				}).build());
	}
}
