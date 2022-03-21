package me.jfenn.wakeMeUp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import androidx.annotation.Nullable;
import me.jfenn.timedatepickers.utils.ConversionUtils;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.utils.DoubleToLongConverter;

public class DecimalPicker extends RelativeLayout {
    private Context context;
    private AttributeSet attrs;
    private int styleAttr;
    private OnClickListener mListener;
    private double initialNumber, finalNumber, lastNumber, currentNumber,moneyValue;
    private TextView editText;
    private String format;
    private OnValueChangeListener onValueChangeListener;
    public static final double FINAL_NUMBER = 10;
    private boolean isMoneyPicker;
    private int step;
    private TextView text;

    public double getFinalNumber() {
        return finalNumber;
    }

    public void setFinalNumber(double finalNumber) {
        this.finalNumber = finalNumber;
    }

    public DecimalPicker(Context context) {
        this(context, null);

    }

    public DecimalPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DecimalPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public double getInitialNumber() {
        return initialNumber;
    }

    public void setInitialNumber(double initialNumber) {
        this.initialNumber = initialNumber;
    }



    public double getMoneyValue() {
        return moneyValue;
    }

    public void setMoneyValue(double moneyValue) {
        this.moneyValue = moneyValue;
    }

    private void initView(){
        inflate(context, R.layout.decimal_picker, this);

        final Resources res = getResources();
        final int defaultColor = res.getColor(R.color.timedatepicker_colorBackgroundPrimary);
        final int defaultTextColor = res.getColor(R.color.timedatepicker_textColorPrimary);
        final Drawable defaultDrawable = res.getDrawable(R.drawable.decimal_picker_shape);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DecimalPicker, styleAttr, 0);

        initialNumber = a.getFloat(R.styleable.DecimalPicker_initialNumber, 0);
        finalNumber = a.getFloat(R.styleable.DecimalPicker_finalNumber, 0);
        moneyValue=a.getFloat(R.styleable.DecimalPicker_moneyValue, 0);

        SharedPreferences pref = getContext().getSharedPreferences("threshold", Context.MODE_PRIVATE);


        float textSize = a.getDimension(R.styleable.DecimalPicker_textSize, 24);
        int color = a.getColor(R.styleable.DecimalPicker_backGroundColor,defaultColor);
        int textColor = a.getColor(R.styleable.DecimalPicker_textColor,defaultTextColor);
        Drawable drawable = a.getDrawable(R.styleable.DecimalPicker_backgroundDrawable);

        TextView buttonMinus = (TextView) findViewById(R.id.subtract_btn);
        TextView buttonPlus = (TextView) findViewById(R.id.add_btn);
        buttonMinus.setText("<");
        buttonPlus.setText(">");
        buttonMinus.setTextSize(ConversionUtils.dpToPx(9));
        buttonPlus.setTextSize(ConversionUtils.dpToPx(9));
        editText = (TextView) findViewById(R.id.number_counter);
        editText.setTextSize(ConversionUtils.dpToPx(9));
        this.text=editText;
        //Canvas canvas=editText.getCanvas();
        //editText.setTextOnScreen(canvas,(int)0.75*canvas.getWidth(),true);
        /*editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString().trim();
                double valueDouble = -1;
                try {
                    valueDouble = parseDouble(value.isEmpty() ? "0" : value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (valueDouble >= 0){
                    lastNumber = currentNumber;
                    currentNumber = valueDouble;
                    callListener(DecimalPicker.this);
                }
            }
        });


        editText.setTextColor(textColor);
        editText.setTextSize(textSize);
*/
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.decimal_picker_layout);
/*
        if (drawable == null){
            drawable = defaultDrawable;
        }
        assert drawable != null;
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        if (Build.VERSION.SDK_INT > 16)
            mLayout.setBackground(drawable);
        else
            mLayout.setBackgroundDrawable(drawable);
*/
        editText.setText(new DecimalFormat("##.##").format( initialNumber));

        currentNumber = initialNumber;
        lastNumber = initialNumber;


        buttonMinus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View mView) {
                double num = parseDouble(editText.getText().toString());
                if(!isMoneyPicker)num=num - moneyValue;
                else{
                    if(step>0&&step<=10)num=DoubleToLongConverter.longToDouble(pref.getLong("price0"+(--step),0));
                }
                if(num<initialNumber)num=initialNumber;
                 setNumber(""+new DecimalFormat("##.##").format(num),true);
            }
        });
        buttonPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View mView) {
                double num = parseDouble(editText.getText().toString());
                if(!isMoneyPicker)num=num + moneyValue;
                else{
                    if(step>=0&&step<10)num=DoubleToLongConverter.longToDouble(pref.getLong("price0"+(++step),0));
                }
                if(num>finalNumber) num=finalNumber;
                setNumber(new DecimalFormat("##.##").format(num),true);
            }
        });
        a.recycle();
    }

    private void callListener(View view){
        if (mListener != null)
            mListener.onClick(view);

        if (onValueChangeListener != null && lastNumber != currentNumber)
            onValueChangeListener.onValueChange(this, lastNumber, currentNumber);
    }

    public double getNumber(){
        return (double)currentNumber;
    }

    public void setNumber(double number){
        currentNumber=number;
        if(isMoneyPicker)setNumber(""+number) ;
        else             setNumber(""+(int)number);
    }
    public void setNumber(String number) {
        try {
            double n = parseDouble(number);
            if (n > finalNumber)
                n = finalNumber;

            if (n < initialNumber)
                n = initialNumber;

            /*if (format != null) {
                String num = String.format(Utils.getCurrentLocale(getContext()), format, n);
                num = removeTrailingZeroes(num);
                editText.setText(num);
            } else*/
                editText.setText(number);
            lastNumber = currentNumber;
            currentNumber = parseDouble(editText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private double parseDouble(String str) throws NumberFormatException {
        return Double.parseDouble(str.replace(",","."));
    }


    private String removeTrailingZeroes(String num) {
        NumberFormat nf = NumberFormat.getInstance();
        if (nf instanceof DecimalFormat) {
            DecimalFormatSymbols sym = ((DecimalFormat) nf).getDecimalFormatSymbols();
            char decSeparator = sym.getDecimalSeparator();
            String[] split = num.split((decSeparator == '.' ? "\\" : "") + String.valueOf(decSeparator));
            if (split.length == 2 && split[1].replace("0", "").isEmpty())
                num = split[0];
        }
        return num;
    }

    private void setNumber(String number, boolean notifyListener){
        setNumber(number);
        if (notifyListener) callListener(this);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mListener = onClickListener;
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener){
        this.onValueChangeListener = onValueChangeListener;
    }

    public void isMoneyPicker(boolean b) {
        isMoneyPicker=b;
    }

    public void setStepNumber(int currNum) {
        step=currNum;
    }

    public int getStep() {
        return step;
    }






/*

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


*/





    public interface OnClickListener {
        void onClick(View view);
    }

    public interface OnValueChangeListener {
        void onValueChange(DecimalPicker view, double oldValue, double newValue);
    }

    public void setRange(Double startingNumber, Double endingNumber) {
        initialNumber = startingNumber;
        finalNumber = endingNumber;
    }

    public void setFormat(String format){
        this.format = format;
    }
}
