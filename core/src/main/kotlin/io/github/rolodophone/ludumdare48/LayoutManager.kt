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
		arrayOf(null,		BAG,		null,		null,		null,		null),
		arrayOf(null,		null,		null,		null,		STONE,		null),
		arrayOf(null,		STONE,		APPLE,		null,		null,		BAG),
		arrayOf(null,		null,		null,		null,		STONE,		null),
		arrayOf(DYNAMITE,	STONE,		null,		STONE,		STONE,		STONE),
		arrayOf(null,		null,		null,		null,		null,		CAN),
		arrayOf(null,		SEWAGE,		PIPE_END,	PIPE,		PIPE,		PIPE),
		arrayOf(null,		null,		null,		null,		null,		null),
		arrayOf(STONE,		DYNAMITE,	null,		STONE,		STONE,		APPLE),
		arrayOf(null,		null,		null,		STONE,		STONE,		null),
		arrayOf(STONE,		BOOT,		null,		null,		APPLE,		null),
		arrayOf(STONE,		STONE,		null,		null,		STN_BLK,	null),
		arrayOf(STONE,		DIAMOND,	null,		STN_BLK,	STN_BLK,	ROOT),
		arrayOf(null,		APPLE,		null,		ROOT,		null,		null),
		arrayOf(TREASURE,	null,		null,		null,		null,		null),
		arrayOf(null,		null,		FOSSIL,		STONE,		STONE,		null),
		arrayOf(NAIL,		null,		STN_BLK,	STN_BLK,	BONE,		null),
		arrayOf(null,		null,		null,		STN_BLK,	null,		NAIL),

		).reversed()
}
