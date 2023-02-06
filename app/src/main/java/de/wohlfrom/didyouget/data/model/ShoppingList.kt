package de.wohlfrom.didyouget.data.model

data class ShoppingList(
    val id: String,
    val name: String,
    val listItems: List<ListItem>? = null
)
