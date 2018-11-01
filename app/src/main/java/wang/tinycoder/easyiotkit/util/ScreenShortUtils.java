package wang.tinycoder.easyiotkit.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.util
 * Desc：截屏工具类
 * Author：TinycoderWang
 * CreateTime：2018/10/31 18:51
 */
public class ScreenShortUtils {

    /**
     * 剪切整个屏幕到Bitmap
     *
     * @param activity 当前的activity
     * @return
     */
    public Bitmap cutAllScreen(Activity activity) {
        View dView = activity.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        return Bitmap.createBitmap(dView.getDrawingCache());
    }


    /**
     * 剪切整个屏幕到文件
     *
     * @param activity 当前的activity
     * @param path     保存的位置
     * @param name     保存的名称
     * @return
     */
    public File cutAllScreenToDisk(Activity activity, String path, String name) {
        View dView = activity.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        if (bitmap != null) {
            try {
                // 图片文件路径
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(path, name);
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                return file;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 剪切View到Bitmap
     *
     * @param view 要剪切的View
     * @return
     */
    public Bitmap cutView(View view) {
        if (view != null && View.VISIBLE == view.getVisibility()) {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            return Bitmap.createBitmap(view.getDrawingCache());
        } else {
            return null;
        }
    }


    /**
     * 剪切View到文件
     *
     * @param view 要剪切的View
     * @param path 保存的位置
     * @param name 保存的名称
     * @return
     */
    public File cutViewToDisk(View view, String path, String name) {
        if (view != null && View.VISIBLE == view.getVisibility()) {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            if (bitmap != null) {
                try {
                    // 图片文件路径
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(path, name);
                    FileOutputStream os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    return file;
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


}
