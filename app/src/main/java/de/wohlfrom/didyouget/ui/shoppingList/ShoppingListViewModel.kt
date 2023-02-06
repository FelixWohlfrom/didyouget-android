package de.wohlfrom.didyouget.ui.shoppingList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.sources.Result
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val shoppingListRepository: ShoppingListRepository) : ViewModel() {

    private val _shoppingListResult = MutableLiveData<ShoppingListResult>()
    val shoppingListResult: LiveData<ShoppingListResult> = _shoppingListResult

    fun loadShoppingLists() {
        viewModelScope.launch {
            val result = shoppingListRepository.loadShoppingLists()

            if (result is Result.Success) {
                _shoppingListResult.value =
                    ShoppingListResult(success = ShoppingListView(shoppingLists = result.data))
            } else if (result is Result.Error) {
                _shoppingListResult.value = ShoppingListResult(error = result.exception.message)
            }
        }
    }
}
