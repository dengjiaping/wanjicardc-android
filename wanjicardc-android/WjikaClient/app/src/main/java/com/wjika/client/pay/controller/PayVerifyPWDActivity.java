package com.wjika.client.pay.controller;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.EnCryptionUtils;
import com.common.utils.NetWorkUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.cardpackage.controller.CardPackageActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.message.controller.MessageCenterActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.PayVerifyPwdEntity;
import com.wjika.client.network.entities.VerifyResultEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.ComplainMessageFirstActivity;
import com.wjika.client.person.controller.FindPassByAuthActivity;
import com.wjika.client.person.controller.PersonalFragment;
import com.wjika.client.person.controller.VerifySafeQuestionActivity;
import com.wjika.client.utils.ConfigUtils;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.ViewInjectUtils;
import com.wjika.client.widget.PasswordInputView;

import java.util.Calendar;
import java.util.IdentityHashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jacktian on 15/11/2.
 * 校验支付密码
 */
public class PayVerifyPWDActivity extends ToolBarActivity implements View.OnClickListener {

    public static final String FROM_EXTRA = "extra_from";
    public static final String IDENTITY_EXTRA = "identity_extra";
    public static final int LAUNCH = 1;
    public static final int SETTING = 2;
    public static final int PAYCONSUMES = 3;
	public static final int INPUT_PHONE = 4;
	public static final int ALTER_PAY_PWD = 6;
    private static final int VERIFY_PWD_TIMES = 5;//第五次提示锁定信息
    private static final int REQUEST_VERIFY_PAY_PWD_CODE = 0x1;
    private static final int REQUEST_PAY_PWD_SALT_CODE = 0x2;
    private static final int REQUEST_VERIFY_ACCOUNT_CODE = 0x3;
    private static final int REQUEST_SET_PAY_PWD_CODE = 1001;

    @ViewInject(R.id.pay_pwd_input)
    private PasswordInputView paypwdInput;
    @ViewInject(R.id.txt_forget_pwd)
    private TextView mTxtForgetPwd;

