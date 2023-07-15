package de.wohlfrom.didyouget.data

import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource
import java.util.LinkedList

class ShoppingListRepository(val dataSource: ShoppingListDataSource) {

    private lateinit var _shoppingLists: MutableList<ShoppingList>
    private var _shoppingListsById: MutableMap<String, ShoppingList>? = null
    private var _shoppingListItemsById: MutableMap<String, ListItem>? = null

    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        val result = dataSource.loadShoppingLists()

        if (result is Result.Success) {
            _shoppingLists = result.data.toMutableList()
            _shoppingListsById = HashMap()
            _shoppingListItemsById = HashMap()
            _shoppingLists.forEach { list ->
                _shoppingListsById?.set(list.id, list)
                list.listItems?.forEach { item ->
                    _shoppingListItemsById?.set(item.id, item)
                }
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
        return when (val result = dataSource.addShoppingList(name)) {
            is Result.Success -> {
                _shoppingListsById?.set(result.data.id, result.data)
                _shoppingLists.add(result.data)
                Result.Success(true)
            }

            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }

    suspend fun renameShoppingList(itemId: String, newName: String): Result<Boolean> {
        val result = dataSource.renameShoppingList(itemId, newName)
        if (result is Result.Success) {
            _shoppingListsById?.get(itemId)!!.name = newName
        }
        return result
    }

    suspend fun addListItem(listId: String, value: String): Result<Boolean> {
        return when (val result = dataSource.addListItem(listId, value)) {
            is Result.Success -> {
                _shoppingListsById?.get(listId)!!.listItems!!.add(result.data)
                Result.Success(true)
            }

            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }

    suspend fun updateShoppingListItem(listItemId: String, newValue: String, bought: Boolean):
            Result<Boolean> {
        return when (val result = dataSource.updateShoppingListItem(listItemId, newValue, bought)) {
            is Result.Success -> {
                _shoppingListItemsById?.get(listItemId)!!.value = newValue
                _shoppingListItemsById?.get(listItemId)!!.bought = bought
                Result.Success(true)
            }

            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }

    suspend fun markListItemBought(listItemId: String, bought: Boolean): Result<Boolean> {
        return dataSource.markListItemBought(listItemId, bought)
    }
}
