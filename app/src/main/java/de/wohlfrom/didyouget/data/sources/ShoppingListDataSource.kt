package de.wohlfrom.didyouget.data.sources

import android.util.Log
import com.apollographql.apollo3.api.Optional
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.graphql.AddShoppingListItemMutation
import de.wohlfrom.didyouget.graphql.AddShoppingListMutation
import de.wohlfrom.didyouget.graphql.MarkShoppingListItemBoughtMutation
import de.wohlfrom.didyouget.graphql.RenameShoppingListMutation
import de.wohlfrom.didyouget.graphql.ShoppingListsQuery
import de.wohlfrom.didyouget.graphql.UpdateShoppingListItemMutation
import de.wohlfrom.didyouget.graphql.type.AddShoppingListInput
import de.wohlfrom.didyouget.graphql.type.AddShoppingListItemInput
import de.wohlfrom.didyouget.graphql.type.BoughtShoppingListItemInput
import de.wohlfrom.didyouget.graphql.type.RenameShoppingListInput
import de.wohlfrom.didyouget.graphql.type.UpdateShoppingListItemInput
import java.io.IOException
import java.util.LinkedList

class ShoppingListDataSource {

    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        val response = try {
            apolloClient().query(ShoppingListsQuery()).execute()
        } catch (e: Exception) {
            Log.e("loadShoppingList", e.toString())
            return Result.Error(IOException("Error fetching shopping list", e))
        }

        val shoppingListsData = response.data?.shoppingLists
        if (shoppingListsData == null || response.hasErrors()) {
            return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
        }

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
        val response = try {
            apolloClient().mutation(
                AddShoppingListMutation(AddShoppingListInput(name))
            ).execute()
        } catch (e: Exception) {
            Log.e("addShoppingList", e.toString())
            return Result.Error(IOException("Error adding shopping list", e))
        }
        val newList = response.data?.addShoppingList
        if (newList == null || response.hasErrors()) {
            return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
        }
        return Result.Success(ShoppingList(newList.id, newList.name))
    }

    suspend fun renameShoppingList(itemId: String, newName: String): Result<Boolean> {
        val response = try {
            apolloClient().mutation(
                RenameShoppingListMutation(RenameShoppingListInput(itemId, newName))
            ).execute()
        } catch (e: Exception) {
            Log.e("renameShoppingList", e.toString())
            return Result.Error(IOException("Error renaming shopping list", e))
        }
        val success = response.data?.renameShoppingList
        if (success != null) {
            return if (success.success) {
                Result.Success(true)
            } else if (!success.failureMessage.isNullOrEmpty()) {
                Result.Error(Exception(success.failureMessage))
            } else {
                Result.Error(Exception("Unknown error"))
            }
        }
        return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
    }

    suspend fun addListItem(listId: String, value: String): Result<ListItem> {
        val response = try {
            apolloClient().mutation(
                AddShoppingListItemMutation(AddShoppingListItemInput(listId, value))
            ).execute()
        } catch (e: Exception) {
            Log.e("addListItem", e.toString())
            return Result.Error(IOException("Error adding shopping list item", e))
        }
        val newItem = response.data?.addShoppingListItem
        if (newItem == null || response.hasErrors()) {
            return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
        }
        return Result.Success(ListItem(newItem.id, newItem.value, newItem.bought))
    }

    suspend fun updateShoppingListItem(listItemId: String, newValue: String, bought: Boolean):
            Result<Boolean> {
        val response = try {
            apolloClient().mutation(
                UpdateShoppingListItemMutation(
                    UpdateShoppingListItemInput(listItemId, newValue, Optional.present(bought))
                )
            ).execute()
        } catch (e: Exception) {
            Log.e("updateShoppingListItem", e.toString())
            return Result.Error(IOException("Error update shopping list item", e))
        }
        val success = response.data?.updateShoppingListItem
        if (success != null) {
            return if (success.success) {
                Result.Success(true)
            } else if (!success.failureMessage.isNullOrEmpty()) {
                Result.Error(Exception(success.failureMessage))
            } else {
                Result.Error(Exception("Unknown error"))
            }
        }
        return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
    }

    suspend fun markListItemBought(listItemId: String, bought: Boolean): Result<Boolean> {
        val response = try {
            apolloClient().mutation(
                MarkShoppingListItemBoughtMutation(
                    BoughtShoppingListItemInput(listItemId, Optional.present(bought))
                )
            ).execute()
        } catch (e: Exception) {
            Log.e("markListItemBought", e.toString())
            return Result.Error(IOException("Error marking shopping list item bought", e))
        }
        val success = response.data?.markShoppingListItemBought
        if (success != null) {
            return if (success.success) {
                Result.Success(true)
            } else if (!success.failureMessage.isNullOrEmpty()) {
                Result.Error(Exception(success.failureMessage))
            } else {
                Result.Error(Exception("Unknown error"))
            }
        }
        return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
    }
}
