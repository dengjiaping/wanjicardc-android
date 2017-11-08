package com.wjika.client.person.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.ShareEntivity;
import com.wjika.client.network.parser.Parsers;
import com.wjika.client.utils.CommonShareUtil;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/7 19:55.
 * 邀请好友
 */
public class InviteFriendActivity extends ToolBarActivity implements View.OnClickListener{

	private static final int REQUEST_PERSON_INVITE_CODE = 100;

	@ViewInject(R.id.person_invite_img)
	private ImageView personInviteImg;
	@ViewInject(R.id.person_invite_friend)
	private Button personInviteFriend;
	@ViewInject(R.id.person_invite_share)
	private Button personInviteShare;

	private ShareEntivity shareEntivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_invite);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void initView() {
		setLeftTitle(getString(R.string.person_invite_friend));
		personInviteFriend.setOnClickListener(this);
		personInviteShare.setOnClickListener(this);
	}

	private void loadData() {
		showProgressDialog();
//		requestHttpData(String.format(Constants.Urls.URL_GET_INVITE_INFO, UserCenter.getToken(this)), REQUEST_PERSON_INVITE_CODE);
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put(Constants.TOKEN, UserCenter.getToken(this));
		requestHttpData(Constants.Urls.URL_GET_INVITE_INFO, REQUEST_PERSON_INVITE_CODE, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
	}

	@Override
	protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
		switch (requestCode){
			case REQUEST_PERSON_INVITE_CODE:
				List<ShareEntivity> shareEntivityList = Parsers.getShareList(data);
				if (shareEntivityList != null && shareEntivityList.size() > 0){
					shareEntivity = shareEntivityList.get(0);
					String url = shareEntivity.getImgUrl();
					if (!TextUtils.isEmpty(url) && !url.contains("?")) {
						ImageUtils.setSmallImg(personInviteImg,url);
					}
				}
				break;
		}
	}

	@Override
	public void onClick(View v) {
		if (shareEntivity != null){
			switch (v.getId()){
				case R.id.person_invite_friend:
//						Uri uri = Uri.parse("smsto:");
//						Intent it = new Intent(Intent.ACTION_SENDTO, uri);
//						it.putExtra("sms_body", shareEntivity.getContent());
//						startActivity(it);
//					break;
				case R.id.person_invite_share:
					CommonShareUtil.share(this, shareEntivity.getShareContent(), shareEntivity.getTitle(), shareEntivity.getShareImgUrl(), shareEntivity.getUrl());
					break;
			}
		}
	}
}
