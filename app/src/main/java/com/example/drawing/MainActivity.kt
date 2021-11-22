package com.example.drawing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonArray = JSONArray(loadJSONFromAsset())

        val listPath : ArrayList<PathModel> = arrayListOf()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val gson = GsonBuilder().create()
            val model : ArrayList<CoordinateModel> = arrayListOf()
            gson.fromJson(jsonObject.getString("lsVectorsInStroke"),Array<CoordinateModel>::class.java).forEach {
                model.add(CoordinateModel(it.x , it.y, it.z))
            }

            listPath.add(PathModel(model))
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val paintView = findViewById<PaintView>(R.id.paintView)
            paintView.startDrawing(listPath)
        }
    }

    private fun loadJSONFromAsset(): String {
        var json: String = ""
        json = try {
            val `is`: InputStream = this.assets.open("A_Ve_Mau.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "null"
        }
        return json
    }
}