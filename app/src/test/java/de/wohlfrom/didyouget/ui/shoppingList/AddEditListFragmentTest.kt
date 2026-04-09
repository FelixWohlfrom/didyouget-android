package de.wohlfrom.didyouget.ui.shoppingList

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import de.wohlfrom.didyouget.BuildConfig
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.ui.shoppingListItem.AddListItemFragment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowToast

@RunWith(AndroidJUnit4::class)
class AddEditListFragmentTest {
    @Before
    fun setup() {
        assumeTrue("Skipping fragment test in release mode", BuildConfig.DEBUG)
    }

    /**
     * Verifies that an added list can be stored in the repository successfully.
     */
    @Test
    fun testAddListSuccess() {
        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().addShoppingList(any()) } returns Result.Success(true)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<AddEditListFragment>(
            fragmentArgs = Bundle().apply {
                putString("listId", null)
                putString("listName", null)
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.addEditListFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.addEditName)).check(matches(withText("")))
        onView(withId(R.id.addEditListLabel)).check(matches(withText(R.string.add_new_list)));

        onView(withId(R.id.addEditName)).perform(typeText("New List"))
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).addShoppingList("New List") }
    }

    /**
     * Verifies that in case of a failure a list is not stored
     * in the repository.
     */
    @Test
    fun testAddListFailure() {
        val expectedFailureMessage = "Failure message"

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().addShoppingList(any()) } returns Result.Error(
            Exception(expectedFailureMessage)
        )

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<AddEditListFragment>(
            fragmentArgs = Bundle().apply {
                putString("listId", null)
                putString("listName", null)
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.addEditListFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.addEditName)).check(matches(withText("")))
        onView(withId(R.id.addEditListLabel)).check(matches(withText(R.string.add_new_list)));

        onView(withId(R.id.addEditName)).perform(typeText("New List"))
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).addShoppingList("New List") }
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(expectedFailureMessage)
    }

    /**
     * Verifies that an edited list can be stored in the repository successfully.
     */
    @Test
    fun testEditListSuccess() {
        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().renameShoppingList(any(), any()) } returns Result.Success(true)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<AddEditListFragment>(
            fragmentArgs = Bundle().apply {
                putString("listId", "0")
                putString("listName", "Old list")
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.addEditListFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.addEditName)).check(matches(withText("Old list")))
        onView(withId(R.id.addEditListLabel)).check(matches(withText(R.string.edit_list)))

        onView(withId(R.id.addEditName)).perform(clearText(), typeText("New Item"))
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).renameShoppingList("0", "New Item") }
    }

    /**
     * Verifies that in case of a failure an edited list is not stored
     * in the repository.
     */
    @Test
    fun testEditListFailure() {
        val expectedFailureMessage = "Failure message"

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().renameShoppingList(any(), any()) } returns Result.Error(
            Exception(expectedFailureMessage)
        )

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<AddEditListFragment>(
            fragmentArgs = Bundle().apply {
                putString("listId", "0")
                putString("listName", "Old list")
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.addEditListFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.addEditName)).check(matches(withText("Old list")))
        onView(withId(R.id.addEditListLabel)).check(matches(withText(R.string.edit_list)));

        onView(withId(R.id.addEditName)).perform(clearText(), typeText("New Item"))
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).renameShoppingList("0", "New Item") }
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(expectedFailureMessage)
    }
}
