package de.wohlfrom.didyouget.ui.shoppingList

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import de.wohlfrom.didyouget.BuildConfig
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowToast
import java.util.LinkedList

@RunWith(AndroidJUnit4::class)
class ShoppingListFragmentTest {
    @Before
    fun setup() {
        assumeTrue("Skipping fragment test in release mode", BuildConfig.DEBUG)
    }

    @Test
    fun testLoadingFailure() {
        val expectedFailureMessage = "Failure message"

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().loadShoppingLists() } returns
                Result.Error(Exception(expectedFailureMessage))

        val scenario = launchFragmentInContainer<ShoppingListFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.moveToState(Lifecycle.State.DESTROYED)

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(expectedFailureMessage)
    }

    @Test
    fun testLoadingEmptyListSuccess() {
        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().loadShoppingLists() } returns
                Result.Success(data = LinkedList<ShoppingList>())

        val scenario = launchFragmentInContainer<ShoppingListFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.moveToState(Lifecycle.State.DESTROYED)

        coVerify { ShoppingListRepository(mockk()).loadShoppingLists() }
    }

    @Test
    fun testLoadingListSuccess() {
        val resultList = listOf(
            ShoppingList("1", "First item"),
            ShoppingList("2", "Second item")
        )

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().loadShoppingLists() } returns
                Result.Success(data = resultList)

        val scenario = launchFragmentInContainer<ShoppingListFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.list)).perform(
            scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText("First item"))))

        scenario.moveToState(Lifecycle.State.DESTROYED)

        coVerify { ShoppingListRepository(mockk()).loadShoppingLists() }
    }

    @Test
    fun testClickOnList() {
        val resultList = listOf(
            ShoppingList("1", "First item"),
            ShoppingList("2", "Second item")
        )

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().loadShoppingLists() } returns
                Result.Success(data = resultList)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val scenario = launchFragmentInContainer<ShoppingListFragment>()
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.shoppingListFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.list))
            .perform(scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText("First item"))))
        onView(withText("First item")).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.shoppingListItemFragment)
        val currentArgs = navController.backStack.last().arguments
        assertThat(currentArgs?.getInt("listId")).isEqualTo(0)
        assertThat(currentArgs?.getString("listName")).isEqualTo("First item")
    }
}
