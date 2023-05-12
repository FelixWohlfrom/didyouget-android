package de.wohlfrom.didyouget.data.sources

import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.model.ShoppingList
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.isA
import org.junit.Test
import java.util.LinkedList

class ShoppingListRepositoryTest {

    /**
     * Verifies the successful loading of a shopping list
     */
    @Test
    fun loadShoppingLists_Success() = runBlocking {
        val shoppingListsToLoad = LinkedList<ShoppingList>()
        shoppingListsToLoad.add(ShoppingList("1", "MyList", null))
        shoppingListsToLoad.add(ShoppingList("1", "MySecondList", null))

        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Success(shoppingListsToLoad)

        val repository = ShoppingListRepository(dataSource)
        val result = repository.loadShoppingLists()

        if (result is Result.Success) {
            assertThat(result.data, `is`(shoppingListsToLoad))
        } else {
            TestCase.fail("Failed to load shopping lists")
        }
    }

    /**
     * Verifies that failures during loading of the shopping list are successfully propagated
     */
    @Test
    fun loadShoppingLists_Failure() = runBlocking {
        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Error(
            exception = Exception("An error happened")
        )

        val repository = ShoppingListRepository(dataSource)
        val result = repository.loadShoppingLists()

        if (result is Result.Error) {
            assertThat(result.exception.message, `is`("An error happened"))
        } else {
            TestCase.fail("Unexpected success on loading shopping lists")
        }
    }
}