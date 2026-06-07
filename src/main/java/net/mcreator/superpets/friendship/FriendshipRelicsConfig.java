package net.mcreator.superpets.friendship;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class FriendshipRelicsConfig {
	public static int shrineSpacing = 40;
	public static int shrineSeparation = 16;
	public static double stoneOfFriendshipLootChance = 0.18d;
	public static int giantSoulStoneStoneCost = 4;
	public static double standardPetScale = 4.0d;
	public static double giantPetScale = 6.0d;
	public static double mountSpeedMultiplier = 1.0d;
	public static float resinJumpFactor = 0.25f;
	public static float resinSlipperiness = 0.98f;
	public static float resinSpeedFactor = 1.35f;
	public static float slabSlipperiness = resinSlipperiness;
	public static float slabSpeedFactor = resinSpeedFactor;
	public static boolean creatorStoneAiEnabled = false;
	public static String creatorStoneAiHelperCommand = "";
	public static String creatorStoneGeneratedAssetRoot = "config/super_pets/generated_ai";
	public static int creatorStoneAnvilCost = 4;

	private FriendshipRelicsConfig() {
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("friendship_relics.properties");
		Properties properties = defaults();
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				properties.load(reader);
			} catch (IOException ignored) {
			}
		} else {
			try {
				Files.createDirectories(path.getParent());
				try (Writer writer = Files.newBufferedWriter(path)) {
					properties.store(writer, "Friendship Relics base config");
				}
			} catch (IOException ignored) {
			}
		}
		shrineSpacing = readInt(properties, "shrine_spacing", shrineSpacing);
		shrineSeparation = readInt(properties, "shrine_separation", shrineSeparation);
		stoneOfFriendshipLootChance = readDouble(properties, "stone_of_friendship_loot_chance", stoneOfFriendshipLootChance);
		giantSoulStoneStoneCost = readInt(properties, "giant_soul_stone_stone_cost", giantSoulStoneStoneCost);
		standardPetScale = readDouble(properties, "standard_pet_scale", standardPetScale);
		giantPetScale = readDouble(properties, "giant_pet_scale", giantPetScale);
		mountSpeedMultiplier = readDouble(properties, "mount_speed_multiplier", mountSpeedMultiplier);
		resinJumpFactor = (float) readDouble(properties, "ancient_resin_jump_factor", resinJumpFactor);
		resinSlipperiness = (float) readDouble(properties, "ancient_resin_slipperiness", readDouble(properties, "ancient_resin_slab_slipperiness", resinSlipperiness));
		resinSpeedFactor = (float) readDouble(properties, "ancient_resin_speed_factor", readDouble(properties, "ancient_resin_slab_speed_factor", resinSpeedFactor));
		slabSlipperiness = (float) readDouble(properties, "ancient_resin_slab_slipperiness", resinSlipperiness);
		slabSpeedFactor = (float) readDouble(properties, "ancient_resin_slab_speed_factor", resinSpeedFactor);
		creatorStoneAiEnabled = readBoolean(properties, "creator_stone_ai_enabled", creatorStoneAiEnabled);
		creatorStoneAiHelperCommand = properties.getProperty("creator_stone_ai_helper_command", creatorStoneAiHelperCommand).trim();
		creatorStoneGeneratedAssetRoot = properties.getProperty("creator_stone_generated_asset_root", creatorStoneGeneratedAssetRoot).trim();
		creatorStoneAnvilCost = readInt(properties, "creator_stone_anvil_cost", creatorStoneAnvilCost);
	}

	private static Properties defaults() {
		Properties properties = new Properties();
		properties.setProperty("shrine_spacing", String.valueOf(shrineSpacing));
		properties.setProperty("shrine_separation", String.valueOf(shrineSeparation));
		properties.setProperty("stone_of_friendship_loot_chance", String.valueOf(stoneOfFriendshipLootChance));
		properties.setProperty("giant_soul_stone_stone_cost", String.valueOf(giantSoulStoneStoneCost));
		properties.setProperty("standard_pet_scale", String.valueOf(standardPetScale));
		properties.setProperty("giant_pet_scale", String.valueOf(giantPetScale));
		properties.setProperty("mount_speed_multiplier", String.valueOf(mountSpeedMultiplier));
		properties.setProperty("ancient_resin_jump_factor", String.valueOf(resinJumpFactor));
		properties.setProperty("ancient_resin_slipperiness", String.valueOf(resinSlipperiness));
		properties.setProperty("ancient_resin_speed_factor", String.valueOf(resinSpeedFactor));
		properties.setProperty("ancient_resin_slab_slipperiness", String.valueOf(slabSlipperiness));
		properties.setProperty("ancient_resin_slab_speed_factor", String.valueOf(slabSpeedFactor));
		properties.setProperty("creator_stone_ai_enabled", String.valueOf(creatorStoneAiEnabled));
		properties.setProperty("creator_stone_ai_helper_command", creatorStoneAiHelperCommand);
		properties.setProperty("creator_stone_generated_asset_root", creatorStoneGeneratedAssetRoot);
		properties.setProperty("creator_stone_anvil_cost", String.valueOf(creatorStoneAnvilCost));
		return properties;
	}

	private static int readInt(Properties properties, String key, int fallback) {
		try {
			return Integer.parseInt(properties.getProperty(key, String.valueOf(fallback)).trim());
		} catch (NumberFormatException ignored) {
			return fallback;
		}
	}

	private static double readDouble(Properties properties, String key, double fallback) {
		try {
			return Double.parseDouble(properties.getProperty(key, String.valueOf(fallback)).trim());
		} catch (NumberFormatException ignored) {
			return fallback;
		}
	}

	private static boolean readBoolean(Properties properties, String key, boolean fallback) {
		String value = properties.getProperty(key, String.valueOf(fallback)).trim();
		if ("true".equalsIgnoreCase(value))
			return true;
		if ("false".equalsIgnoreCase(value))
			return false;
		return fallback;
	}
}
