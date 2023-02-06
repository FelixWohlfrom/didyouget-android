package de.wohlfrom.didyouget.ui.shoppingList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import de.wohlfrom.didyouget.databinding.FragmentListBinding

/**
 * A fragment representing a list of Items.
 */
class ShoppingListFragment : Fragment() {

    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shoppingListViewModel =
            ViewModelProvider(this, ShoppingListViewModelFactory())[ShoppingListViewModel::class.java]

        shoppingListViewModel.shoppingListResult.observe(viewLifecycleOwner,
            Observer { shoppingListResult ->
                shoppingListResult ?: return@Observer
                shoppingListResult.error?.let {
                    showLoadingFailed(it)
                }
                shoppingListResult.success?.let {
                    showShoppingLists(it)
                }
            }
        )

        shoppingListViewModel.loadShoppingLists()
    }

    private fun showShoppingLists(model: ShoppingListView) {
        for (shoppingList in model.shoppingLists) {
            Log.i("shoppingList", shoppingList.name)
        }

        // Set the adapter
        val view = binding.list
        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = ShoppingListAdapter(model.shoppingLists)
        }
    }

    private fun showLoadingFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}
