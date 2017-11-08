package com.wjika.client.person.controller;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.network.HttpUtil;
import com.common.utils.BitmapUtil;
import com.common.utils.FileUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.db.CityDBManager;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.network.entities.UserEntity;
import com.wjika.client.person.adapter.SelectPhotoAdapter;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.TimeUtil;
import com.wjika.client.utils.ViewInjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.IdentityHashMap;

/**
 * Created by Liu_ZhiChao on 2015/8/31 11:07.
 * 用户信息
 */
public class PersonInfoActivity extends ToolBarActivity implements View.OnClickListener {

	public static final int AUTHENTICATION_STATUS_UNAUTHERIZED = 0;
	public static final int AUTHENTICATION_STATUS_VERIFYING = 1;
	public static final int AUTHENTICATION_STATUS_VERIFIED = 2;
	private static final int REQUEST_CAMERA_CODE = 100;//拍照
	private static final int REQUEST_PHOTO_CODE = 200;//相册
	private static final int REQUEST_ALTER_AVATAR_CODE = 300;//上传头像
	private static final int REQUEST_ALTER_GENDER = 500;//修改性别
	private static final int REQUEST_ALTER_BIRTHDAY = 600;//修改生日
	private static final int REQUEST_SELECT_CITY_CODE = 700;//选择城市界面
	private static final int REQUEST_AUTH_SECESS = 800;//实名认证成功
	public static final String SELECT_CITY = "city_name";
	public static final String IMAGE_CACHE_DIR = "image";

	@ViewInject(R.id.person_info_head)
	private LinearLayout personInfoHead;
	@ViewInject(R.id.person_info_avatar)
	private ImageView personInfoAvatar;
	@ViewInject(R.id.person_info_gender_ll)
	private LinearLayout personInfoGenderLL;
	@ViewInject(R.id.person_info_gender)
	private TextView personInfoGender;
	@ViewInject(R.id.person_info_birthday_ll)
	private LinearLayout personInfoBirthdayLL;
	@ViewInject(R.id.person_info_birthday)
	private TextView personInfoBirthday;
	@ViewInject(R.id.person_info_location_ll)
	private LinearLayout personInfoLocationLL;
	@ViewInject(R.id.person_info_location)
	private TextView personInfoLocation;
	@ViewInject(R.id.person_info_authentication)
	private LinearLayout personInfoAuthentication;
	@ViewInject(R.id.person_info_authentication_status)
	private TextView personInfoAuthenticationStatus;

