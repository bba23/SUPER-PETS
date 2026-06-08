package net.mcreator.superpets.ai;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuperPetsAiMod implements ModInitializer {
	public static final String MODID = "super_pets_ai";
	public static final Logger LOGGER = LogManager.getLogger(SuperPetsAiMod.class);

	@Override
	public void onInitialize() {
		CreatorStoneAiConfig.load();
		SuperPetsAiRegistry.load();
		CreatorStoneCreativeTabEvents.load();
		CreatorStoneLootEvents.load();
		LOGGER.info("Initialized Super Pets AI optional add-on");
	}
}
