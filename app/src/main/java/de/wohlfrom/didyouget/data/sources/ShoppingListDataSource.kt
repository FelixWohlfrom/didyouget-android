package de.wohlfrom.didyouget.data.sources

import android.util.Log
import com.apollographql.apollo.api.Optional
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.mutations.AddShoppingListItemMutation
import de.wohlfrom.didyouget.mutations.AddShoppingListMutation
import de.wohlfrom.didyouget.mutations.DeleteShoppingListItemMutation
import de.wohlfrom.didyouget.mutations.DeleteShoppingListMutation
import de.wohlfrom.didyouget.mutations.MarkShoppingListItemBoughtMutation
import de.wohlfrom.didyouget.mutations.RenameShoppingListMutation
import de.wohlfrom.didyouget.mutations.UpdateShoppingListItemMutation
import de.wohlfrom.didyouget.queries.ShoppingListsQuery
import de.wohlfrom.didyouget.type.AddShoppingListInput
import de.wohlfrom.didyouget.type.AddShoppingListItemInput
import de.wohlfrom.didyouget.type.BoughtShoppingListItemInput
import de.wohlfrom.didyouget.type.DeleteShoppingListInput
import de.wohlfrom.didyouget.type.DeleteShoppingListItemInput
import de.wohlfrom.didyouget.type.RenameShoppingListInput
import de.wohlfrom.didyouget.type.UpdateShoppingListItemInput
import java.io.IOException
import java.util.LinkedList

class ShoppingListDataSource {

    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        val response = apolloClient().query(ShoppingListsQuery()).execute()

        if (response.data == null || response.data!!.shoppingLists == null) {
            if (response.exception != null) {
                Log.e("loadShoppingList", response.exception.toString())
                return Result.Error(IOException("Error fetching shopping list", response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }

        val shoppingListsData = response.data!!.shoppingLists!!
        val shoppingLists = LinkedList<ShoppingList>()
        for (shoppingListData in shoppingListsData) {
            val shoppingListItems = LinkedList<ListItem>()
            for (listItem in shoppingListData!!.listItems ?: LinkedList()) {
                shoppingListItems.add(
                    ListItem(
                        listItem!!.id,
                        listItem.value,
                        listItem.bought,
                    )
                )
            }

            shoppingLists.add(
                ShoppingList(
                    shoppingListData.id,
                    shoppingListData.name,
                    shoppingListItems
                )
            )
        }
        return Result.Success(shoppingLists)
    }

    suspend fun addShoppingList(name: String): Result<ShoppingList> {
        val response = apolloClient().mutation(
                AddShoppingListMutation(AddShoppingListInput(name))
            ).execute()

        if (response.data == null || response.data!!.addShoppingList == null) {
            if (response.exception != null) {
                Log.e("addShoppingList", response.exception.toString())
                return Result.Error(IOException("Error adding shopping list", response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }

        val newList = response.data!!.addShoppingList!!
        return Result.Success(ShoppingList(newList.id, newList.name))
    }

    suspend fun renameShoppingList(itemId: String, newName: String): Result<Boolean> {
        val response = apolloClient().mutation(
                RenameShoppingListMutation(RenameShoppingListInput(id = itemId, name = newName))
            ).execute()

        if (response.data == null) {
            if (response.exception != null) {
                Log.e("renameShoppingList", response.exception.toString())
                return Result.Error(IOException("Error renaming shopping list", response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }

        val success = response.data!!.renameShoppingList
        return if (success.success) {
            Result.Success(true)
        } else if (!success.failureMessage.isNullOrEmpty()) {
            Result.Error(Exception(success.failureMessage))
        } else {
            Result.Error(Exception("Unknown error"))
        }
    }

    suspend fun deleteShoppingList(itemId: String): Result<Boolean> {
        val response = apolloClient().mutation(
                DeleteShoppingListMutation(DeleteShoppingListInput(itemId))
            ).execute()

        if (response.data == null) {
            if (response.exception != null) {
                Log.e("deleteShoppingList", response.exception.toString())
                return Result.Error(IOException("Error deleting shopping list", response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }

        val success = response.data!!.deleteShoppingList
        return if (success.success) {
            Result.Success(true)
        } else if (!success.failureMessage.isNullOrEmpty()) {
            Result.Error(Exception(success.failureMessage))
        } else {
            Result.Error(Exception("Unknown error"))
        }
    }

    suspend fun addListItem(listId: String, value: String): Result<ListItem> {
        val response = apolloClient().mutation(
                AddShoppingListItemMutation(AddShoppingListItemInput(
                    shoppingListId = listId,
                    value = value
                ))
            ).execute()

        if (response.data == null || response.data!!.addShoppingListItem == null) {
            if (response.exception != null) {
                Log.e("addListItem", response.exception.toString())
                return Result.Error(IOException("Error adding shopping list item",
                    response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }
        val newItem = response.data!!.addShoppingListItem!!
        return Result.Success(ListItem(newItem.id, newItem.value, newItem.bought))
    }

    suspend fun updateShoppingListItem(listItemId: String, newValue: String, bought: Boolean):
            Result<Boolean> {
        val response = apolloClient().mutation(
                UpdateShoppingListItemMutation(
                    UpdateShoppingListItemInput(
                        shoppingListItemId = listItemId,
                        newValue = newValue,
                        bought = Optional.present(bought)
                    )
                )
            ).execute()

        if (response.data == null) {
            if (response.exception != null) {
                Log.e("updateShoppingListItem", response.exception.toString())
                return Result.Error(IOException("Error update shopping list item",
                    response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }

        val success = response.data!!.updateShoppingListItem
        return if (success.success) {
            Result.Success(true)
        } else if (!success.failureMessage.isNullOrEmpty()) {
            Result.Error(Exception(success.failureMessage))
        } else {
            Result.Error(Exception("Unknown error"))
        }
    }

    suspend fun markListItemBought(listItemId: String, bought: Boolean): Result<Boolean> {
        val response = apolloClient().mutation(
                MarkShoppingListItemBoughtMutation(
                    BoughtShoppingListItemInput(
                        shoppingListItemId = listItemId,
                        bought = Optional.present(bought)
                    )
                )
            ).execute()

        if (response.data == null) {
            if (response.exception != null) {
                Log.e("markListItemBought", response.exception.toString())
                return Result.Error(IOException("Error marking shopping list item bought",
                    response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }

        val success = response.data!!.markShoppingListItemBought
        return if (success.success) {
            Result.Success(true)
        } else if (!success.failureMessage.isNullOrEmpty()) {
            Result.Error(Exception(success.failureMessage))
        } else {
            Result.Error(Exception("Unknown error"))
        }
    }

    suspend fun deleteShoppingListItem(listItemId: String): Result<Boolean> {
        val response = apolloClient().mutation(
                DeleteShoppingListItemMutation(DeleteShoppingListItemInput(listItemId))
            ).execute()

        if (response.data == null) {
            if (response.exception != null) {
                Log.e("deleteShoppingList", response.exception.toString())
                return Result.Error(IOException("Error delete shopping list", response.exception))
            } else {
                return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
            }
        }

        val success = response.data!!.deleteShoppingListItem
        return if (success.success) {
            Result.Success(true)
        } else if (!success.failureMessage.isNullOrEmpty()) {
            Result.Error(Exception(success.failureMessage))
        } else {
            Result.Error(Exception("Unknown error"))
        }
    }
}
