package com.wjika.client.market.controller;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.EnCryptionUtils;
import com.common.utils.NetWorkUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.market.adapter.MyElectCardItemAdapter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.MyElectCardEntity;
import com.wjika.client.network.entities.MyElectCardItemEntity;
import com.wjika.client.network.entities.MyElectResultEntity;
import com.wjika.client.network.entities.PayVerifyPwdEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.person.controller.ComplainMessageFirstActivity;
import com.wjika.client.person.controller.FindPassByAuthActivity;
import com.wjika.client.person.controller.VerifySafeQuestionActivity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.InputMethodUtil;
import com.wjika.client.utils.ViewInjectUtils;
import com.wjika.client.widget.PasswordInputView;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZHXIA on 2016/8/17
 * 我的电子卡
 */
public class MyElectCardActivity extends ToolBarActivity implements View.OnClickListener {

	private static final int REQUEST_GET_MY_ELECT_CARD_CODE_MORE = 0x1;
	private static final int REQUEST_GET_MY_ELECT_CARD_CODE = 0x2;
	private static final int REQUEST_VERIFY_PAY_PWD_CODE = 0x3;
	private static final int REQUEST_PAY_PWD_SALT_CODE = 0x6;

	@ViewInject(R.id.mycard_listview)
	private FootLoadingListView mycardListview;
	private PasswordInputView dialogPayPwdInput;

