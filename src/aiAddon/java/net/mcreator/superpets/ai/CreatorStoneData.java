package net.mcreator.superpets.ai;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import net.mcreator.superpets.friendship.FriendshipRelicsData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public final class CreatorStoneData {
	private static final String GENERATED = "creator_stone_generated";
	private static final String TOOL_GENERATED = "creator_stone_tool_generated";
	private static final String BOND_ID = "friendship_relics_bond_id";
	private static final String PET_TYPE = "creator_stone_pet_type";
	private static final String PET_VARIANT = "creator_stone_pet_variant";
	private static final String PET_NAME = "creator_stone_pet_name";
	private static final String GIANT = "creator_stone_legendary_giant";
	private static final String SEED = "creator_stone_generation_seed";
	private static final String TEXTURE_PATH = "creator_stone_texture_path";
	private static final String TOOL_TEXTURE_PATH = "creator_stone_tool_texture_path";
	private static final String DESCRIPTION = "creator_stone_description";
	private static final String CACHE_KEY = "creator_stone_cache_key";
	private static final String TOOL_KIND = "creator_stone_tool_kind";
	private static final String FALLBACK_REASON = "creator_stone_fallback_reason";

	private CreatorStoneData() {
	}

	public static boolean isGeneratedCreatorStone(ItemStack stack) {
		return stack.getItem() == SuperPetsAiRegistry.CREATOR_STONE && tag(stack).map(data -> data.getBooleanOr(GENERATED, false)).orElse(false);
	}

	public static Optional<String> description(ItemStack stack) {
		return stringValue(stack, DESCRIPTION);
	}

	public static Optional<String> cacheKey(ItemStack stack) {
		return stringValue(stack, CACHE_KEY);
	}

	public static Optional<String> petLabel(ItemStack stack) {
		Optional<CompoundTag> tag = tag(stack);
		if (tag.isEmpty())
			return Optional.empty();
		String type = tag.get().getStringOr(PET_TYPE, "");
		if (type.isBlank())
			return Optional.empty();
		String variant = tag.get().getStringOr(PET_VARIANT, "");
		String name = tag.get().getStringOr(PET_NAME, "");
		String prefix = tag.get().getBooleanOr(GIANT, false) ? "Legendary Giant " : "";
		String label = prefix + title(type);
		if (!variant.isBlank())
			label += " " + variant;
		if (!name.isBlank())
			label += " named " + name;
		return Optional.of(label);
	}

	public static InteractionResult tryGenerateFromPet(ItemStack stack, Player player, LivingEntity target) {
		if (player.level().isClientSide())
			return InteractionResult.SUCCESS;
		if (!(target instanceof Parrot) && !(target instanceof Axolotl))
			return InteractionResult.PASS;
		if (isGeneratedCreatorStone(stack)) {
			player.sendSystemMessage(Component.literal("This Creator's Stone already holds a generated pet artifact."));
			return InteractionResult.SUCCESS;
		}
		if (!FriendshipRelicsData.isBondedPet(target)) {
			player.sendSystemMessage(Component.literal("The Creator's Stone needs a bonded large pet or Legendary Giant pet."));
			return InteractionResult.SUCCESS;
		}
		if (!FriendshipRelicsData.playerOwnsPet(player, target)) {
			player.sendSystemMessage(Component.literal("This bonded pet does not match your Master Stone."));
			return InteractionResult.SUCCESS;
		}
		String bondId = FriendshipRelicsData.getEntityBondId(target).orElse("");
		String petType = petType(target);
		String variant = petVariant(target);
		String petName = target.getCustomName() == null ? "" : target.getCustomName().getString();
		boolean giant = FriendshipRelicsData.isGiantPet(target);
		long seed = generationSeed(player, target, bondId);
		String cacheKey = cacheKey(bondId, petType, variant, petName, giant, seed);
		String texturePath = CreatorStoneAiConfig.generatedAssetRoot + "/creator_stones/" + cacheKey + ".png";
		String fallbackReason = fallbackReason();
		String description = descriptionFor(petType, variant, petName, giant);
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putBoolean(GENERATED, true);
			tag.putString(BOND_ID, bondId);
			tag.putString(PET_TYPE, petType);
			tag.putString(PET_VARIANT, variant);
			tag.putString(PET_NAME, petName);
			tag.putBoolean(GIANT, giant);
			tag.putLong(SEED, seed);
			tag.putString(TEXTURE_PATH, texturePath);
			tag.putString(DESCRIPTION, description);
			tag.putString(CACHE_KEY, cacheKey);
			tag.putString(FALLBACK_REASON, fallbackReason);
		});
		stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(SuperPetsAiMod.MODID, "creator_stone_" + petType));
		stack.set(DataComponents.CUSTOM_NAME, Component.literal(title(petType) + " Creator's Stone"));
		stack.set(DataComponents.LORE, lore(description, "Texture: " + texturePath, fallbackReason));
		player.sendSystemMessage(Component.literal("The Creator's Stone absorbed " + petLabel(stack).orElse("the pet") + "."));
		return InteractionResult.SUCCESS;
	}

	public static ItemStack applyToTool(ItemStack tool, ItemStack creatorStone) {
		if (tool.isEmpty() || creatorStone.isEmpty() || !isGeneratedCreatorStone(creatorStone) || !isSupportedTool(tool))
			return ItemStack.EMPTY;
		CompoundTag source = tag(creatorStone).map(CompoundTag::copy).orElseGet(CompoundTag::new);
		String petType = source.getStringOr(PET_TYPE, "parrot");
		String toolKind = toolKind(tool);
		if (toolKind.isBlank())
			return ItemStack.EMPTY;
		ItemStack result = tool.copy();
		String cacheKey = source.getStringOr(CACHE_KEY, cacheKey(tool.getItem().toString(), petType, source.getStringOr(PET_VARIANT, ""), source.getStringOr(PET_NAME, ""), source.getBooleanOr(GIANT, false), source.getLongOr(SEED, 0L)));
		String texturePath = CreatorStoneAiConfig.generatedAssetRoot + "/tools/" + cacheKey + "_" + toolKind + ".png";
		String description = "A " + toolKind + " skin shaped by " + source.getStringOr(DESCRIPTION, "a generated Creator's Stone.");
		CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> {
			tag.putBoolean(TOOL_GENERATED, true);
			tag.putString(BOND_ID, source.getStringOr(BOND_ID, ""));
			tag.putString(PET_TYPE, petType);
			tag.putString(PET_VARIANT, source.getStringOr(PET_VARIANT, ""));
			tag.putString(PET_NAME, source.getStringOr(PET_NAME, ""));
			tag.putBoolean(GIANT, source.getBooleanOr(GIANT, false));
			tag.putLong(SEED, source.getLongOr(SEED, 0L));
			tag.putString(CACHE_KEY, cacheKey);
			tag.putString(TOOL_KIND, toolKind);
			tag.putString(TOOL_TEXTURE_PATH, texturePath);
			tag.putString(DESCRIPTION, description);
			tag.putString(FALLBACK_REASON, fallbackReason());
		});
		result.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(SuperPetsAiMod.MODID, "generated_tool_" + toolKind + "_" + petType));
		result.set(DataComponents.CUSTOM_NAME, Component.literal(title(petType) + "-forged " + tool.getHoverName().getString()));
		result.set(DataComponents.LORE, lore(description, "Creator key: " + cacheKey.substring(0, Math.min(12, cacheKey.length())), "Texture: " + texturePath));
		return result;
	}

	public static boolean isSupportedTool(ItemStack stack) {
		return !toolKind(stack).isBlank();
	}

	private static ItemLore lore(String first, String second, String third) {
		return new ItemLore(List.of(Component.literal(first), Component.literal(second), Component.literal(third)));
	}

	private static Optional<CompoundTag> tag(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? Optional.empty() : Optional.of(data.copyTag());
	}

	private static Optional<String> stringValue(ItemStack stack, String key) {
		return tag(stack).map(data -> data.getStringOr(key, "")).filter(value -> !value.isBlank());
	}

	private static String fallbackReason() {
		if (!CreatorStoneAiConfig.aiEnabled)
			return "AI disabled; deterministic fallback visual active.";
		if (CreatorStoneAiConfig.aiHelperCommand.isBlank())
			return "AI helper missing; deterministic fallback visual active.";
		return "Local AI helper requested; fallback visual remains available.";
	}

	private static long generationSeed(Player player, LivingEntity target, String bondId) {
		long seed = target.level() instanceof ServerLevel serverLevel ? serverLevel.getSeed() : 0L;
		seed ^= target.getUUID().getMostSignificantBits();
		seed ^= target.getUUID().getLeastSignificantBits();
		seed ^= player.getUUID().getLeastSignificantBits();
		seed ^= bondId.hashCode();
		return seed;
	}

	private static String cacheKey(String bondId, String petType, String variant, String petName, boolean giant, long seed) {
		String source = bondId + "|" + petType + "|" + variant + "|" + petName + "|" + giant + "|" + seed;
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(source.getBytes(StandardCharsets.UTF_8));
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 16 && i < digest.length; i++) {
				builder.append(String.format("%02x", digest[i]));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException ignored) {
			return Integer.toHexString(source.hashCode());
		}
	}

	private static String descriptionFor(String petType, String variant, String petName, boolean giant) {
		String subject = petName.isBlank() ? "this " + title(petType) : petName;
		String scale = giant ? "legendary giant " : "";
		if ("axolotl".equals(petType))
			return "A tide-bright Creator's Stone echoing the " + scale + "axolotl bond of " + subject + ".";
		return "A feather-bright Creator's Stone echoing the " + scale + "parrot bond of " + subject + ".";
	}

	private static String petType(LivingEntity pet) {
		if (pet instanceof Axolotl)
			return "axolotl";
		if (pet instanceof Parrot)
			return "parrot";
		return "unknown";
	}

	private static String petVariant(LivingEntity pet) {
		if (pet instanceof Axolotl axolotl)
			return String.valueOf(axolotl.getVariant()).toLowerCase();
		if (pet instanceof Parrot parrot)
			return String.valueOf(parrot.getVariant()).toLowerCase();
		return "";
	}

	private static String toolKind(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.WOODEN_SWORD || item == Items.COPPER_SWORD || item == Items.STONE_SWORD || item == Items.GOLDEN_SWORD || item == Items.IRON_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD)
			return "sword";
		if (item == Items.WOODEN_AXE || item == Items.COPPER_AXE || item == Items.STONE_AXE || item == Items.GOLDEN_AXE || item == Items.IRON_AXE || item == Items.DIAMOND_AXE || item == Items.NETHERITE_AXE)
			return "axe";
		if (item == Items.WOODEN_PICKAXE || item == Items.COPPER_PICKAXE || item == Items.STONE_PICKAXE || item == Items.GOLDEN_PICKAXE || item == Items.IRON_PICKAXE || item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE)
			return "pickaxe";
		if (item == Items.WOODEN_SHOVEL || item == Items.COPPER_SHOVEL || item == Items.STONE_SHOVEL || item == Items.GOLDEN_SHOVEL || item == Items.IRON_SHOVEL || item == Items.DIAMOND_SHOVEL || item == Items.NETHERITE_SHOVEL)
			return "shovel";
		if (item == Items.WOODEN_HOE || item == Items.COPPER_HOE || item == Items.STONE_HOE || item == Items.GOLDEN_HOE || item == Items.IRON_HOE || item == Items.DIAMOND_HOE || item == Items.NETHERITE_HOE)
			return "hoe";
		return "";
	}

	private static String title(String value) {
		if (value == null || value.isBlank())
			return "Pet";
		return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
	}
}
