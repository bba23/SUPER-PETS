package net.mcreator.superpets;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ClientModInitializer;

import net.mcreator.superpets.friendship.FriendshipRelicsClientEvents;

@Environment(EnvType.CLIENT)
public class SuperPetsModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Start of user code block mod constructor
		// End of user code block mod constructor
		// Start of user code block mod init
		FriendshipRelicsClientEvents.load();
		// End of user code block mod init
	}
	// Start of user code block mod methods
	// End of user code block mod methods
}
