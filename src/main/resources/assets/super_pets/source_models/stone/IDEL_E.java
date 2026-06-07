// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class IDEL_E extends EntityModel<Entity> {
	private final ModelPart bone;
	private final ModelPart bone2;
	public IDEL_E(ModelPart root) {
		this.bone = root.getChild("bone");
		this.bone2 = root.getChild("bone2");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -5.0F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 9.0F, 0.0F));

		ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(0, 30).cuboid(-3.9364F, -4.4698F, -4.6042F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.3869F, 0.5721F, -0.0619F));

		ModelPartData cube_r2 = bone.addChild("cube_r2", ModelPartBuilder.create().uv(0, 20).cuboid(-3.9364F, -4.4698F, -4.6042F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0967F, 0.5721F, -0.0619F));

		ModelPartData cube_r3 = bone.addChild("cube_r3", ModelPartBuilder.create().uv(0, 10).cuboid(-1.0F, -4.0F, -5.0F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 0.0F, 0.0F, 0.0872F, -0.1198F, -0.1683F));

		ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(30, 0).cuboid(-4.0F, -4.0F, -5.0F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, 9.0F, 0.0F));

		ModelPartData cube_r4 = bone2.addChild("cube_r4", ModelPartBuilder.create().uv(30, 30).cuboid(-3.9364F, -4.4698F, -4.6042F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.3869F, 0.5721F, -0.0619F));

		ModelPartData cube_r5 = bone2.addChild("cube_r5", ModelPartBuilder.create().uv(30, 20).cuboid(-3.9364F, -4.4698F, -4.6042F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0967F, 0.5721F, -0.0619F));

		ModelPartData cube_r6 = bone2.addChild("cube_r6", ModelPartBuilder.create().uv(30, 10).cuboid(-1.0F, -4.0F, -5.0F, 9.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 0.0F, 0.0F, 0.0872F, -0.1198F, -0.1683F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		bone2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}