package com.saqcess.qicpic.customeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.fonts.FontUtils;

public class TypeEditTextView extends AppCompatEditText {
    public TypeEditTextView(@NonNull Context context) {
        super(context);
    }

    public TypeEditTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inIt(context, attrs);
    }

    public TypeEditTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inIt(context, attrs);
    }



    private void inIt(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            if (attrs != null) {
                TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TypeEditTextView);
                String fontName = typedArray.getString(R.styleable.TypeEditTextView_custom_font);
                int anInt = typedArray.getInt(R.styleable.TypeTextView_android_textStyle, 0);
                Typeface typeface = FontUtils.getInstance().getFont(context, fontName);
                if (typeface != null) {
                    setTypeface(typeface,anInt);

                }
            }


        }
    }

    public interface DeleteEmptyListener {
        void onDeleteEmpty();
    }
    private DeleteEmptyListener mDeleteEmptyListener;
    private void listenForDeleteEmpty() {
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && mDeleteEmptyListener != null
                        && length() == 0) {
                    mDeleteEmptyListener.onDeleteEmpty();
                }
                return false;
            }
        });
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new SoftDeleteInputConnection(super.onCreateInputConnection(outAttrs), true);
    }

    private class SoftDeleteInputConnection extends InputConnectionWrapper {

        public SoftDeleteInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // This method works on modern versions of Android with soft keyboard delete.
            if (getTextBeforeCursor(1, 0).length() == 0 && mDeleteEmptyListener != null) {
                mDeleteEmptyListener.onDeleteEmpty();
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
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
