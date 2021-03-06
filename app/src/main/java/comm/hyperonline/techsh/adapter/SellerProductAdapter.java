package comm.hyperonline.techsh.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.google.gson.Gson;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.activity.ProductDetailActivity;
import comm.hyperonline.techsh.customview.MaterialRatingBar;
import comm.hyperonline.techsh.customview.like.animation.SparkButton;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.helper.DatabaseHelper;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;
import comm.hyperonline.techsh.javaclasses.AddToCartVariation;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.model.WishList;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Config;
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

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.CategoryGridHolder> {


    private static final String TAG = "SellerProductAdapter";
    AlertDialog alertDialog;
    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private DatabaseHelper databaseHelper;


    public SellerProductAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        databaseHelper = new DatabaseHelper(activity);
    }


    public void addAll(List<CategoryList> list) {
        for (CategoryList item : list) {
            add(item);
        }
//        this.list = list;
//        notifyDataSetChanged();
    }

    public void add(CategoryList item) {
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
    public CategoryGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_category, parent, false);

        return new CategoryGridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryGridHolder holder, final int position) {
        holder.llSale.setVisibility(View.GONE);
        if (Config.IS_RTL) {
            holder.flDiscount.setRotation(90);
        } else {
            holder.flDiscount.setRotation(0);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClickProduct(position);

            }
        });



        new AddToCartVariation(activity).addToCart(holder.tvAddToCart,new Gson().toJson(list.get(position)));


        if (!list.get(position).averageRating.equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).averageRating));
        } else {
            holder.ratingBar.setRating(0);
        }
        if (list.get(position).appthumbnail != null) {

            Glide.with(activity)
                    .load(list.get(position).appthumbnail)
                   .format(DecodeFormat.PREFER_ARGB_8888)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);

        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }


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
        holder.tvPrice.setTextSize(15);
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);


        holder.ivWishList.setActivetint(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        holder.ivWishList.setColors(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));


        if (Constant.IS_WISH_LIST_ACTIVE) {
            holder.ivWishList.setVisibility(View.VISIBLE);

            if (databaseHelper.getWishlistProduct(list.get(position).id + "")) {
                holder.ivWishList.setChecked(true);
            } else {
                holder.ivWishList.setChecked(false);
            }
        } else {
            holder.ivWishList.setVisibility(View.GONE);
        }


        holder.ivWishList.setOnClickListener(new View.OnClickListener() {
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
                    onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.insert, 0);

                    String value = holder.tvPrice1.getText().toString();
                    if (value.contains(Constant.CURRENCYSYMBOL)) {
                        value = value.replaceAll(Constant.CURRENCYSYMBOL, "");
                    }
                    if (value.contains(Constant.CURRENCYSYMBOL)) {
                        value = value.replace(Constant.CURRENCYSYMBOL, "");
                    }
                    value = value.replaceAll("\\s", "");
                    value = value.replaceAll(",", "");



                }
            }
        });


        holder.ll_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClickProduct(position);
            }
        });

        ViewTreeObserver vto = holder.ivImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                holder.ivImage.getViewTreeObserver().removeOnPreDrawListener(this);
                Log.e("Height: " + holder.ivImage.getMeasuredHeight(), " Width: " + holder.ivImage.getMeasuredWidth());
                return true;
            }
        });


        if (!list.get(position).type.contains(RequestParamUtils.variable)&&list.get(position).onSale) {
            ((BaseActivity) activity).showDiscount(holder.flDiscount, holder.tvDiscount, holder.ivDiscount, list.get(position).salePrice, list.get(position).regularPrice,list.get(position).onSale);
        } else {
            holder.flDiscount.setVisibility(View.GONE);
        }
    }



    @Override
    public void onViewRecycled(CategoryGridHolder holder) {
        super.onViewRecycled(holder);

        Picasso.get()
                .cancelRequest(holder.ivImage);

    }

    public void ClickProduct(int position) {
        if (list.get(position).type.equals(RequestParamUtils.external)) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
            activity.startActivity(browserIntent);
        } else {
            Constant.CATEGORYDETAIL = list.get(position);
            Intent intent = new Intent(activity, ProductDetailActivity.class);
            activity.startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }



    public class CategoryGridHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ll_content)
        LinearLayout ll_content;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.ivWishList)
        SparkButton ivWishList;

        @BindView(R.id.tvName)
        TextViewLight tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.flDiscount)
        FrameLayout flDiscount;

        @BindView(R.id.ivDiscount)
        ImageView ivDiscount;

        @BindView(R.id.tvDiscount)
        TextViewRegular tvDiscount;

        @BindView(R.id.llSale)
        FrameLayout llSale;

        @BindView(R.id.tvAddToCart)
        TextViewRegular tvAddToCart;

        @BindView(R.id.main)
        LinearLayout main;

        public CategoryGridHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}