package de.wohlfrom.didyouget.ui.shoppingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.databinding.FragmentAddEditListBinding
import de.wohlfrom.didyouget.ui.common.SimpleResult

/**
 * A fragment showing an add/edit possibility for a shopping list.
 */
class AddEditListFragment : Fragment() {

    private val shoppingListViewModel: ShoppingListViewModel by activityViewModels {
        ShoppingListViewModel.Factory
    }
    private lateinit var binding: FragmentAddEditListBinding
    private val args: AddEditListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handler for save button click
        binding.saveButton.setOnClickListener {
            val listName = binding.addEditName.text.toString()
            if (args.listId == null) {
                shoppingListViewModel.addList(listName) {
                    handleResult(it)
                }
            } else {
                shoppingListViewModel.renameShoppingList(args.listId!!, listName) {
                    handleResult(it)
                }
            }
        }

        if (args.listName == null || args.listName!!.isBlank()) {
            binding.addEditListLabel.setText(R.string.add_new_list)
        } else {
            binding.addEditListLabel.setText(R.string.edit_list)
        }

        binding.addEditName.setText(args.listName)
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