	private String path;
	private int gender;
	private UserEntity userInfo;
	private String birthday;
	private String newBirthday;
//	private String[] tempBirthday = null;
	private String year1;
	private String month1;
	private String day1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_act_info);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}


	private void initView() {
		setLeftTitle(res.getString(R.string.person_info_title));
		Intent intent = getIntent();
		userInfo = intent.getParcelableExtra(PersonalFragment.USER_INFO);
		if (userInfo != null) {
			personInfoHead.setOnClickListener(this);
			personInfoGenderLL.setOnClickListener(this);
			personInfoBirthdayLL.setOnClickListener(this);
			personInfoLocationLL.setOnClickListener(this);
			personInfoAuthentication.setOnClickListener(this);
			if (2 == userInfo.getAuthentication()) {
				personInfoAuthenticationStatus.setText(getString(R.string.person_auth_authenticated));
			} else if (1 == userInfo.getAuthentication()) {
				personInfoAuthenticationStatus.setText(getString(R.string.person_auth_verifying));
			} else {
				personInfoAuthenticationStatus.setText(getString(R.string.person_auth_unautherized));
			}
		}
	}

	@Override
	public void setLeftTitle(String title) {
		super.setLeftTitle(title);
		mBtnTitleLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK, getIntent().putExtra(PersonalFragment.USER_INFO, userInfo));
				finish();
			}
		});
	}

	private void loadData() {
		if (userInfo != null) {
			String imgUrl = userInfo.getHeadImg() == null ? "" : userInfo.getHeadImg();
			if (!TextUtils.isEmpty(imgUrl) && !imgUrl.contains("?")) {
				ImageUtils.setSmallImg(personInfoAvatar,imgUrl);
			}
			personInfoGender.setText(userInfo.isGender());
			if (null == userInfo.getBirthDay()) {
				personInfoBirthday.setText(res.getString(R.string.person_info_is_not_set));
			} else {
				birthday = userInfo.getBirthDay();
				String []tempBirthday = birthdayReplace(birthday);
				String month = tempBirthday[1];
				if (month.length() ==  2 && month.startsWith("0")) {
					month = month.substring(1);
				}
				personInfoBirthday.setText(String.format(res.getString(R.string.person_info_birthday), tempBirthday[0], month));
			}

			String addressId = userInfo.getAddress();
			String address = CityDBManager.getNameById(this, addressId);
			if (!TextUtils.isEmpty(address)) {
				personInfoLocation.setText(address);
			} else {
				personInfoLocation.setText(res.getString(R.string.person_info_is_not_set));
			}
			changeStatus();
		}
	}

	private void changeStatus() {
		switch (userInfo.getAuthentication()) {
			case AUTHENTICATION_STATUS_UNAUTHERIZED:
				personInfoAuthenticationStatus.setText(res.getString(R.string.person_auth_unautherized));
				break;
			case AUTHENTICATION_STATUS_VERIFYING:
				personInfoAuthenticationStatus.setText(res.getString(R.string.person_auth_verifying));
				break;
			case AUTHENTICATION_STATUS_VERIFIED:
				personInfoAuthenticationStatus.setText(res.getString(R.string.person_auth_authenticated));
				break;
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		switch (requestCode) {
			case REQUEST_ALTER_AVATAR_CODE:
				try {
					ToastUtil.shortShow(this, res.getString(R.string.person_info_avatar_toast));
					FileUtil.deleteDir(FileUtil.getDiskCacheFile(this, IMAGE_CACHE_DIR));
					JSONObject jsonObject = new JSONObject(data).getJSONObject("val");
					String headImgurl = jsonObject.optString("url");
					userInfo.setHeadImg(headImgurl);
					if (!TextUtils.isEmpty(headImgurl) && !headImgurl.contains("?")) {
						ImageUtils.setSmallImg(personInfoAvatar,headImgurl);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case REQUEST_ALTER_GENDER:
				switch (gender) {
					case 0:
						personInfoGender.setText(res.getString(R.string.person_info_is_not_set));
						userInfo.setGender(0);
						break;
					case 1:
						personInfoGender.setText(res.getString(R.string.person_info_sex_man));
						userInfo.setGender(1);
						break;
					case 2:
						personInfoGender.setText(res.getString(R.string.person_info_sex_woman));
						userInfo.setGender(2);
						break;
					default:
						break;
				}
				break;
			case REQUEST_ALTER_BIRTHDAY:
				personInfoBirthday.setText(newBirthday);
				userInfo.setBirthDay(year1 + "," + month1 + "," + day1);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.person_info_head:
				AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
				builder1.setAdapter(new SelectPhotoAdapter(this), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (0 == which) {
							Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							File file = FileUtil.getDiskCacheFile(PersonInfoActivity.this, IMAGE_CACHE_DIR);
							file = new File(file.getPath() + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg");
							path = file.getPath();
							Uri imageUri = Uri.fromFile(file);
							openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(openCameraIntent, REQUEST_CAMERA_CODE);
						} else {
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
							startActivityForResult(intent, REQUEST_PHOTO_CODE);
						}
						dialog.dismiss();
					}
				});
				AlertDialog alertDialog1 = builder1.create();
				alertDialog1.setCanceledOnTouchOutside(true);
				alertDialog1.show();
				break;
			case R.id.person_info_gender_ll:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(res.getString(R.string.person_info_sex_hint));
				builder.setItems(new String[]{res.getString(R.string.person_info_sex_man), res.getString(R.string.person_info_sex_woman)}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
						if (0 == which) {
							gender = 1;
							identityHashMap.put("userSex", "1");
						} else if (1 == which) {
							gender = 2;
							identityHashMap.put("userSex", "2");
						} else {
							gender = 0;
							identityHashMap.put("userSex", "0");
						}
						if (userInfo != null) {
							identityHashMap.put("userRealname", userInfo.getUserRealName());
							identityHashMap.put("userBirthday", userInfo.getBirthDay());
							identityHashMap.put("address", "" + userInfo.getAddress());
							identityHashMap.put("token", UserCenter.getToken(getApplicationContext()));
							showProgressDialog();
							requestHttpData(Constants.Urls.URL_POST_UPDATE_INFO, REQUEST_ALTER_GENDER, FProtocol.HttpMethod.POST, identityHashMap);
						}
						dialog.dismiss();
					}
				});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				break;
			case R.id.person_info_birthday_ll:
				Calendar calendar = Calendar.getInstance();
				//不添加DateSet回调，通过自己处理确定事件获取日期，兼容4.x系统
				final DatePickerDialog datePickerDialog = new DatePickerDialog(this, null, 1990, 0, 1);
				datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, res.getString(R.string.person_confirm), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatePicker datePicker = datePickerDialog.getDatePicker();
						int year = datePicker.getYear();
						int month = datePicker.getMonth();
						int day = datePicker.getDayOfMonth();
						Calendar calendarSelect = Calendar.getInstance();
						calendarSelect.set(year, month, day);
						String registarTime = TimeUtil.formatTime(calendarSelect.getTimeInMillis(), "yyyyMMdd");
						year1 = registarTime.substring(0,4);
						month1 = registarTime.substring(4,6);
						day1 = registarTime.substring(6);
						newBirthday = String.format(res.getString(R.string.person_info_birthday), year, month + 1);
						if (!newBirthday.equals(birthday)) {
							IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
							identityHashMap.put("userBirthday", year1 + "," + month1 + "," + day1);
							identityHashMap.put("userRealname", userInfo.getUserRealName());
							identityHashMap.put("userSex", "" + userInfo.getGender());
							identityHashMap.put("address", "" + userInfo.getAddress());
							identityHashMap.put("token", UserCenter.getToken(getApplicationContext()));
							showProgressDialog();
							requestHttpData(Constants.Urls.URL_POST_UPDATE_INFO, REQUEST_ALTER_BIRTHDAY, FProtocol.HttpMethod.POST, identityHashMap);
						}
					}
				});
				datePickerDialog.setCancelable(true);
				datePickerDialog.setCanceledOnTouchOutside(true);
				DatePicker datePicker = datePickerDialog.getDatePicker();
				datePicker.setMaxDate(calendar.getTimeInMillis());
				datePickerDialog.show();
				break;
			case R.id.person_info_location_ll:
				Intent cityIntent = new Intent(this, SelectCityActivity.class);
				String address = "";
				if (!TextUtils.isEmpty("" + userInfo.getAddress())) {
					address = CityDBManager.getNameById(this, userInfo.getAddress());
				}
				cityIntent.putExtra(SELECT_CITY, address);
				cityIntent.putExtra("userinfo", userInfo);
				startActivityForResult(cityIntent, REQUEST_SELECT_CITY_CODE);
				break;
			case R.id.person_info_authentication:
				if (0 == userInfo.getAuthentication()) {
					Intent intent = new Intent(this, AuthenticationActivity.class);
					intent.putExtra("from", AuthenticationActivity.FROM_PERSON_INFO);
					startActivityForResult(intent,REQUEST_AUTH_SECESS);
				} else if (1 == userInfo.getAuthentication()){
					ToastUtil.shortShow(this,getString(R.string.person_auth_toast_verifying));
				} else {
					startActivity(new Intent(this, AuthInfoActivity.class));
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_PHOTO_CODE:
					Uri uri = data.getData();
					if (uri != null) {
						path = getRealPathFromURI(uri);
					}
					updateAvatar();
					break;
				case REQUEST_CAMERA_CODE:
					updateAvatar();
					break;
				case REQUEST_SELECT_CITY_CODE:
					String addressId = data.getStringExtra("addressid");
					String address = data.getStringExtra("address");
					personInfoLocation.setText(address);
					userInfo.setAddress(addressId);
					break;
				case REQUEST_AUTH_SECESS:
					personInfoAuthenticationStatus.setText(res.getString(R.string.person_auth_authenticated));
					break;
			}
		}
	}

	private void updateAvatar() {
		File file = new File(BitmapUtil.revitionLocalImage(this, path));
		IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
		identityHashMap.put("token", UserCenter.getToken(this));
		showProgressDialog();
		requestHttpData(Constants.Urls.URL_POST_UPDATE_AVATAR,
				REQUEST_ALTER_AVATAR_CODE,
				FProtocol.HttpMethod.POST, identityHashMap, HttpUtil.MULTIPART_DATA_NAME, file);
	}

	private String getRealPathFromURI(Uri contentUri) {
		try {
			String[] proj = {MediaStore.Images.Media.DATA};
			Cursor cursor = managedQuery(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return contentUri.getPath();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getIntent().putExtra(PersonalFragment.USER_INFO, userInfo));
		super.onBackPressed();
	}

	private String[] birthdayReplace(String str) {
		String [] tempBirthday = null;
		if (str.length() != 0) {
			if (str.contains(",")) {//包含,号的字符串
				for (int i = 0; i < str.length(); i++) {
					tempBirthday = str.split(",");
				}
			} else {//19900901类型的字符串
				String year = str.substring(0,4);
				String month = str.substring(4,6);
				tempBirthday = new String[]{year,month};
			}
		}
		return tempBirthday;
	}

	@Override
	protected void receiverLogout(String data) {
		super.receiverLogout(data);
		finish();
	}
}
