package net.mcreator.superpets.friendship;

import net.minecraft.world.item.ItemStack;

public interface FriendshipNecklaceHolder {
	ItemStack superPets$getNecklaceItem();

	void superPets$setNecklaceItem(ItemStack stack);

	ItemStack superPets$getNecklaceStone();

	void superPets$setNecklaceStone(ItemStack stack);
}
