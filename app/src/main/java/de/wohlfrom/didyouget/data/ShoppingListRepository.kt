package de.wohlfrom.didyouget.data

import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource

class ShoppingListRepository(val dataSource: ShoppingListDataSource) {

    var shoppingLists = HashMap<Number, ShoppingList>()
}
