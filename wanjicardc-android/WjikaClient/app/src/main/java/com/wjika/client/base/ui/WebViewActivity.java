package com.wjika.client.base.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utils.AnalysisUtil;
import com.common.utils.StringUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.main.controller.MainActivity;
import com.wjika.client.main.controller.OneBrandListActivity;
import com.wjika.client.network.Constants;
import com.wjika.client.pay.controller.BaoziRechargeActivity;
import com.wjika.client.person.controller.AuthenticationActivity;
import com.wjika.client.store.controller.CardDetailActivity;
import com.wjika.client.store.controller.StoreDetailActivity;
import com.wjika.client.utils.CommonShareUtil;
import com.wjika.client.utils.LocationUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 公用WebView加载页
 */
public class WebViewActivity extends ToolBarActivity {

	public static final String EXTRA_FROM = "extra_from";
	public static final String EXTRA_URL = "URL";
	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_MERID = "extra_merid";
	public static final int FROM_PUSH = 1;
	public static final int FROM_CARD_PKG_DETAIL = 2;
	public static final int FROM_PRIVILEGE_EXPLAIN = 3;
	private ProgressBar mHorizontalProgress;
	private WebView mWebView;
	private ImageView backBtn;
	private boolean mIsImmediateBack = false;
	private boolean mIsLeftBtnDisplay = true;
	private boolean mIsRightBtnDisplay = true;
	private int from;
	private String url;
	private String title;
	private String merId;
	private Handler refreshProgressHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.arg1 >= 100) {
				if (mHorizontalProgress != null) {
					mHorizontalProgress.setVisibility(View.GONE);
				}
			} else {
				if (mHorizontalProgress != null && msg.arg1 >= 0) {
					mHorizontalProgress.setVisibility(View.VISIBLE);
					mHorizontalProgress.setProgress(msg.arg1);
				}
			}
		}
	};

	@SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.webview_act);

		url = getIntent().getStringExtra(EXTRA_URL);
		//url = "file:///android_asset/test.html";
		title = getIntent().getStringExtra(EXTRA_TITLE);
		from = getIntent().getIntExtra(EXTRA_FROM, 0);
		if (!TextUtils.isEmpty(title)) {
			setLeftTitle(title);
		}
		if (FROM_CARD_PKG_DETAIL == from) {
			merId = getIntent().getStringExtra(EXTRA_MERID);
			rightText.setText("特权说明");
			rightText.setVisibility(View.VISIBLE);
			rightText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent privilegeIntent = new Intent(WebViewActivity.this, WebViewActivity.class);
					privilegeIntent.putExtra(EXTRA_FROM, FROM_PRIVILEGE_EXPLAIN);
					privilegeIntent.putExtra(WebViewActivity.EXTRA_URL, Constants.Urls.URL_PRIVILEGE_DOMAIN + "?privilegeType=merchant&merchantId="
							+ merId + "&token=" + UserCenter.getToken(WebViewActivity.this));
					privilegeIntent.putExtra(WebViewActivity.EXTRA_TITLE, "特权说明");
					startActivity(privilegeIntent);
				}
			});
		}

		mHorizontalProgress = (ProgressBar) findViewById(R.id.progress_horizontal);
		mWebView = (WebView) findViewById(R.id.webview);
		// 设置支持JavaScript
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new WjikaJavaScript(), "wjika");
		webSettings.setSupportZoom(true);
