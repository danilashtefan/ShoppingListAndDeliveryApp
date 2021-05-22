package bme.bme.cs.shoppingHW

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import androidx.fragment.app.DialogFragment
import bme.bme.cs.shoppingHW.data.ShoppingItem
import kotlinx.android.synthetic.main.shopping_dialog.view.*
import kotlinx.android.synthetic.main.shopping_dialog.view.editItemDescription
import kotlinx.android.synthetic.main.shopping_dialog.view.editItemName
import kotlinx.android.synthetic.main.shopping_dialog.view.editItemPrice


class ShoppingDialog : DialogFragment() {

    interface ShoppingHandler{
        fun shoppingCreated(shoppingItem: ShoppingItem)
        fun shoppingUpdated(shoppingItem: ShoppingItem)
    }

    lateinit var shoppingHandler: ShoppingHandler
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ShoppingHandler){
            shoppingHandler = context
        } else {
            throw RuntimeException(
                "The Activity is not implementing the ShoppingHandler interface.")
        }
    }

    lateinit var editItemName: EditText
    lateinit var editItemPrice: EditText
    lateinit var editItemDescription: EditText
    lateinit var itemPurchased: Switch
    lateinit var spinnerCategory: Spinner


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle("Shopping dialog")
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.shopping_dialog, null
        )

        editItemName = dialogView.editItemName
        editItemPrice = dialogView.editItemPrice
        editItemDescription = dialogView.editItemDescription
        spinnerCategory = dialogView.spinnerCategory
        itemPurchased = dialogView.itemPurchased



        var categoryAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.entries,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCategory.adapter = categoryAdapter
        //spinnerCategory.setSelection(1)


        dialogBuilder.setView(dialogView)

        val arguments = this.arguments
        // if we are in EDIT mode
        if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
            val shoppingItem = arguments.getSerializable(ScrollingActivity.KEY_EDIT) as ShoppingItem

            when(shoppingItem.category){
                0->spinnerCategory.setSelection(0)
                1->spinnerCategory.setSelection(1)
                2->spinnerCategory.setSelection(2)
            }

            editItemName.setText(shoppingItem.shoppingName)
            editItemPrice.setText(shoppingItem.shoppingPrice)
            editItemDescription.setText(shoppingItem.shoppingDescription)
            itemPurchased.isChecked = shoppingItem.isPurchased


            dialogBuilder.setTitle("Edit shopping")
        }

        dialogBuilder.setPositiveButton("Ok") {
                dialog, which ->
        }
        dialogBuilder.setNegativeButton("Cancel") {
                dialog, which ->
        }


        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {

                val arguments = this.arguments
                // IF EDIT MODE
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
                    handleShoppingEdit()
                } else {
                    handleShoppingCreate()
                }
                dialog!!.dismiss()



        }
    }

    private fun handleShoppingCreate() {
        shoppingHandler.shoppingCreated(
            ShoppingItem(
                null,
                editItemName.text.toString(),
                editItemPrice.text.toString(),
                editItemDescription.text.toString(),
                itemPurchased.isChecked,
                spinnerCategory.selectedItemPosition

            )
        )
    }

    private fun handleShoppingEdit() {
        val shoppingToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_EDIT
        ) as ShoppingItem
        shoppingToEdit.shoppingName = editItemName.text.toString()
        shoppingToEdit.shoppingPrice = editItemPrice.text.toString()
        shoppingToEdit.shoppingDescription = editItemDescription.text.toString()
        shoppingToEdit.isPurchased = itemPurchased.isChecked
        shoppingToEdit.category = spinnerCategory.selectedItemPosition
        shoppingHandler.shoppingUpdated(shoppingToEdit)
    }


}