package com.example.tlebihan.rangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by t.lebihan on 14/10/2016
 */

public class RangeBar extends RelativeLayout {
    public final static int DEFAULT_MIN_VALUE = 0;
    public final static int DEFAULT_MAX_VALUE = 150;

    public final int DEFAULT_THUMB_RADIUS = (int) Utils.dpToPx(getContext(), 30);
    public final int DEFAULT_THUMB_RESOURCE = R.drawable.ic_chevron;
    public final int DEFAULT_LINE_WIDTH = (int) Utils.dpToPx(getContext(), 10);
    public final int DEFAULT_LINE_COLOR = getResources().getColor(R.color.colorRangebar);
    public final int DEFAULT_CONNECTING_LINE_WIDTH = (int) Utils.dpToPx(getContext(), 10);
    public final int DEFAULT_CONNECTING_LINE_COLOR = getResources().getColor(R.color.colorAccent);


    private int mMin = DEFAULT_MIN_VALUE,
            mMax = DEFAULT_MAX_VALUE;
    private Thumb mThumbMin, mThumbMax;
    private Paint mLinePaint;
    private Paint mConnectingLinePaint;

    private OnValueChangeListener mValueListener;

    public RangeBar(Context context) {
        super(context);
        init();
    }

    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int y = getHeight()/2;

        canvas.drawLine(DEFAULT_THUMB_RADIUS/2, y, mThumbMin.getLeft() + DEFAULT_THUMB_RADIUS/2, y, mLinePaint);
        canvas.drawLine(mThumbMin.getRight() - DEFAULT_THUMB_RADIUS/2, y, mThumbMax.getLeft() + DEFAULT_THUMB_RADIUS/2, y, mConnectingLinePaint);
        canvas.drawLine(mThumbMax.getRight() - DEFAULT_THUMB_RADIUS/2, y, getWidth() - DEFAULT_THUMB_RADIUS/2, y, mLinePaint);
    }

    private void init(){
        mThumbMin = new Thumb(getContext());
        mThumbMax = new Thumb(getContext());
        mLinePaint = new Paint();
        mConnectingLinePaint = new Paint();

        mThumbMin.setImageResource(DEFAULT_THUMB_RESOURCE);
        mThumbMax.setImageResource(DEFAULT_THUMB_RESOURCE);
        mThumbMax.setRotation(180);

        mLinePaint.setColor(DEFAULT_LINE_COLOR);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLinePaint.setStrokeWidth(DEFAULT_LINE_WIDTH);
        mConnectingLinePaint.setColor(DEFAULT_CONNECTING_LINE_COLOR);
        mConnectingLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mConnectingLinePaint.setStrokeWidth(DEFAULT_CONNECTING_LINE_WIDTH);

        mThumbMin.setOnTouchListener(mThumbMinTouchListener);
        mThumbMax.setOnTouchListener(mThumbMaxTouchListener);

        addView(mThumbMin);
        addView(mThumbMax);

        setWillNotDraw(false); // To call ViewGroup onDraw method
        resize();
    }

    private void resize(){
        RelativeLayout.LayoutParams lpThumbMin = new RelativeLayout.LayoutParams(DEFAULT_THUMB_RADIUS, DEFAULT_THUMB_RADIUS);
        lpThumbMin.topMargin = getHeight()/2 - DEFAULT_THUMB_RADIUS/2;
        mThumbMin.setLayoutParams(lpThumbMin);

        RelativeLayout.LayoutParams lpThumbMax = new RelativeLayout.LayoutParams(DEFAULT_THUMB_RADIUS, DEFAULT_THUMB_RADIUS);
        lpThumbMax.leftMargin = getWidth() - DEFAULT_THUMB_RADIUS;
        lpThumbMax.topMargin = getHeight()/2 - DEFAULT_THUMB_RADIUS/2;
        mThumbMax.setLayoutParams(lpThumbMax);

        invalidate();
        requestLayout();
    }

    public void setBounds(int min, int max) {
        if (max > mMin) {
            mMin = min;
            mMax = max;
        }
    }

    public int getThumbMinValue(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mThumbMin.getLayoutParams();
        return Math.round(((float) layoutParams.leftMargin / (getWidth() - DEFAULT_THUMB_RADIUS)) * (mMax-mMin)) + mMin;
    }

    public int getThumbMaxValue(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mThumbMax.getLayoutParams();
        return Math.round(((float) layoutParams.leftMargin / (getWidth() - DEFAULT_THUMB_RADIUS)) * (mMax-mMin)) + mMin;
    }

    private void moveThumbByCoord(Thumb v, int x) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        if(x < 0) x = 0;
        else if(x > getWidth()-DEFAULT_THUMB_RADIUS) x = getWidth()-DEFAULT_THUMB_RADIUS;
        layoutParams.leftMargin = x;
        v.setLayoutParams(layoutParams);

        notifyValueChanged();
    }

    private void setThumbMinByCoord(int x){
        if(x > mThumbMax.getLeft()) {
            x = mThumbMax.getLeft();
        }
        moveThumbByCoord(mThumbMin, x);
    }

    private void setThumbMaxByCoord(int x){
        if(x < mThumbMin.getLeft()) {
            x = mThumbMin.getLeft();
        }
        moveThumbByCoord(mThumbMax, x);
    }

    public void setThumbMinByValue(int value){
        int px = Math.round(((float) (value * (getWidth() - DEFAULT_THUMB_RADIUS)) / mMax));
        if(px > mThumbMax.getLeft()) px = mThumbMax.getLeft();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mThumbMin.getLayoutParams();
        layoutParams.leftMargin = px;
        mThumbMin.setLayoutParams(layoutParams);
        mThumbMin.bringToFront();

        invalidate();
        requestLayout();
        notifyValueChanged();
    }

    public void setThumbMaxByValue(int value){
        int px = Math.round(((float) (value * (getWidth() - DEFAULT_THUMB_RADIUS)) / mMax));
        if(px < mThumbMin.getLeft()) px = mThumbMin.getLeft();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mThumbMax.getLayoutParams();
        layoutParams.leftMargin = px;
        mThumbMax.setLayoutParams(layoutParams);
        mThumbMax.bringToFront();

        invalidate();
        requestLayout();
        notifyValueChanged();
    }

    public void setOnValueChangedListener(OnValueChangeListener listener){
        mValueListener = listener;
    }

    private void notifyValueChanged(){
        if(mValueListener != null)
            mValueListener.onValueChanged(this, getThumbMinValue(), getThumbMaxValue());
    }


    private class Thumb extends ImageView {
        private RectF mRectf;

        public Thumb(Context context) {
            super(context);
            init();
        }

        public Thumb(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init(){
            setBackgroundResource(R.drawable.background_rangebar_thumb);
        }
    }

    private class OnThumbTouchListener implements OnTouchListener {
        private boolean mIsTouching;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Thumb thumb = (Thumb) v;

            switch(event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    // Begin touch action
                    mIsTouching = true;
                    thumb.bringToFront();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if(mIsTouching) {
                        int x = (int) event.getRawX() - (v.getWidth() / 2);
                        if(thumb == mThumbMax) setThumbMaxByCoord(x);
                        else if(thumb == mThumbMin) setThumbMinByCoord(x);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // End touch action
                    mIsTouching = false;
                    return true;
            }
            return false;
        }
    }

    public interface OnValueChangeListener {
        void onValueChanged(RangeBar view, int min, int max);
    }

    private OnThumbTouchListener mThumbMinTouchListener = new OnThumbTouchListener();
    private OnThumbTouchListener mThumbMaxTouchListener = new OnThumbTouchListener();
}
