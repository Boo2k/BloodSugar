package com.example.basekotlin.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;


public class CustomSeekBar extends AppCompatSeekBar {

    private double maxValue = 50.0; // Giá trị tối đa kiểu double
    private Paint textPaint;
    private String progressText = "50";
    private float textSize = 32f; // Kích thước chữ mong muốn
    private float textPadding = 20f; // Khoảng cách giữa chữ và Thumb (dp)
    private OnProgressChangedListener progressChangedListener;
    double mBS;
    private int textColor; // Màu sắc chữ

    public CustomSeekBar(Context context) {
        super(context);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMax(500);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dpToPx(textSize)); // Điều chỉnh kích thước văn bản
        Typeface customTypeface = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            customTypeface = getResources().getFont(R.font.roboto_500);
//        }
//        textPaint.setTypeface(customTypeface);
//        textPaint.setColor(getResources().getColor(R.color._0077CC)); // Điều chỉnh màu sắc văn bản
//        textPaint.setColor();

        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                notifyProgressChanged();
                invalidate(); // Yêu cầu vẽ lại khi giá trị thay đổi
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // Phương thức tùy chỉnh để đặt màu chữ
    public void setTextColor(int color) {
        textColor = color;
        textPaint.setColor(textColor);
        invalidate(); // Yêu cầu vẽ lại khi màu sắc thay đổi
    }

    @Override
    public synchronized void setMax(int max) {
        super.setMax(max);
        maxValue = (double) max / 10.0; // Chuyển đổi giá trị tối đa thành kiểu double
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        notifyProgressChanged();
    }

    @Override
    public synchronized void setProgress(int progress, boolean fromUser) {
        super.setProgress(progress, fromUser);
        if (fromUser) {
            notifyProgressChanged();
        }
    }

    private void notifyProgressChanged() {
        double progressValue = getProgress() / 10.0; // Chuyển đổi giá trị kiểu int thành kiểu double
        if (progressChangedListener != null) {
            progressChangedListener.onProgressChanged(progressValue);
        }
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(double progress);
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        progressChangedListener = listener;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawTextOnThumb(canvas);
    }

    private void drawTextOnThumb(Canvas canvas) {
        float thumbX = calculateThumbX();
        double mBS = Double.parseDouble(getProgressText());
        /*if (mBS < 16.0) {
            textPaint.setColor(getResources().getColor(R.color._4799D4));
        } else if (mBS >= 16.0 && mBS <= 16.9) {
            textPaint.setColor(getResources().getColor(R.color._4EC8F1));
        } else if (mBS >= 17.0 && mBS <= 18.4) {
            textPaint.setColor(getResources().getColor(R.color._87D4EB));
        } else if (mBS >= 18.5 && mBS <= 24.9) {
            textPaint.setColor(getResources().getColor(R.color._59BD83));
        } else if (mBS >= 25.0 && mBS <= 29.9) {
            textPaint.setColor(getResources().getColor(R.color._EDE734));
        } else if (mBS >= 30.0 && mBS <= 34.9) {
            textPaint.setColor(getResources().getColor(R.color.FAA71A));
        } else if (mBS >= 35.0 && mBS <= 39.9) {
            textPaint.setColor(getResources().getColor(R.color.F15A22));
        } else {
            textPaint.setColor(getResources().getColor(R.color.EE3330));
        }*/
        float textWidth = textPaint.measureText(getProgressText());
        float textX = thumbX - textWidth / 2;
        float textY = getPaddingTop() - dpToPx(textPadding);

        canvas.drawText(getProgressText(), textX, textY, textPaint);
    }

    private float calculateThumbX() {
        float progressRatio = (float) getProgress() / getMax();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        return getPaddingLeft() + progressRatio * width;
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    public void setProgressText(String text) {
        progressText = text;
        invalidate(); // Yêu cầu vẽ lại
    }

    private String getProgressText() {
//        return String.valueOf(getProgress() / 10.0);
        return String.valueOf(progressText);
    }
}
