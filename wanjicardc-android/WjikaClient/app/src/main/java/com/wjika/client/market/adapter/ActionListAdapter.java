package com.wjika.client.market.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.ActionListItemEntity;
import com.wjika.client.utils.ImageUtils;

import java.util.List;

/**
 * Created by bob on 16/5/20.
 */
public class ActionListAdapter extends BaseAdapterNew<ActionListItemEntity> {

    public ActionListAdapter(Context context, List<ActionListItemEntity> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getResourceId(int Position) {
        return R.layout.action_list_item;
    }

    @Override
    protected void setViewData(View convertView, int position) {

        ActionListItemEntity entity = getItem(position);
        ImageView ivAction = ViewHolder.get(convertView, R.id.action_list_image);
        ImageView ivActionStatus = ViewHolder.get(convertView, R.id.iv_action_status);
        View vSpacing = ViewHolder.get(convertView,R.id.v_spacing);
//        ViewGroup.LayoutParams layoutParams = ivAction.getLayoutParams();
//        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        layoutParams.height = 100;
        ivAction.setPadding(0, 0, 0, 0);
        ivAction.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String url = entity.getPcUrl();
        if (!TextUtils.isEmpty(url) && !url.contains("?")) {
            ImageUtils.setSmallImg(ivAction,url);
        }

       if("0".equals(entity.getState()) || "1".equals(entity.getState())) {
            ivActionStatus.setVisibility(View.VISIBLE);
            int statusResid = R.drawable.activity_new;
            if("1".equals(entity.getState())) {
                statusResid = R.drawable.activity_end;
            }
            ivActionStatus.setBackgroundResource(statusResid);
        } else {
            ivActionStatus.setVisibility(View.GONE);
        }

        if(position == getCount()-1) {
            vSpacing.setVisibility(View.VISIBLE);
        } else {
            vSpacing.setVisibility(View.GONE);
        }
    }
}
