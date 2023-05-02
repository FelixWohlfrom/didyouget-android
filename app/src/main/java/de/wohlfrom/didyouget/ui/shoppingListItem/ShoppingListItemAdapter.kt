package de.wohlfrom.didyouget.ui.shoppingListItem

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.wohlfrom.didyouget.data.model.ListItem
import de.wohlfrom.didyouget.databinding.FragmentItemBinding

/**
 * [RecyclerView.Adapter] that can display a [ListItem].
 */
class ShoppingListItemAdapter(
    private val values: List<ListItem>
) : RecyclerView.Adapter<ShoppingListItemAdapter.ViewHolder>() {

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
        holder.itemName.text = item.value
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemName: TextView = binding.itemName

        override fun toString(): String {
            return super.toString() + " '" + itemName.text + "'"
        }
    }
}
