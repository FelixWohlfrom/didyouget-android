package de.wohlfrom.didyouget.ui.shoppingListItem

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.wohlfrom.didyouget.BuildConfig
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.data.sources.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListItemFragmentTest {
    @Before
    fun setup() {
        assumeTrue("Skipping fragment test in release mode", BuildConfig.DEBUG)
    }

    @Test
    fun testLoadingListSuccess() {
        val listItems = listOf(
            ListItem("1", "First item", false),
            ListItem("2", "Second item", true)
        )

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().loadListItems(any()) } returns listItems

        val scenario = launchFragmentInContainer<ListItemFragment>(
            fragmentArgs = bundleOf(
                "listId" to "0",
                "listName" to "First list"
            ),
            initialState = Lifecycle.State.INITIALIZED
        )
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.listItem)).perform(
            scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText("First item"))))

        scenario.moveToState(Lifecycle.State.DESTROYED)

        coVerify { ShoppingListRepository(mockk()).loadListItems(any()) }
    }

    @Test
    fun testMarkBoughtSuccess() {
        val listItems = listOf(
            ListItem("1", "First item", false),
            ListItem("2", "Second item", true)
        )

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().loadListItems(any()) } returns listItems
        coEvery { anyConstructed<ShoppingListRepository>().markListItemBought(any(), any()) } returns Result.Success(true)

        launchFragmentInContainer<ListItemFragment>(
            fragmentArgs = bundleOf(
                "listId" to "0",
                "listName" to "First list"
            )
        )

        onView(withId(R.id.listItem))
            .perform(scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText("First item"))))
        onView(withText("First item")).perform(click())

        coVerify { ShoppingListRepository(mockk()).markListItemBought("1", true) }
    }
}
