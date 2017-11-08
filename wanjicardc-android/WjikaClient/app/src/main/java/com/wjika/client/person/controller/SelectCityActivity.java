package com.wjika.client.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.CityEntity;
import com.wjika.client.network.entities.UserEntity;
import com.wjika.client.person.adapter.CityListAdapter;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by Liu_ZhiChao on 2015/9/26 12:37.
 * 选择城市
 */
public class SelectCityActivity extends ToolBarActivity implements AdapterView.OnItemClickListener {

	private static final String GET_PROVINCE_CODE = "none";

	@ViewInject(R.id.city_current_city)
	private TextView cityCurrentCity;
	@ViewInject(R.id.city_name_list)
	private ListView cityNameList;

	private List<CityEntity> cityList;
	private String currentId = GET_PROVINCE_CODE;
	private CityListAdapter cityAdapter;
	private String province;
	private String city;
	private UserEntity userInfo;
	private String theid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_city);
		ViewInjectUtils.inject(this);
		cityNameList.setOnItemClickListener(this);
		setLeftTitle(res.getString(R.string.person_info_city_title));
		Intent intent = getIntent();
		userInfo = intent.getParcelableExtra("userinfo");
		loadData();
	}

	private void loadData() {
		String city = getIntent().getStringExtra(PersonInfoActivity.SELECT_CITY);
		if (!TextUtils.isEmpty(city)){
			cityCurrentCity.setText(String.format(res.getString(R.string.person_info_city_current), city));
		}else {
			cityCurrentCity.setText(String.format(res.getString(R.string.person_info_city_current), ""));
		}
		cityList = getCityList(GET_PROVINCE_CODE);
		cityAdapter = new CityListAdapter(this, cityList);
		cityNameList.setAdapter(cityAdapter);
	}

	private List<CityEntity> getCityList(String id){
		if (GET_PROVINCE_CODE.equals(id)){
			return CityDBManager.getAllProvince(this);
		}else {
			return CityDBManager.getCityByProvinceId(this, id);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (GET_PROVINCE_CODE.equals(currentId)){
			//省
			CityEntity cityEntity = cityList.get(position);
			currentId = cityEntity.getId();
			province = cityEntity.getName();
			cityCurrentCity.setText(String.format(res.getString(R.string.person_info_city_current), province));
			cityList = getCityList(currentId);
			cityAdapter.clear();
			cityAdapter.addDatas(cityList);
			cityNameList.smoothScrollToPosition(0);
		}else {
			//市
			city = cityList.get(position).getName();
			theid = cityList.get(position).getId();
			cityCurrentCity.setText(String.format(res.getString(R.string.person_info_city_current), province + city));
			showProgressDialog();
			IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
			/*identityHashMap.put("province", province);
			identityHashMap.put("city", city);*/
			identityHashMap.put("userSex", "" + userInfo.getGender());
			identityHashMap.put("userRealname", userInfo.getUserRealName());
			identityHashMap.put("userBirthday", userInfo.getBirthDay());
//			identityHashMap.put("address", province + city);
			identityHashMap.put("address", theid);
			identityHashMap.put("token", UserCenter.getToken(this));
			requestHttpData(Constants.Urls.URL_POST_UPDATE_INFO, 100, FProtocol.HttpMethod.POST, identityHashMap);
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
		Intent intent = new Intent();
		intent.putExtra("addressid", theid);
		intent.putExtra("address",province + city);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
