package com.wjika.client.store.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.store.adapter.BigImageAdapter;
import com.wjika.client.network.entities.StoreImgEntity;
import com.wjika.client.utils.ViewInjectUtils;

import java.util.ArrayList;

/**
 * 显示商户照片大图。
 * Created by Leo_Zhang on 2016/6/6.
 */
public class BigImageActivity extends BaseActivity implements View.OnClickListener {

	public static final String EXTRA_IMGPATHS = "imgPaths";
	public static final String EXTRA_POSITION = "position";

	@ViewInject(R.id.big_image_gallery)
	private Gallery big_image_gallery;
	@ViewInject(R.id.big_img_count)
	private TextView bigImageCount;
	@ViewInject(R.id.big_img_return)
	private LinearLayout bigImgReturn;

	private ArrayList<StoreImgEntity> entities;
	private BigImageAdapter bigImageAdapter;

	private int BigImgPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_big_image_act);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		entities = getIntent().getParcelableArrayListExtra(EXTRA_IMGPATHS);
		int position = getIntent().getIntExtra(EXTRA_POSITION, 0);

		bigImgReturn.setOnClickListener(this);
		bigImageCount.setText(getString(R.string.store_divide, String.valueOf(BigImgPosition + 1), String.valueOf(entities.size())));
		if (bigImageAdapter == null) {
			if (entities != null && entities.size() > 0) {
				bigImageAdapter = new BigImageAdapter(this, entities);
				big_image_gallery.setAdapter(bigImageAdapter);
				big_image_gallery.setSelection(position);
			}
		}

		big_image_gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				BigImgPosition = position;
				bigImageCount.setText(BigImageActivity.this.getString(R.string.store_divide, String.valueOf(BigImgPosition + 1), String.valueOf(entities.size())));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.big_img_return:
				finish();
				break;
		}
	}
}
