package net.mcreator.superpets.friendship;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class GiantLinkedFriendshipStoneItem extends Item {
	public GiantLinkedFriendshipStoneItem(Properties properties) {
		super(properties);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		String bondId = FriendshipRelicsData.ensureBondId(stack);
		ItemStack partner = new ItemStack(FriendshipRelicsRegistry.LINKED_FRIENDSHIP_STONE, stack.getCount());
		FriendshipRelicsData.setBondId(partner, bondId);
		if (!player.getInventory().add(partner)) {
			player.drop(partner, false);
		}
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		return FriendshipRelicsEvents.tryBondPet(stack, player, target, hand, true);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
		FriendshipRelicsData.getBondId(stack).ifPresent(id -> consumer.accept(Component.literal("Legendary Bond ID: " + id.substring(0, Math.min(8, id.length())))));
	}
}
