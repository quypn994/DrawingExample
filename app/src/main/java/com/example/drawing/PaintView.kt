package com.example.drawing

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.gson.GsonBuilder
import org.json.JSONArray
import java.util.ArrayList

class PaintView : View {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val brushSample = Paint()
    private val brushStroke = Paint()
    private val brushDraw = ArrayList<Paint>()
    private val pathSample = Path()
    private val pathStroke = Path()
    private val pathDraw = ArrayList<Path>()
    fun initBrush() {
        brushSample.isAntiAlias = true
        brushSample.style = Paint.Style.STROKE
        brushSample.strokeJoin = Paint.Join.ROUND
        brushSample.strokeCap = Paint.Cap.ROUND
        brushSample.color = Color.CYAN
        brushSample.strokeWidth = 30f
        brushStroke.isAntiAlias = true
        brushStroke.style = Paint.Style.STROKE
        brushStroke.strokeJoin = Paint.Join.ROUND
        brushStroke.strokeCap = Paint.Cap.ROUND
        brushStroke.color = Color.BLACK
        brushStroke.strokeWidth = 45f
        pathSample.fillType = Path.FillType.EVEN_ODD
        pathStroke.fillType = Path.FillType.EVEN_ODD
    }

    var isDrawSample = false

    fun drawSample(jsArray: JSONArray) {
        val listPathDraw : ArrayList<PathModel> = arrayListOf()
        for (i in 0 until jsArray.length()) {
            val jsonObject = jsArray.getJSONObject(i)
            val gson = GsonBuilder().create()
            val model : ArrayList<CoordinateModel> = arrayListOf()
            gson.fromJson(jsonObject.getString("lsVectorsInStroke"),Array<CoordinateModel>::class.java).forEach {
                model.add(CoordinateModel(it.x , it.y, it.z))
            }

            listPathDraw.add(PathModel(model))
        }

        measurePathScale(listPathDraw)
        for (i in listPathDraw.indices) {
            pathSample.moveTo(listPathDraw[i].path[0].x, listPathDraw[i].path[0].y)
            pathStroke.moveTo(listPathDraw[i].path[0].x, listPathDraw[i].path[0].y)
            for (j in 1 until listPathDraw[i].path.size) {
                pathSample.lineTo(listPathDraw[i].path[j].x, listPathDraw[i].path[j].y)
                pathStroke.lineTo(listPathDraw[i].path[j].x, listPathDraw[i].path[j].y)
            }
        }
        isDrawSample = true
        invalidate()
    }

