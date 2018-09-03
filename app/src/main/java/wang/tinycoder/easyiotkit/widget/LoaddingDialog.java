package wang.tinycoder.easyiotkit.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.util.DensityUtils;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.widget
 * Desc：加载框
 * Author：TinycoderWang
 * CreateTime：2018/9/3 18:49
 */
public class LoaddingDialog extends DialogFragment {


    private AVLoadingIndicatorView mLoaddingView;
    private TextView mMessage;

    private String message;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = "正在加载";
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.loadding_dialog, null);
        mLoaddingView = view.findViewById(R.id.loadding);
        mMessage = view.findViewById(R.id.msg);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(DensityUtils.dip2px(context,120), DensityUtils.dip2px(context,120));
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
