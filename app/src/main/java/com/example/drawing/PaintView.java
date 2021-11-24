package com.example.drawing;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PaintView extends View {
    public ViewGroup.LayoutParams layoutParams;

    float width = 1920;
    float height = 1080;
    public PaintView(Context context) {
        super(context);

        layoutParams = new ViewGroup.LayoutParams((int) width, (int) height);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint brushSample = new Paint();
    private Paint brushStroke = new Paint();
    private Paint brushDraw = new Paint();

    private Path pathSample = new Path();
    private Path pathStroke = new Path();
    private Path pathDraw = new Path();
    public void initBrush() {
        brushSample.setAntiAlias(true);
        brushSample.setStyle(Paint.Style.STROKE);
        brushSample.setStrokeJoin(Paint.Join.ROUND);
        brushSample.setStrokeCap(Paint.Cap.ROUND);
        brushSample.setColor(Color.CYAN);
        brushSample.setStrokeWidth(30f);

        brushStroke.setAntiAlias(true);
        brushStroke.setStyle(Paint.Style.STROKE);
        brushStroke.setStrokeJoin(Paint.Join.ROUND);
        brushStroke.setStrokeCap(Paint.Cap.ROUND);
        brushStroke.setColor(Color.BLACK);
        brushStroke.setStrokeWidth(45f);

        brushDraw.setAntiAlias(true);
        brushDraw.setStyle(Paint.Style.STROKE);
        brushDraw.setStrokeJoin(Paint.Join.ROUND);
        brushDraw.setStrokeCap(Paint.Cap.ROUND);
        brushDraw.setColor(Color.YELLOW);
        brushDraw.setStrokeWidth(30f);

        pathSample.setFillType(Path.FillType.EVEN_ODD);
        pathStroke.setFillType(Path.FillType.EVEN_ODD);
        pathDraw.setFillType(Path.FillType.EVEN_ODD);
    }


    boolean isDrawSample = false;
    public void drawSample(ArrayList<PathModel> listPath) {
        for (int i = 0; i < listPath.size(); i++) {
            pathSample.moveTo(listPath.get(i).getPath().get(0).getX(), listPath.get(i).getPath().get(0).getY());
            pathStroke.moveTo(listPath.get(i).getPath().get(0).getX(), listPath.get(i).getPath().get(0).getY());
            for (int j = 1; j < listPath.get(i).getPath().size(); j++) {
                pathSample.lineTo(listPath.get(i).getPath().get(j).getX(), listPath.get(i).getPath().get(j).getY());
                pathStroke.lineTo(listPath.get(i).getPath().get(j).getX(), listPath.get(i).getPath().get(j).getY());
            }
        }
        isDrawSample = true;
        invalidate();
    }

    boolean isDrawAnimation = false;
    public void drawAnimation(ArrayList<PathModel> listPath) {
        for (int i = 0; i < listPath.size(); i++) {
            pathDraw.moveTo(listPath.get(i).getPath().get(0).getX(), listPath.get(i).getPath().get(0).getY());
            for (int j = 1; j < listPath.get(i).getPath().size(); j++) {
                pathDraw.lineTo(listPath.get(i).getPath().get(j).getX(), listPath.get(i).getPath().get(j).getY());
            }
        }

        PathMeasure measure = new PathMeasure(pathDraw, true);
        float length = measure.getLength();

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
        animator.setDuration(5000);
        animator.addUpdateListener(valueAnimator -> {
            brushDraw.setPathEffect(createPathEffect(length, (Float) valueAnimator.getAnimatedValue(), 0.0f));
            invalidate();//will calll onDraw
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isDrawAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    public void setPhase(float phase) {
        Log.d("qqDebug","setPhase called with:" + phase);
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0,getHeight());
        canvas.scale(1f, -1f);
        super.onDraw(canvas);

        if (isDrawSample) {
            canvas.drawPath(pathStroke, brushStroke);
            canvas.drawPath(pathSample, brushSample);
            isDrawSample = false;
        }

        if (isDrawAnimation) {
            canvas.drawPath(pathDraw, brushDraw);
        }
    }
}
