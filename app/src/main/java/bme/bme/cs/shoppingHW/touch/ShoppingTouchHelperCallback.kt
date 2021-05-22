package bme.bme.cs.shoppingHW.touch

interface ShoppingTouchHelperCallback {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}