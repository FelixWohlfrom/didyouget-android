package de.wohlfrom.didyouget.ui.shoppingListItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.data.model.ListItem
import java.util.LinkedList

/**
 * A fragment representing a list of Items.
 */
class ShoppingListItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = ShoppingListItemAdapter(LinkedList<ListItem>())
            }
        }
        return view
    }
}
