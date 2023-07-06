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
            }
        }

        binding.addEditName.setText(args.listName)
    }
    
    private fun handleResult(result: SimpleResult) {
        result.error?.let {
            showUpdatingFailed(it)
        }
        result.success?.let {
            this.findNavController().popBackStack()
        }
    }

    private fun showUpdatingFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}