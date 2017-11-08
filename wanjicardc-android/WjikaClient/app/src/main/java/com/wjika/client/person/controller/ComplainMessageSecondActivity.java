package com.wjika.client.person.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.network.HttpUtil;
import com.common.utils.FileUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.ToolBarActivity;
import com.wjika.client.login.utils.UserCenter;
import com.wjika.client.network.Constants;
import com.wjika.client.person.adapter.SelectListAdapter;
import com.wjika.client.utils.ExitManager;
import com.wjika.client.utils.ViewInjectUtils;

import java.io.File;
import java.util.IdentityHashMap;

/**
 * Created by leo_Zhang on 2015/12/10.
 * 申诉第二步，上传身份证
 */
public class ComplainMessageSecondActivity extends ToolBarActivity implements View.OnClickListener {

    public static final String IMAGE_CACHE_DIR = "image";
    public static final int REQUEST_CAMERA_CODE = 100;//拍照
    public static final int REQUEST_PHOTO_CODE = 200;//相册
    public static final int UPDATA_PHOTO_SUCCESS_CODE = 300;
    public static final int SUBMIT_FORMATION_SUCCESS_CODE = 400;
    public static final String REAL_NAME = "real_name";
    public static final String ID_NUMBER = "id_number";
    public static final String PHONE_NUM = "phone_num";
    public static final String REGISTER_TIME = "register_time";

    @ViewInject(R.id.person_complain_msg_completed)
    private Button personComplainMsgCompleted;
    @ViewInject(R.id.person_complain_msg_second_add)
    private ImageView personComplainMsgSecondAdd;
    @ViewInject(R.id.person_complain_msg_photo)
    private ImageView personComplainMsgPhoto;
    @ViewInject(R.id.person_complain_msg_add_detail)
    private TextView personComplainMsgAddDetail;

    private String path;
	private String realName;
    private String idNumber;
    private String phoneNUmber;
    private String registerTime;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_act_complain_msg_sesond);
        ViewInjectUtils.inject(this);
	    ExitManager.instance.addVerifyActivity(this);
        initView();
        initData();
    }

    private void initView() {
        setLeftTitle(res.getString(R.string.complain_message_information_second));
        personComplainMsgCompleted.setOnClickListener(this);
        personComplainMsgSecondAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_complain_msg_completed:
//                updatePhoto();
                submitFormation();
                break;
            case R.id.person_complain_msg_second_add:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setAdapter(new SelectListAdapter(this), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*if (0 == which) {//屏蔽拍照功能
                            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = FileUtil.getDiskCacheFile(ComplainMessageSecondActivity.this, IMAGE_CACHE_DIR);
                            file = new File(file.getPath() + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            path = file.getPath();
                            imageUri = Uri.fromFile(file);
                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(openCameraIntent, REQUEST_CAMERA_CODE);
                        } else {*/
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, REQUEST_PHOTO_CODE);
//                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog1 = builder1.create();
                alertDialog1.show();
                break;
        }
    }

    private void initData() {
        realName = getIntent().getStringExtra(REAL_NAME);
        idNumber = getIntent().getStringExtra(ID_NUMBER);
        phoneNUmber = getIntent().getStringExtra(PHONE_NUM);
        registerTime = getIntent().getStringExtra(REGISTER_TIME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_CAMERA_CODE:
                        personComplainMsgPhoto.setImageURI(imageUri);
                        personComplainMsgSecondAdd.setVisibility(View.INVISIBLE);
                        personComplainMsgAddDetail.setVisibility(View.INVISIBLE);
                        break;
                    case REQUEST_PHOTO_CODE:
	                    Uri uri = data.getData();
                        if(uri != null){
                            path = getRealPathFromURI(uri);
                        }
                        Bitmap bitmap = getSmallBitmap(path);
                        personComplainMsgPhoto.setImageBitmap(bitmap);
                        personComplainMsgSecondAdd.setVisibility(View.INVISIBLE);
                        personComplainMsgAddDetail.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        }

    private void updatePhoto() {
        if (!TextUtils.isEmpty(path)){
            File file = new File(path);
            IdentityHashMap<String,String> identityHashMap = new IdentityHashMap<>();
            identityHashMap.put("mobile",phoneNUmber);
            showProgressDialog();
            requestHttpData(Constants.Urls.URL_POST_SECURITY_IDNOIMG, UPDATA_PHOTO_SUCCESS_CODE, FProtocol.HttpMethod.POST, identityHashMap, "image", file);
        }else{
            ToastUtil.shortShow(this,res.getString(R.string.complain_message_select_your_images));
        }
    }

    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        switch (requestCode){
            case UPDATA_PHOTO_SUCCESS_CODE:
//                String fileName = Parsers.getFileName(data);
                submitFormation();
                break;
            case SUBMIT_FORMATION_SUCCESS_CODE:
                FileUtil.deleteDir(FileUtil.getDiskCacheFile(this, IMAGE_CACHE_DIR));
//                ToastUtil.shortShow(this, res.getString(R.string.complain_message_submit_success));
                showAlertDialog(getString(R.string.complain_message_submit_success), getString(R.string.complain_message_submit_success_content), false, getString(R.string.wjk_alert_know), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserCenter.setAppealStatus(ComplainMessageSecondActivity.this, 10);//审核中
                        ExitManager.instance.closeVerifyActivity();
                        ExitManager.instance.closeLoginActivity();
                        closeDialog();
                    }
                });
                break;
        }
    }

    private void submitFormation() {
        if (!TextUtils.isEmpty(path)){
            File file = new File(path);
            showProgressDialog();
            IdentityHashMap<String,String> identityHashMap = new IdentityHashMap<>();
            identityHashMap.put("token", UserCenter.getToken(this));
            identityHashMap.put("name",realName);
            identityHashMap.put("phone", phoneNUmber);
            identityHashMap.put("identity", idNumber);
            identityHashMap.put("years", registerTime);
//        identityHashMap.put("fileName", fileName);

            requestHttpData(Constants.Urls.URL_POST_SECURITY_ADDAPPEAL,
                    SUBMIT_FORMATION_SUCCESS_CODE,
                    FProtocol.HttpMethod.POST, identityHashMap, HttpUtil.MULTIPART_DATA_NAME, file);
        }else{
            ToastUtil.shortShow(this,res.getString(R.string.complain_message_select_your_images));
        }
    }


    private String getRealPathFromURI(Uri contentUri){
        try{
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch (Exception e){
            return contentUri.getPath();
        }
    }
    // 根据路径获得图片并压缩，返回bitmap用于显示
    private Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    private int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    @Override
    protected void receiverLogout(String data) {
        super.receiverLogout(data);
        finish();
    }
}
