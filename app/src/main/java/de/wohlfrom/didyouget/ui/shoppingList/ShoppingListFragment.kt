package de.wohlfrom.didyouget.ui.shoppingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.wohlfrom.didyouget.databinding.FragmentListBinding

/**
 * A fragment representing a shopping list.
 */
class ShoppingListFragment: Fragment() {

    private val shoppingListViewModel: ShoppingListViewModel by activityViewModels {
        ShoppingListViewModel.Factory
    }
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

        binding.addList.setOnClickListener {
            it.findNavController().navigate(
                ShoppingListFragmentDirections.showShoppingListItemAddEdit(null, null))
        }

        shoppingListViewModel.loadShoppingLists()
    }

    private fun showShoppingLists(model: ShoppingListView) {
        // Set the adapter
        val view = binding.list
        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = ShoppingListAdapter(shoppingListViewModel, model.shoppingLists)
        }
    }

    private fun showLoadingFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}
