package com.wjika.client.base.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.interfaces.IActivityHelper;
import com.common.network.FProtocol;
import com.common.ui.FBaseActivity;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.utils.VersionInfoUtils;
import com.common.widget.ProgressImageView;
import com.wjika.cardagent.client.R;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.UpdateVersionEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.update.UpdateActiviy;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ConfigUtils;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.widget.PasswordInputView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.IdentityHashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * @author songxudong
 */
public class BaseActivity extends FBaseActivity implements IActivityHelper {

    public static final String RESPONSE_SUCCESS_CODE = "000";//全部网络请求，逻辑处理成功时返回的code
    public static final String RESPONSE_NO_LOGIN_CODE = "50009003";//全部网络请求未登录或token失效时返回的code
    public static final String RESPONSE_VERIFY_FAILED_CODE = "50008052";
    public static final String RESPONSE_ADD_FAILED_CODE = "50008056";//优惠码添加失败的code
    public static final String RESPONSE_USER_LOCKED = "50008053";//优惠码添加失败的code
	public static final String RESPONSE_NO_REGISTER = "50008001";//用户未注册
    public static final int REQUEST_UPDATE_VERSION_CODE = -3;
	protected static final int REQUEST_NET_PAYPWD_SALT = -4;
	protected static final int REQUEST_NET_VERIFY_PAYPWD = -5;
    public static final long IGNORE_UPDATE_DAYS = 7;

    public static final String ACTION_PUSH_RECEIVER = "com.wjk.action.push.receiver";

