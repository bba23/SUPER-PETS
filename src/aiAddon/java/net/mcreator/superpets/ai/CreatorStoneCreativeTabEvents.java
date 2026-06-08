package net.mcreator.superpets.ai;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.mcreator.superpets.init.SuperPetsModTabs;

public final class CreatorStoneCreativeTabEvents {
	private static boolean loaded;

	private CreatorStoneCreativeTabEvents() {
	}

	public static void load() {
		if (loaded)
			return;
		loaded = true;
		CreativeModeTabEvents.modifyOutputEvent(SuperPetsModTabs.TAB_FRIENDS).register(output -> output.accept(SuperPetsAiRegistry.CREATOR_STONE));
	}
}
