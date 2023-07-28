package de.wohlfrom.didyouget.ui.shoppingListItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.wohlfrom.didyouget.databinding.FragmentEditListItemBinding
import de.wohlfrom.didyouget.ui.common.SimpleResult
import de.wohlfrom.didyouget.ui.shoppingList.ShoppingListViewModel

/**
 * A fragment showing an add/edit possibility for a shopping list item.
 */
class EditListItemFragment : Fragment() {

    private val shoppingListViewModel: ShoppingListViewModel by activityViewModels {
        ShoppingListViewModel.Factory
    }
    private lateinit var binding: FragmentEditListItemBinding
    private val args: EditListItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditListItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handler for save button click
        binding.saveButton.setOnClickListener {
            val itemValue = binding.editValue.text.toString()
            val itemBought = binding.itemBought.isChecked
            shoppingListViewModel.updateShoppingListItem(args.listItemId, itemValue, itemBought) {
                handleResult(it)
            }
        }

        binding.editValue.setText(args.listItemValue)
        binding.itemBought.isChecked = args.listItemBought
    }
    
    private fun handleResult(result: SimpleResult) {
        result.error?.let {
            showFailedMessage(it)
        }
        result.success?.let {
            this.findNavController().popBackStack()
        }
    }

    private fun showFailedMessage(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}
