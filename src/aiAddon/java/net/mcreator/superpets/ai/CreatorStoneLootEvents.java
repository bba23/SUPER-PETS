package net.mcreator.superpets.ai;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public final class CreatorStoneLootEvents {
	private static final ResourceKey<LootTable> FRIENDSHIP_SHRINE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("super_pets", "chests/friendship_shrine"));
	private static boolean loaded;

	private CreatorStoneLootEvents() {
	}

	public static void load() {
		if (loaded)
			return;
		loaded = true;
		LootTableEvents.MODIFY.register((key, tableBuilder, source, holder) -> {
			if (!FRIENDSHIP_SHRINE.equals(key) || !source.isBuiltin())
				return;
			tableBuilder.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(LootItem.lootTableItem(SuperPetsAiRegistry.CREATOR_STONE).setWeight(1)));
		});
	}
}
