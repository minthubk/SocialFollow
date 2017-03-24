package com.cathedrale.cview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Aspire on 2/4/2016.
 */
public class TypefaceTextview extends TextView {
    public TypefaceTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TypefaceTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypefaceTextview(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/HelveticaNeueLTStd Lt.otf");
        setTypeface(tf);
    }
}
