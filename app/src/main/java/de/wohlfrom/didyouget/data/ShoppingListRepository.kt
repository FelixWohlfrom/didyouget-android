package de.wohlfrom.didyouget.data

import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource
import java.util.LinkedList

class ShoppingListRepository(val dataSource: ShoppingListDataSource) {

    private lateinit var _shoppingLists: MutableList<ShoppingList>
    private var _shoppingListLoaded: Boolean = false
    private var _shoppingListsById: MutableMap<String, ShoppingList> = HashMap()
    private var _shoppingListItemsById: MutableMap<String, ListItem> = HashMap()

    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        val result = dataSource.loadShoppingLists()

        if (result is Result.Success) {
            _shoppingListLoaded = true
            _shoppingLists = result.data.toMutableList()
            _shoppingListsById.clear()
            _shoppingListItemsById.clear()
            _shoppingLists.forEach { list ->
                _shoppingListsById[list.id] = list
                list.listItems?.forEach { item ->
                    _shoppingListItemsById[item.id] = item
                }
            }
        }

        return result
    }

    /**
     * Returns the list items for a given shopping list id
     *
     * @param itemId The id if the shopping list for which the items should be loaded
     * @return The items of the given list
     */
    suspend fun loadListItems(itemId: String): List<ListItem> {
        if (!_shoppingListLoaded) {
            loadShoppingLists()
        }

        return _shoppingListsById[itemId]?.listItems ?: LinkedList()
    }

    suspend fun addShoppingList(name: String): Result<Boolean> {
        return when (val result = dataSource.addShoppingList(name)) {
            is Result.Success -> {
                _shoppingListsById[result.data.id] = result.data
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
            _shoppingListsById[itemId]!!.name = newName
        }
        return result
    }

    suspend fun deleteShoppingList(itemId: String): Result<List<ShoppingList>> {
        return when (val result = dataSource.deleteShoppingList(itemId)) {
            is Result.Success -> {
                val shoppingList = _shoppingListsById[itemId]
                _shoppingListsById.remove(itemId)
                _shoppingLists.removeIf { it.id == itemId }
                shoppingList!!.listItems?.forEach {
                    _shoppingListItemsById.remove(it.id)
                }
                Result.Success(_shoppingLists)
            }

            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }

    suspend fun addListItem(listId: String, value: String): Result<Boolean> {
        return when (val result = dataSource.addListItem(listId, value)) {
            is Result.Success -> {
                _shoppingListsById[listId]!!.listItems!!.add(result.data)
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
                _shoppingListItemsById[listItemId]!!.value = newValue
                _shoppingListItemsById[listItemId]!!.bought = bought
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

    suspend fun deleteShoppingListItem(listItemId: String): Result<Boolean> {
        return when (val result = dataSource.deleteShoppingListItem(listItemId)) {
            is Result.Success -> {
                val listItem = _shoppingListItemsById[listItemId]
                _shoppingLists.forEach {
                    it.listItems?.remove(listItem)
                }
                _shoppingListItemsById.remove(listItemId)
                Result.Success(true)
            }

            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }
}
