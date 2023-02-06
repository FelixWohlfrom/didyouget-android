package de.wohlfrom.didyouget.data

import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource

class ShoppingListRepository(val dataSource: ShoppingListDataSource) {
    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        return dataSource.loadShoppingList()
    }
}
