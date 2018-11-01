package wang.tinycoder.easyiotkit.util;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.util
 * Desc：高斯模糊工具类
 * Author：TinycoderWang
 * CreateTime：2018/10/31 18:50
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.util.Log;


public class BlurUtil {

    /**
     * 用法
     * <p>
     * Bitmap finalBitmap = BlurUtil.with(Activity)
     * .bitmap(bitmap) //要模糊的图片
     * .radius(10)//模糊半径
     * .scale(4)//指定模糊前缩小的倍数
     * .policy(BlurUtil.BlurPolicy.FAST_BLUR)//使用fastBlur
     * .blur();
     */


    private static final String TAG = "BlurUtil";
    private static final float SCALE = 1.0F;//default scale
    private static volatile BlurUtil singleton = null;
    private Bitmap mBitmap;
    private int mRadius = 13;
    private float mScale = SCALE;
    private Context mContext;
    private BlurPolicy mPolicy = BlurPolicy.RS_BLUR;//默认使用rs 模糊图片

    // 模式
    public enum BlurPolicy {
        RS_BLUR,
        FAST_BLUR
    }


    /**
     * 单例
     *
     * @param context
     * @return
     */
    public static BlurUtil with(Context context) {
        if (singleton == null) {
            synchronized (BlurUtil.class) {
                if (singleton == null) {
                    singleton = new BlurUtil(context);
                }
            }
        }
        return singleton;
    }

    private BlurUtil(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public Bitmap blur() {
        if (mBitmap == null) {
            throw new RuntimeException("Bitmap can not be null");
        }
        if (mRadius == 0) {
            throw new RuntimeException("radius must > 0");
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            if (mPolicy == BlurPolicy.FAST_BLUR) {
                Log.d(TAG, "blur fast algorithm");
                return fastBlur(mBitmap, mScale, mRadius);
            } else {
                Log.d(TAG, "blur render script  algorithm");
                return rsBlur(mContext, mBitmap, mRadius, mScale);
            }

        } else {
            Log.d(TAG, "blur fast algorithm");
            return fastBlur(mBitmap, mScale, mRadius);
        }

    }

    /**
     * 模糊的算法策略
     *
     * @param policy
     * @return
     */
    public BlurUtil policy(BlurPolicy policy) {
        this.mPolicy = policy;
        return this;
    }

    /**
     * 模糊的Bitmap
     *
     * @param bitmap
     * @return
     */
    public BlurUtil bitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        return this;
    }

    /**
     * 缩放的系数
     *
     * @param scale
     * @return
     */
    public BlurUtil scale(int scale) {
        if ((scale & (scale - 1)) != 0) {   // 判断是否为2的次方
            scale = 8;
        }
        if (scale < 2) {
            scale = 2;
        }
        if (scale > 8) {
            scale = 8;
        }
        this.mScale = 1.0f / scale;
        return this;
    }

    /**
     * 模糊的半径，0-25
     *
     * @param radius
     * @return
     */
    public BlurUtil radius(int radius) {

        if (radius < 1) {
            radius = 1;
        }
        if (radius > 25) {
            radius = 25;
        }
        this.mRadius = radius;
        return this;
    }

    /**
     * 使用RenderScript 模糊图片
     *
     * @param context
     * @param source
     * @return
     */
    private static Bitmap rsBlur(Context context, Bitmap source, int radius, float scale) {
        try {
            Log.i(TAG, "origin size:" + source.getWidth() + "*" + source.getHeight());
            int width = Math.round(source.getWidth() * scale);
            int height = Math.round(source.getHeight() * scale);

            Bitmap bmp = Bitmap.createScaledBitmap(source, width, height, false);

            RenderScript renderScript = RenderScript.create(context);

            Log.i(TAG, "scale size:" + bmp.getWidth() + "*" + bmp.getHeight());

            // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
            final Allocation input = Allocation.createFromBitmap(renderScript, bmp);
            //Type: “一个Type描述了一个Allocation或者并行操作的Element和dimensions ”
            Type type = input.getType();
            final Allocation output = Allocation.createTyped(renderScript, type);

            //创建一个模糊效果的RenderScript的工具对象
            //第二个参数Element相当于一种像素处理的算法，高斯模糊的话用这个就好
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            //设置渲染的模糊程度, 25f是最大模糊度
            script.setRadius(radius);
            // 设置blurScript对象的输入内存
            script.setInput(input);
            // 将输出数据保存到输出刚刚创建的输出内存中
            script.forEach(output);
            // 将数据填充到bitmap中
            output.copyTo(bmp);

            //销毁它们释放内存
            input.destroy();
            output.destroy();
            script.destroy();
            type.destroy();

            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return fastBlur(source, scale, radius);
        }
    }

    /**
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     * <p>
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012
     * <p>
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * <p>
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     * <p>
     * If you are using this algorithm in your code please add
     * the following line:
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */

    private static Bitmap fastBlur(Bitmap sentBitmap, float scale, int radius) {
        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }


}
