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

class MainActivity : AppCompatActivity() {
    private var fontsize=10f
    private var width=200
    private var flag = 0
    var myList = ArrayList<ArrayList<TextView>>()

    private lateinit var backgroundThreadRealm: Realm
    private lateinit var addNotes: FloatingActionButton
    private lateinit var increase: FloatingActionButton
    private lateinit var decrease: FloatingActionButton
    private lateinit var table2: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        decrease = findViewById(R.id.decrease)
        increase = findViewById(R.id.increase)
        addNotes = findViewById(R.id.addNotes)
        table2 = findViewById(R.id.table2)
        backgroundThreadRealm = Realm.getDefaultInstance()

/*
        val note = DataModel()
        //<Create
        note._id = "0-0"
        note.description="Column of c language"
        backgroundThreadRealm.executeTransaction { transactionRealm ->
            transactionRealm.insert(note)
        }//Create>


        val note1 = DataModel()
        //<Create
        note1._id = "0-1"
        note1.description="Column of c language"
        backgroundThreadRealm.executeTransaction { transactionRealm ->
            transactionRealm.insert(note1)
        }
*/

        for (i in 0 until 55) {//findviewbyid in for loop
            var subarr = ArrayList<TextView>()
            for (j in 0 until 22) {
                val textviewID = "a1r$i"+"c$j"
                val resID = resources.getIdentifier(textviewID, "id", packageName)
                val data:DataModel? = backgroundThreadRealm.where(DataModel::class.java).equalTo("_id", textviewID).findFirst()
                var rc: TextView=findViewById(resID)
                rc.text = textviewID+" "+data?.description
                rc.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize)
                val lp: LinearLayout.LayoutParams = rc.layoutParams as LinearLayout.LayoutParams
                lp.width += 500
                subarr.add(rc)
            }
            myList.add(subarr)
        }

/*
        val data0:DataModel? = backgroundThreadRealm.where(DataModel::class.java).equalTo("_id", "a1r0c0").findFirst()
        r0c0 = findViewById(R.id.a1r0c0)
        r0c0.text = data0?.description

        val data1:DataModel? = backgroundThreadRealm.where(DataModel::class.java).equalTo("_id", "a1r0c1").findFirst()
        r0c1 = findViewById(R.id.a1r0c1)
        r0c1.text = data1?.description*/

        increase.setOnClickListener {
            fontsize += 1f
            //width += 1
            for (i in 0 until 55) {
                for (j in 0 until 22) {
                    myList[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize)
                    val lp: LinearLayout.LayoutParams = myList[i][j].layoutParams as LinearLayout.LayoutParams
                    //if(flag == 0){
                    //    lp.width += width
                    //}else{
                    lp.width += 50
                    //}
                    //lp.height += width
                    Log.e("QUICKSTART", i.toString()+" " + j.toString()+" " + lp.height.toString())
                }
            }
            flag=1
        }
        decrease.setOnClickListener {
            fontsize -= 1f
            // width -= 1
            for (i in 0 until 55) {
                for (j in 0 until 22) {
                    myList[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize)
                    val lp: LinearLayout.LayoutParams = myList[i][j].layoutParams as LinearLayout.LayoutParams
                    lp.width -= 50
                    //lp.height -= width
                }
            }
        }

        addNotes.setOnClickListener {
            startActivity(Intent(this, UpdateNotesActivity::class.java))
            finish()
        }

        table2.setOnClickListener {
            startActivity(Intent(this, Table2::class.java))
            finish()
        }
    }

}