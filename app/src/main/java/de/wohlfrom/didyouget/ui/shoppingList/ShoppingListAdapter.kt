package de.wohlfrom.didyouget.ui.shoppingList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import de.wohlfrom.didyouget.data.model.ShoppingList
import de.wohlfrom.didyouget.databinding.FragmentListItemBinding

/**
 * [RecyclerView.Adapter] that can display a single [ShoppingList].
 */
class ShoppingListAdapter(
    private val shoppingListViewModel: ShoppingListViewModel,
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

        holder.listName.setOnClickListener {
            it.findNavController().navigate(
                ShoppingListFragmentDirections.showShoppingListItem(item.id))
        }

        holder.editList.setOnClickListener {
            it.findNavController().navigate(
                ShoppingListFragmentDirections.showShoppingListItemAddEdit(item.id, item.name))
        }

        holder.deleteList.setOnClickListener {
            shoppingListViewModel.deleteShoppingList(item.id)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val listName: TextView = binding.listName
        val editList: ImageButton = binding.editList
        val deleteList: ImageButton = binding.deleteList

        override fun toString(): String {
            return super.toString() + " '" + listName.text + "'"
        }
    }
}
