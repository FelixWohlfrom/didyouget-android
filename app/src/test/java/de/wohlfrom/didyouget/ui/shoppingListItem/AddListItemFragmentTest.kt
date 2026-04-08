package de.wohlfrom.didyouget.ui.shoppingListItem

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import de.wohlfrom.didyouget.BuildConfig
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.data.ShoppingListRepository
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

@RunWith(AndroidJUnit4::class)
class AddListItemFragmentTest {
    @Before
    fun setup() {
        assumeTrue("Skipping fragment test in release mode", BuildConfig.DEBUG)
    }

    /**
     * Verifies that an added list item can be stored in the repository
     * successfully.
     */
    @Test
    fun testAddListItemSuccess() {
        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().addListItem(any(), any()) } returns Result.Success(true)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<AddListItemFragment>(
            fragmentArgs = Bundle().apply {
                putString("listId", "0")
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.addListItemFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.addValue)).perform(typeText("New Item"))
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).addListItem("0", "New Item") }
    }

    /**
     * Verifies that in case of a failure a list item is not stored
     * in the repository.
     */
    @Test
    fun testAddListItemFailure() {
        val expectedFailureMessage = "Failure message"

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().addListItem(any(), any()) } returns Result.Error(
            Exception(expectedFailureMessage)
        )

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<AddListItemFragment>(
            fragmentArgs = Bundle().apply {
                putString("listId", "0")
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.addListItemFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.addValue)).perform(typeText("New Item"))
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).addListItem("0", "New Item") }
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(expectedFailureMessage)
    }
}
