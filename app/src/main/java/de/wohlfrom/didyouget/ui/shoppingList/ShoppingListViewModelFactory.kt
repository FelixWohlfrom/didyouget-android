package de.wohlfrom.didyouget.ui.shoppingList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource

/**
 * ViewModel provider factory to instantiate ShoppingListViewModel.
 * Required given ShoppingListViewModel has a non-empty constructor
 */
class ShoppingListViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            return ShoppingListViewModel(
                shoppingListRepository = ShoppingListRepository(
                    dataSource = ShoppingListDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
