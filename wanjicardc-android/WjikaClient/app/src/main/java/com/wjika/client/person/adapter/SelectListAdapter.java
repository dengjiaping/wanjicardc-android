package com.wjika.client.person.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.wjika.cardagent.client.R;

import java.util.Arrays;

/**
 * Created by Liu_ZhiChao on 2015/9/8 14:13.
 * 拍照和相册
 */
public class SelectListAdapter extends BaseAdapterNew<String> {

	private static final int[] SELECT_ICON_LIST = {R.drawable.person_photo_select_icon};
	private Resources resources;

	public SelectListAdapter(Activity activity) {
		super(activity, Arrays.asList(activity.getResources().getString(R.string.person_info_avatar_photo)));
		resources = activity.getResources();
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.person_photo_select_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		TextView personPhotoItemName = (TextView) convertView.findViewById(R.id.person_photo_item_name);
		View personPhotoItemDivide = convertView.findViewById(R.id.person_photo_item_divide);
		personPhotoItemName.setText(getItem(position));
		personPhotoItemName.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(SELECT_ICON_LIST[position]), null, null, null);
		if (getCount() - 1 > position){
			personPhotoItemDivide.setVisibility(View.VISIBLE);
		}
	}
}
