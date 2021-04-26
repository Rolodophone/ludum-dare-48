package io.github.rolodophone.ludumdare48

@Suppress("PropertyName", "MoveLambdaOutsideParentheses")
class LayoutManager(textures: MyTextures) {

	val PIPE_END = Obstacle(textures.block_pipe_end)
	val PIPE = Obstacle(textures.block_pipe_middle)
	val STN_BLK = Obstacle(textures.block_stone_black)
	val STONE = Obstacle(textures.block_stone)
	val FOSSIL = Obstacle(textures.fossil)
	val BOOT = Obstacle(textures.boot)
	val ROOT = Obstacle(textures.root_middle)
	val TREASURE = Obstacle(textures.treasure)
	val DIAMOND = Obstacle(textures.diamonds)
	val APPLE = Item(textures.dog_happy_apple_core,
		listOf("Yummy! I love apples!", "I can now dig twice as fast."),
		{ it.digDuration /= 2f })
	val BONE = BoneItem(textures.dog_happy_bone,
		listOf("Ah, here it is! I've found my bone!"))
	val DYNAMITE = HurtItem(textures.dog_hurt_dynamite,
		listOf("Oh no, a stick of dynamite!"))
	val BAG = HurtItem(textures.dog_hurt_plastic_bag,
		listOf("Ahh! A plastic bag!"))
	val SEWAGE = HurtItem(textures.dog_hurt_sewage,
		listOf("Eugh! Sewage."))
	val CAN = HurtItem(textures.dog_hurt_tin_can,
		listOf("Ow, tin cans are sharp."))
	val NAIL = HurtItem(textures.dog_hurt_nail,
		listOf("Ow! It's a nail!"))

	val layout = arrayOf<Array<LayoutTile?>>(
		arrayOf(null,		null,		BAG,		APPLE,		null,		null),
		arrayOf(STONE,		null,		null,		null,		STONE,		null),
		arrayOf(STONE,		STONE,		STONE,		null,		STONE,		CAN),
		arrayOf(null,		null,		null,		null,		null,		NAIL),
		arrayOf(null,		NAIL,		null,		STONE,		null,		null),
		arrayOf(null,		APPLE,		STONE,		STONE,		STONE,		null),
		arrayOf(null,		null,		null,		null,		null,		null),
		arrayOf(null,		SEWAGE,		PIPE_END,	PIPE,		PIPE,		PIPE),
		arrayOf(null,		null,		null,		null,		null,		null),
		arrayOf(DYNAMITE,	null,		STN_BLK,	null,		FOSSIL,		APPLE),
		arrayOf(null,		STN_BLK,	STN_BLK,	null,		STONE,		null),
		arrayOf(CAN,		null,		APPLE,		null,		STONE,		STONE),
		arrayOf(null,		null,		BOOT,		TREASURE,	null,		ROOT),
		arrayOf(APPLE,		null,		null,		null,		null,		null),
		arrayOf(STONE,		STONE,		null,		DYNAMITE,	STN_BLK,	STN_BLK),
		arrayOf(STONE,		DIAMOND,	null,		null,		null,		BONE),
		arrayOf(STONE,		STONE,		null,		null,		null,		null),
		arrayOf(STONE,		STONE,		STN_BLK,	STN_BLK,	STN_BLK,	STN_BLK),
		).reversed()
}
