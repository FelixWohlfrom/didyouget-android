package de.wohlfrom.didyouget.ui.shoppingList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.wohlfrom.didyouget.data.ShoppingListRepository
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.data.sources.Result
import de.wohlfrom.didyouget.data.sources.ShoppingListDataSource
import de.wohlfrom.didyouget.ui.shoppingListItem.ListItemView
import kotlinx.coroutines.launch
import java.util.LinkedList

class ShoppingListViewModel(private val shoppingListRepository: ShoppingListRepository) :
    ViewModel() {

    private val _shoppingListResult = MutableLiveData<ShoppingListResult>()
    val shoppingListResult: LiveData<ShoppingListResult> = _shoppingListResult

    private val _shoppingListItems = MutableLiveData<ListItemView>()
    val shoppingListItems: LiveData<ListItemView> = _shoppingListItems

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

    fun loadListItems(itemId: String) {
        viewModelScope.launch {
            _shoppingListItems.value = ListItemView(shoppingListRepository.loadListItems(itemId))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
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
    }
}
