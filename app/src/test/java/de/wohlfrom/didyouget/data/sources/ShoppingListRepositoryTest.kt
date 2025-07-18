package de.wohlfrom.didyouget.data.sources

import com.google.common.truth.Truth.assertThat
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.model.ShoppingList
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.LinkedList

class ShoppingListRepositoryTest {

    /**
     * Verifies the successful loading of a shopping list
     */
    @Test
    fun loadShoppingLists_Success() = runBlocking {
        val shoppingListsToLoad = listOf(
            ShoppingList("1", "MyList"),
            ShoppingList("2", "MySecondList", null),
            ShoppingList(
                "3", "MyThirdList",
                mutableListOf(ListItem("1", "Item", true))
            )
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

    /**
     * Verifies that the items of a single list can successfully be fetched.
     */
    @Test
    fun loadShoppingListsItems_Success() = runBlocking {
        val shoppingListsToLoad = listOf(
            ShoppingList("1", "MyList"),
            ShoppingList("2", "MySecondList", null),
            ShoppingList(
                "3", "MyThirdList",
                mutableListOf(
                    ListItem("1", "Item", true),
                    ListItem("2", "SecondItem", false)
                )
            )
        )

        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Success(shoppingListsToLoad)

        val repository = ShoppingListRepository(dataSource)
        var result = repository.loadListItems("1")
        assertThat(result).hasSize(0)

        result = repository.loadListItems("2")
        assertThat(result).hasSize(0)

        result = repository.loadListItems("3")
        assertThat(result).hasSize(2)
        assertThat(result[1].id).isEqualTo("2")
        assertThat(result[1].value).isEqualTo("SecondItem")

        result = repository.loadListItems("invalid")
        assertThat(result).hasSize(0)
    }

    /**
     * Verifies that a new shopping list can be added successfully.
     */
    @Test
    fun addShoppingList_Success() = runBlocking {
        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Success(listOf())
        coEvery { dataSource.addShoppingList(any()) } answers {
            Result.Success(ShoppingList("42", firstArg<String>()))
        }

        val repository = ShoppingListRepository(dataSource)
        repository.loadShoppingLists()

        val result = repository.addShoppingList("New list")
        coVerify { dataSource.addShoppingList("New list") }
        assertThat(result is Result.Success).isTrue()
    }

    /**
     * Verifies that adding a new shopping list returns an error if storing in
     * the datasource fails.
     */
    @Test
    fun addShoppingList_Failure() = runBlocking {
        val expectedFailure = "Update failure"

        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.addShoppingList(any()) } answers {
            Result.Error(Exception(expectedFailure))
        }

        val repository = ShoppingListRepository(dataSource)

        val result = repository.addShoppingList("New list")
        coVerify { dataSource.addShoppingList("New list") }
        if (result is Result.Error) {
            assertThat(result.exception.message).isEqualTo(expectedFailure)
        } else {
            TestCase.fail("Unexpected success on adding a shopping list")
        }
    }

    /**
     * Verifies that an existing shopping list can be renamed successfully.
     */
    @Test
    fun renameShoppingList_Success() = runBlocking {
        val mockList = listOf(ShoppingList("42", "Old name"))
        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Success(mockList)
        coEvery { dataSource.renameShoppingList(any(), any()) } returns
                Result.Success(true)

        val repository = ShoppingListRepository(dataSource)
        repository.loadShoppingLists()

        val result = repository.renameShoppingList("42", "New name")
        coVerify { dataSource.renameShoppingList("42", "New name") }
        assertThat(result is Result.Success).isTrue()
        assertThat(mockList[0].name).isEqualTo("New name")
    }

    /**
     * Verifies that renaming a shopping list returns an error if storing in
     * the datasource fails.
     */
    @Test
    fun renameShoppingList_Failure() = runBlocking {
        val expectedFailure = "Update failure"

        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.renameShoppingList(any(), any()) } returns
                Result.Error(Exception(expectedFailure))

        val repository = ShoppingListRepository(dataSource)

        val result = repository.renameShoppingList("42", "New name")
        coVerify { dataSource.renameShoppingList("42", "New name") }
        if (result is Result.Error) {
            assertThat(result.exception.message).isEqualTo(expectedFailure)
        } else {
            TestCase.fail("Unexpected success on renaming a shopping list")
        }
    }

    /**
     * Verifies that an empty shopping list can be deleted successfully.
     */
    @Test
    fun deleteShoppingList_Success_NoItems() = runBlocking {
        val mockList = listOf(ShoppingList("42", "List name"))
        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Success(mockList)
        coEvery { dataSource.deleteShoppingList(any()) } returns Result.Success(true)

        val repository = ShoppingListRepository(dataSource)
        repository.loadShoppingLists()

        val result = repository.deleteShoppingList("42")
        coVerify { dataSource.deleteShoppingList("42") }
        assertThat(result is Result.Success).isTrue()
    }

    /**
     * Verifies that a non empty shopping list can be deleted successfully.
     */
    @Test
    fun deleteShoppingList_Success_WithItems() = runBlocking {
        val mockList = listOf(
            ShoppingList("42", "List name",
                mutableListOf(ListItem("1", "List item", false))
            )
        )
        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.loadShoppingLists() } returns Result.Success(mockList)
        coEvery { dataSource.deleteShoppingList(any()) } returns Result.Success(true)

        val repository = ShoppingListRepository(dataSource)
        repository.loadShoppingLists()

        val result = repository.deleteShoppingList("42")
        coVerify { dataSource.deleteShoppingList("42") }
        assertThat(result is Result.Success).isTrue()
    }

    /**
     * Verifies that renaming a shopping list returns an error if deleting it in
     * the datasource fails.
     */
    @Test
    fun deleteShoppingList_Failure() = runBlocking {
        val expectedFailure = "Update failure"

        val dataSource = mockk<ShoppingListDataSource>()
        coEvery { dataSource.deleteShoppingList(any()) } returns
                Result.Error(Exception(expectedFailure))

        val repository = ShoppingListRepository(dataSource)

        val result = repository.deleteShoppingList("42")
        coVerify { dataSource.deleteShoppingList("42") }
        if (result is Result.Error) {
            assertThat(result.exception.message).isEqualTo(expectedFailure)
        } else {
            TestCase.fail("Unexpected success on deleting a shopping list")
        }
    }
}
