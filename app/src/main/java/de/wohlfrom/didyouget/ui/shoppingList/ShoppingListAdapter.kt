package de.wohlfrom.didyouget.ui.shoppingList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.databinding.FragmentListItemBinding
import de.wohlfrom.didyouget.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class ShoppingListAdapter(
    private val values: List<ShoppingList>
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.listName.text = item.name
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val listName: TextView = binding.listName

        override fun toString(): String {
            return super.toString() + " '" + listName.text + "'"
        }
    }
}
