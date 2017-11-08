package com.wjika.client.store.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.common.utils.DeviceUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.StoreImgEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * 店铺详情照片。
 * Created by Leo_Zhang on 2016/5/31.
 */
public class BigStorePhotoAdapter extends BaseAdapterNew<StoreImgEntity> {

	private int mImageWidth;
	private int mImageHeight;

	public BigStorePhotoAdapter(Context context, List<StoreImgEntity> mDatas) {
		super(context, mDatas);
		mImageWidth = DeviceUtil.getWidth(context);
		mImageHeight = (int) (mImageWidth * ((float) 300 / (float) 480));
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.store_big_image;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		StoreImgEntity item = getItem(position);
		if (item != null) {
			String imgPath = item.getImgPath();

			ImageView imageView = ViewHolder.get(convertView, R.id.store_big_image);
			ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
			layoutParams.width = mImageWidth;
			layoutParams.height = mImageHeight;
			imageView.setPadding(0, 0, 0, 0);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			if (!TextUtils.isEmpty(imgPath) && !imgPath.contains("?")) {
				ImageUtils.setSmallImg(imageView,imgPath);
			}
		}
	}
}
