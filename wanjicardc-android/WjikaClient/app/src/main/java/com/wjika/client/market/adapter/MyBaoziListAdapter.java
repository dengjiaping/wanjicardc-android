package com.wjika.client.market.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.BaoziTransRecordsEntity;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by bob on 16/5/20.
 */
public class MyBaoziListAdapter extends BaseAdapterNew<BaoziTransRecordsEntity> {

    public MyBaoziListAdapter(Context context, List<BaoziTransRecordsEntity> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getResourceId(int Position) {
        return R.layout.mybaozi_list_item;
    }

    @Override
    protected void setViewData(View convertView, int position) {
        BaoziTransRecordsEntity entity = getItem(position);
        TextView tvDay = ViewHolder.get(convertView, R.id.tv_day);//今天或周几
        TextView tvTime = ViewHolder.get(convertView, R.id.tv_time);//清单时间
        TextView tvBaoziCount = ViewHolder.get(convertView, R.id.tv_baozi_count);//清单包子数量
        TextView tvBaoziGiveInfo = ViewHolder.get(convertView, R.id.tv_baozi_give_info);//包子赠送信息
        TextView tvBillType = ViewHolder.get(convertView, R.id.tv_bill_type);//清单类型介绍：充值或消费
        SimpleDraweeView ivBaoziIcon = ViewHolder.get(convertView, R.id.iv_baozi_type_icon);//清单图标

        tvDay.setText(entity.getDayStr());
        tvTime.setText(entity.getDays());
        tvBaoziCount.setText(entity.getBun());
        tvBaoziCount.setTextColor(getContext().getResources().getColor(entity.getBaoziTextColorResId()));
        if("0".equals(entity.getGit())) {
            tvBaoziGiveInfo.setText("");
        } else {
            tvBaoziGiveInfo.setText(entity.getGit());
        }
        tvBillType.setText(entity.getTypeStr());
        ivBaoziIcon.setBackgroundResource(entity.getIconByType());
    }
}
