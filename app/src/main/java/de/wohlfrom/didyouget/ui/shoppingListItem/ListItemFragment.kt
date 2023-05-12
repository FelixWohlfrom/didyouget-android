package de.wohlfrom.didyouget.ui.shoppingListItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import de.wohlfrom.didyouget.databinding.FragmentListBinding
import de.wohlfrom.didyouget.ui.shoppingList.ShoppingListViewModel

/**
 * A fragment representing a list of Items.
 */
class ListItemFragment : Fragment() {

    private val shoppingListViewModel: ShoppingListViewModel by activityViewModels {
        ShoppingListViewModel.Factory
    }
    private lateinit var binding: FragmentListBinding
    private val args: ListItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shoppingListViewModel.shoppingListItems.observe(viewLifecycleOwner,
            Observer { listItems ->
                listItems ?: return@Observer
                this.showShoppingListItems(listItems)
            }
        )

        shoppingListViewModel.loadListItems(args.listId)
    }

    private fun showShoppingListItems(model: ListItemView) {
        // Set the adapter
        val view = binding.list
        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = ListItemAdapter(model.shoppingListItems)
        }
    }
}
