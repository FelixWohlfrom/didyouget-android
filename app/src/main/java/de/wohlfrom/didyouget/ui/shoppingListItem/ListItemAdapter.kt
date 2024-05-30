package de.wohlfrom.didyouget.ui.shoppingListItem

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.databinding.FragmentItemBinding
import de.wohlfrom.didyouget.ui.shoppingList.ShoppingListViewModel

/**
 * [RecyclerView.Adapter] that can display a [ListItem].
 */
class ListItemAdapter(
    private val shoppingListViewModel: ShoppingListViewModel,
    private val values: List<ListItem>,
    private val context: Context
) : RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.item.text = item.value
        holder.item.isChecked = item.bought

        holder.item.setOnClickListener {
            shoppingListViewModel.markListItemBought(item.id, holder.item.isChecked) { result ->
                result.error?.let {
                    showFailedMessage(it)
                }

                // TODO: Update design, e.g. strike through
            }
        }

        holder.editItem.setOnClickListener {
            it.findNavController().navigate(
                ListItemFragmentDirections.showShoppingListItemEdit(item.id, item.value,
                    item.bought))
        }

        holder.deleteItem.setOnClickListener {
            shoppingListViewModel.deleteShoppingListItem(item.id) { result ->
                result.error?.let {
                    showFailedMessage(it)
                }

                result.success?.let {
                    this.notifyItemRemoved(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = values.size

    private fun showFailedMessage(errorString: String) {
        val appContext = context.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    inner class ViewHolder(binding: FragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val item: CheckBox = binding.itemName
        val editItem: ImageButton = binding.editItem
        val deleteItem: ImageButton = binding.deleteItem

        override fun toString(): String {
            return super.toString() + " '" + item.text + "'"
        }
    }
}
