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

public class CreatorStoneItem extends Item {
	public CreatorStoneItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		return CreatorStoneData.tryGenerateFromPet(stack, player, target);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return CreatorStoneData.isGeneratedCreatorStone(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
		if (CreatorStoneData.isGeneratedCreatorStone(stack)) {
			CreatorStoneData.description(stack).ifPresent(text -> consumer.accept(Component.literal(text)));
			CreatorStoneData.petLabel(stack).ifPresent(text -> consumer.accept(Component.literal("Bound to " + text)));
			CreatorStoneData.cacheKey(stack).ifPresent(key -> consumer.accept(Component.literal("Generation key: " + key.substring(0, Math.min(8, key.length())))));
		} else {
			consumer.accept(Component.literal("Right-click a bonded large pet or Legendary Giant pet."));
			consumer.accept(Component.literal("The same stone becomes the generated artifact."));
		}
	}
}
