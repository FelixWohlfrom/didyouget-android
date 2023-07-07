package de.wohlfrom.didyouget.data.model

data class ShoppingList(
    val id: String,
    var name: String,
    val listItems: MutableList<ListItem>? = null
)
