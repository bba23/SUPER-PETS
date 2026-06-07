package net.mcreator.superpets.friendship;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AncientResinSlabBlock extends Block {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final IntegerProperty PART = IntegerProperty.create("part", 0, 3);
	private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

	public AncientResinSlabBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, PART);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide()) {
			BlockPos origin = originFromPart(pos, state);
			if (!player.getAbilities().instabuild) {
				Block.popResource(level, origin, new ItemStack(this.asItem()));
			}
			removeAllParts(level, origin, state.getValue(FACING), this);
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	public static BlockPos[] positions(BlockPos origin, Direction facing) {
		Direction right = facing.getClockWise();
		return new BlockPos[]{origin, origin.relative(right), origin.relative(facing), origin.relative(facing).relative(right)};
	}

	public static BlockState stateForPart(Block block, Direction facing, int part) {
		return block.defaultBlockState().setValue(FACING, facing).setValue(PART, part);
	}

	private static BlockPos originFromPart(BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		Direction right = facing.getClockWise();
		return switch (state.getValue(PART)) {
			case 1 -> pos.relative(right.getOpposite());
			case 2 -> pos.relative(facing.getOpposite());
			case 3 -> pos.relative(facing.getOpposite()).relative(right.getOpposite());
			default -> pos;
		};
	}

	private static void removeAllParts(Level level, BlockPos origin, Direction facing, Block block) {
		BlockPos[] positions = positions(origin, facing);
		for (BlockPos partPos : positions) {
			BlockState partState = level.getBlockState(partPos);
			if (partState.getBlock() == block) {
				level.setBlock(partPos, Blocks.AIR.defaultBlockState(), 35);
			}
		}
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(level, pos, state, entity);
		if (!entity.onGround() || entity.isShiftKeyDown()) {
			return;
		}
		Vec3 movement = entity.getDeltaMovement();
		double horizontalSpeedSqr = movement.horizontalDistanceSqr();
		if (horizontalSpeedSqr > 1.0E-6 && horizontalSpeedSqr < 0.49d) {
			double slideFactor = Math.min(1.12d, 1.0d + Math.max(0.0d, FriendshipRelicsConfig.slabSlipperiness - 0.6d) * 0.32d);
			entity.setDeltaMovement(movement.x * slideFactor, movement.y, movement.z * slideFactor);
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
