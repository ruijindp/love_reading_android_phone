package com.ljmob.lovereadingphone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ljmob.lovereadingphone.R;

/**
 * Created by 英伦 on 2015/3/17.
 * RecorderRipple
 */
public class RecorderRipple extends View implements Handler.Callback, Runnable {
    private Paint paint;
    private Handler handler;

    private int rippleColor;
    private float insetRadius = 0;
    private float currentRadius = 0;
    private float targetRadius = 0;

    private boolean isRunning = false;

    public RecorderRipple(Context context) {
        this(context, null);
    }

    public RecorderRipple(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        rippleColor = ContextCompat.getColor(context, typedValue.resourceId);
        currentRadius = insetRadius;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(rippleColor);
        handler = new Handler(this);
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
        paint.setColor(rippleColor);
    }

    public void setAmplitude(int amplitude) {
        float rad = (float) amplitude / 32767f;
        if (rad > 0.9f) {
            rad = 0.9f;
        }
        setAmplitude(rad);
    }

    public void setAmplitude(float rad) {
        if (rad > 0.9f) {
            rad = 0.9f;
        }
        float distance = getWidth() / 2 - insetRadius;
        float deltaRadius = rad * distance;
        targetRadius = deltaRadius + insetRadius;
    }


    /**
     * set true if you want to play this view Ripple Change animation, and false to stop it
     *
     * @param isRunning weather the animation is should be played
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
        if (isRunning) {
            new Thread(this).start();
        }
    }

    /**
     * calculate the distance should be changed of the ripple,an make this change to be invalidated
     */
    private void changeRipple() {
        float surplus = targetRadius - currentRadius;
        currentRadius += surplus / 3;
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cX, cY;
        cX = getWidth() / 2;
        cY = getHeight() / 2;
        if (isInEditMode()) {
            currentRadius = getWidth() / 2;
        }
        canvas.drawCircle(cX, cY, currentRadius, paint);
    }

    @Override
    public boolean handleMessage(Message msg) {
        invalidate();
        return false;
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            changeRipple();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        targetRadius = insetRadius - 1;
        while (true) {
            changeRipple();
            if (currentRadius < targetRadius + 1) {
                break;
            }
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