    protected Resources res;
    protected View mLayoutLoading;
    protected ProgressImageView mImgLoading;
    protected TextView mTxtCardEmpty;
    protected TextView mTxtLoadingEmpty;
    protected TextView mTxtLoadingRetry;
    protected ImageView mImgLoadingRetry;
    protected ImageView mImgLoadingEmpty;
    private AlertDialog wjkAlertDialog;
	protected String paypwd;
	protected PasswordInputView ecardDialogPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitManager.instance.addActivity(this);
        res = getResources();
        registCityBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            receiverLogout(data);
        }
    };

    private void registCityBroadcast() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(pushReceiver, new IntentFilter(ACTION_PUSH_RECEIVER));
    }

    private void unregistCityBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pushReceiver);
    }

    protected void receiverLogout(String data) {
        UserCenter.cleanLoginInfo(this);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            int key = jsonObject.optInt("type");
            int msgStrResId = R.string.str_token_invalid;
            if (key == 5) {
                msgStrResId = R.string.str_user_frozen;
            }
            ToastUtil.shortShow(this, getResources().getString(msgStrResId));
        }
    }

    protected void noLogin() {
    }

    @Override
    public void success(int requestCode, String data) {
        closeProgressDialog();

        Entity entity = Parsers.getResponseSatus(data);
        if (RESPONSE_NO_LOGIN_CODE.equals(entity.getResultCode())) {
            //未登录或token失效，清空数据，打开登录页面
            UserCenter.cleanLoginInfo(this);
            ToastUtil.shortShow(this, getResources().getString(R.string.str_token_invalid));
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            noLogin();
        } else if (!RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())) {
            //返回数据不正确
            ToastUtil.shortShow(this, entity.getResultMsg());
            if (mLayoutLoading != null && mLayoutLoading.getVisibility() == View.VISIBLE) {
                setLoadingStatus(LoadingStatus.RETRY);
            }
        } else if (requestCode == REQUEST_UPDATE_VERSION_CODE) {
            UpdateVersionEntity updateVersionEntity = Parsers.getUpdateVersionInfo(data);
            if (updateVersionEntity != null && updateVersionEntity.getType() != 0) {
                long ignoreDate = ConfigUtils.getIgnoreDate(this);
                if (ignoreDate > 0) {
                    long now = new Date().getTime();
                    long howLong = now - ignoreDate;
                    long days = howLong / 1000 / 60 / 60 / 24;
                    if (days < IGNORE_UPDATE_DAYS && days >= 0) {
                        return;
                    } else {
                        ConfigUtils.setIgnoreDate(this, 0);
                    }
                }
                String url = updateVersionEntity.getDownloadUrl() == null ? "" : updateVersionEntity.getDownloadUrl();
                String version = updateVersionEntity.getVersion() == null ? "" : updateVersionEntity.getVersion();
                startActivity(new Intent().setClass(this, UpdateActiviy.class)
                        .putExtra(UpdateActiviy.KEY_UPDATE_TYPE, updateVersionEntity.getType())
                        .putExtra(UpdateActiviy.KEY_UPDATE_URL, url)
                        .putExtra(UpdateActiviy.KEY_UPDATE_VERSION_NAME, version)
                        .putExtra(UpdateActiviy.KEY_UPDATE_VERSION_DESC, updateVersionEntity.getDesc())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } else {
            parseData(requestCode, data);
        }
    }

    /**
     * 请求成功后实际处理数据的方法
     */
    protected void parseData(int requestCode, String data) {
        setLoadingStatus(LoadingStatus.GONE);
    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        closeProgressDialog();
        setLoadingStatus(LoadingStatus.GONE);
        ToastUtil.shortShow(this, errorMessage);
    }

    protected boolean checkUpdateVersion() {
        int versionCode = VersionInfoUtils.getVersionCode(this);
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("version", "" + versionCode);
        params.put(Constants.TOKEN, "");
        requestHttpData(Constants.Urls.URL_CHECK_UPDATE_VERSION, REQUEST_UPDATE_VERSION_CODE, FProtocol.HttpMethod.POST, params);
        return false;
    }

	protected void showAlertDialog(String title,
                                   @NonNull String message,
                                   boolean cancelable,
                                   @NonNull String okText,
                                   @NonNull View.OnClickListener onOkListener) {
        View view = LayoutInflater.from(this).inflate(R.layout.wjk_alert_dialog, null);
        wjkAlertDialog = new AlertDialog.Builder(this).setView(view).create();
        wjkAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtTitle = (TextView) view.findViewById(R.id.alert_title);
        TextView txtMessage = (TextView) view.findViewById(R.id.alert_message);
        TextView btnOne = (TextView) view.findViewById(R.id.alert_btn_one);
        View llBtn = view.findViewById(R.id.alert_btn_ll);
        if (StringUtil.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
        }
        txtMessage.setText(message);
        llBtn.setVisibility(View.GONE);
        btnOne.setVisibility(View.VISIBLE);
        btnOne.setText(okText);
        btnOne.setOnClickListener(onOkListener);
        wjkAlertDialog.setCancelable(cancelable);
        wjkAlertDialog.show();
        setAlertDialogWidth();

    }

    protected void showAlertDialog(String title,
                                   @NonNull String message,
                                   @NonNull String cancelText,
                                   @NonNull String okText,
                                   @NonNull View.OnClickListener onCancelListener,
                                   @NonNull View.OnClickListener onOkListener) {
        showAlertDialog(title,message,true,cancelText,okText,onCancelListener,onOkListener);
    }

    protected void showAlertDialog(String title,
                                   @NonNull String message,
                                   boolean cancelable,
                                   @NonNull String cancelText,
                                   @NonNull String okText,
                                   @NonNull View.OnClickListener onCancelListener,
                                   @NonNull View.OnClickListener onOkListener) {
        View view = LayoutInflater.from(this).inflate(R.layout.wjk_alert_dialog, null);
        wjkAlertDialog = new AlertDialog.Builder(this).setView(view).create();
        wjkAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtTitle = (TextView) view.findViewById(R.id.alert_title);
        TextView txtMessage = (TextView) view.findViewById(R.id.alert_message);
        TextView btnCancel = (TextView) view.findViewById(R.id.alert_btn_cancel);
        TextView btnOk = (TextView) view.findViewById(R.id.alert_btn_ok);
        if (StringUtil.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
        }
        txtMessage.setText(message);
        if (StringUtil.isEmpty(cancelText)) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setText(cancelText);
            btnCancel.setOnClickListener(onCancelListener);
        }
        btnOk.setText(okText);
        btnOk.setOnClickListener(onOkListener);
        wjkAlertDialog.setCancelable(cancelable);
        wjkAlertDialog.show();
        setAlertDialogWidth();
    }

    protected void showAlertDialog(String title,
                                   @NonNull String message,
                                   @NonNull String cancelText,
                                   @NonNull String okText,
                                   @NonNull View.OnClickListener onCancelListener,
                                   @NonNull final AlertEtClickListenner onOkListener) {
        View view = LayoutInflater.from(this).inflate(R.layout.wjk_alert_dialog, null);
        wjkAlertDialog = new AlertDialog.Builder(this).setView(view).create();
        wjkAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtTitle = (TextView) view.findViewById(R.id.alert_title);
        TextView txtMessage = (TextView) view.findViewById(R.id.alert_message);
        TextView btnCancel = (TextView) view.findViewById(R.id.alert_btn_cancel);
        TextView btnOk = (TextView) view.findViewById(R.id.alert_btn_ok);
        TextView tvEditTextTitle = (TextView) view.findViewById(R.id.alert_edittext_title);
        View llAlertEditText = view.findViewById(R.id.alert_edittext_ll);
        final EditText etAlert = (EditText) view.findViewById(R.id.alert_et);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodUtil.showInput(BaseActivity.this, etAlert);
            }
        }, 100);

        if (StringUtil.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
        }
        txtMessage.setVisibility(View.GONE);
        llAlertEditText.setVisibility(View.VISIBLE);

        tvEditTextTitle.setText(message);

        if (StringUtil.isEmpty(cancelText)) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setText(cancelText);
            btnCancel.setOnClickListener(onCancelListener);
        }

        btnOk.setText(okText);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkListener.onClick(v, etAlert);
            }
        });
        wjkAlertDialog.show();
        wjkAlertDialog.setCancelable(false);
        setAlertDialogWidth();
    }

    public interface AlertEtClickListenner {
        void onClick(View v, EditText editText);
    }

    protected void showAlertDialog(String title,
                                   @NonNull String message,
                                   @NonNull String okText,
                                   @NonNull View.OnClickListener onOkListener) {
        View view = LayoutInflater.from(this).inflate(R.layout.wjk_alert_dialog, null);
        wjkAlertDialog = new AlertDialog.Builder(this).setView(view).create();
        wjkAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtTitle = (TextView) view.findViewById(R.id.alert_title);
        TextView txtMessage = (TextView) view.findViewById(R.id.alert_message);
        TextView btnOne = (TextView) view.findViewById(R.id.alert_btn_one);
        View llBtn = view.findViewById(R.id.alert_btn_ll);
        if (StringUtil.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
        }
        txtMessage.setText(message);
        llBtn.setVisibility(View.GONE);
        btnOne.setVisibility(View.VISIBLE);
        btnOne.setText(okText);
        btnOne.setOnClickListener(onOkListener);
        wjkAlertDialog.show();
        setAlertDialogWidth();
    }

    private void setAlertDialogWidth() {
        WindowManager.LayoutParams params = wjkAlertDialog.getWindow().getAttributes();
        params.width = CommonTools.dp2px(this, 270f);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wjkAlertDialog.getWindow().setAttributes(params);
    }

    protected void showAlertDialog(String title,
                                   @NonNull SpannableStringBuilder message,
                                   @NonNull String okText,
                                   @NonNull View.OnClickListener onOkListener) {
        View view = LayoutInflater.from(this).inflate(R.layout.wjk_alert_dialog, null);
        wjkAlertDialog = new AlertDialog.Builder(this).setView(view).create();
        wjkAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtTitle = (TextView) view.findViewById(R.id.alert_title);
        TextView txtMessage = (TextView) view.findViewById(R.id.alert_message);
        TextView btnOne = (TextView) view.findViewById(R.id.alert_btn_one);
        View llBtn = view.findViewById(R.id.alert_btn_ll);

        if (StringUtil.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
        }
        txtMessage.setText(message);
        llBtn.setVisibility(View.GONE);
        btnOne.setVisibility(View.VISIBLE);
        btnOne.setText(okText);
        btnOne.setOnClickListener(onOkListener);
        wjkAlertDialog.show();
        setAlertDialogWidth();
    }

    protected void closeDialog() {
        if (wjkAlertDialog != null) {
            wjkAlertDialog.dismiss();
        }
    }

    protected void initLoadingView(View.OnClickListener listener) {
        mLayoutLoading = findViewById(R.id.loading_layout);
        mImgLoading = (ProgressImageView) findViewById(R.id.loading_img_anim);
        mTxtLoadingEmpty = (TextView) findViewById(R.id.loading_txt_empty);
        mTxtLoadingRetry = (TextView) findViewById(R.id.loading_txt_retry);
        mImgLoadingRetry = (ImageView) findViewById(R.id.loading_img_refresh);
        mImgLoadingEmpty = (ImageView) findViewById(R.id.loading_img_empty);
        mTxtCardEmpty = (TextView) findViewById(R.id.loading_btn_card_empty);
        if (mTxtCardEmpty != null) {
            mTxtCardEmpty.setOnClickListener(listener);
            mTxtCardEmpty.setClickable(false);
        }
        if (mLayoutLoading != null) {
            mLayoutLoading.setOnClickListener(listener);
            mLayoutLoading.setClickable(false);
        }
    }

    protected void setLoadingStatus(LoadingStatus status) {
        if (mLayoutLoading == null || mImgLoading == null || mImgLoadingEmpty == null
                || mImgLoadingRetry == null || mTxtLoadingEmpty == null || mTxtLoadingRetry == null) {
            return;
        }
        switch (status) {
            case LOADING: {
                mLayoutLoading.setClickable(false);
                mLayoutLoading.setVisibility(View.VISIBLE);
                mImgLoading.setVisibility(View.VISIBLE);
                mImgLoadingEmpty.setVisibility(View.GONE);
                mImgLoadingRetry.setVisibility(View.GONE);
                mTxtLoadingEmpty.setVisibility(View.GONE);
                mTxtLoadingRetry.setVisibility(View.GONE);
                mTxtCardEmpty.setVisibility(View.GONE);
                mTxtCardEmpty.setClickable(false);
                break;
            }
            case EMPTY: {
                mTxtCardEmpty.setClickable(false);
                mLayoutLoading.setClickable(false);
                mLayoutLoading.setVisibility(View.VISIBLE);
                mImgLoading.setVisibility(View.GONE);
                mImgLoadingEmpty.setVisibility(View.VISIBLE);
                mTxtLoadingEmpty.setVisibility(View.VISIBLE);
                mImgLoadingRetry.setVisibility(View.GONE);
                mTxtLoadingRetry.setVisibility(View.GONE);
                mTxtCardEmpty.setVisibility(View.GONE);
                break;
            }
            case RETRY: {
                mTxtCardEmpty.setClickable(false);
                mLayoutLoading.setClickable(true);
                mLayoutLoading.setVisibility(View.VISIBLE);
                mImgLoading.setVisibility(View.GONE);
                mImgLoadingEmpty.setVisibility(View.GONE);
                mTxtLoadingEmpty.setVisibility(View.GONE);
                mImgLoadingRetry.setVisibility(View.VISIBLE);
                mTxtLoadingRetry.setVisibility(View.VISIBLE);
                mTxtCardEmpty.setVisibility(View.GONE);
                break;
            }
            case GONE: {
                mTxtCardEmpty.setClickable(false);
                mLayoutLoading.setClickable(false);
                mLayoutLoading.setVisibility(View.GONE);
                mImgLoading.setVisibility(View.GONE);
                mTxtLoadingEmpty.setVisibility(View.GONE);
                mTxtLoadingRetry.setVisibility(View.GONE);
                mImgLoadingEmpty.setVisibility(View.GONE);
                mImgLoadingRetry.setVisibility(View.GONE);
                mTxtCardEmpty.setVisibility(View.GONE);
                break;
            }
        }
    }

    public enum LoadingStatus {
        INIT,
        LOADING,
        EMPTY,
        RETRY,
        GONE
    }

    @Override
    public void onDestroy() {
        ExitManager.instance.remove(this);
        super.onDestroy();
        unregistCityBroadcast();
    }
}
