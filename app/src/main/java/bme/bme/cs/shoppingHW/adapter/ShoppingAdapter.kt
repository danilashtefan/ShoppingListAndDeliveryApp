package bme.bme.cs.shoppingHW.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import bme.bme.cs.shoppingHW.R
import bme.bme.cs.shoppingHW.ScrollingActivity
import bme.bme.cs.shoppingHW.data.AppDatabase
import bme.bme.cs.shoppingHW.data.ShoppingItem
import bme.bme.cs.shoppingHW.touch.ShoppingTouchHelperCallback
import kotlinx.android.synthetic.main.shopping_row.view.*
import java.util.*


class ShoppingAdapter : RecyclerView.Adapter<ShoppingAdapter.ViewHolder>, ShoppingTouchHelperCallback {

    var shoppingItems = mutableListOf<ShoppingItem>()

    var count = 0
    val context: Context


    constructor(context: Context, listShoppingItems: List<ShoppingItem>) {
        this.context = context
        shoppingItems.addAll(listShoppingItems)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.shopping_row, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shoppingItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentShopping = shoppingItems[position]
        holder.tvName.text = currentShopping.shoppingName
        holder.tvDescription.text = currentShopping.shoppingDescription
        holder.tvPrice.text = currentShopping.shoppingPrice.toString()
        holder.swPurchased.isChecked = currentShopping.isPurchased

        when(currentShopping.category){
            0->holder.imView.setImageResource(R.drawable.hamburger)
            1->holder.imView.setImageResource(R.drawable.utilities)
            2->holder.imView.setImageResource(R.drawable.medicine)
        }



//TODO Implement buttons
       /* holder.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }*/

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditTodoDialog(
                shoppingItems[holder.adapterPosition], holder.adapterPosition
            )
        }

        holder.swPurchased.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            shoppingItems[holder.adapterPosition].isPurchased =  holder.swPurchased.isChecked


           Thread{
               AppDatabase.getInstance(context).shoppingDao().updateShopping(shoppingItems[holder.adapterPosition])
           }.start()
        })




    }

   fun shoppingClear(){
       shoppingItems.clear()
       notifyDataSetChanged()
   }


    private fun deleteShopping(position: Int) {
        Thread {

            AppDatabase.getInstance(context).shoppingDao().deleteTodo(
                shoppingItems.get(position))

            (context as ScrollingActivity).runOnUiThread {
                shoppingItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()
    }

    public fun addTodo(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)

        //notifyDataSetChanged() // this refreshes the whole list
        notifyItemInserted(shoppingItems.lastIndex)
    }

    public fun updateShopping(shoppingItem: ShoppingItem, editIndex: Int) {
        shoppingItems.set(editIndex, shoppingItem)
        notifyItemChanged(editIndex)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.itemName
        val tvPrice = itemView.itemPrice
        val tvDescription = itemView.itemDescription
        val swPurchased: Switch = itemView.itemPurchased
        val btnEdit = itemView.btnEdit
        val imView = itemView.imView
    }

    override fun onDismissed(position: Int) {
        deleteShopping(position)


    }


    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(shoppingItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

    }

}

