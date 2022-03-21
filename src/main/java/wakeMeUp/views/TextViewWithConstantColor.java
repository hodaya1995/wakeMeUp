package me.jfenn.wakeMeUp.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import me.jfenn.timedatepickers.interfaces.Themable;
import me.jfenn.timedatepickers.utils.ConversionUtils;
import me.jfenn.wakeMeUp.R;

public class TextViewWithConstantColor extends View implements Themable {
    private int textColorAccent,colorAccent,textColorSecondary;
    private Paint textSecondaryPaint;
    private Paint textAccentPaint;
    private Paint lineAccentPaint;
    private Paint accentPaint;
    private int colorLineAccent;
    private Paint textPrimaryPaint, backgroundPrimaryPaint, backgroundSecondaryPaint;
    private String text="";
    private Canvas canvas;

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    protected void onDraw(Canvas canvas){
        //canvas.drawText("somethong ", size / 2, (size / 2) - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
       super.onDraw(canvas);
       this.canvas=canvas;
        int size = Math.min(canvas.getWidth(), canvas.getHeight());
        textPrimaryPaint = new Paint();
        textPrimaryPaint.setTextAlign(Paint.Align.LEFT);
        textPrimaryPaint.setTextSize(ConversionUtils.spToPx(16));
        textPrimaryPaint.setColor(ContextCompat.getColor(getContext(), R.color.timedatepicker_textColorPrimary));
        textPrimaryPaint.setAntiAlias(true);
        textPrimaryPaint.setDither(true);
        setTextOnScreen(canvas,0,(size / 2) - ((textPrimaryPaint.descent() + textPrimaryPaint.ascent()) / 2),false,textPrimaryPaint);

    }

    public void setTextOnScreen(Canvas canvas, float x,float y, boolean toValidate, Paint paint) {

        if(text!=null)canvas.drawText(text, x,  y, paint);
        if(toValidate)invalidate();
    }


    public TextViewWithConstantColor(Context context) {
        this(context, null);
    }

    public TextViewWithConstantColor(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewWithConstantColor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String text){
        this.text=text;
        invalidate();
    }
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextViewWithConstantColor(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        colorAccent = ContextCompat.getColor(context, R.color.timedatepicker_colorAccent);
        colorLineAccent = Color.argb(50, Color.red(colorAccent), Color.green(colorAccent), Color.blue(colorAccent));
        textColorSecondary = ContextCompat.getColor(context, R.color.timedatepicker_textColorSecondary);

        lineAccentPaint = new Paint();
        lineAccentPaint.setStyle(Paint.Style.FILL);
        lineAccentPaint.setColor(colorLineAccent);
        lineAccentPaint.setAntiAlias(true);
        lineAccentPaint.setDither(true);

        accentPaint = new Paint();
        accentPaint.setStyle(Paint.Style.FILL);
        accentPaint.setColor(colorAccent);
        accentPaint.setAntiAlias(true);
        accentPaint.setDither(true);

        textPrimaryPaint = new Paint();
        textPrimaryPaint.setTextAlign(Paint.Align.LEFT);
        textPrimaryPaint.setTextSize(ConversionUtils.spToPx(16));
        textPrimaryPaint.setColor(ContextCompat.getColor(context, R.color.timedatepicker_textColorPrimary));
        textPrimaryPaint.setAntiAlias(true);
        textPrimaryPaint.setDither(true);

        textSecondaryPaint = new Paint();
        textSecondaryPaint.setTextAlign(Paint.Align.CENTER);
        textSecondaryPaint.setTextSize(ConversionUtils.spToPx(12));
        textSecondaryPaint.setColor(getResources().getColor(textColorSecondary));
        textSecondaryPaint.setAntiAlias(true);
        textSecondaryPaint.setDither(true);

        textAccentPaint = new Paint();
        textAccentPaint.setTextAlign(Paint.Align.CENTER);
        textAccentPaint.setTextSize(ConversionUtils.spToPx(12));
        textAccentPaint.setColor(ContextCompat.getColor(context, R.color.timedatepicker_colorAccent));
        textAccentPaint.setAntiAlias(true);
        textAccentPaint.setDither(true);

        textColorAccent = ContextCompat.getColor(context, R.color.timedatepicker_textColorAccent);

        backgroundPrimaryPaint = new Paint();
        backgroundPrimaryPaint.setStyle(Paint.Style.FILL);
        backgroundPrimaryPaint.setColor(ContextCompat.getColor(context, R.color.timedatepicker_colorBackgroundPrimary));
        setBackgroundColor(backgroundPrimaryPaint.getColor());

        backgroundSecondaryPaint = new Paint();
        backgroundSecondaryPaint.setStyle(Paint.Style.FILL);
        backgroundSecondaryPaint.setColor(ContextCompat.getColor(context, R.color.timedatepicker_colorBackgroundSecondary));

    }

    @Override
    public void setSelectionColor(int color) {
        setSelectionColor(color, 50);
    }

    public void setSelectionColor(@ColorInt int accentColor, int dimmedAlpha) {
        colorAccent = accentColor;
        accentPaint.setColor(colorAccent);
        colorLineAccent = Color.argb(dimmedAlpha, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor));
        lineAccentPaint.setColor(colorLineAccent);
        postInvalidate();
    }

    @Override
    public int getSelectionColor() {
        return colorAccent;
    }

    @Override
    public void setSelectionTextColor(int color) {
        textColorAccent = color;
        textAccentPaint.setColor(color);
        postInvalidate();
    }

    @Override
    public int getSelectionTextColor() {
        return textColorAccent;
    }

    public void setPrimaryTextColor(@ColorInt int textColor) {
        textPrimaryPaint.setColor(textColor);
        postInvalidate();
    }

    @Override
    public int getPrimaryTextColor() {
        return textPrimaryPaint.getColor();
    }

    @Override
    public void setSecondaryTextColor(@ColorInt int textColor) {
        textColorSecondary = textColor;
        textSecondaryPaint.setColor(textColor);
        postInvalidate();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public int getSecondaryTextColor() {
        return textColorSecondary;
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public int getBackgroundColor() {
        return getBackground() instanceof ColorDrawable ? ((ColorDrawable) getBackground()).getColor() : Color.TRANSPARENT;
    }

    @Override
    public void setPrimaryBackgroundColor(int color) {
        backgroundPrimaryPaint.setColor(color);
        postInvalidate();
    }

    @Override
    public int getPrimaryBackgroundColor() {
        return backgroundPrimaryPaint.getColor();
    }

    @Override
    public void setSecondaryBackgroundColor(int color) {
        backgroundSecondaryPaint.setColor(color);
        postInvalidate();
    }
    @Override
    public int getSecondaryBackgroundColor() {
        return backgroundSecondaryPaint.getColor();
    }

    public String getText() {
        return text;
    }
}