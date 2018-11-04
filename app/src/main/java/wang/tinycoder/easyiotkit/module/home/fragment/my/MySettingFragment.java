package wang.tinycoder.easyiotkit.module.home.fragment.my;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.app.Constants;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.base.BaseFragment;
import wang.tinycoder.easyiotkit.bean.User;
import wang.tinycoder.easyiotkit.module.login.LoginActivity;
import wang.tinycoder.easyiotkit.module.webview.WebViewActivity;
import wang.tinycoder.easyiotkit.net.imageloader.GlideApp;
import wang.tinycoder.easyiotkit.widget.CircleImageView;
import wang.tinycoder.easyiotkit.widget.DrawableSizeTextView;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.home.fragment.my
 * Desc：个人设置的页面
 * Author：TinycoderWang
 * CreateTime：2018/4/22 10:55
 */
public class MySettingFragment extends BaseFragment<MySettingPresenter> implements MySettingContract.View {

    @BindView(R.id.iv_header_bg)
    ImageView mIvHeaderBg;
    @BindView(R.id.iv_header)
    CircleImageView mIvHeader;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.et_sign)
    EditText mEtSign;
    @BindView(R.id.tv_logout)
    TextView mTvLogout;
    @BindView(R.id.tv_qq_group)
    DrawableSizeTextView mTvQqGroup;
    @BindView(R.id.tv_github)
    DrawableSizeTextView mTvGithub;
    @BindView(R.id.tv_phone)
    DrawableSizeTextView mTvPhone;
    @BindView(R.id.tv_email)
    DrawableSizeTextView mTvEmail;


    @Override
    protected int getlayoutId() {
        return R.layout.fragment_my_setting;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new MySettingPresenter(this, new MySettingModel());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mEtSign.setText("EasyLinker，让物联更简单！");
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        // 加载个人信息
        mPresenter.loadSelfInfo();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {
        ((BaseActivity) mActivity).showToast(message);
    }

    @Override
    public void onLoadSelfInfo(boolean success) {
        if (success) {

            User userInfo = mPresenter.getUserInfo();
            if (userInfo != null) {

                // 头像
                String avatar = userInfo.getAvatar();
                if (!TextUtils.isEmpty(avatar)) {
                    if (avatar.startsWith("/")) {
                        avatar = Constants.BASE_HOST + "api" + avatar;
                    } else {
                        avatar = Constants.BASE_HOST + "api/" + avatar;
                    }
                }
                GlideApp.with(this)
                        .load(avatar)
                        .placeholder(R.drawable.ic_header_default)
                        .error(R.drawable.ic_header_default)
                        .into(mIvHeader);

                // 名字
                String username = userInfo.getUsername();
                mTvName.setText(TextUtils.isEmpty(username) ? "用户名" : username);
                // 手机
                String phone = userInfo.getPhone();
                mTvPhone.setText(TextUtils.isEmpty(phone) ? "无" : phone);
                // 邮箱
                String email = userInfo.getEmail();
                mTvEmail.setText(TextUtils.isEmpty(email) ? "无" : email);
            }
        }
    }

    @Override
    public void logoutSuccess() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(intent);
        ((BaseActivity) mActivity).finish();
    }


    @OnClick({R.id.tv_logout, R.id.tv_qq_group, R.id.tv_github})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_logout:   // 退出
                mPresenter.logout();
                break;
            case R.id.tv_qq_group:   // qq群
                // 将qq群号复制到剪切板
                try {
                    String qqGroup = mActivity.getString(R.string.qq_group);
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("qqGroupNumber", qqGroup);
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);

                    showMessage("qq群号已经复制进剪切板，欢迎加入！");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_github:   // github
                // webview
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra(Constants.WEB_URL, mActivity.getString(R.string.github_addr));
                mActivity.startActivity(intent);
//                // 手机浏览器应用
//                try {
//                    Uri uri = Uri.parse(mActivity.getString(R.string.github_addr));
//                    Intent git = new Intent(Intent.ACTION_VIEW, uri);
//                    git.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(git);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
        }
    }

}
