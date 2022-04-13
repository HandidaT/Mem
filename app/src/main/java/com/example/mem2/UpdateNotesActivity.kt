package com.example.mem2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.realm.Realm
import io.realm.RealmResults
import java.io.*


class UpdateNotesActivity : AppCompatActivity() {
    private lateinit var idED: EditText
    private lateinit var descriptionED: EditText
    private lateinit var fetchNoteBtn: Button
    private lateinit var saveNoteBtn: Button
    private lateinit var deleteChangesBtn: Button
    private lateinit var updateoutBtn: Button
    private lateinit var updateInBtn: Button
    private lateinit var backgroundThreadRealm: Realm
    private var outfilename = ""
    private var infilename = ""
    private var filepath = ""
    private var fileContent = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_notes)

        idED=findViewById(R.id.idET)
        descriptionED=findViewById(R.id.descET)
        fetchNoteBtn=findViewById(R.id.fetchBtn)
        saveNoteBtn=findViewById(R.id.saveBtn)
        deleteChangesBtn=findViewById(R.id.deleteU)
        updateoutBtn=findViewById(R.id.updateout)
        updateInBtn=findViewById(R.id.updatein)
        backgroundThreadRealm= Realm.getDefaultInstance()
        outfilename = "myFile.txt"
        infilename = "toandroid.txt"
        filepath = "MyFileDir"
        fetchNoteBtn.setOnClickListener{
            fetchNotesFromDB()
        }
        saveNoteBtn.setOnClickListener{
            updateNotesOnDB()
        }
        deleteChangesBtn.setOnClickListener{
            deleteChanges()
        }
        if(!isExternalStorageAvailableForRW()){
            updateoutBtn.isEnabled = false
        }/*
        migrateBtn.setOnClickListener{
            Toast.makeText(this,"migrate", Toast.LENGTH_SHORT).show()
        }*/

        updateoutBtn.setOnClickListener{
            fileContent = "Hello text"
            // Check for Storage Permission
            if (isStoragePermissionGranted()) {
                // If input is not empty, we'll proceed
                //if (fileContent != "") {
                val allData : RealmResults<DataModel> = backgroundThreadRealm.where( DataModel::class.java).findAll()
                val changed : List<DataModel> = allData.where().beginsWith("_id", "UU").findAll()
                val noOfChanges : Int = changed.size
                val myExternalFile = File(getExternalFilesDir(filepath), outfilename)
                // Create an object of FileOutputStream for writing data to myFile.txt
                var fos: FileOutputStream? = null
                fos = FileOutputStream(myExternalFile,true)
                try{
                    backgroundThreadRealm.executeTransaction { transactionRealm ->
                        for(i in changed){
                            var str: String= i.description.toString()//.subSequence(0, 3) as String
                            var str1:String=str.subSequence(1, 4) as String
                            Log.e("QUICKSTART",str1)
                            val dataToMigrate : DataModel = transactionRealm.where(DataModel::class.java).equalTo("_id", str).findFirst()!!
                            try {
                                str=dataToMigrate._id.toString() + ":" + dataToMigrate.description.toString() + "\n"
                                fos.write(str.toByteArray())
                                //fos.close()
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        fos.close()
                    }
                    Toast.makeText(this,"$noOfChanges data out", Toast.LENGTH_SHORT).show()
                }catch(e: Exception){
                    Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
                }
                //}
            }
        }

        updateInBtn.setOnClickListener{
            // Create a FileReader object reference. FileReader is typically suitable for reading
            // streams of characters.
            // For reading streams of raw bytes, you can use a FileInputStream.
            var fr: FileReader?
            val myExternalFile = File(getExternalFilesDir(filepath),infilename)
            // Instantiate a StringBuilder object. This class is an alternative to String Class
            // and it is mutable, has methods such as append(), insert(), or replace() that allow to
            // modify strings. Hence it is more efficient.
            val stringBuilder = StringBuilder()
            try {
                // Instantiate the FileReader object and pass myExternalFile in the constructor
                fr = FileReader(myExternalFile)
                // Instantiate a BufferedReader object and pass FileReader object in constructor.
                // The BufferedReader maintains an internal buffer and can be used with different
                // types of readers to read text from an Input stream more efficiently.
                val br = BufferedReader(fr)
                // Next, call readLine() method on BufferedReader object to read a line of text.
                var line: String? = null
                var strings: List<String>
                backgroundThreadRealm.beginTransaction()
                // Use a while loop to read the entire file
                while (run {
                        line = br.readLine()
                        line
                    } != null) {
                    //line?.let { it1 -> Log.e("QUICKSTART", it1.split(":",limit=2).toString()) }
                    // line?.split(":",limit=2)?.get(1)?.let { it1 -> Log.e("QUICKSTART", it1.toString()) }
                    //line?.split(":",limit=2)?.get(1)?.let { it1 -> Log.e("QUICKSTART", it1.toString()) }
                    strings= line?.split(":",limit=2)!!
                    //Log.e("QUICKSTART", strings.size.toString())
                    Log.e("QUICKSTART", strings[1])
                    //backgroundThreadRealm.beginTransaction()
                    val notes=DataModel()
                    notes._id= strings[0]
                    notes.description= strings[1]
                    backgroundThreadRealm.copyToRealmOrUpdate(notes)
                    //backgroundThreadRealm.commitTransaction()
                }
                backgroundThreadRealm.commitTransaction()
                Toast.makeText(this,"Data in", Toast.LENGTH_SHORT).show()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                //backgroundThreadRealm.commitTransaction()
            } finally {
                // Convert the StringBuilder content into String and add text "File contents\n"
                // at the beginning.
                val fileContents = "File contents\n$stringBuilder"
                // Set the TextView with fileContents
                // tvLoad.setText(fileContents)
            }
        }

    }

    private fun isExternalStorageAvailableForRW(): Boolean {
        // Check if the external storage is available for read and write by calling
        // Environment.getExternalStorageState() method. If the returned state is MEDIA_MOUNTED,
        // then you can read and write files. So, return true in that case, otherwise, false.
        val extStorageState = Environment.getExternalStorageState()
        if (extStorageState == Environment.MEDIA_MOUNTED) {
            return true//[ERROR] LiveLiteralKt Not found! search this error on web
        }
        return false
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                //Permission is granted
                true
            } else {
                //Permission is revoked
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            //Permission is granted
            true
        }
    }















    private fun fetchNotesFromDB() {
        try{
            backgroundThreadRealm.beginTransaction()
            val notes=DataModel()
            notes.description=descriptionED.text.toString()
            val data1 = backgroundThreadRealm.where(DataModel::class.java).equalTo("_id",idED.text.toString()).findFirst()!!
            descriptionED.setText(data1.description)
            backgroundThreadRealm.commitTransaction()
            Toast.makeText(this,"Fetched successfully", Toast.LENGTH_SHORT).show()
        }catch(e: Exception){
            backgroundThreadRealm.commitTransaction()
            Toast.makeText(this,"Fetch Failed $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNotesOnDB() {
        try{
            backgroundThreadRealm.beginTransaction()
            val notes=DataModel()
            // notes.title=titleED.text.toString()
            notes.description=descriptionED.text.toString()
            notes._id=idED.text.toString()
            backgroundThreadRealm.copyToRealmOrUpdate(notes)
            backgroundThreadRealm.commitTransaction()


            backgroundThreadRealm.beginTransaction()
            val changes=DataModel()
            // notes.title=titleED.text.toString()
            val id="UU"+idED.text.toString()//Updated ids
            changes.description=idED.text.toString()
            changes._id=id
            backgroundThreadRealm.copyToRealmOrUpdate(changes)
            backgroundThreadRealm.commitTransaction()
            Toast.makeText(this,"Notes added successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }catch(e: Exception){
            backgroundThreadRealm.commitTransaction()
            Toast.makeText(this,"Update Failed $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteChanges() {
        val allData : RealmResults<DataModel> = backgroundThreadRealm.where( DataModel::class.java).findAll()
        val changed : List<DataModel> = allData.where().beginsWith("_id", "UU").findAll()
        val noOfChanges : Int = changed.size
        try{
            backgroundThreadRealm.executeTransaction { transactionRealm ->
                for(i in changed){
                    Log.e("QUICKSTART","$i._id")
                    val dataToDelete : DataModel = transactionRealm.where(DataModel::class.java).equalTo("_id", i._id).findFirst()!!
                    dataToDelete.deleteFromRealm()
                }
            }
            Toast.makeText(this,"$noOfChanges", Toast.LENGTH_SHORT).show()
        }catch(e: Exception){
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
        }
    }


}