package de.wohlfrom.didyouget.ui.shoppingListItem

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
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
class EditListItemFragmentTest {
    @Before
    fun setup() {
        assumeTrue("Skipping fragment test in release mode", BuildConfig.DEBUG)
    }

    /**
     * Verifies that an edited list item can be stored in the repository
     * successfully.
     */
    @Test
    fun testEditListItemSuccess() {
        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().updateShoppingListItem(any(), any(), any()) } returns Result.Success(true)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<EditListItemFragment>(
            fragmentArgs = Bundle().apply {
                putString("listItemId", "0")
                putString("listItemValue", "Old Value")
                putBoolean("listItemBought", false)
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.editListItemFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.editValue)).perform(clearText(), typeText("New Value"))
        onView(withId(R.id.itemBought)).perform(click())
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).updateShoppingListItem("0", "New Value", true) }
    }

    /**
     * Verifies that in case of a failure a list item is not stored
     * in the repository.
     */
    @Test
    fun testEditListItemFailure() {
        val expectedFailureMessage = "Failure message"

        mockkConstructor(ShoppingListRepository::class)
        coEvery { anyConstructed<ShoppingListRepository>().updateShoppingListItem(any(), any(), any()) } returns Result.Error(
            Exception(expectedFailureMessage)
        )

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer<EditListItemFragment>(
            fragmentArgs = Bundle().apply {
                putString("listItemId", "0")
                putString("listItemValue", "Old Value")
                putBoolean("listItemBought", false)
            }
        )

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.navigation_graph)
            navController.setCurrentDestination(R.id.editListItemFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.editValue)).perform(clearText(), typeText("New Value"))
        onView(withId(R.id.itemBought)).perform(click())
        onView(withId(R.id.saveButton)).perform(click())

        coVerify { ShoppingListRepository(mockk()).updateShoppingListItem("0", "New Value", true) }
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(expectedFailureMessage)
    }
}
