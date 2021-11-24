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
import android.util.DisplayMetrics





class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonArraySample = JSONArray(loadJSONFromAsset("A_Ve_Mau.json"))
        val listPathSample : ArrayList<PathModel> = arrayListOf()
        for (i in 0 until jsonArraySample.length()) {
            val jsonObject = jsonArraySample.getJSONObject(i)
            val gson = GsonBuilder().create()
            val model : ArrayList<CoordinateModel> = arrayListOf()
            gson.fromJson(jsonObject.getString("lsVectorsInStroke"),Array<CoordinateModel>::class.java).forEach {
                model.add(CoordinateModel(it.x , it.y, it.z))
            }

            listPathSample.add(PathModel(model))
        }

        val jsonArrayDraw = JSONArray(loadJSONFromAsset("A_Be_ve.json"))
        val listPathDraw : ArrayList<PathModel> = arrayListOf()
        for (i in 0 until jsonArrayDraw.length()) {
            val jsonObject = jsonArrayDraw.getJSONObject(i)
            val gson = GsonBuilder().create()
            val model : ArrayList<CoordinateModel> = arrayListOf()
            gson.fromJson(jsonObject.getString("lsVectorsInStroke"),Array<CoordinateModel>::class.java).forEach {
                model.add(CoordinateModel(it.x , it.y, it.z))
            }

            listPathDraw.add(PathModel(model))
        }

        val paintView = findViewById<PaintView>(R.id.paintView)
        paintView.initBrush()

        val paintView1 = findViewById<PaintView>(R.id.paintView1)
        paintView1.initBrush()

        val paintView2 = findViewById<PaintView>(R.id.paintView2)
        paintView2.initBrush()
        
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            paintView.drawAnimation(listPathDraw)
            paintView1.drawAnimation(listPathDraw)
            paintView2.drawAnimation(listPathDraw)
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        paintView.setScreenMeasure(width, height)
        paintView.drawSample(listPathSample)

        paintView1.setScreenMeasure(width, height)
        paintView1.drawSample(listPathSample)

        paintView2.setScreenMeasure(width, height)
        paintView2.drawSample(listPathSample)
    }



    private fun loadJSONFromAsset(assetName: String): String {
        var json: String = ""
        json = try {
            val `is`: InputStream = this.assets.open(assetName)
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