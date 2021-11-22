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
    private Path path = new Path();
    private Paint brush = new Paint();

    public PaintView(Context context) {
        super(context);

        layoutParams = new ViewGroup.LayoutParams(1920, 1080);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float length = 0;
    public void startDrawing(ArrayList<PathModel> listPath) {
        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeCap(Paint.Cap.ROUND);
        brush.setColor(Color.BLACK);
        brush.setStrokeWidth(3f);
        path.setFillType(Path.FillType.EVEN_ODD);

        for (int i = 0; i < listPath.size(); i++) {
            path.moveTo(listPath.get(i).getPath().get(0).getX(), listPath.get(i).getPath().get(0).getY());
            for (int j = 1; j < listPath.get(i).getPath().size(); j++) {
                path.lineTo(listPath.get(i).getPath().get(j).getX(), listPath.get(i).getPath().get(j).getY());
            }
        }

        PathMeasure measure = new PathMeasure(path, true);
        float length = measure.getLength();
        Log.d("qqDebug", "pathLength: " + length);

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
        animator.setDuration(5000);
        animator.addUpdateListener(valueAnimator -> {
            brush.setPathEffect(createPathEffect(length, (Float) valueAnimator.getAnimatedValue(), 0.0f));
            invalidate();//will calll onDraw
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

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

    private static PathEffect createPathEffect(float pathLength, float phase, float offset)
    {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, brush);
    }
}
