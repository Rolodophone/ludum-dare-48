package io.github.rolodophone.ludumdare48

@Suppress("PropertyName", "MoveLambdaOutsideParentheses")
class LayoutManager(textures: MyTextures) {

	val PIPE_END = Obstacle(textures.block_pipe_end)
	val PIPE = Obstacle(textures.block_pipe_middle)
	val STONE_BLACK = Obstacle(textures.block_stone_black)
	val STONE_SAND = Obstacle(textures.block_stone_sand)
	val STONE = Obstacle(textures.block_stone)
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

	val layout = arrayOf<Array<LayoutTile?>>(
		arrayOf(null,		BAG,		null,		null,		null,		null),
		arrayOf(null,		null,		null,		null,		STONE,		null),
		arrayOf(null,		STONE,		APPLE,		null,		null,		BAG),
		arrayOf(null,		null,		null,		null,		STONE,		null),
		arrayOf(DYNAMITE,	STONE,		null,		STONE,		STONE,		STONE),
		arrayOf(null,		null,		null,		null,		null,		CAN),
		arrayOf(null,		SEWAGE,		PIPE_END,	PIPE,		PIPE,		PIPE),
		arrayOf(null,		null,		null,		null,		null,		null),
		arrayOf(STONE,		DYNAMITE,	BONE,		STONE,		STONE,		APPLE),
	).reversed()
}
