package de.wohlfrom.didyouget.data

import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource
import java.util.LinkedList

class ShoppingListRepository(val dataSource: ShoppingListDataSource) {

    private lateinit var _shoppingLists: MutableList<ShoppingList>
    private var _shoppingListsById: MutableMap<String, ShoppingList>? = null

    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        val result = dataSource.loadShoppingLists()

        if (result is Result.Success) {
            _shoppingLists = result.data.toMutableList()
            _shoppingListsById = HashMap()
            _shoppingLists.forEach {
                _shoppingListsById?.set(it.id, it)
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

    suspend fun addShoppingList(name: String): Result<Boolean> {
        val result = dataSource.addShoppingList(name)

        if (result is Result.Success) {
            _shoppingListsById?.set(result.data.id, result.data)
            _shoppingLists.add(result.data)
            return Result.Success(true)
        } else if (result is Result.Error) {
            return Result.Error(result.exception)
        }
        return Result.Error(Exception("Unknown error"))
    }

    suspend fun addListItem(listId: String, newName: String): Result<Boolean> {
        val result = dataSource.addListItem(listId, newName)

        if (result is Result.Success) {
            _shoppingListsById?.get(listId)!!.listItems!!.add(result.data)
            return Result.Success(true)
        } else if (result is Result.Error) {
            return Result.Error(result.exception)
        }
        return Result.Error(Exception("Unknown error"))
    }

    suspend fun markListItemBought(listItemId: String, bought: Boolean): Result<Boolean> {
        return when (val result = dataSource.markListItemBought(listItemId, bought)) {
            is Result.Success -> {
                Result.Success(true)
            }

            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }
}
