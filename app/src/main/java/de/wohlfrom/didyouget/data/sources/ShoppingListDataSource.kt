package de.wohlfrom.didyouget.data.sources

import android.util.Log
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.graphql.AddShoppingListItemMutation
import de.wohlfrom.didyouget.graphql.AddShoppingListMutation
import de.wohlfrom.didyouget.graphql.ShoppingListsQuery
import de.wohlfrom.didyouget.graphql.type.AddShoppingListInput
import de.wohlfrom.didyouget.graphql.type.AddShoppingListItemInput
import de.wohlfrom.didyouget.graphql.type.ShoppingListItem
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
            return Result.Error(IOException("Error adding shopping list item", e))
        }
        val newList = response.data?.addShoppingList
        if (newList == null || response.hasErrors()) {
            return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
        }
        return Result.Success(ShoppingList(newList.id, newList.name))
    }

    suspend fun addListItem(listId: String, name: String): Result<ListItem> {
        val response = try {
            apolloClient().mutation(
                AddShoppingListItemMutation(AddShoppingListItemInput(listId, name))
            ).execute()
        } catch (e: Exception) {
            Log.e("addShoppingList", e.toString())
            return Result.Error(IOException("Error adding shopping list item", e))
        }
        val newItem = response.data?.addShoppingListItem
        if (newItem == null || response.hasErrors()) {
            return Result.Error(Exception(response.errors?.get(0)?.message ?: "Unknown error"))
        }
        return Result.Success(ListItem(newItem.id, newItem.value, newItem.bought))
    }
}
