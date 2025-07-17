package de.wohlfrom.didyouget.data.sources

import com.google.common.truth.Truth.assertThat
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.model.ShoppingList
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ShoppingListRepositoryTest {

    /**
     * Verifies the successful loading of a shopping list
     */
    @Test
    fun loadShoppingLists_Success() = runBlocking {
        val shoppingListsToLoad = listOf(
            ShoppingList("1", "MyList", null),
            ShoppingList("1", "MySecondList", null)
        )

        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Success(shoppingListsToLoad)

        val repository = ShoppingListRepository(dataSource)
        val result = repository.loadShoppingLists()

        if (result is Result.Success) {
            assertThat(result.data).isEqualTo(shoppingListsToLoad)
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
            assertThat(result.exception.message).isEqualTo("An error happened")
        } else {
            TestCase.fail("Unexpected success on loading shopping lists")
        }
    }
}