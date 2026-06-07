package net.mcreator.superpets.friendship;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import net.mcreator.superpets.init.SuperPetsModItems;

import java.util.Optional;
import java.util.UUID;

public final class FriendshipRelicsData {
	private static final String BOND_ID = "friendship_relics_bond_id";
	private static final String PET_TYPE = "friendship_relics_pet_type";
	private static final String PET_VARIANT = "friendship_relics_pet_variant";
	private static final String PET_NAME = "friendship_relics_pet_name";
	private static final String WORLD_SEED = "friendship_relics_world_seed";
	private static final String GIANT = "friendship_relics_giant";
	private static final String ENTITY_BOND_PREFIX = "super_pets_bond_";
	private static final String ENTITY_GIANT = "super_pets_legendary_giant";
	private static final String ENTITY_STANDARD = "super_pets_bonded";

	private FriendshipRelicsData() {
	}

	public static boolean isPet(Entity entity) {
		return entity instanceof Parrot || entity instanceof Axolotl;
	}

	public static Optional<String> getBondId(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null)
			return Optional.empty();
		String value = data.copyTag().getStringOr(BOND_ID, "");
		return value.isBlank() ? Optional.empty() : Optional.of(value);
	}

	public static String ensureBondId(ItemStack stack) {
		Optional<String> existing = getBondId(stack);
		if (existing.isPresent())
			return existing.get();
		String bondId = UUID.randomUUID().toString();
		setBondId(stack, bondId);
		return bondId;
	}

	public static void setBondId(ItemStack stack, String bondId) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(BOND_ID, bondId));
	}

	public static boolean isLinkedStone(ItemStack stack) {
		return stack.getItem() == FriendshipRelicsRegistry.LINKED_FRIENDSHIP_STONE || stack.getItem() == FriendshipRelicsRegistry.GIANT_LINKED_FRIENDSHIP_STONE;
	}

	public static boolean isMasterStone(ItemStack stack) {
		return stack.getItem() == FriendshipRelicsRegistry.MASTER_STONE;
	}

	public static boolean isNecklaceStone(ItemStack stack) {
		return isLinkedStone(stack) || isMasterStone(stack);
	}

	public static ItemStack toMasterStone(ItemStack linkedStone, LivingEntity pet, boolean giant) {
		String bondId = ensureBondId(linkedStone);
		ItemStack master = new ItemStack(FriendshipRelicsRegistry.MASTER_STONE);
		CompoundTag tag = new CompoundTag();
		tag.putString(BOND_ID, bondId);
		tag.putString(PET_TYPE, petType(pet));
		tag.putString(PET_VARIANT, petVariant(pet));
		tag.putString(PET_NAME, pet.getCustomName() == null ? "" : pet.getCustomName().getString());
		tag.putBoolean(GIANT, giant);
		if (pet.level() instanceof ServerLevel serverLevel) {
			tag.putLong(WORLD_SEED, serverLevel.getSeed());
		}
		CustomData.set(DataComponents.CUSTOM_DATA, master, tag);
		return master;
	}

	public static boolean isGiant(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBooleanOr(GIANT, false);
	}

	public static void markBonded(LivingEntity pet, String bondId, boolean giant, Player owner) {
		pet.entityTags().stream().filter(tag -> tag.startsWith(ENTITY_BOND_PREFIX)).toList().forEach(pet::removeTag);
		pet.addTag(ENTITY_BOND_PREFIX + bondId);
		pet.addTag(ENTITY_STANDARD);
		if (giant) {
			pet.addTag(ENTITY_GIANT);
		}
		if (pet instanceof TamableAnimal tame) {
			tame.tame(owner);
			tame.setOrderedToSit(false);
		}
		applyBondedAttributes(pet);
	}

	public static Optional<String> getEntityBondId(Entity entity) {
		return entity.entityTags().stream().filter(tag -> tag.startsWith(ENTITY_BOND_PREFIX)).map(tag -> tag.substring(ENTITY_BOND_PREFIX.length())).findFirst();
	}

	public static boolean isBondedPet(Entity entity) {
		return isPet(entity) && getEntityBondId(entity).isPresent();
	}

	public static boolean isGiantPet(Entity entity) {
		return entity.entityTags().contains(ENTITY_GIANT);
	}

	public static void applyBondedAttributes(LivingEntity pet) {
		if (!isBondedPet(pet))
			return;
		double scale = isGiantPet(pet) ? FriendshipRelicsConfig.giantPetScale : FriendshipRelicsConfig.standardPetScale;
		setBase(pet, Attributes.SCALE, scale);
		setBase(pet, Attributes.STEP_HEIGHT, isGiantPet(pet) ? 1.4d : 1.1d);
		if (pet instanceof Axolotl) {
			setBase(pet, Attributes.MOVEMENT_SPEED, 0.22d * FriendshipRelicsConfig.mountSpeedMultiplier);
			setBase(pet, Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0d);
		} else if (pet instanceof Parrot) {
			setBase(pet, Attributes.MOVEMENT_SPEED, 0.18d * FriendshipRelicsConfig.mountSpeedMultiplier);
			setBase(pet, Attributes.FLYING_SPEED, 0.16d * FriendshipRelicsConfig.mountSpeedMultiplier);
		}
	}

	public static Optional<String> findOwnedMasterBond(Player player) {
		ItemStack necklaceStone = necklaceStone(player);
		Optional<String> necklaceBond = getBondId(necklaceStone).filter(id -> isMasterStone(necklaceStone));
		if (necklaceBond.isPresent())
			return necklaceBond;
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (isMasterStone(stack)) {
				Optional<String> id = getBondId(stack);
				if (id.isPresent())
					return id;
			}
		}
		return Optional.empty();
	}

	public static boolean playerOwnsPet(Player player, Entity pet) {
		Optional<String> petBond = getEntityBondId(pet);
		return petBond.isPresent() && findOwnedMasterBond(player).filter(petBond.get()::equals).isPresent();
	}

	public static ItemStack necklaceStone(Player player) {
		if (player instanceof FriendshipNecklaceHolder holder) {
			return holder.superPets$getNecklaceStone();
		}
		return ItemStack.EMPTY;
	}

	public static void setNecklaceStone(Player player, ItemStack stack) {
		if (player instanceof FriendshipNecklaceHolder holder) {
			holder.superPets$setNecklaceStone(stack);
		}
	}

	public static boolean hasFriendshipNecklace(Player player) {
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (inventory.getItem(i).getItem() == FriendshipRelicsRegistry.FRIENDSHIP_NECKLACE)
				return true;
		}
		return false;
	}

	public static boolean isStoneOfFriendship(ItemStack stack) {
		return stack.getItem() == SuperPetsModItems.STONEBUTWIRD;
	}

	private static void setBase(LivingEntity entity, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value) {
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance != null && Math.abs(instance.getBaseValue() - value) > 0.001d) {
			instance.setBaseValue(value);
			entity.refreshDimensions();
		}
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
			return String.valueOf(axolotl.getVariant());
		if (pet instanceof Parrot parrot)
			return String.valueOf(parrot.getVariant());
		return "";
	}
}
