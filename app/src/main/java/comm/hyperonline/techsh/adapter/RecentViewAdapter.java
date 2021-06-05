package comm.hyperonline.techsh.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.activity.ProductDetailActivity;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.utils.AdvancedView;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class RecentViewAdapter extends RecyclerView.Adapter<RecentViewAdapter.RecentViewHolde> {

    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public RecentViewAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
    }

    public void addAll(List<CategoryList> list) {
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    @Override
    public RecentViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_5, parent, false);

        return new RecentViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(RecentViewHolde holder, final int position) {
        holder.main.getLayoutParams().width = width;
        holder.main.getLayoutParams().height = (height * 12) / 10;

        if (!list.get(position).type.contains(RequestParamUtils.variable) && list.get(position).onSale) {
            ((BaseActivity) activity).showDiscount(holder.fram, holder.tvDiscount, holder.iDiscount, list.get(position).salePrice, list.get(position).regularPrice,list.get(position).onSale);
        } else {
            holder.fram.setVisibility(View.GONE);
        }

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (list.get(position).type.equals(RequestParamUtils.external)) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
                    activity.startActivity(browserIntent);
                } else {
                    Constant.CATEGORYDETAIL = list.get(position);
                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                    intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                    activity.startActivity(intent);
                }
            }
        });

        if(list.get(position).appthumbnail!=null) {
            Picasso.get().load(list.get(position).appthumbnail)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);
        }else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.
                os.Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).name + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).name + ""));
        }

        //holder.tvPrice.setTextSize(15);
        if (list.get(position).priceHtml != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml + "", Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml) + "");
            }
        //holder.tvPrice.setTextSize(15);

        holder.iDiscount.setColors(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR));
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);
        holder.toman.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        holder.tvPrice.setText(holder.tvPrice.getText().toString().replace("تومان", ""));
        holder.tvPrice1.setText(holder.tvPrice1.getText().toString().replace("تومان", ""));
    }

    @Override
    public int getItemCount() {
        return Math.min(list.size(), 6);
    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - 10;
        height = width + height_value;
    }

    public class RecentViewHolde extends RecyclerView.ViewHolder {

        @BindView(R.id.main)
        ConstraintLayout main;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.toman)
        TextView toman;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvName)
        TextViewLight tvName;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.fram)
        FrameLayout fram;

        @BindView(R.id.iDiscount)
        AdvancedView iDiscount;

        @BindView(R.id.tvDiscount)
        TextViewRegular tvDiscount;

        public RecentViewHolde(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}