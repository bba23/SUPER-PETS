package net.mcreator.superpets.friendship;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.parrot.Parrot;

public final class FriendshipRelicsClientEvents {
	private static boolean loaded;

	private FriendshipRelicsClientEvents() {
	}

	public static void load() {
		if (loaded)
			return;
		loaded = true;
		ClientTickEvents.END_CLIENT_TICK.register(FriendshipRelicsClientEvents::sendMountInput);
	}

	private static void sendMountInput(Minecraft minecraft) {
		if (minecraft.player == null)
			return;
		Entity vehicle = minecraft.player.getVehicle();
		if (!(vehicle instanceof Parrot || vehicle instanceof Axolotl))
			return;
		if (!ClientPlayNetworking.canSend(FriendshipMountInputPayload.TYPE))
			return;
		Options options = minecraft.options;
		float forward = (options.keyUp.isDown() ? 1.0f : 0.0f) - (options.keyDown.isDown() ? 1.0f : 0.0f);
		float strafe = (options.keyRight.isDown() ? 1.0f : 0.0f) - (options.keyLeft.isDown() ? 1.0f : 0.0f);
		ClientPlayNetworking.send(new FriendshipMountInputPayload(forward, strafe, options.keyJump.isDown(), options.keyShift.isDown(), options.keySprint.isDown(), minecraft.player.getYRot(), minecraft.player.getXRot()));
	}
}
