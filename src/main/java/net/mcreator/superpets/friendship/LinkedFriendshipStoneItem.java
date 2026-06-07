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

public class LinkedFriendshipStoneItem extends Item {
	public LinkedFriendshipStoneItem(Properties properties) {
		super(properties);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		FriendshipRelicsData.ensureBondId(stack);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		return FriendshipRelicsEvents.tryBondPet(stack, player, target, hand, false);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
		FriendshipRelicsData.getBondId(stack).ifPresent(id -> consumer.accept(Component.literal("Bond ID: " + id.substring(0, Math.min(8, id.length())))));
	}
}
