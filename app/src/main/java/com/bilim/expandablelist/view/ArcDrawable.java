package com.bilim.expandablelist.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bilim.expandablelist.R;

/**
 * Created by zhangdi on 11/29/18.
 */
public class ArcDrawable extends View {
    
    private static final String TAG = "ArcDrawable_tag";
    
    private float rectWidth;
    private float radius;//default
    private Paint arcPaint;
    private Canvas canvas;
    private RectF arcRectF;
    private Paint textPaint;
    private float strokeWidth;
    private float finishedStartAngle;
    private float finishedSweepAngle;
    private float unFinishedAngle;
    private float unFinishedSweepAngle;
    private float arcBottomTextHeight;
    private int arcBottomTextSize;
    private int currentProgress;
    private int progressTextSize;
    private int suffixTextPaddingLeft;
    private int innerArcColor = Color.LTGRAY;
    private int outerArcColor = Color.BLUE;
    private int textColor = Color.BLACK;
    private String bottomText;
    private String suffixText;
    private Handler handler = new Handler();
    private Context mContext;
    private int finalProgress;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean cancel = false;
            if(currentProgress < finalProgress){
                currentProgress++;
            }else if(currentProgress > finalProgress){
                currentProgress--;
            }
            postInvalidate();
            if (currentProgress == finalProgress) cancel = true;
            if (!cancel)
                handler.postDelayed(runnable, 20);
        }
    };

    public ArcDrawable(Context context) {
        this(context, null);
    }

    public ArcDrawable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArcDrawable(Context context, AttributeSet attrs,
                       int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        strokeWidth = dp2px(getResources(), 15);
        arcBottomTextSize = (int) dp2px(getResources(), 18);
        progressTextSize = (int) sp2px(getResources(), 40);
        suffixTextPaddingLeft = (int) dp2px(getResources(), 10);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArcDrawable);
            Log.e(TAG, "indexCount:" + array.getIndexCount() + " attrs:" + attrs
                    .getAttributeCount());

            strokeWidth = dp2px(getResources(), array.getDimension(R.styleable
                    .ArcDrawable_strokeWidth, strokeWidth));
            finishedStartAngle = array.getFloat(R.styleable.ArcDrawable_arcStartAngle, 120f);
            finishedSweepAngle = array.getFloat(R.styleable.ArcDrawable_arcSweepAngle, 300f);
            finalProgress = array.getInt(R.styleable.ArcDrawable_defaultProcess, 10);
            setProgress(finalProgress);
            arcBottomTextSize = (int) sp2px(getResources(), array.getDimension(R.styleable
                    .ArcDrawable_bottomTextSize, arcBottomTextSize));
            innerArcColor = array.getColor(R.styleable.ArcDrawable_innerArcColor, Color.LTGRAY);
            outerArcColor = array.getColor(R.styleable.ArcDrawable_outerArcColor, Color.parseColor("#FF05AAF6"));
            progressTextSize = (int) sp2px(getResources(), array.getDimension(R.styleable
                    .ArcDrawable_processTextSize, progressTextSize));
            bottomText = array.getString(R.styleable.ArcDrawable_bottomText);
            if (bottomText == null) bottomText = "";
            suffixText = array.getString(R.styleable.ArcDrawable_suffixText);
            if (suffixText == null) suffixText = "%";
            suffixTextPaddingLeft = (int) dp2px(getResources(), array.getDimension(R.styleable
                    .ArcDrawable_suffixTextPaddingLeft, suffixTextPaddingLeft));
            array.recycle();
        }
        initPaint();
    }

    private float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    private void initPaint() {
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setColor(innerArcColor);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);

        canvas = new Canvas();

        arcRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        rectWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize
                (heightMeasureSpec));
        float left = getPaddingLeft() + strokeWidth / 2;
        float top = getPaddingTop() + strokeWidth / 2;
        float right = rectWidth - left - strokeWidth / 2;
        float bottom = rectWidth - top - strokeWidth / 2;
        arcRectF.set(left, top, right, bottom);
        radius = rectWidth / 2f;
        float unSweepAngle = 360 - finishedSweepAngle;
        arcBottomTextHeight = (float) (radius * (1 - Math.cos((unSweepAngle / 2) / 180 * Math.PI)));
        unFinishedAngle = finishedStartAngle;
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        arcPaint.setColor(innerArcColor);
        canvas.drawArc(arcRectF, finishedStartAngle, finishedSweepAngle, false, arcPaint);
        arcPaint.setColor(outerArcColor);
        Log.e(TAG, "getProgress()：" + getProgress() + ",finishedSweepAngle = " +
                finishedSweepAngle);
        unFinishedSweepAngle = getProgress() / 100f * finishedSweepAngle;
        Log.e(TAG, "unFinishedAngle：" + unFinishedAngle);
        Log.e(TAG,"unFinishedSweepAngle:"+unFinishedSweepAngle);
        canvas.drawArc(arcRectF, unFinishedAngle, unFinishedSweepAngle, false, arcPaint);

        //draw progress
        String progress = String.valueOf((int) getProgress());

        if (!TextUtils.isEmpty(progress)) {
            textPaint.setColor(textColor);
            textPaint.setTextSize(progressTextSize);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float progressWidth = textPaint.measureText(progress);
            float x = (rectWidth - progressWidth) / 2;
            //文字高度的一半到基线的距离
            float dy = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
            float baseLine = rectWidth / 2 + dy;
            canvas.drawText(progress, x, baseLine, textPaint);
            textPaint.setTextSize(progressTextSize / 2);
            fontMetrics = textPaint.getFontMetrics();
            canvas.drawText(suffixText, rectWidth / 2 + progressWidth / 2 +
                    suffixTextPaddingLeft, rectWidth / 2, textPaint);
        }

        if (!TextUtils.isEmpty(bottomText)) {
            textPaint.setTextSize(arcBottomTextSize);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float dy = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
            float bottomTextBaseLine = (rectWidth - arcBottomTextHeight) - dy;
            canvas.drawText(bottomText, rectWidth / 2 - textPaint.measureText(bottomText) / 2,
                    bottomTextBaseLine, textPaint);
        }

    }

    public int getProgress() {
        return this.currentProgress;
    }

    public void setProgress(int size) {
        this.finalProgress = size;
        handler.post(runnable);

    }

}
