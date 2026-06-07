package net.mcreator.superpets.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;

import net.mcreator.superpets.init.SuperPetsModBlocks;
import net.mcreator.superpets.friendship.FriendshipRelicsConfig;

import java.util.function.Consumer;

public class AncientresiengreenBlock extends Block {
	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

	public AncientresiengreenBlock(BlockBehaviour.Properties properties) {
		super(properties.sound(SoundType.STONE).strength(1.5f, 6f).lightLevel(blockstate -> 2).requiresCorrectToolForDrops().friction(FriendshipRelicsConfig.resinSlipperiness).speedFactor(FriendshipRelicsConfig.resinSpeedFactor).jumpFactor(FriendshipRelicsConfig.resinJumpFactor).pushReaction(PushReaction.BLOCK));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public int getLightDampening(BlockState state) {
		return 10;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state == null)
			return null;
		return state.setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	public static class Item extends BlockItem {
		public Item(Item.Properties properties) {
			super(SuperPetsModBlocks.ANCIENTRESIENGREEN, properties);
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag flag) {
			super.appendHoverText(itemstack, context, tooltipDisplay, componentConsumer, flag);
			componentConsumer.accept(Component.translatable("block.super_pets.ancientresiengreen.description_0"));
		}
	}
}
