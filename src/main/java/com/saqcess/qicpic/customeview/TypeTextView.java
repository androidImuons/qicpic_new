package com.saqcess.qicpic.customeview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.fonts.FontUtils;

public class TypeTextView extends TextView {
    public TypeTextView(@NonNull Context context) {
        super(context);
    }



    public TypeTextView (Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TypeTextView (Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TypeTextView (Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    protected void init (Context context, AttributeSet attributeSet) {
//        if (!isInEditMode()) {
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TypeTextView);
            String fontName = typedArray.getString(R.styleable.TypeTextView_custom_font);
            int anInt = typedArray.getInt(R.styleable.TypeTextView_android_textStyle, 0);
            Typeface typeface = FontUtils.getInstance().getFont(context, fontName);
            if (typeface != null) {
                setTypeface(typeface,anInt);

            }
        }
//        }
    }

    public void setCustomFont(String fontName){
        Typeface typeface = FontUtils.getInstance().getFont(getContext(), fontName);
        if (typeface != null) {
            setTypeface(typeface);

        }
    }

    public void setCustomFont(String fontName, int style){
        Typeface typeface = FontUtils.getInstance().getFont(getContext(), fontName);
        if (typeface != null) {
            setTypeface(typeface,style);

        }
    }
}
