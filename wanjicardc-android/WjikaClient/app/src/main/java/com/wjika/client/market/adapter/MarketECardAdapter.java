package com.wjika.client.market.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.widget.BaseRecycleAdapter;
import com.common.widget.RecyclerViewHolder;
import com.wjika.cardagent.client.R;
import com.wjika.client.network.entities.ECardEntity;
import com.wjika.client.utils.CommonTools;
import com.wjika.client.utils.ImageUtils;
import com.wjika.client.utils.NumberFormatUtil;

import java.util.List;

/**
 * Created by bob on 16/8/27.
 */
public class MarketECardAdapter extends BaseRecycleAdapter<ECardEntity> {

    private float width;
    private float height;
    private Context context;
    private View.OnClickListener onClickListener;

    public MarketECardAdapter(Context context, List<ECardEntity> datas, View.OnClickListener onClickListener) {
        super(datas);
        this.context = context;
        this.onClickListener = onClickListener;
        int screenWidth = DeviceUtil.getWidth(context);
        width = (screenWidth - CommonTools.dp2px(context, 12 * 3)) / 2;
        int off = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//低于5.0的SDK版本，CardView会自己设置圆角padding，所以给高度加个值
            off = (int) ((1 - Math.cos(Math.PI / 4)) * CommonTools.dp2px(context, 5) * 4 + 0.5);
        }
        height = width * 9 / 16 + off;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(parent, R.layout.item_ecard);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ECardEntity entity = getItemData(position);
        CardView ecardItemEcard = holder.getView(R.id.ecard_item_ecard);
        ImageView ivEcardIc = holder.getView(R.id.ecard_item_logo);
        TextView tvFaceValue = holder.getView(R.id.ecard_item_value);
        View ecardItemTag = holder.getView(R.id.ecard_item_tag);
        TextView tvEcardName = holder.getView(R.id.ecard_item_name);
        TextView tvBaoziCount = holder.getView(R.id.ecard_item_price);
        TextView tvEcardLimit = holder.getView(R.id.ecard_item_limit);

        //设置logo图标
        String url = entity.getLogoUrl();
        if (!TextUtils.isEmpty(url) && !url.contains("?")) {
            ImageUtils.setSmallImg(ivEcardIc,url);
        }

        //设置背景高度及颜色
        ViewGroup.LayoutParams lp = ecardItemEcard.getLayoutParams();
        lp.height = (int) height;
        ecardItemEcard.setLayoutParams(lp);
        if (!StringUtil.isEmpty(entity.getBgcolor())) {
            ecardItemEcard.setCardBackgroundColor(Color.parseColor(entity.getBgcolor()));
        } else {
            ecardItemEcard.setCardBackgroundColor(Color.parseColor("#487AE0"));
        }
        //设置面值
        tvFaceValue.setText(context.getString(R.string.money, NumberFormatUtil.formatBun(entity.getFacePrice())));
        tvFaceValue.getPaint().setFakeBoldText(true);

        //设置电子卡名称
        tvEcardName.setText(entity.getName());

        //包子数量
        tvBaoziCount.setText(NumberFormatUtil.formatBun(entity.getSalePrice()));

        if (ECardEntity.ACTIVITY_TYPE_LIMIT == entity.getActivityType()) {
            ecardItemTag.setVisibility(View.VISIBLE);
            //限购数量
            tvEcardLimit.setVisibility(View.VISIBLE);
            tvEcardLimit.setText(context.getResources().getString(R.string.ecard_limit, String.valueOf(entity.getLimitCount())));
        } else {
            ecardItemTag.setVisibility(View.INVISIBLE);
            tvEcardLimit.setVisibility(View.INVISIBLE);
        }

        holder.setTag(R.id.ecard_item_layout, entity);
        holder.setOnClickListener(R.id.ecard_item_layout, onClickListener);
    }
}
