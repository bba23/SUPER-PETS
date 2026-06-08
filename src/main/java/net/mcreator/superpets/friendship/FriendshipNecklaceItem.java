package net.mcreator.superpets.friendship;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class FriendshipNecklaceItem extends Item {
	public FriendshipNecklaceItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide())
			return InteractionResult.SUCCESS;
		ItemStack stored = FriendshipRelicsData.necklaceStone(player);
		if (!stored.isEmpty()) {
			FriendshipRelicsData.unequipNecklace(player, true);
			player.sendSystemMessage(Component.literal("Unequipped the Friendship Necklace."));
			return InteractionResult.SUCCESS;
		}
		InteractionHand sourceHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		ItemStack source = player.getItemInHand(sourceHand);
		if (!FriendshipRelicsData.isNecklaceStone(source)) {
			player.sendSystemMessage(Component.literal("Hold a Linked Friendship Stone or Master Stone in the other hand."));
			return InteractionResult.SUCCESS;
		}
		ItemStack necklace = player.getItemInHand(hand).copyWithCount(1);
		ItemStack inserted = source.copyWithCount(1);
		FriendshipRelicsData.setNecklaceItem(player, necklace);
		FriendshipRelicsData.setNecklaceStone(player, inserted);
		player.getItemInHand(hand).shrink(1);
		source.shrink(1);
		player.sendSystemMessage(Component.literal("Equipped the Friendship Necklace."));
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
		consumer.accept(Component.literal("Right-click with a stone in the other hand to use the necklace slot."));
		consumer.accept(Component.literal("Sneak-right-click with an empty hand to unequip."));
	}
}
