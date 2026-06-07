/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.superpets.init;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;

import net.mcreator.superpets.block.AncientresienredBlock;
import net.mcreator.superpets.block.AncientresienpurpleBlock;
import net.mcreator.superpets.block.AncientresienpinkBlock;
import net.mcreator.superpets.block.AncientresiengreenBlock;
import net.mcreator.superpets.SuperPetsMod;

import java.util.function.Function;

public class SuperPetsModBlocks {
	public static Block ANCIENTRESIENPINK;
	public static Block ANCIENTRESIENPURPLE;
	public static Block ANCIENTRESIENGREEN;
	public static Block ANCIENTRESIENRED;

	public static void load() {
		ANCIENTRESIENPINK = register("ancientresienpink", AncientresienpinkBlock::new);
		ANCIENTRESIENPURPLE = register("ancientresienpurple", AncientresienpurpleBlock::new);
		ANCIENTRESIENGREEN = register("ancientresiengreen", AncientresiengreenBlock::new);
		ANCIENTRESIENRED = register("ancientresienred", AncientresienredBlock::new);
	}

	// Start of user code block custom blocks
	// End of user code block custom blocks
	private static <B extends Block> B register(String name, Function<BlockBehaviour.Properties, B> supplier) {
		return (B) Blocks.register(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(SuperPetsMod.MODID, name)), (Function<BlockBehaviour.Properties, Block>) supplier, BlockBehaviour.Properties.of());
	}
}