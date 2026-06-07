package net.mcreator.superpets.friendship;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import net.mcreator.superpets.SuperPetsMod;

public record FriendshipMountInputPayload(float forward, float strafe, boolean jump, boolean shift, boolean sprint, float yaw, float pitch) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<FriendshipMountInputPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SuperPetsMod.MODID, "mount_input"));
	public static final StreamCodec<RegistryFriendlyByteBuf, FriendshipMountInputPayload> CODEC = StreamCodec.ofMember(FriendshipMountInputPayload::write, FriendshipMountInputPayload::read);

	private void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeFloat(this.forward);
		buffer.writeFloat(this.strafe);
		buffer.writeBoolean(this.jump);
		buffer.writeBoolean(this.shift);
		buffer.writeBoolean(this.sprint);
		buffer.writeFloat(this.yaw);
		buffer.writeFloat(this.pitch);
	}

	private static FriendshipMountInputPayload read(RegistryFriendlyByteBuf buffer) {
		return new FriendshipMountInputPayload(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readFloat(), buffer.readFloat());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