	private MyElectCardItemAdapter electCardItemAdapter;
	private MyElectCardItemEntity cardOrderItemEntity;
	private String paypwd;
	private String paypwdSalt;
	private AlertDialog mAlertDialog;
	private String preDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_my_elect_card);
		ViewInjectUtils.inject(this);
		initView();
		initLoadingView(this);
		setLoadingStatus(LoadingStatus.LOADING);
		loadData(false);
	}

	private void initView() {
		setLeftTitle("我的电子卡");
		mycardListview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		mycardListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(true);
			}
		});
	}

	private void loadData(boolean loadMore) {
		if (loadMore) {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("pageNum", electCardItemAdapter.getPage() + 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			requestHttpData(Constants.Urls.URL_POST_MY_ELECT_CARD_LIST,
					REQUEST_GET_MY_ELECT_CARD_CODE_MORE,
					FProtocol.HttpMethod.POST,
					params);
		} else {
			IdentityHashMap<String, String> params = new IdentityHashMap<>();
			params.put(Constants.TOKEN, UserCenter.getToken(this));
			params.put("pageNum", 1 + "");
			params.put("pageSize", Constants.PAGE_SIZE + "");
			requestHttpData(Constants.Urls.URL_POST_MY_ELECT_CARD_LIST,
					REQUEST_GET_MY_ELECT_CARD_CODE,
					FProtocol.HttpMethod.POST,
					params);
		}
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		if (REQUEST_GET_MY_ELECT_CARD_CODE == requestCode) {
			mycardListview.onRefreshComplete();
			if (data != null) {
				MyElectCardEntity myElectCardLists = Parsers.getMyElectCardList(data);//我的电子卡
				if (myElectCardLists != null) {
					List<MyElectResultEntity> myCardResultList = myElectCardLists.getMyCardResultList();//电子卡日期分类
					if (myCardResultList != null && myCardResultList.size() > 0) {
						setLoadingStatus(LoadingStatus.GONE);
						List<MyElectCardItemEntity> mAllElectCardsList1 = new ArrayList<>();
						for (int i = 0; i < myCardResultList.size(); i++) {
							MyElectResultEntity myElectResultEntity = myCardResultList.get(i);//单个日期电子卡
							if (myElectResultEntity.getMyElectCardItemEntity() != null && myElectResultEntity.getMyElectCardItemEntity().size() > 0) {
								MyElectCardItemEntity entity = myElectResultEntity.getMyElectCardItemEntity().get(0);//拿到每个日期下的第一条电子卡数据
								preDate = myElectResultEntity.getOrderCardItemDate();//第一页最后一个日期
								entity.setDate(myElectResultEntity.getOrderCardItemDate());//为每天第一条数据设置日期
								mAllElectCardsList1.addAll(myElectResultEntity.getMyElectCardItemEntity());//最终显示的数据集合
							}
						}
						electCardItemAdapter = new MyElectCardItemAdapter(this, mAllElectCardsList1, this);
						mycardListview.setAdapter(electCardItemAdapter);
						if (myElectCardLists.getPages() > 1) {
							mycardListview.setCanMoreAndUnReFresh(true);
						} else {
							mycardListview.setCanMoreAndUnReFresh(false);
						}
					} else {
						setLoadingStatus(LoadingStatus.EMPTY);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			} else {
				setLoadingStatus(LoadingStatus.EMPTY);
			}
		} else if (REQUEST_GET_MY_ELECT_CARD_CODE_MORE == requestCode) {
			mycardListview.onRefreshComplete();
			if (data != null) {
				MyElectCardEntity myElectCardLists = Parsers.getMyElectCardList(data);
				if (myElectCardLists != null && myElectCardLists.getMyCardResultList() != null && myElectCardLists.getMyCardResultList().size() > 0) {
					List<MyElectCardItemEntity> mAllElectCardsList2 = new ArrayList<>();
					for (int i = 0; i < myElectCardLists.getMyCardResultList().size(); i++) {
						if (myElectCardLists.getMyCardResultList().get(i).getMyElectCardItemEntity() != null
								&& myElectCardLists.getMyCardResultList().get(i).getMyElectCardItemEntity().size() > 0) {
							String tempDate = myElectCardLists.getMyCardResultList().get(i).getOrderCardItemDate();
							if (preDate != null && !preDate.equals(tempDate)) {
								MyElectCardItemEntity entity = myElectCardLists.getMyCardResultList().get(i).getMyElectCardItemEntity().get(0);
								entity.setDate(tempDate);
								preDate = tempDate;
							}
						}
						mAllElectCardsList2.addAll(myElectCardLists.getMyCardResultList().get(i).getMyElectCardItemEntity());
					}
					setLoadingStatus(LoadingStatus.GONE);
					electCardItemAdapter.addDatas(mAllElectCardsList2);//page++
					electCardItemAdapter.notifyDataSetChanged();
					if (myElectCardLists.getPages() > myElectCardLists.getPageNum()) {
						mycardListview.setCanMoreAndUnReFresh(true);
					} else {
						mycardListview.setCanMoreAndUnReFresh(false);
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			} else {
				setLoadingStatus(LoadingStatus.EMPTY);
			}
		} else if (REQUEST_VERIFY_PAY_PWD_CODE == requestCode) {
			if (data != null) {
				PayVerifyPwdEntity entity = Parsers.getPayVerifyPwdEntity(data);
				if (entity != null) {
					if (RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())) {
						UserCenter.setUserPaypwd(this, paypwd);
						UserCenter.setToken(this, entity.getToken());
						Intent intent = new Intent(this, ExtractElectCardDetailActivity.class);
						intent.putExtra("cardOrderItemEntity", cardOrderItemEntity);
						startActivity(intent);
						dialogPayPwdInput.setText("");
						if (mAlertDialog != null) {
							mAlertDialog.dismiss();
						}
					} else {
						dialogPayPwdInput.setText("");
						if (entity.isLockedStatus()) {
							showVerifyFailedDialog(getString(R.string.title_account_locked), entity.getResultMsg(), R.string.pay_verify_pay_back_password);
						} else {
							showVerifyFailedDialog(getString(R.string.verify_safe_validation_fails), entity.getResultMsg(), R.string.person_pay_setting_confirm_pwd);
						}
					}
				} else {
					setLoadingStatus(LoadingStatus.EMPTY);
				}
			} else {
				setLoadingStatus(LoadingStatus.EMPTY);
			}
		} else if (REQUEST_PAY_PWD_SALT_CODE == requestCode)
			if (data != null) {
				paypwdSalt = Parsers.getPaypwdSalt(data);
				if (!StringUtil.isEmpty(paypwdSalt)) {
					UserCenter.setUserPaypwdSalt(this, paypwdSalt);
				}
			}
	}

	private void showVerifyFailedDialog(String title, String msg, int okTextResId) {
		closeProgressDialog();
		dialogPayPwdInput.setText("");
		showAlertDialog(title,
				msg,
				res.getString(R.string.verify_safe_cancel),
				res.getString(okTextResId),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
						if (mAlertDialog != null) {
							mAlertDialog.dismiss();
						}
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView tv = (TextView) v;
						if (tv.getText().equals(res.getString(R.string.pay_verify_pay_back_password))) {
							showMenu(UserCenter.isSetSecurity(getApplicationContext()), UserCenter.isAuthencaiton(getApplicationContext()));
						}
						closeDialog();
					}
				}
		);
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		mycardListview.onRefreshComplete();
		if (REQUEST_GET_MY_ELECT_CARD_CODE == requestCode) {
			setLoadingStatus(LoadingStatus.RETRY);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.loading_layout:
				setLoadingStatus(LoadingStatus.LOADING);
				loadData(false);
				break;
			case R.id.bt_mycard_extract:
				cardOrderItemEntity = (MyElectCardItemEntity) v.getTag();
				showVerifyFailedDialog("请输入支付密码");
				break;
		}
	}

	private void showVerifyFailedDialog(String title) {
		closeProgressDialog();
		View view = LayoutInflater.from(this).inflate(R.layout.wjk_alert_dialog, null);
		mAlertDialog = new AlertDialog.Builder(this).setView(view).create();
		mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		TextView txtTitle = (TextView) view.findViewById(R.id.alert_title);
		TextView txtTitle1 = (TextView) view.findViewById(R.id.alert_title1);
		TextView txtMessage = (TextView) view.findViewById(R.id.alert_message);
		ImageView ivCloseDialog = (ImageView) view.findViewById(R.id.alert_ic_close);
		View lineTop = view.findViewById(R.id.line_top);
		View lineBottom = view.findViewById(R.id.line_bottom);
		TextView btnTwo = (TextView) view.findViewById(R.id.alert_btn_two);
		dialogPayPwdInput = (PasswordInputView) view.findViewById(R.id.dialog_pay_pwd_input);
		dialogPayPwdInput.setText("");
		dialogPayPwdInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				paypwd = dialogPayPwdInput.getText().toString();
				if (6 == paypwd.length()) {
					if (NetWorkUtil.isConnect(MyElectCardActivity.this)) {
						IdentityHashMap<String, String> params = new IdentityHashMap<>();
						paypwdSalt = UserCenter.getUserPaypwdSalt(MyElectCardActivity.this);
						paypwd = EnCryptionUtils.SHA1(paypwd, paypwdSalt);
						params.put("payPassword", paypwd);
						params.put("token", UserCenter.getToken(MyElectCardActivity.this));
						params.put("userPushID", JPushInterface.getRegistrationID(getApplicationContext()));
						params.put("identity", UserCenter.getUserIdentity(getApplicationContext()));
						requestHttpData(Constants.Urls.URL_VERIFY_PAY_PWD, REQUEST_VERIFY_PAY_PWD_CODE, FProtocol.HttpMethod.POST, params);
					} else {
						ToastUtil.shortShow(MyElectCardActivity.this, res.getString(R.string.pay_verify_check_network));
						if (mAlertDialog != null) {
							mAlertDialog.dismiss();
						}
					}
				}

			}
		});
		View llBtn = view.findViewById(R.id.alert_btn_ll);
		if (StringUtil.isEmpty(title)) {
			txtTitle.setVisibility(View.GONE);
		} else {
			txtTitle.setVisibility(View.GONE);
			txtTitle1.setVisibility(View.VISIBLE);
			ivCloseDialog.setVisibility(View.VISIBLE);
			lineTop.setVisibility(View.VISIBLE);
			lineBottom.setVisibility(View.GONE);
			dialogPayPwdInput.setVisibility(View.VISIBLE);
			txtTitle1.setText(title);
		}
		InputMethodUtil.showInput(this, dialogPayPwdInput);
		llBtn.setVisibility(View.GONE);
		txtMessage.setVisibility(View.GONE);
		btnTwo.setVisibility(View.VISIBLE);
		btnTwo.setText("忘记密码？");
		ivCloseDialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
			}
		});
		btnTwo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tv = (TextView) v;
				if (tv.getText().equals("忘记密码？")) {
					showMenu(UserCenter.isSetSecurity(getApplicationContext()), UserCenter.isAuthencaiton(MyElectCardActivity.this));
				}
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
			}
		});
		mAlertDialog.show();
		WindowManager.LayoutParams params = mAlertDialog.getWindow().getAttributes();
		params.width = CommonTools.dp2px(this, 270f);
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		mAlertDialog.getWindow().setAttributes(params);
	}

	private void showMenu(boolean isShowSecurity, boolean isAuthencation) {
		final Dialog dialog = new Dialog(MyElectCardActivity.this, R.style.ActionSheetDialogStyle);
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
		if (isShowSecurity) {
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
				Intent securityIntent = new Intent(MyElectCardActivity.this, VerifySafeQuestionActivity.class);
				securityIntent.putExtra(VerifySafeQuestionActivity.FROM_EXTRA, VerifySafeQuestionActivity.MODIFY);
				startActivity(securityIntent);
				dialog.dismiss();
			}
		});
		//实名找回
		tvAuthenticaion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MyElectCardActivity.this, FindPassByAuthActivity.class));
				dialog.dismiss();
			}
		});
		//申诉找回
		dialog.findViewById(R.id.tv_complain_find).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (10 != UserCenter.getAppealStatus(MyElectCardActivity.this)) {
					startActivity(new Intent(MyElectCardActivity.this, ComplainMessageFirstActivity.class));
				} else {
					ToastUtil.longShow(getApplicationContext(), "您的账号正在申诉中");
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

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		ExitManager.instance.toActivity(MainActivity.class);
	}
}
