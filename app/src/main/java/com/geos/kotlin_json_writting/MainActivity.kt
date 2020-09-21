package com.geos.kotlin_json_writting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.OutputStream
import java.io.PrintWriter

class MainActivity : AppCompatActivity() {
    //퍼미션변수
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var filename: String
    lateinit var outputstream: OutputStream


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                PERMISSIONS_STORAGE.get(2)
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                PERMISSIONS_STORAGE.get(3)
            ) == PackageManager.PERMISSION_GRANTED
        )
        //todo when passed
        else {
            verifyStoragePermissions(this)
        }

        //Json작성
        var jobj: JSONObject = JSONObject()

        jobj.put("이름", "호라리")
        jobj.put("나이", "10")
        jobj.put("직업", "동물")

        val maglist = JSONArray()
        maglist.put(jobj)
        maglist.put(jobj)
        maglist.put(jobj)

        val jobj2 = JSONObject()
        jobj2.put("arr", maglist)

        Log.d("jobj ", "" + jobj)
        Log.d("maglist ", "" + maglist)
        Log.d("jobj2 ", "" + jobj2)

        //Json해석
        lateinit var obj: JSONObject

        obj = JSONObject(jobj2.toString())

        val array = obj.getJSONArray("arr")
        val dataObj = array.getJSONObject(0)

        val friendName = dataObj.getString("이름")
        val friendAge = dataObj.getString("나이")

        Log.i("friendName", "" + friendName)
        Log.i("friendAge", "" + friendAge)

        //배열 집어넣기 예제?
        val id = "userID"
        val planName = "planA"

        val _placeList = ArrayList<Place>()
        _placeList.add(Place("플러리", "뿌요뿌요테트리스", "유튜브", "게임"))
        _placeList.add(Place("녹두로", "슈마메", "유튜브", "게임"))
        _placeList.add(Place("살인마협회장", "데바데", "유튜브", "게임"))

        obj = JSONObject()
        val _jArray = JSONArray()
        for (i in _placeList) {
//            Log.d("^^", "" + i)
            var _sObject = JSONObject()
            _sObject.put("contentid", i.contentid)
            _sObject.put("contenttypeid", i.contenttypeid)
            _sObject.put("mapx", i.mapx)
            _sObject.put("mapy", i.mapy)
            _jArray.put(_sObject)
        }
        obj.put("planName", planName)
        obj.put("id", id)
        obj.put("item", _jArray)

        Log.i("배열 넣기", "" + obj.toString())
        seeJson.text = obj.toString()

        btn_save.setOnClickListener {


            if (ContextCompat.checkSelfPermission(
                    this,
                    PERMISSIONS_STORAGE.get(0)
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    PERMISSIONS_STORAGE.get(1)
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("^:^", "btn!!")
                val ess = Environment.getExternalStorageState()
                if (ess == Environment.MEDIA_MOUNTED) {
                    val sdCardPath =
                        getRemovableSDCardPath(applicationContext).split("/Android".toRegex())
                            .toTypedArray()[0]
                    Log.d("^9^", sdCardPath)
                    val rootDir = "$sdCardPath/jsondata"
                    filename = "testJson" + ".json"
                    val path: File = File(rootDir, filename)
                    var write = FileWriter(path,false)
                    var out = PrintWriter(write)
                    out.println(obj.toString())
                    out.close()

//
//                    outputstream = openFileOutput(filename, Context.MODE_PRIVATE)
//                    outputstream.write(obj.toString().toByteArray())
//                    outputstream.close()
                    Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "File cannot be saved as long as you don't let us",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun verifyStoragePermissions(activity: AppCompatActivity) {
        val permission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                PERMISSIONS_STORAGE.get(0)
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                PERMISSIONS_STORAGE.get(1)
            ) == PackageManager.PERMISSION_GRANTED
        )
        //todo when passed
        else {
            Toast.makeText(
                this,
                "File cannot be saved as long as you don't let us",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
    }

    fun getRemovableSDCardPath(context: Context?): String {
        val storages = ContextCompat.getExternalFilesDirs(context!!, null)
        val pass = storages[0].absolutePath
        return pass
    }

}