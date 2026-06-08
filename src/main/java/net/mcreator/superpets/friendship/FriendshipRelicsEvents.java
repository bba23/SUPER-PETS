package net.mcreator.superpets.friendship;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class FriendshipRelicsEvents {
	private static final Set<UUID> WATER_BREATHING_RIDERS = new HashSet<>();
	private static final Map<UUID, Entity> RIDER_MOUNTS = new HashMap<>();
	private static final Map<UUID, MountInput> MOUNT_INPUTS = new HashMap<>();
	private static boolean loaded;

	private FriendshipRelicsEvents() {
	}

	public static void load() {
		if (loaded)
			return;
		loaded = true;
		PayloadTypeRegistry.serverboundPlay().register(FriendshipMountInputPayload.TYPE, FriendshipMountInputPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(FriendshipMountInputPayload.TYPE, (payload, context) -> context.server().executeIfPossible(() -> updateMountInput(context.player(), payload)));
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (level.isClientSide() || !FriendshipRelicsData.isBondedPet(entity))
				return InteractionResult.PASS;
			if (!FriendshipRelicsData.playerOwnsPet(player, entity))
				return InteractionResult.PASS;
			int maxPassengers = FriendshipRelicsData.isGiantPet(entity) ? (entity instanceof Axolotl ? 4 : 2) : 1;
			if (entity.getPassengers().size() >= maxPassengers)
				return InteractionResult.FAIL;
			player.startRiding(entity, true, true);
			return InteractionResult.SUCCESS;
		});
		UseItemCallback.EVENT.register((player, level, hand) -> {
			if (!player.isShiftKeyDown() || !player.getItemInHand(hand).isEmpty())
				return InteractionResult.PASS;
			if (level.isClientSide())
				return InteractionResult.SUCCESS;
			if (!FriendshipRelicsData.hasEquippedNecklace(player))
				return InteractionResult.PASS;
			FriendshipRelicsData.unequipNecklace(player, true);
			player.sendSystemMessage(Component.literal("Unequipped the Friendship Necklace."));
			return InteractionResult.SUCCESS;
		});
		ServerLivingEntityEvents.AFTER_DEATH.register(FriendshipRelicsEvents::afterPetDeath);
		ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(FriendshipRelicsEvents::tickPlayer));
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			ItemStack oldNecklace = FriendshipRelicsData.necklaceItem(oldPlayer);
			ItemStack oldStone = FriendshipRelicsData.necklaceStone(oldPlayer);
			if (alive || FriendshipRelicsData.isMasterStone(oldStone)) {
				FriendshipRelicsData.setNecklaceItem(newPlayer, oldNecklace.copy());
				FriendshipRelicsData.setNecklaceStone(newPlayer, oldStone.copy());
			} else {
				if (!oldNecklace.isEmpty()) {
					oldPlayer.drop(oldNecklace.copy(), false, true);
				}
				if (!oldStone.isEmpty()) {
					oldPlayer.drop(oldStone.copy(), false, true);
				}
				FriendshipRelicsData.setNecklaceItem(newPlayer, ItemStack.EMPTY);
				FriendshipRelicsData.setNecklaceStone(newPlayer, ItemStack.EMPTY);
			}
		});
	}

	public static void updateMountInput(Player player, FriendshipMountInputPayload payload) {
		Entity vehicle = player.getVehicle();
		if (!(vehicle instanceof LivingEntity livingVehicle) || !FriendshipRelicsData.playerOwnsPet(player, livingVehicle)) {
			MOUNT_INPUTS.remove(player.getUUID());
			return;
		}
		MOUNT_INPUTS.put(player.getUUID(), new MountInput(clamp(payload.forward()), clamp(payload.strafe()), payload.jump(), payload.shift(), payload.sprint(), payload.yaw(), payload.pitch(), player.level().getGameTime()));
	}

	public static InteractionResult tryBondPet(ItemStack handStone, Player player, LivingEntity target, InteractionHand hand, boolean giant) {
		if (player.level().isClientSide())
			return InteractionResult.SUCCESS;
		if (!FriendshipRelicsData.isPet(target))
			return InteractionResult.PASS;
		if (FriendshipRelicsData.isBondedPet(target)) {
			player.sendSystemMessage(Component.literal("This pet already has a friendship bond."));
			return InteractionResult.SUCCESS;
		}
		if (!FriendshipRelicsData.hasFriendshipNecklace(player)) {
			player.sendSystemMessage(Component.literal("A Friendship Necklace is required."));
			return InteractionResult.SUCCESS;
		}
		ItemStack necklaceStone = FriendshipRelicsData.necklaceStone(player);
		if (necklaceStone.isEmpty() || necklaceStone.getItem() != FriendshipRelicsRegistry.LINKED_FRIENDSHIP_STONE) {
			player.sendSystemMessage(Component.literal("Equip the matching Linked Friendship Stone in the necklace slot first."));
			return InteractionResult.SUCCESS;
		}
		String handBond = FriendshipRelicsData.ensureBondId(handStone);
		String necklaceBond = FriendshipRelicsData.ensureBondId(necklaceStone);
		if (!handBond.equals(necklaceBond)) {
			player.sendSystemMessage(Component.literal("These linked stones do not share a Bond ID."));
			return InteractionResult.SUCCESS;
		}
		ItemStack master = FriendshipRelicsData.toMasterStone(necklaceStone, target, giant);
		FriendshipRelicsData.setNecklaceStone(player, master);
		FriendshipRelicsData.markBonded(target, handBond, giant, player);
		if (target instanceof Mob mob) {
			mob.setPersistenceRequired();
		}
		handStone.shrink(1);
		player.sendSystemMessage(Component.literal(giant ? "A legendary friendship bond was created." : "A friendship bond was created."));
		return InteractionResult.SUCCESS;
	}

	private static void afterPetDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
		if (!(entity.level() instanceof ServerLevel serverLevel) || !FriendshipRelicsData.isBondedPet(entity))
			return;
		String bondId = FriendshipRelicsData.getEntityBondId(entity).orElse("");
		if (bondId.isBlank())
			return;
		for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
			if (!FriendshipRelicsData.playerOwnsBond(player, bondId))
				continue;
			boolean necklaceHeldDeadPet = FriendshipRelicsData.isMasterStoneForBond(FriendshipRelicsData.necklaceStone(player), bondId);
			if (necklaceHeldDeadPet) {
				FriendshipRelicsData.unequipNecklace(player, false);
			}
			player.sendSystemMessage(Component.literal(petDeathMessage(entity, necklaceHeldDeadPet)));
		}
	}

	private static String petDeathMessage(LivingEntity entity, boolean necklaceUnequipped) {
		String petName = entity.getCustomName() == null ? "friendship pet" : "pet " + entity.getCustomName().getString();
		return "Your " + petName + " died." + (necklaceUnequipped ? " Your Friendship Necklace was unequipped." : "");
	}

	private static void tickPlayer(Player player) {
		Entity vehicle = player.getVehicle();
		boolean ridingBondedAxolotl = vehicle instanceof Axolotl && FriendshipRelicsData.isBondedPet(vehicle);
		if (vehicle instanceof LivingEntity livingVehicle && FriendshipRelicsData.playerOwnsPet(player, livingVehicle)) {
			Entity previous = RIDER_MOUNTS.put(player.getUUID(), vehicle);
			if (previous instanceof Parrot previousParrot && previous != vehicle) {
				previousParrot.setNoGravity(false);
			}
			FriendshipRelicsData.applyBondedAttributes(livingVehicle);
			driveMount(player, livingVehicle);
			if (ridingBondedAxolotl) {
				player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, false, false, true));
				WATER_BREATHING_RIDERS.add(player.getUUID());
			}
		} else if (WATER_BREATHING_RIDERS.remove(player.getUUID())) {
			player.removeEffect(MobEffects.WATER_BREATHING);
			MOUNT_INPUTS.remove(player.getUUID());
			clearPreviousMount(player);
		} else {
			MOUNT_INPUTS.remove(player.getUUID());
			clearPreviousMount(player);
		}
	}

	private static void clearPreviousMount(Player player) {
		Entity previous = RIDER_MOUNTS.remove(player.getUUID());
		if (previous instanceof Parrot parrot) {
			parrot.setNoGravity(false);
		}
	}

	private static void driveMount(Player rider, LivingEntity mount) {
		MountInput input = MOUNT_INPUTS.getOrDefault(rider.getUUID(), MountInput.fromVanilla(rider));
		if (rider.level().getGameTime() - input.updatedAt > 10) {
			input = MountInput.fromVanilla(rider);
		}
		if (mount instanceof Parrot) {
			mount.setNoGravity(true);
		} else {
			mount.setNoGravity(false);
		}
		float forward = input.forward;
		float strafe = input.strafe;
		double baseSpeed = mount instanceof Axolotl ? 0.72d : 0.42d;
		if (FriendshipRelicsData.isGiantPet(mount)) {
			baseSpeed *= mount instanceof Axolotl ? 1.35d : 1.2d;
		}
		if (input.sprint) {
			baseSpeed *= 1.35d;
		}
		baseSpeed *= FriendshipRelicsConfig.mountSpeedMultiplier;
		Vec3 look = Vec3.directionFromRotation(input.pitch, input.yaw);
		Vec3 right = Vec3.directionFromRotation(0, input.yaw + 90f);
		Vec3 motion = look.scale(forward * baseSpeed).add(right.scale(strafe * baseSpeed * 0.55d));
		if (mount instanceof Parrot) {
			double vertical = input.jump ? 0.38d : input.shift ? -0.32d : motion.y * 0.65d;
			motion = new Vec3(motion.x, vertical, motion.z);
			mount.resetFallDistance();
		} else {
			double vertical = input.jump ? 0.24d : input.shift ? -0.24d : motion.y * (mount.isInWater() ? 0.85d : 0.25d);
			double horizontalScale = mount.isInWater() ? 1.0d : 0.35d;
			motion = new Vec3(motion.x * horizontalScale, vertical, motion.z * horizontalScale);
		}
		mount.setYRot(input.yaw);
		mount.setYHeadRot(input.yaw);
		mount.setYBodyRot(input.yaw);
		mount.setXRot(input.pitch * 0.5f);
		mount.setDeltaMovement(motion);
		mount.move(MoverType.SELF, motion);
		mount.hurtMarked = true;
		mount.needsSync = true;
	}

	private static float clamp(float value) {
		return Math.max(-1.0f, Math.min(1.0f, value));
	}

	private record MountInput(float forward, float strafe, boolean jump, boolean shift, boolean sprint, float yaw, float pitch, long updatedAt) {
		static MountInput fromVanilla(Player player) {
			return new MountInput(clamp(player.zza), clamp(player.xxa), player.isJumping(), player.isShiftKeyDown(), player.isSprinting(), player.getYRot(), player.getXRot(), player.level().getGameTime());
		}
	}
}
