package net.mcreator.superpets.mixin;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.mcreator.superpets.event.PlayerEvents;
import net.mcreator.superpets.friendship.FriendshipNecklaceHolder;

@Mixin(Player.class)
public abstract class PlayerMixin implements FriendshipNecklaceHolder {
	@Unique
	private ItemStack superPets$necklaceStone = ItemStack.EMPTY;

	@Inject(method = "tick()V", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		PlayerEvents.END_PLAYER_TICK.invoker().onEndTick((Player) (Object) this);
	}

	@Inject(method = "giveExperiencePoints(I)V", at = @At("HEAD"))
	public void giveExperiencePoints(int amount, CallbackInfo ci) {
		PlayerEvents.XP_CHANGE.invoker().onXpChange((Player) (Object) this, amount);
	}

	@Inject(method = "giveExperienceLevels(I)V", at = @At("HEAD"))
	public void giveExperienceLevels(int amount, CallbackInfo ci) {
		PlayerEvents.LEVEL_CHANGE.invoker().onLevelChange((Player) (Object) this, amount);
	}

	@Inject(method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("TAIL"))
	public void superPets$readNecklace(ValueInput input, CallbackInfo ci) {
		this.superPets$necklaceStone = input.read("SuperPetsNecklaceStone", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
	}

	@Inject(method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("TAIL"))
	public void superPets$writeNecklace(ValueOutput output, CallbackInfo ci) {
		if (!this.superPets$necklaceStone.isEmpty()) {
			output.store("SuperPetsNecklaceStone", ItemStack.OPTIONAL_CODEC, this.superPets$necklaceStone);
		}
	}

	@Override
	public ItemStack superPets$getNecklaceStone() {
		return this.superPets$necklaceStone;
	}

	@Override
	public void superPets$setNecklaceStone(ItemStack stack) {
		this.superPets$necklaceStone = stack == null ? ItemStack.EMPTY : stack.copy();
	}
}
