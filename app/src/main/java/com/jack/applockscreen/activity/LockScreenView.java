package com.jack.applockscreen.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.jack.applockscreen.R;


/**
 * 解锁按钮控件
 *
 * @author chenglei
 */
public class LockScreenView extends View {

    private int mWidth;
    private int mHeight;

    private int mScreenWidth;
    private int mScreenHeight;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private float mTouchPadRadius;
    private float mTouchPadBorderWidthNormal;
    private float mTouchPadBorderWidthPressed;
    private Paint mPaintNormal;
    private TextPaint mTextPaint;
    private Paint mBackgroundPaint;

    private Point mTouchPadCenter = new Point();

    private int mTouchSlop;
    private int mStartX;
    private int mCurrX;
    private int mOffectX;
    private boolean mCanMove;

    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private Drawable mDrawableCenter;
    private String mTextLeft;
    private String mTextRight;

    private Point mTargetDrawableLeftPosition = new Point();
    private Point mTargetDrawableRightPosition = new Point();
    private Point mTargetTextLeftPosition = new Point();
    private Point mTargetTextRightPosition = new Point();
    private float mDensity;
    private static final float TOUCH_PAD_BORDER_WIDTH_NORMAL = 3;
    private static final float TOUCH_PAD_BORDER_WIDTH_PRESSED = 3;
    private static final float TARGET_DRAWABLE_PADDING = 32;
    private static final float TARGET_TEXT_SIZE = 16;

    private static final int STATE_TRIGGER_NONE = 0;
    private static final int STATE_TRIGGER_LEFT = 1;
    private static final int STATE_TRIGGER_RIGHT = 2;
    private int mTouchPadTriggerState = STATE_TRIGGER_NONE;

    private OnTriggerListener mOnTriggerListener;

    private Drawable mDotsLeft;
    private Drawable mDotsRight;

    public LockScreenView(Context context) {
        this(context, null, 0);
    }

    public LockScreenView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockScreenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mWindowManager = ((Activity) getContext()).getWindowManager();
        mDisplay = mWindowManager.getDefaultDisplay();
        mScreenWidth = mDisplay.getWidth();
        mScreenHeight = mDisplay.getHeight();

//		mTouchPadBorderWidthNormal = DensityUtil.dip2px(getContext(), 8);
//		mTouchPadBorderWidthPressed = DensityUtil.dip2px(getContext(), 2);

