package com.wjika.client.base.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.interfaces.IActivityHelper;
import com.common.ui.FBaseFragment;
import com.common.utils.ToastUtil;
import com.common.widget.ProgressImageView;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.ExitManager;


public class BaseFragment extends FBaseFragment implements IActivityHelper {

    private static final String BASETAG = FBaseFragment.class.getSimpleName();
    public static final String RESPONSE_SUCCESS_CODE = "000";//全部网络请求，逻辑处理成功时返回的code
    public static final String RESPONSE_NO_LOGIN_CODE = "50009003";//全部网络请求未登录或token失效时返回的code
    protected View mLayoutLoading;
    protected ProgressImageView mImgLoading;
    protected TextView mTxtCardEmpty;
    protected TextView mTxtLoadingEmpty;
    protected TextView mTxtLoadingRetry;
    protected ImageView mImgLoadingRetry;
    protected ImageView mImgLoadingEmpty;

    private String baseTitle = "";

    public String getTitle() {
        return baseTitle;
    }

    public void setTitle(int titleId) {
        baseTitle = getString(titleId);
    }

    public void setTitle(String title) {
        baseTitle = title;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("baseTitle", baseTitle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            baseTitle = savedInstanceState.getString("baseTitle", getTitle());
        }
        registCityBroadcast();
    }
    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            receiverLogout(data);
        }
    };

    private void registCityBroadcast() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        lbm.registerReceiver(pushReceiver, new IntentFilter(BaseActivity.ACTION_PUSH_RECEIVER));
    }

    private void unregistCityBroadcast() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pushReceiver);
    }

    protected void receiverLogout(String data) {
        UserCenter.cleanLoginInfo(getActivity());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregistCityBroadcast();
    }
    @Override
    public void success(int requestCode, String data) {
        super.success(requestCode, data);
        closeProgressDialog();
        Entity entity = Parsers.getResponseSatus(data);
        if (RESPONSE_NO_LOGIN_CODE.equals(entity.getResultCode())) {
            //未登录或token失效，清空数据，打开登录页面
            UserCenter.cleanLoginInfo(this.getActivity());
            ToastUtil.shortShow(this.getActivity(), getResources().getString(R.string.str_token_invalid));
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (!RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())) {
            //返回数据不正确
            ToastUtil.shortShow(this.getActivity(), entity.getResultMsg());
        }
    }

    protected void initLoadingView(View.OnClickListener listener, View parentView) {
        mLayoutLoading = parentView.findViewById(R.id.loading_layout);
        mImgLoading = (ProgressImageView) parentView.findViewById(R.id.loading_img_anim);
        mTxtLoadingEmpty = (TextView) parentView.findViewById(R.id.loading_txt_empty);
        mTxtLoadingRetry = (TextView) parentView.findViewById(R.id.loading_txt_retry);
        mImgLoadingRetry = (ImageView) parentView.findViewById(R.id.loading_img_refresh);
        mImgLoadingEmpty = (ImageView) parentView.findViewById(R.id.loading_img_empty);
        mTxtCardEmpty = (TextView) parentView.findViewById(R.id.loading_btn_card_empty);
        mTxtCardEmpty.setOnClickListener(listener);
        mLayoutLoading.setOnClickListener(listener);
        mLayoutLoading.setClickable(false);
        mTxtCardEmpty.setClickable(false);
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

    public static enum LoadingStatus {
        INIT,
        LOADING,
        EMPTY,
        RETRY,
        GONE
    }
}
