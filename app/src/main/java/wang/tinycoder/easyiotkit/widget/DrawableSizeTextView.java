package wang.tinycoder.easyiotkit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import wang.tinycoder.easyiotkit.R;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.widget
 * Desc：可设置drawable大小的textview
 * Author：TinycoderWang
 * CreateTime：2018/11/4 16:16
 */
public class DrawableSizeTextView extends android.support.v7.widget.AppCompatTextView {
    public DrawableSizeTextView(Context context) {
        this(context, null);
    }

    public DrawableSizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableSizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /**
         * 取得自定义属性值
         */
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DrawableSizeTextView);
        int drawableWidth = ta.getDimensionPixelSize(R.styleable.DrawableSizeTextView_dstv_drawableWidth, -1);
        int drawableHeight = ta.getDimensionPixelSize(R.styleable.DrawableSizeTextView_dstv_drawableHeight, -1);

        /**
         * 取得TextView的Drawable(左上右下四个组成的数组值)
         */
        Drawable[] drawables = getCompoundDrawables();
        Drawable textDrawable = null;
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                textDrawable = drawable;
            }
        }

        /**
         * 设置宽高
         */
        if (textDrawable != null && drawableWidth != -1 && drawableHeight != -1) {
            textDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
        }
        /**
         * 设置给TextView
         */
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        /**
         * 回收ta
         */
        ta.recycle();
    }
}