    var isDrawAnimation = false
    var isAnimationFinish = 0
    var currentPath = 0
    var length = 0f
    fun drawAnimation(jsArray: JSONArray) {
        val listPathDraw : ArrayList<PathModel> = arrayListOf()
        for (i in 0 until jsArray.length()) {
            val jsonObject = jsArray.getJSONObject(i)
            val gson = GsonBuilder().create()
            val model : ArrayList<CoordinateModel> = arrayListOf()
            gson.fromJson(jsonObject.getString("lsVectorsInStroke"),Array<CoordinateModel>::class.java).forEach {
                model.add(CoordinateModel(it.x , it.y, it.z))
            }

            listPathDraw.add(PathModel(model))
        }

        if (currentPath >= listPathDraw.size) return

        for (i in listPathDraw.indices) {
            val subPath = Path()
            subPath.fillType = Path.FillType.EVEN_ODD
            subPath.moveTo(listPathDraw[i].path[0].x, listPathDraw[i].path[0].y)
            for (j in 1 until listPathDraw[i].path.size) {
                subPath.lineTo(listPathDraw[i].path[j].x, listPathDraw[i].path[j].y)
            }
            pathDraw.add(subPath)
            val brush = Paint()
            brush.isAntiAlias = true
            brush.style = Paint.Style.STROKE
            brush.strokeJoin = Paint.Join.ROUND
            brush.strokeCap = Paint.Cap.ROUND
            brush.color = Color.YELLOW
            brush.strokeWidth = 30f
            brushDraw.add(brush)
        }
        val measure = PathMeasure(pathDraw[currentPath], false)
        length = measure.length
        val animator = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f)
        animator.duration = 5000
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            brushDraw[currentPath].pathEffect =
                createPathEffect(length, valueAnimator.animatedValue as Float, 0.0f)
            invalidate() //will calll onDraw
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                isDrawAnimation = true
            }

            override fun onAnimationEnd(animator: Animator) {
                isDrawAnimation = false
                if (currentPath < pathDraw.size - 1) {
                    currentPath++
                    val measure = PathMeasure(pathDraw[currentPath], false)
                    length = measure.length
                    Log.d("qqDebug", "pathLength: $length")
                    isAnimationFinish = currentPath
                    drawAnimation(jsArray)
                }
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        animator.start()
    }

    fun setPhase(phase: Float) {
//        Log.d("qqDebug","setPhase called with:" + phase);
    }

    var scaleRatio = 1f
    var screenW = 0
    var screenH = 0
    var offsetRatio = 0f
    fun setScreenMeasure(screenW: Int, screenH: Int) {
        this.screenW = screenW
        this.screenH = screenH
    }

    var pathWidth = 1f
    var pathHeight = 1f
    var maxX = 1f
    var maxY = 1f
    private fun measurePathScale(listPath: ArrayList<PathModel>) {
        val xList : ArrayList<Float> = arrayListOf()
        val yList : ArrayList<Float> = arrayListOf()

        for (i in 0 until listPath.size) {
            xList.add(listPath[i].path.minOf { axes -> axes.x })
            xList.add(listPath[i].path.maxOf { axes -> axes.x })

            yList.add(listPath[i].path.minOf { axes -> axes.y })
            yList.add(listPath[i].path.maxOf { axes -> axes.y })
        }

        maxX = xList.maxOf { float -> float }
        maxY = yList.maxOf { float -> float }
        pathWidth = xList.maxOf { float -> float } - xList.minOf { float -> float }
        pathHeight = yList.maxOf { float -> float } - yList.minOf { float -> float }

        Log.d("qqDebug", "pathW: $pathWidth\npathH: $pathHeight")
    }


    fun initRatio(viewW: Int, viewH: Int) {
        Log.d("qqDebug", "maxX: $maxX\nmaxY: $maxY")
        if (screenW > screenH) {
            scaleRatio = viewW.toFloat() / pathWidth - /*for padding*/(viewW.toFloat() / pathWidth) / 9f
            offsetRatio = scaleRatio * -95f
//            offsetRatio = scaleRatio * (screenW - maxX)
        } else {
            scaleRatio = viewH.toFloat() / pathHeight - /*for padding*/(viewW.toFloat() / pathHeight) / 9f
            offsetRatio = scaleRatio * -95f
//            offsetRatio = scaleRatio * (screenH - maxY)
        }

        Log.d("qqDebug", "sW: $screenW, sH: $screenH")
        Log.d("qqDebug", "vW: $viewW, vH: $viewH")
        Log.d("qqDebug", "scaleRatio: $scaleRatio")
        Log.d("qqDebug", "offsetRatio: $offsetRatio")
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(0f, height.toFloat())
        canvas.scale(1f, -1f)
        initRatio(width, height)
        canvas.translate(offsetRatio * 4, offsetRatio * 0.9f)
        canvas.scale(scaleRatio, scaleRatio)
        super.onDraw(canvas)
        canvas.drawPath(pathStroke, brushStroke)
        canvas.drawPath(pathSample, brushSample)
        Log.d("qqDebug", "draw times: $isAnimationFinish")
        when (isAnimationFinish) {
            1 -> canvas.drawPath(pathDraw[0], brushDraw[0])
            2 -> {
                canvas.drawPath(pathDraw[0], brushDraw[0])
                canvas.drawPath(pathDraw[1], brushDraw[1])
            }
            3 -> {
                canvas.drawPath(pathDraw[0], brushDraw[0])
                canvas.drawPath(pathDraw[1], brushDraw[1])
                canvas.drawPath(pathDraw[2], brushDraw[2])
            }
            else -> {
            }
        }
        if (isDrawAnimation) {
            canvas.drawPath(pathDraw[currentPath], brushDraw[currentPath])
        }
        if (!pathDraw.isEmpty()) {
            canvas.drawPath(pathDraw[currentPath], brushDraw[currentPath])
        }
    }

    companion object {
        private fun createPathEffect(pathLength: Float, phase: Float, offset: Float): PathEffect {
            return DashPathEffect(
                floatArrayOf(pathLength, pathLength),
                Math.max(phase * pathLength, offset)
            )
        }
    }
}