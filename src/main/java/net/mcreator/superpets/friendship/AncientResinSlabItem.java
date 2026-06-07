package net.mcreator.superpets.friendship;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AncientResinSlabItem extends BlockItem {
	public AncientResinSlabItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		Direction facing = context.getHorizontalDirection().getOpposite();
		BlockPlaceContext placeContext = new BlockPlaceContext(context);
		BlockPos origin = context.getClickedPos().relative(context.getClickedFace());
		if (level.getBlockState(context.getClickedPos()).canBeReplaced(placeContext)) {
			origin = context.getClickedPos();
		}
		BlockPos[] positions = AncientResinSlabBlock.positions(origin, facing);
		for (BlockPos pos : positions) {
			BlockState existing = level.getBlockState(pos);
			if (!existing.canBeReplaced()) {
				return InteractionResult.FAIL;
			}
		}
		if (!level.isClientSide()) {
			for (int i = 0; i < positions.length; i++) {
				level.setBlock(positions[i], AncientResinSlabBlock.stateForPart(getBlock(), facing, i), 3);
			}
			ItemStack stack = context.getItemInHand();
			if (player == null || !player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
