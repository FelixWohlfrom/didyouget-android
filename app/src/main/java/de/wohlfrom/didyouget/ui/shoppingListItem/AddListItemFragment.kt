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
import de.wohlfrom.didyouget.databinding.FragmentAddListItemBinding
import de.wohlfrom.didyouget.ui.common.SimpleResult
import de.wohlfrom.didyouget.ui.shoppingList.ShoppingListViewModel

/**
 * A fragment showing an add/edit possibility for a shopping list item.
 */
class AddListItemFragment : Fragment() {

    private val shoppingListViewModel: ShoppingListViewModel by activityViewModels {
        ShoppingListViewModel.Factory
    }
    private lateinit var binding: FragmentAddListItemBinding
    private val args: AddListItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddListItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handler for save button click
        binding.saveButton.setOnClickListener {
            val itemValue = binding.addValue.text.toString()
            shoppingListViewModel.addListItem(args.listId, itemValue) {
                handleResult(it)
            }
        }
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
