package net.mcreator.superpets.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;

import net.mcreator.superpets.friendship.CreatorStoneData;
import net.mcreator.superpets.friendship.FriendshipRelicsConfig;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
	@Shadow
	@Final
	protected Container inputSlots;

	@Shadow
	@Final
	protected ResultContainer resultSlots;

	@Shadow
	@Final
	private DataSlot cost;

	@Shadow
	public abstract void broadcastChanges();

	@Inject(method = "createResult()V", at = @At("HEAD"), cancellable = true)
	private void superPets$createCreatorStoneToolResult(CallbackInfo ci) {
		ItemStack result = CreatorStoneData.applyToTool(this.inputSlots.getItem(0), this.inputSlots.getItem(1));
		if (result.isEmpty())
			return;
		this.cost.set(Math.max(1, FriendshipRelicsConfig.creatorStoneAnvilCost));
		this.resultSlots.setItem(0, result);
		this.broadcastChanges();
		ci.cancel();
	}
}
