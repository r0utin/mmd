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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import comm.hyperonline.techsh.utils.AdvancedView;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.activity.ProductDetailActivity;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.helper.DatabaseHelper;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.RelatedProductHolder> {

    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private DatabaseHelper databaseHelper;
    private int      width = 0, height = 0;

    public RelatedProductAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        databaseHelper = new DatabaseHelper(activity);
    }

//    public void addAll(List<CategoryList> list) {
//        int index = this.list.size();
//        this.list.addAll(list);
//        notifyItemRangeInserted(index, list.size());
////        notifyDataSetChanged();
//    }

    public void addAll(List<CategoryList> list) {
        for (CategoryList item : list) {
            add(item);
        }
        getWidthAndHeight();
//        this.list = list;
//        notifyDataSetChanged();
    }

    public void add(CategoryList item) {
        int prevSize = list.size();
        this.list.add(item);
        if (list.size() > 1) {
            notifyItemInserted(list.size() - 1);
        } else {
            notifyDataSetChanged();
        }

    }

    public void newList() {
        this.list = new ArrayList<>();
    }


    @Override
    public RelatedProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_3, parent, false);

        return new RelatedProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RelatedProductHolder holder, final int position) {
        holder.main.getLayoutParams().width = width;
        holder.main.getLayoutParams().height = (height * 12) / 10;

        if (!list.get(position).type.contains(RequestParamUtils.variable) && list.get(position).onSale) {
            ((BaseActivity) activity).showDiscount(holder.fram, holder.tvDiscount, holder.iDiscount, list.get(position).salePrice, list.get(position).regularPrice,list.get(position).onSale);
        } else {
            holder.fram.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (list.get(position).type.equals(RequestParamUtils.external)) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
                    activity.startActivity(browserIntent);
                } else {
                    Constant.CATEGORYDETAIL = list.get(position);
                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                    activity.startActivity(intent);
                }

            }
        });

        /*if (!list.get(position).averageRating.equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).averageRating));
        } else {
            holder.ratingBar.setRating(0);
        }*/

//        Picasso.with(activity)
//                .load(list.get(position).appthumbnail)
//                .noFade()
//                .into(holder.ivImage);

        if (list.get(position).appthumbnail != null) {
            Picasso.get().load(list.get(position).appthumbnail)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }
        /*Glide.with(activity)
                .load(list.get(position).appthumbnail)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(holder.ivImage);*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).name, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).name));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml));
        }
        //holder.tvPrice.setTextSize(15);
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);
        holder.iDiscount.setColors(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR));
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);
        holder.toman.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        holder.tvPrice.setText(holder.tvPrice.getText().toString().replace("تومان", ""));
        holder.tvPrice1.setText(holder.tvPrice1.getText().toString().replace("تومان", ""));

        /*holder.ivWishList.setActivetint(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        holder.ivWishList.setColors(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
*/

        /*if (Constant.IS_WISH_LIST_ACTIVE) {
            holder.ivWishList.setVisibility(View.VISIBLE);

            if (databaseHelper.getWishlistProduct(list.get(position).id + "")) {
                holder.ivWishList.setChecked(true);
            } else {
                holder.ivWishList.setChecked(false);
            }
        } else {
            holder.ivWishList.setVisibility(View.GONE);
        }*/


        /*holder.ivWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.getWishlistProduct(list.get(position).id + "")) {
                    holder.ivWishList.setChecked(false);
                    onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.delete, 0);
                    databaseHelper.deleteFromWishList(list.get(position).id + "");
                } else {
                    holder.ivWishList.setChecked(true);
                    holder.ivWishList.playAnimation();
                    WishList wishList = new WishList();
                    wishList.setProduct(new Gson().toJson(list.get(position)));
                    wishList.setProductid(list.get(position).id + "");
                    databaseHelper.addToWishList(wishList);
                    onItemClickListner.onItemClick(list.get(position).id, "insert", 0);
                }
            }
        });*/

        ViewTreeObserver vto = holder.ivImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                holder.ivImage.getViewTreeObserver().removeOnPreDrawListener(this);
                Log.e("Height: " + holder.ivImage.getMeasuredHeight(), " Width: " + holder.ivImage.getMeasuredWidth());
                return true;
            }
        });

    }

    @Override
    public void onViewRecycled(RelatedProductHolder holder) {
        super.onViewRecycled(holder);

        Picasso.get()
                .cancelRequest(holder.ivImage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - 10;
        height = width + height_value;
    }

    public class RelatedProductHolder extends RecyclerView.ViewHolder {

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


        public RelatedProductHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
