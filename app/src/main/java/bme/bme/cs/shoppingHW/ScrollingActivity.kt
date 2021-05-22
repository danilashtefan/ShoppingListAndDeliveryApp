package bme.bme.cs.shoppingHW

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.appbar.CollapsingToolbarLayout
import bme.bme.cs.shoppingHW.adapter.ShoppingAdapter
import bme.bme.cs.shoppingHW.data.AppDatabase
import bme.bme.cs.shoppingHW.data.ShoppingItem
import bme.bme.cs.shoppingHW.touch.TodoRecyclerTouchCallback
import com.parse.*
import kotlinx.android.synthetic.main.activity_scrolling.*


class ScrollingActivity : AppCompatActivity(), ShoppingDialog.ShoppingHandler {

    lateinit var todoAdapter: ShoppingAdapter
    var count = 0;

    companion object {
        const val KEY_EDIT = "KEY_EDIT"

        const val PREF_NAME = "PREFTODO"
        const val KEY_STARTED = "KEY_STARTED"
        const val KEY_LAST_USED = "KEY_LAST_USED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        ParseAnalytics.trackAppOpenedInBackground(intent)
        if(ParseUser.getCurrentUser()==null)
        {
            ParseAnonymousUtils.logIn(object: LogInCallback {
                override fun done(user: ParseUser?, e: ParseException?) {
                    if(e==null){
                        Log.i("Info","Login Successfull");
                    }
                    else
                    {
                        Log.i("Info","Login Failed");
                    }
                }
            })
        }
        else{

            Log.i("Info", "Existing user log in")

            if(ParseUser.getCurrentUser().get("type")!=null){
                var type = ParseUser.getCurrentUser().get("type")
                Log.i("Info","Type of the User is: "+ type);
            }
        }

        btnLeft.setOnClickListener{
           for(todo in todoAdapter.shoppingItems){
               if(todo.isPurchased==false)count++
           }
            val toast = Toast.makeText(applicationContext, "Items Left to buy: " + count, Toast.LENGTH_SHORT)
            toast.show()
            count = 0;
        }


        btnDelivery.setOnClickListener{
            ParseUser.getCurrentUser().remove("good")

             var itemNames:String
            itemNames =""

            for(i in 0..todoAdapter.shoppingItems.size-1){
                itemNames = itemNames + todoAdapter.shoppingItems.get(i).shoppingName+" "
            }

            ParseUser.getCurrentUser().add("good",itemNames)

            ParseUser.getCurrentUser().saveInBackground()
            startActivity(Intent(this,Delivery::class.java))
        }


        Thread {
            var todoList = AppDatabase.getInstance(this).shoppingDao().getAllShoppings()

            runOnUiThread{
                todoAdapter = ShoppingAdapter(this,todoList)
                recyclerTodo.adapter = todoAdapter

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerTodo.addItemDecoration(itemDecoration)


                val touchCallbakList = TodoRecyclerTouchCallback(todoAdapter)
                val itemTouchHelper = ItemTouchHelper(touchCallbakList)
                itemTouchHelper.attachToRecyclerView(recyclerTodo)
            }
        }.start()
    }
    fun showAddTodoDialog() {
        ShoppingDialog().show(supportFragmentManager, "Dialog")
    }
    var editIndex: Int = -1
    public fun showEditTodoDialog(shoppingItemToEdit: ShoppingItem, index: Int) {
        editIndex = index

        val editItemDialog = ShoppingDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_EDIT, shoppingItemToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager, "EDITDIALOG")
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if(id == R.id.action_Add) {
            showAddTodoDialog()

        }

        if(id == R.id.action_DeleteAll){
           clearTodo()
        }
        return true
    }

    override fun shoppingCreated(shoppingItem: ShoppingItem) {
        saveTodo(shoppingItem)
    }

        private fun clearTodo() {
        Thread{
            AppDatabase.getInstance(this).shoppingDao().nukeTable()
            runOnUiThread() {

                todoAdapter.shoppingClear()

            }
        }.start()


    }
    private fun saveTodo(shoppingItem: ShoppingItem) {
        Thread{
            AppDatabase.getInstance(this).shoppingDao().insertShopping(shoppingItem)

            runOnUiThread {
                todoAdapter.addTodo(shoppingItem)
            }
        }.start()


    }

    override fun shoppingUpdated(shoppingItem: ShoppingItem) {
        Thread{
            AppDatabase.getInstance(this).shoppingDao().updateShopping(shoppingItem)

            runOnUiThread {
                todoAdapter.updateShopping(shoppingItem, editIndex)
            }
        }.start()

}
}