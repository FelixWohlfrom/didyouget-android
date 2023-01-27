package de.wohlfrom.didyouget.data.model

data class ShoppingList(
    val id: Number,
    val name: String,
    val listItems: List<ListItem>
)
