package de.wohlfrom.didyouget.data.sources

import android.util.Log
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.graphql.ShoppingListsQuery
import java.io.IOException
import java.util.LinkedList

class ShoppingListDataSource {

    suspend fun loadShoppingLists(): Result<List<ShoppingList>> {
        try {
            val response = try {
                apolloClient().query(ShoppingListsQuery()).execute()
            } catch (e: Exception) {
                Log.e("loadShoppingList", e.toString())
                null
            }

            val shoppingListsData = response?.data?.shoppingLists
            if (shoppingListsData == null || response.hasErrors()) {
                return Result.Error(Exception(response?.errors?.get(0)?.message ?: "Unknown error"))
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
        } catch (e: Throwable) {
            return Result.Error(IOException("Error fetching shopping list", e))
        }
    }
}
