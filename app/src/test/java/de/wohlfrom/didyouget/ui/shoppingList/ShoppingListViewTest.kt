package de.wohlfrom.didyouget.ui.shoppingList

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import de.wohlfrom.didyouget.BuildConfig
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShoppingListViewTest {

    @Before
    fun setup() {
        assumeTrue("Skipping fragment test in release mode", BuildConfig.DEBUG)
    }

    @Test
    fun testInstantiation() {
        val scenario = launchFragmentInContainer<ShoppingListFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )
        scenario.moveToState(Lifecycle.State.RESUMED)
    }
}