    private String paypwd;
    private String paypwdSalt;
    private int from;
    private String identity;
    private long lastLocTimestamp;
    private int lastLocNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_verify_pwd_act);
        ViewInjectUtils.inject(this);
        from = getIntent().getIntExtra(FROM_EXTRA, 0);
        identity = getIntent().getStringExtra(IDENTITY_EXTRA);
	    if (ALTER_PAY_PWD == from){
		    ExitManager.instance.addVerifyActivity(this);
	    } else {
            ExitManager.instance.addLoginActivity(this);
        }

        initVerifyErrorTimes();
        initView();
    }

    private void initView() {
        setLeftTitle(res.getString(R.string.pay_verify_password_authentication));
        if (from == LAUNCH){
            mBtnTitleLeft.setVisibility(View.GONE);
        }
        if(INPUT_PHONE == from) {
            mTxtForgetPwd.setVisibility(View.GONE);
        } else {
            mTxtForgetPwd.setVisibility(View.VISIBLE);
        }
//        mBtnTitleLeft.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_close_button));
        mTxtForgetPwd.setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodUtil.showInput(PayVerifyPWDActivity.this, paypwdInput);
            }
        }, 100);
        paypwdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                paypwd = paypwdInput.getText().toString();
                if (6 == paypwd.length()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog();
                            if (INPUT_PHONE == from) {//更换手机号
                                IdentityHashMap<String, String> params = new IdentityHashMap<>();
                                params.put("token", "");
                                params.put("phone", getIntent().getStringExtra("mobile"));
                                params.put("payPassword", paypwd);
                                requestHttpData(Constants.Urls.URL_POST_VERIFY_MOBILE, REQUEST_VERIFY_ACCOUNT_CODE, FProtocol.HttpMethod.POST, params);
                            } else {
                                loadPaypwdSalt();
                            }
                        }
                    }, 50);
                }
            }
        });
    }

    private void initVerifyErrorTimes() {
        String locTimes = ConfigUtils.getLocVerifyPWDTimes(this);
        try {
            if (locTimes != null && locTimes.contains(ConfigUtils.TIMES_SPLIT)){
                lastLocTimestamp = Long.parseLong(locTimes.split(ConfigUtils.TIMES_SPLIT)[0]);
                lastLocNumber = Integer.parseInt(locTimes.split(ConfigUtils.TIMES_SPLIT)[1]);
            }
        }catch (Exception e){
            lastLocTimestamp = 0;
            lastLocNumber = 0;
        }
        long currentTimestamp = Calendar.getInstance().getTimeInMillis();
        if (lastLocNumber >= VERIFY_PWD_TIMES && currentTimestamp - lastLocTimestamp >= ConfigUtils.TIMES_SPACE){
            lastLocTimestamp = 0;
            lastLocNumber = 0;
            ConfigUtils.setLocVerifyPWDTimes(this, currentTimestamp + ConfigUtils.TIMES_SPLIT + lastLocNumber);
        }
    }

    private void loadPaypwdSalt(){
        if (StringUtil.isEmpty(paypwdSalt)){
            paypwdSalt = UserCenter.getUserPaypwdSalt(this);
        }
        if (StringUtil.isEmpty(paypwdSalt)){
            requestHttpData(String.format(Constants.Urls.URL_PAY_PWD_SHA_KEY, UserCenter.getToken(this)), REQUEST_PAY_PWD_SALT_CODE);
        }else {
            verifyPaypwd();
        }
    }

    private void verifyPaypwd() {
        if (from == PAYCONSUMES && !StringUtil.isEmpty(UserCenter.getUserPaypwd(this))) { //本地验证
            boolean verifyOk = TextUtils.equals(EnCryptionUtils.SHA1(paypwd, paypwdSalt), UserCenter.getUserPaypwd(this));
            if(doVerifyPwdResult(verifyOk)) {
                setResult(RESULT_OK);
                finish();
            }
        } else { //网络验证
            if (NetWorkUtil.isConnect(this)) {
                IdentityHashMap<String, String> params = new IdentityHashMap<>();
                paypwd = EnCryptionUtils.SHA1(paypwd, paypwdSalt);
                params.put("payPassword", paypwd);
                params.put("token", UserCenter.getToken(this));
                params.put("userPushID", JPushInterface.getRegistrationID(this.getApplicationContext()));
                params.put("identity",identity);
                requestHttpData(Constants.Urls.URL_VERIFY_PAY_PWD, REQUEST_VERIFY_PAY_PWD_CODE, FProtocol.HttpMethod.POST, params);
            } else {
                ToastUtil.shortShow(this, res.getString(R.string.pay_verify_check_network));
                closeProgressDialog();
            }
        }
    }

    private boolean doVerifyPwdResult(boolean isVerifyOk) {
        long currentTimestamp = Calendar.getInstance().getTimeInMillis();
        if (lastLocNumber >= VERIFY_PWD_TIMES && currentTimestamp - lastLocTimestamp >= ConfigUtils.TIMES_SPACE){
            lastLocTimestamp = currentTimestamp;
            lastLocNumber = 0;
        }
        if (lastLocNumber < VERIFY_PWD_TIMES) {
            if (isVerifyOk) {
                lastLocNumber=0;
                ConfigUtils.setLocVerifyPWDTimes(this, currentTimestamp+ConfigUtils.TIMES_SPLIT+lastLocNumber);
                return true;
            } else {
                lastLocNumber = lastLocNumber + 1;
                lastLocTimestamp = currentTimestamp;
                paypwdInput.setText("");
                if (lastLocNumber < VERIFY_PWD_TIMES){
                    ConfigUtils.setLocVerifyPWDTimes(this, lastLocTimestamp + ConfigUtils.TIMES_SPLIT + lastLocNumber);
                    showVerifyFailedDialog(getString(R.string.verify_safe_validation_fails),String.format(getString(R.string.pay_verify_wrong_password), String.valueOf(VERIFY_PWD_TIMES - lastLocNumber)),R.string.person_pay_setting_confirm_pwd);
                }else {
                    showResidueTime(currentTimestamp);
                }
                return false;
            }
        }else {
            showResidueTime(currentTimestamp);
            return false;
        }
    }

	private void showResidueTime(long currentTimestamp) {
		long time = currentTimestamp - lastLocTimestamp;
		long tempTime = (ConfigUtils.TIMES_SPACE - time) / 60 / 60 / 1000;
        String message = "";
        if (tempTime < 1){
			time = tempTime == 0 ? 1 : tempTime;
            message = getString(R.string.pwd_locked_minute,String.valueOf(time));
			showVerifyFailedDialog(getString(R.string.title_account_locked), message ,R.string.pay_verify_pay_back_password);
		}else {
            message = getString(R.string.pwd_locked_hour,String.valueOf(tempTime));
			showVerifyFailedDialog(getString(R.string.title_account_locked), message , R.string.pay_verify_pay_back_password);
		}
	}

	@Override
    public void success(int requestCode, String data) {
        closeProgressDialog();
        PayVerifyPwdEntity entity = null;
        if (data != null){
            entity = Parsers.getPayVerifyPwdEntity(data);
            if (RESPONSE_NO_LOGIN_CODE.equals(entity.getResultCode())){
                //未登录或token失效，清空数据，打开登录页面
                UserCenter.cleanLoginInfo(this);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (RESPONSE_NO_REGISTER.equals(entity.getResultCode())){
                ToastUtil.shortShow(this,entity.getResultMsg());
            }
        }

        switch (requestCode){
            case REQUEST_VERIFY_PAY_PWD_CODE:
				if (entity != null){
					if (RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())){
						UserCenter.setUserPaypwd(this, paypwd);
                        UserCenter.setToken(this,entity.getToken());
						if (from == LAUNCH) {
                            Intent intent = new Intent(PersonalFragment.ACTION_PERSON_LOGIN);
                            intent.putExtra("login_result", true);
                            LocalBroadcastManager.getInstance(PayVerifyPWDActivity.this).sendBroadcast(intent);
                            ExitManager.instance.closeLoginActivity();
						} else if (from == SETTING || from == ALTER_PAY_PWD) {
							setResult(RESULT_OK);
                            finish();
						} else if (from == LoginActivity.FROM_MAIN_CARD_PKG) {
							Intent intent = new Intent(PersonalFragment.ACTION_PERSON_LOGIN);
							intent.putExtra("login_result", true);
							LocalBroadcastManager.getInstance(PayVerifyPWDActivity.this).sendBroadcast(intent);
							startActivity(new Intent(this, CardPackageActivity.class));
							ExitManager.instance.closeLoginActivity();
						} else if(from == LoginActivity.FROM_MESSAGE_CENTER) {
                            Intent messageIntent = new Intent(PayVerifyPWDActivity.this, MessageCenterActivity.class);
                            messageIntent.putExtra(MessageCenterActivity.EXTRA_FROM,MessageCenterActivity.FROM_LOGIN);
                            startActivity(messageIntent);
                            ExitManager.instance.closeLoginActivity();
                        } else if (from == LoginActivity.FROM_PERSON_CENTER) {
                            setResult(RESULT_OK);
                            finish();
                        } else if(from == LoginActivity.FROM_CARD_DETAIL_CODE){
                            setResult(RESULT_OK);
                            finish();
                        } else if (from == LoginActivity.FROM_MAIN_ACTIVITY) {
							setResult(RESULT_OK);
							finish();
						} else {
                            ExitManager.instance.closeLoginActivity();
                        }
					} else {
                        if(entity.isLockedStatus()) {
                            showVerifyFailedDialog(getString(R.string.title_account_locked),entity.getResultMsg(),R.string.pay_verify_pay_back_password);
                        } else {
                            showVerifyFailedDialog(getString(R.string.verify_safe_validation_fails),entity.getResultMsg(),R.string.person_pay_setting_confirm_pwd);
                        }
					}
					paypwdInput.setText("");
				}
                break;
            case REQUEST_PAY_PWD_SALT_CODE:
                if (data != null){
                    paypwdSalt = Parsers.getPaypwdSalt(data);
                    if (!StringUtil.isEmpty(paypwdSalt)){
                        UserCenter.setUserPaypwdSalt(this, paypwdSalt);
                        verifyPaypwd();
                    }
                }
                break;
            case REQUEST_VERIFY_ACCOUNT_CODE:
	            VerifyResultEntity verifyResult = Parsers.getVerifyResult(data);
	            if (verifyResult != null){
		            if (RESPONSE_SUCCESS_CODE.equals(verifyResult.getResultCode())){
			            Intent intent = new Intent(this, LoginActivity.class);
			            intent.putExtra(LoginActivity.EXTRA_FROM, LoginActivity.FROM_INPUT_OLD_PHONE);
			            intent.putExtra("token", verifyResult.getToken());
			            intent.putExtra("paypwd", paypwd);
			            startActivity(intent);
			            paypwdInput.setText("");
			            finish();
		            }else {
			            paypwdInput.setText("");
                        if(entity.isLockedStatus()) {
                            showVerifyFailedDialog(getString(R.string.title_account_locked),entity.getResultMsg(),R.string.pay_verify_pay_back_password);
                        } else {
                            showVerifyFailedDialog(getString(R.string.verify_safe_validation_fails),entity.getResultMsg(),R.string.person_pay_setting_confirm_pwd);
                        }
			            UserCenter.setAppealStatus(this, verifyResult.getAppealStatus());
		            }
	            }
	            break;
        }
    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        super.mistake(requestCode, status, errorMessage);
        paypwdInput.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_SET_PAY_PWD_CODE:
                {
                    if (from == LAUNCH){
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }else if (from == SETTING || from == ALTER_PAY_PWD){
                        setResult(RESULT_OK);
                    }else if (from == PAYCONSUMES){
                        setResult(RESULT_OK);
                    }
                    finish();
                }
            }
        }
    }

    /**
     *
     * @param msg
     * @param okTextResId 再试一次R.string.person_pay_setting_confirm_pwd; 找回支付密码R.string.pay_verify_pay_back_password
     */
    private void showVerifyFailedDialog(String title,String msg,int okTextResId){
        closeProgressDialog();
        paypwdInput.setText("");
        showAlertDialog(title,
                msg,
                res.getString(R.string.verify_safe_cancel),
                res.getString(okTextResId),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeDialog();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView tv = (TextView)v;
                        if(tv.getText().equals(res.getString(R.string.pay_verify_pay_back_password))) {
                            showMenu(UserCenter.isSetSecurity(getApplicationContext()),UserCenter.isAuthencaiton(getApplicationContext()));
                        }
                        closeDialog();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_forget_pwd:
                showMenu(UserCenter.isSetSecurity(getApplicationContext()),UserCenter.isAuthencaiton(this));
        }
    }

    private void showMenu(boolean isShowSecurity,boolean isAuthencation) {
        final Dialog dialog = new Dialog(PayVerifyPWDActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(R.layout.chooseway_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //window.setWindowAnimations(R.style.dialogAnimation);
        // 可以在此设置显示动画
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        View tvSecurity = dialog.findViewById(R.id.tv_security_find);
        TextView tvAuthenticaion = (TextView) dialog.findViewById(R.id.tv_authentication_find);
        View vLine1 = dialog.findViewById(R.id.v_Line1);
        View vLine2 = dialog.findViewById(R.id.v_Line2);
        if (isAuthencation) {
            tvAuthenticaion.setVisibility(View.VISIBLE);
            vLine1.setVisibility(View.VISIBLE);
        } else {
            tvAuthenticaion.setVisibility(View.GONE);
            vLine1.setVisibility(View.GONE);
        }
        if(isShowSecurity) {
            tvSecurity.setVisibility(View.VISIBLE);
            vLine2.setVisibility(View.VISIBLE);
        } else {
            tvSecurity.setVisibility(View.GONE);
            vLine2.setVisibility(View.GONE);
        }
        //安全问题找回
        tvSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent securityIntent = new Intent(PayVerifyPWDActivity.this, VerifySafeQuestionActivity.class);
                securityIntent.putExtra(VerifySafeQuestionActivity.FROM_EXTRA,VerifySafeQuestionActivity.MODIFY);
                startActivity(securityIntent);
                dialog.dismiss();
            }
        });
        //实名找回
        tvAuthenticaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PayVerifyPWDActivity.this, FindPassByAuthActivity.class));
                dialog.dismiss();
            }
        });
        //申诉找回
        dialog.findViewById(R.id.tv_complain_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (10 != UserCenter.getAppealStatus(PayVerifyPWDActivity.this)) {
                    startActivity(new Intent(PayVerifyPWDActivity.this, ComplainMessageFirstActivity.class));
                } else {
                    ToastUtil.longShow(getApplicationContext(),"您的账号正在申诉中");
                }
            }
        });
        //取消
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.onWindowAttributesChanged(wl);
        dialog.setCancelable(false);
        dialog.show();
    }
}
