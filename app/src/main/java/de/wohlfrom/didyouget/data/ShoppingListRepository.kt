package de.wohlfrom.didyouget.data

import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource
import java.util.LinkedList

class ShoppingListRepository(val dataSource: ShoppingListDataSource) {

    private lateinit var _shoppingLists: List<ShoppingList>
    private var _shoppingListsById: Map<String, ShoppingList>? = null

    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        val result = dataSource.loadShoppingLists()

        if (result is Result.Success) {
            _shoppingLists = result.data
            _shoppingListsById = HashMap()
            _shoppingLists.forEach {
                (_shoppingListsById as HashMap<String, ShoppingList>)[it.id] = it
            }
        }

        return result
    }

    suspend fun loadListItems(itemId: String): List<ListItem> {
        if (_shoppingListsById == null) {
            loadShoppingLists()
        }

        return _shoppingListsById!![itemId]?.listItems ?: LinkedList()
    }
}