//		webSettings.setDatabaseEnabled(true);
//		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setGeolocationEnabled(true);
//		webSettings.setGeolocationDatabasePath(dir);
		webSettings.setDomStorageEnabled(true);
		webSettings.setUseWideViewPort(true);//图片调整到适合WebView的大小
		webSettings.setLoadWithOverviewMode(true);//缩放至屏幕大小

		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		if (Build.VERSION.SDK_INT > 11) {
			mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains("wjika.com")) {
					mWebView.loadUrl(url);
					return true;
				}
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onFormResubmission(WebView view, Message dontResend, Message resend) {
				resend.sendToTarget();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(view.getContext(), "网络异常！", Toast.LENGTH_SHORT).show();
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (refreshProgressHandler != null) {
					if (refreshProgressHandler.hasMessages(0)) {
						refreshProgressHandler.removeMessages(0);
					}
					Message mMessage = refreshProgressHandler.obtainMessage(0, newProgress, 0, null);
					refreshProgressHandler.sendMessageDelayed(mMessage, 100);
				}
			}

			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}
		});

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		mWebView.loadUrl(url);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (FROM_CARD_PKG_DETAIL == from || FROM_PRIVILEGE_EXPLAIN == from) {
			AnalysisUtil.onEvent(this, "Android_act_PrivilegeExplain");
		}
		mWebView.onResume();
		if (backBtn != null) {
			if (mIsLeftBtnDisplay) {
				backBtn.setVisibility(View.VISIBLE);
			} else {
				backBtn.setVisibility(View.GONE);
			}

			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(backBtn.getApplicationWindowToken(), 0);

					if (mIsImmediateBack) {
						onBackPressed();
					} else {
						if (mWebView.canGoBack()) {
							mWebView.goBack();
						} else {
							onBackPressed();
						}
					}
				}
			});
		}

		final TextView closeBtn = null;
		if (closeBtn != null) {
			if (mIsRightBtnDisplay) {
				closeBtn.setVisibility(View.VISIBLE);
			} else {
				closeBtn.setVisibility(View.GONE);
			}
			closeBtn.setText("关闭");
			closeBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(closeBtn.getApplicationWindowToken(), 0);
					onBackPressed();
				}
			});
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				onBackPressed();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void finish() {
		switch (from) {
			case FROM_PUSH:
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				break;
		}
		super.finish();
	}

	@Override
	public void onDestroy() {
		mWebView.destroy();
		super.onDestroy();
	}

	public class WjikaJavaScript {

		@JavascriptInterface
		public void open(String type, String params) {
			if (StringUtil.isEmpty(params)) {
				return;
			}
			switch (type) {
				case "merchant"://跳转到商家详情页
				{
					String storeId = null;
					try {
						storeId = new JSONObject(params).optString("merId");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(WebViewActivity.this, StoreDetailActivity.class);
					intent.putExtra(StoreDetailActivity.EXTRA_STORE_ID, storeId);
					intent.putExtra(StoreDetailActivity.EXTRA_FROM, StoreDetailActivity.FROM_STORE_LIST_ACTIVITY);
					startActivity(intent);
					break;
				}
				case "product"://跳转到商品详情页
				{
					String cardId = null;
					String title = null;
					try {
						JSONObject jsonObject = new JSONObject(params);
						cardId = jsonObject.optString("cardId");
						title = jsonObject.optString("title");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(WebViewActivity.this, CardDetailActivity.class);
					intent.putExtra(CardDetailActivity.EXTRA_CARD_ID, cardId);
					intent.putExtra(CardDetailActivity.EXTRA_CARD_NAME, title);
					intent.putExtra(CardDetailActivity.EXTRA_FROM, CardDetailActivity.FROM_ACTION_WEBVIEW);//须展示购买
					intent.putExtra(CardDetailActivity.EXTRA_IS_MYCARD, false);
					startActivity(intent);
					break;
				}
				case "brand": {
					String brandId = null;
					String brandName = null;
					try {
						JSONObject jsonObject = new JSONObject(params);
						brandId = jsonObject.optString("brandId");
						brandName = jsonObject.optString("brandName");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(WebViewActivity.this, OneBrandListActivity.class);
					intent.putExtra(OneBrandListActivity.BRAND_NAME, brandName);
					intent.putExtra(OneBrandListActivity.MERCHANT_ACCOUNT_ID, brandId);
					intent.putExtra("is_location", !Double.toString(Double.MIN_VALUE).equals(LocationUtils.getLocationLatitude(WebViewActivity.this)));
					startActivity(intent);
					break;
				}
				case "share": {
					if (UserCenter.isLogin(WebViewActivity.this)) {
						if (UserCenter.isAuthencaiton(WebViewActivity.this)) {
							try {
								JSONObject jsonObject = new JSONObject(params);
								final String desc = jsonObject.optString("desc");
								final String title = jsonObject.optString("title");
								final String logo = jsonObject.optString("logo");
								final String url = jsonObject.optString("url");
								WebViewActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										CommonShareUtil.share(WebViewActivity.this, desc, title, logo, url);
									}
								});
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							startActivity(new Intent(WebViewActivity.this, AuthenticationActivity.class));
						}
					}
					break;
				}
				case "rechargeBun": {
					if (UserCenter.isLogin(WebViewActivity.this)) {
						startActivity(new Intent(WebViewActivity.this, BaoziRechargeActivity.class));
					} else {
						startActivity(new Intent(WebViewActivity.this, LoginActivity.class));
					}
					break;
				}
			}
		}
	}
}
