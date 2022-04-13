package com.example.mem2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm

class Table2 : AppCompatActivity() {
    private var fontsize=10f
    private var width=200
    var flag = 0
    var myList = ArrayList<ArrayList<TextView>>()

    private lateinit var backgroundThreadRealm: Realm
    private lateinit var addNotes: FloatingActionButton
    private lateinit var increase: FloatingActionButton
    private lateinit var decrease: FloatingActionButton
    private lateinit var table1: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table2)

        decrease = findViewById(R.id.decrease)
        increase = findViewById(R.id.increase)
        addNotes = findViewById(R.id.addNotes)
        table1 = findViewById(R.id.table1)
        backgroundThreadRealm = Realm.getDefaultInstance()

        for (i in 0 until 53) {//findviewbyid in for loop//53
            var subarr = ArrayList<TextView>()
            for (j in 0 until 28) {
                val textviewID = "a1r$i"+"c$j"
                val text = "a2r$i"+"c$j"
                val resID = resources.getIdentifier(textviewID, "id", packageName)
                val data:DataModel? = backgroundThreadRealm.where(DataModel::class.java).equalTo("_id", text).findFirst()
                var rc: TextView=findViewById(resID)
                rc.text = text+" " + data?.description
                rc.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize)
                val lp: LinearLayout.LayoutParams = rc.layoutParams as LinearLayout.LayoutParams
                lp.width += 500
                subarr.add(rc)
            }
            myList.add(subarr)
        }

        increase.setOnClickListener {
            fontsize += 1f
            //width += 1
            for (i in 0 until 53) {
                for (j in 0 until 28) {
                    myList[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize)
                    val lp: LinearLayout.LayoutParams = myList[i][j].layoutParams as LinearLayout.LayoutParams
                    lp.width += 50
                    //lp.height += width
                    Log.e("QUICKSTART", i.toString()+" " + j.toString()+" " + lp.height.toString())
                }
            }
            flag=1
        }
        decrease.setOnClickListener {
            fontsize -= 1f
            // width -= 1
            for (i in 0 until 53) {
                for (j in 0 until 28) {
                    myList[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize)
                    val lp: LinearLayout.LayoutParams = myList[i][j].layoutParams as LinearLayout.LayoutParams
                    // if(flag == 0){
                    //     lp.width -= width
                    //}else{
                    lp.width -= 50
                    //}
                    //lp.height -= width
                }
            }
            //flag=1
        }

        addNotes.setOnClickListener {
            startActivity(Intent(this, UpdateNotesActivity::class.java))
            finish()
        }

        table1.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}