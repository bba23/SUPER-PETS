package net.mcreator.superpets.friendship;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class MasterStoneItem extends Item {
	public MasterStoneItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
		FriendshipRelicsData.getBondId(stack).ifPresent(id -> consumer.accept(Component.literal("Master Bond ID: " + id.substring(0, Math.min(8, id.length())))));
		if (FriendshipRelicsData.isGiant(stack)) {
			consumer.accept(Component.literal("Legendary Giant bond"));
		}
	}
}
