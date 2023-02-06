package de.wohlfrom.didyouget.data.sources

import android.util.Log
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.graphql.ShoppingListsQuery
import java.io.IOException
import java.util.LinkedList

class ShoppingListDataSource {

    suspend fun loadShoppingList(): Result<List<ShoppingList>> {
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
                shoppingLists.add(ShoppingList(shoppingListData!!.id, shoppingListData.name))
            }
            return Result.Success(shoppingLists)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error fetching shopping list", e))
        }
    }
}