        mDensity = getResources().getDisplayMetrics().density;
        mTouchPadBorderWidthNormal = TOUCH_PAD_BORDER_WIDTH_NORMAL * mDensity;
        mTouchPadBorderWidthPressed = TOUCH_PAD_BORDER_WIDTH_PRESSED * mDensity;

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);

        mDrawableCenter = getResources().getDrawable(R.mipmap.ic_launcher);
        mDotsLeft = getResources().getDrawable(R.mipmap.keyguaid_dots_left);
        mDotsRight = getResources().getDrawable(R.mipmap.keyguaid_dots_right);

        initNormalPaint();
        initTextPaint();
    }

    private void initNormalPaint() {
        mPaintNormal = new Paint();
        mPaintNormal.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaintNormal.setColor(Color.WHITE);
        mPaintNormal.setStyle(Style.STROKE);
        mPaintNormal.setStrokeWidth(mTouchPadBorderWidthNormal);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.parseColor("#66000000"));
    }

    private void initTextPaint() {
        mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setTextSize(TARGET_TEXT_SIZE * mDensity);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawDots(canvas);
        drawTouchPad(canvas);
        drawTargetDrawablesAndTexts(canvas);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, mHeight / 4, mWidth, mHeight * 3 / 4, mBackgroundPaint);
    }

    private void drawDots(Canvas canvas) {
        if (!mTouchDown) {
            BitmapDrawable dotsLeft = (BitmapDrawable) mDotsLeft;
            BitmapDrawable dotsRight = (BitmapDrawable) mDotsRight;
            canvas.drawBitmap(dotsLeft.getBitmap(),
                    mWidth / 4 - mDotsLeft.getIntrinsicWidth() / 2,
                    mHeight / 2 - mDotsLeft.getIntrinsicHeight() / 2,
                    mPaintNormal);
            canvas.drawBitmap(dotsRight.getBitmap(),
                    mWidth * 3 / 4 - mDotsRight.getIntrinsicWidth() / 2,
                    mHeight / 2 - mDotsRight.getIntrinsicHeight() / 2,
                    mPaintNormal);
        }
    }

    private void drawTouchPad(Canvas canvas) {
        BitmapDrawable bd = (BitmapDrawable) mDrawableCenter;
        if (mTouchDown) {
            canvas.drawCircle(mTouchPadCenter.x, mTouchPadCenter.y, mDrawableCenter.getIntrinsicWidth() - 20, mPaintNormal);
        } else {
            canvas.drawBitmap(bd.getBitmap(),
                    mTouchPadCenter.x - mDrawableCenter.getIntrinsicWidth() / 2,
                    mTouchPadCenter.y - mDrawableCenter.getIntrinsicHeight() / 2,
                    mPaintNormal);
            canvas.drawCircle(mTouchPadCenter.x, mTouchPadCenter.y, mDrawableCenter.getIntrinsicWidth() - 20, mPaintNormal);
        }
    }

    private void drawTargetDrawablesAndTexts(Canvas canvas) {
        if (mDrawableLeft != null) {
            BitmapDrawable bd = (BitmapDrawable) mDrawableLeft;
            canvas.drawBitmap(
                    bd.getBitmap(),
                    mTargetDrawableLeftPosition.x - mDrawableLeft.getIntrinsicWidth() / 2,
                    mTargetDrawableLeftPosition.y - mDrawableLeft.getIntrinsicHeight() / 2,
                    mPaintNormal);
        }

        if (mDrawableRight != null) {
            BitmapDrawable bd = (BitmapDrawable) mDrawableRight;
            canvas.drawBitmap(
                    bd.getBitmap(),
                    mTargetDrawableRightPosition.x - mDrawableRight.getIntrinsicWidth() / 2,
                    mTargetDrawableRightPosition.y - mDrawableRight.getIntrinsicHeight() / 2,
                    mPaintNormal);
        }

        if (mTextLeft != null) {
            canvas.drawText(mTextLeft, mTargetTextLeftPosition.x, mTargetTextLeftPosition.y, mTextPaint);
        }

        if (mTextRight != null) {
            canvas.drawText(mTextRight, mTargetTextRightPosition.x, mTargetTextRightPosition.y, mTextPaint);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private boolean mTouchDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                if (mStartX > mTouchPadCenter.x - mTouchPadRadius && mStartX < mTouchPadCenter.x + mTouchPadRadius) {
                    mPaintNormal.setStrokeWidth(mTouchPadBorderWidthPressed);
                    mTouchDown = true;
                    invalidate();
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrX = (int) event.getX();
//			mOffectX = mCurrX - mStartX;
//			
//			if (!mCanMove && Math.abs(mOffectX) < mTouchSlop) {
//				return false;
//			} else {
//				mCanMove = true;
//				if (mOffectX > mTouchSlop) {
//					mOffectX += mTouchSlop;
//				} else if (mOffectX < -mTouchSlop) {
//					mOffectX -= mTouchSlop;
//				}
//			}

                mStartX = mCurrX;
                if (mDrawableRight != null && mCurrX < mWidth * 5 / 12 /*mTargetDrawableLeftPosition.x + mDrawableRight.getIntrinsicWidth() * 2.5*/) {
                    mTouchPadCenter.set(mTargetDrawableLeftPosition.x, mTargetDrawableLeftPosition.y);
                    mTouchPadTriggerState = STATE_TRIGGER_LEFT;
                } else if (mDrawableRight != null && mCurrX > mWidth * 7 / 12 /*mTargetDrawableRightPosition.x - mDrawableRight.getIntrinsicWidth() * 2.5*/) {
                    mTouchPadCenter.set(mTargetDrawableRightPosition.x, mTargetDrawableRightPosition.y);
                    mTouchPadTriggerState = STATE_TRIGGER_RIGHT;
                } else {
                    mTouchPadCenter.x = mCurrX;
                    mTouchPadCenter.set(mTouchPadCenter.x + mOffectX, mTouchPadCenter.y);
                    mTouchPadTriggerState = STATE_TRIGGER_NONE;
                }


                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCanMove = false;
                if (STATE_TRIGGER_NONE == mTouchPadTriggerState) {
                    mTouchPadCenter.set(mWidth / 2, mHeight / 2);
                    //mPaintNormal.setStrokeWidth(mTouchPadBorderWidthNormal);
                    mTouchDown = false;
                    invalidate();
                } else if (STATE_TRIGGER_LEFT == mTouchPadTriggerState) {
                    if (mOnTriggerListener != null) {
                        mOnTriggerListener.onTriggerLeft();
                    }
                } else if (STATE_TRIGGER_RIGHT == mTouchPadTriggerState) {
                    if (mOnTriggerListener != null) {
                        mOnTriggerListener.onTriggerRight();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.  
            mWidth = widthSize;
        } else {
            mWidth = mScreenWidth;

            // Check against our minimum width
            mWidth = Math.max(mWidth, getSuggestedMinimumWidth());

            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = Math.min(widthSize, mWidth);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = mScreenHeight / 4;

            // Check against our minimum width
            mHeight = Math.max(mHeight, getSuggestedMinimumHeight());

            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(heightSize, mHeight);
            }
        }

        setMeasuredDimension(mWidth, mHeight);
        mTouchPadRadius = mDrawableCenter.getIntrinsicWidth();

        mTouchPadCenter.set(mWidth / 2, mHeight / 2);

        mTargetDrawableLeftPosition.set((int) (TARGET_DRAWABLE_PADDING * mDensity), mHeight / 2);
        mTargetDrawableRightPosition.set((int) (mWidth - TARGET_DRAWABLE_PADDING * mDensity), mHeight / 2);
        mTargetTextLeftPosition.set((int) (TARGET_DRAWABLE_PADDING * mDensity), (int) (mHeight / 2 - 20 * mDensity));
        mTargetTextRightPosition.set((int) (mWidth - TARGET_DRAWABLE_PADDING * mDensity), (int) (mHeight / 2 - 20 * mDensity));
    }

    public void setTargetDrawablesAndTexts(Drawable drawableLeft, Drawable drawableRight, String textLeft, String textRight) {
        mDrawableLeft = drawableLeft;
        mDrawableRight = drawableRight;
        mTextLeft = textLeft;
        mTextRight = textRight;
        invalidate();
    }

    public void setTargetDrawablesAndTexts(int drawableLeftId, int drawableRightId, String textLeft, String textRight) {
        mDrawableLeft = drawableLeftId > 0 ? getResources().getDrawable(drawableLeftId) : null;
        mDrawableRight = drawableRightId > 0 ? getResources().getDrawable(drawableRightId) : null;
        mTextLeft = textLeft;
        mTextRight = textRight;
        invalidate();
    }

    public interface OnTriggerListener {
        public void onTriggerLeft();

        public void onTriggerRight();
    }

    public void setOnTriggerListener(OnTriggerListener l) {
        mOnTriggerListener = l;
    }

}
