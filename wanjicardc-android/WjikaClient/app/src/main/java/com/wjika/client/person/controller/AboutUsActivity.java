package com.wjika.client.person.controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.base.ui.WebViewActivity;
import com.wjika.client.login.controller.LoginActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.entities.Entity;
import com.wjika.client.network.entities.UpdateVersionEntity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.update.UpdateActiviy;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * Created by Liu_ZhiChao on 2015/9/16 11:18.
 * 关于我们
 */
public class AboutUsActivity extends ToolBarActivity implements View.OnClickListener{

	@ViewInject(R.id.person_about_update)
	private TextView personAboutUpdate;
	@ViewInject(R.id.person_about_service_tel)
	private TextView personAboutServiceTel;
	@ViewInject(R.id.person_about_license)
	private TextView personAboutLicense;
	@ViewInject(R.id.person_about_help)
	private TextView personAboutHelp;
	@ViewInject(R.id.person_about_version)
	private TextView personAboutVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_about);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(getString(R.string.person_about_us_title));
		personAboutUpdate.setOnClickListener(this);
		personAboutServiceTel.setOnClickListener(this);
		personAboutLicense.setOnClickListener(this);
		personAboutHelp.setOnClickListener(this);
		try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			personAboutVersion.setText(getString(R.string.app_name) + " V" + versionName);
//			personAboutDesc.setText(res.getString(R.string.person_about_us_copyright));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.person_about_update:
				checkUpdateVersion();
				break;
			case R.id.person_about_service_tel:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(res.getString(R.string.person_about_us_line))));
				break;
			case R.id.person_about_license:
				Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, getString(R.string.person_about_us_agreement_url));
				intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.person_about_us_agreement));
				startActivity(intent);
				break;
			case R.id.person_about_help:
				Intent helpIntent = new Intent(this, WebViewActivity.class);
				helpIntent.putExtra(WebViewActivity.EXTRA_URL, res.getString(R.string.person_setting_help_url));
				helpIntent.putExtra(WebViewActivity.EXTRA_TITLE, res.getString(R.string.person_setting_help));
				startActivity(helpIntent);
				break;
			default:
				break;
		}
	}

	@Override
	public void success(int requestCode, String data) {
        closeProgressDialog();
		Entity entity = Parsers.getResponseSatus(data);
		if (RESPONSE_NO_LOGIN_CODE.equals(entity.getResultCode())){
			//未登录或token失效，清空数据，打开登录页面
			UserCenter.cleanLoginInfo(this);
			Intent intent = new Intent(this, LoginActivity.class);
//			ExitManager.instance.exit();
			startActivity(intent);
		}else if (!RESPONSE_SUCCESS_CODE.equals(entity.getResultCode())){
			//返回数据不正确
			ToastUtil.shortShow(this, entity.getResultMsg());
		}else if(requestCode == REQUEST_UPDATE_VERSION_CODE){
			UpdateVersionEntity updateVersionEntity = Parsers.getUpdateVersionInfo(data);
			if (updateVersionEntity != null){
				if (updateVersionEntity.getType() != 0){
					String url = updateVersionEntity.getDownloadUrl() == null ? "" : updateVersionEntity.getDownloadUrl();
					String version = updateVersionEntity.getVersion() == null ? "" : updateVersionEntity.getVersion();
					startActivity(new Intent().setClass(this, UpdateActiviy.class)
							.putExtra(UpdateActiviy.KEY_UPDATE_TYPE, updateVersionEntity.getType())
							.putExtra(UpdateActiviy.KEY_UPDATE_URL, url)
							.putExtra(UpdateActiviy.KEY_UPDATE_VERSION_NAME, version)
							.putExtra(UpdateActiviy.KEY_UPDATE_VERSION_DESC, updateVersionEntity.getDesc())
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}else {
					ToastUtil.shortShow(this,getString(R.string.person_about_us_update));
				}
			}
		}else {
			parseData(requestCode, data);
		}
	}
}
