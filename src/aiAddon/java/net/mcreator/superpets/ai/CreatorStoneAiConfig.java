package net.mcreator.superpets.ai;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class CreatorStoneAiConfig {
	public static boolean aiEnabled = false;
	public static String aiHelperCommand = "";
	public static String generatedAssetRoot = "config/super_pets_ai/generated_ai";
	public static int anvilCost = 4;

	private CreatorStoneAiConfig() {
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("super_pets_ai.properties");
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
					properties.store(writer, "SUPER PETS AI optional add-on config");
				}
			} catch (IOException ignored) {
			}
		}
		aiEnabled = readBoolean(properties, "creator_stone_ai_enabled", aiEnabled);
		aiHelperCommand = properties.getProperty("creator_stone_ai_helper_command", aiHelperCommand).trim();
		generatedAssetRoot = properties.getProperty("creator_stone_generated_asset_root", generatedAssetRoot).trim();
		anvilCost = readInt(properties, "creator_stone_anvil_cost", anvilCost);
	}

	private static Properties defaults() {
		Properties properties = new Properties();
		properties.setProperty("creator_stone_ai_enabled", String.valueOf(aiEnabled));
		properties.setProperty("creator_stone_ai_helper_command", aiHelperCommand);
		properties.setProperty("creator_stone_generated_asset_root", generatedAssetRoot);
		properties.setProperty("creator_stone_anvil_cost", String.valueOf(anvilCost));
		return properties;
	}

	private static int readInt(Properties properties, String key, int fallback) {
		try {
			return Integer.parseInt(properties.getProperty(key, String.valueOf(fallback)).trim());
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
