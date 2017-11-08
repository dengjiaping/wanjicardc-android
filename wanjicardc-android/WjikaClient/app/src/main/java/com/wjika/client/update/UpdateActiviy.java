package com.wjika.client.update;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utils.AnalysisUtil;
import com.common.utils.NetWorkUtil;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ConfigUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.Date;

/**
 * 版本更新升级
 */
public class UpdateActiviy extends Activity implements View.OnClickListener {

	public final static String KEY_UPDATE_TYPE = "force";
	public final static String KEY_UPDATE_URL = "url";
	public final static String KEY_UPDATE_VERSION_NAME = "version_name";
	public final static String KEY_UPDATE_VERSION_DESC = "version_desc";

	@ViewInject(R.id.txt_update_version)
	private TextView mTxtUpdateVersion;
	@ViewInject(R.id.btn_ignore)
	private TextView mBtnIgnore;
	@ViewInject(R.id.btn_update)
	private TextView mBtnUpdate;
	@ViewInject(R.id.btn_force_update)
	private TextView mBtnForceUpdate;

	private int type;
	private String ApkUrl;
	private String VersionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_update_act);
		this.getWindow().addFlags(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		ViewInjectUtils.inject(this);

		Intent startIntent = getIntent();
		if (startIntent != null) {
			type = startIntent.getIntExtra(KEY_UPDATE_TYPE, 0);
			ApkUrl = startIntent.getStringExtra(KEY_UPDATE_URL);
			VersionName = startIntent.getStringExtra(KEY_UPDATE_VERSION_NAME);
		}
		setFinishOnTouchOutside(type != 2);
		mTxtUpdateVersion.setText("V" + VersionName);
		if (type == 2) {//2为强制升级
			mBtnIgnore.setVisibility(View.GONE);
			mBtnUpdate.setVisibility(View.GONE);
			mBtnForceUpdate.setVisibility(View.VISIBLE);
			ConfigUtils.setIgnoreDate(UpdateActiviy.this, 0);
		} else {
			mBtnIgnore.setVisibility(View.VISIBLE);
			mBtnUpdate.setVisibility(View.VISIBLE);
			mBtnForceUpdate.setVisibility(View.GONE);
		}

		mBtnIgnore.setOnClickListener(this);
		mBtnUpdate.setOnClickListener(this);
		mBtnForceUpdate.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onPageStart(getClass().getSimpleName());
		AnalysisUtil.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AnalysisUtil.onPageEnd(getClass().getSimpleName());
		AnalysisUtil.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return type == 2 && KeyEvent.KEYCODE_BACK == keyCode || super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_ignore:
				ConfigUtils.setIgnoreDate(UpdateActiviy.this, new Date().getTime());
				finish();
				break;
			case R.id.btn_update:
			case R.id.btn_force_update:
				if (StringUtil.isEmpty(ApkUrl)) return;
				if (!NetWorkUtil.isConnect(UpdateActiviy.this)) {
					Toast.makeText(UpdateActiviy.this, R.string.no_available_net, Toast.LENGTH_LONG).show();
					return;
				}

				String packageName = "com.android.providers.downloads";
				int state = UpdateActiviy.this.getPackageManager().getApplicationEnabledSetting(packageName);
				if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
						state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER ||
						state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
					//不能使用系统下载管理器
					startActivity(CommonTools.getIntent(UpdateActiviy.this));
					 /*try {
					  //Open the specific App Info page:
					  Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					  i.setData(Uri.parse("package:" + packageName));
					  startActivity(i);
					 } catch ( ActivityNotFoundException e ) {
					  //Open the generic Apps page:
					  Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
					  startActivity(i);
					 }*/
				} else {
					mBtnUpdate.setEnabled(false);
					startService(new Intent().setClass(getApplicationContext(), UpdateService.class).putExtra(KEY_UPDATE_URL, ApkUrl));
				}
				break;
		}
	}
}
