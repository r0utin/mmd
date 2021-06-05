package comm.hyperonline.techsh.adapter;

import android.annotation.SuppressLint;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import comm.hyperonline.techsh.utils.AdvancedView;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.activity.ProductDetailActivity;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.model.Home;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentlyAddedAdapter extends RecyclerView.Adapter<RecentlyAddedAdapter.MyViewHolder> implements OnResponseListner {

    public static final String         TAG = "ChangeLanguageItemAdapter";
    private final       LayoutInflater inflater;
    List<Home.Product> list;
    private Activity activity;
    private int      width = 0, height = 0;


    public RecentlyAddedAdapter(Activity activity) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    public void addAll(List<Home.Product> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product_3, parent, false);
        MyViewHolder holer = new MyViewHolder(view);
        return holer;
    }

    @Override
    public void onBindViewHolder(final RecentlyAddedAdapter.MyViewHolder holder, final int position) {
        holder.main.getLayoutParams().width = width;
        holder.main.getLayoutParams().height = (height * 12) / 10;

        /*if (Config.IS_RTL) {
            holder.flDiscount.setRotation(90);
        } else {
            holder.flDiscount.setRotation(0);
        }*/

        if (!list.get(position).type.contains(RequestParamUtils.variable) && list.get(position).onSale) {
            ((BaseActivity) activity).showDiscount(holder.fram, holder.tvDiscount, holder.iDiscount, list.get(position).salePrice, list.get(position).regularPrice,list.get(position).onSale);
        } else {
            holder.fram.setVisibility(View.GONE);
        }
        //holder.tvDiscount.setText(holder.tvDiscount.getText().toString().split(",")[0] + "%");

        //new AddToCartVariation(activity).addToCart(holder.tvAddToCart, new Gson().toJson(list.get(position)));

        //Add product in wishlist and remove product from wishlist and check wishlist enable or not
        //new AddToWishList(activity).addToWishList(holder.ivWishList, new Gson().toJson(list.get(position)), holder.tvPrice1);

        if (Constant.IS_ADD_TO_CART_ACTIVE) {
            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productDetail = new Gson().toJson(list.get(position));
                    CategoryList categoryListRider = new Gson().fromJson(
                            productDetail, new TypeToken<CategoryList>() {
                            }.getType());
                    Constant.CATEGORYDETAIL = categoryListRider;

                    if (categoryListRider.type.equals("external")) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                        activity.startActivity(browserIntent);
                    } else {
                        Intent intent = new Intent(activity, ProductDetailActivity.class);
                        activity.startActivity(intent);
                    }

                }
            });

        } else {
            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getProductDetail(String.valueOf(list.get(position).id));
                }
            });

        }

        if (list.get(position).image != null) {
            Picasso.get().load(list.get(position).image)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).title + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).title + ""));
        }

        //holder.tvPrice.setTextSize(15);
        if (list.get(position).priceHtml != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml + "", Html.FROM_HTML_MODE_COMPACT));

            } else {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml) + "");
            }

        holder.iDiscount.setColors(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR));
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);
        holder.toman.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        holder.tvPrice.setText(holder.tvPrice.getText().toString().replace("تومان", ""));
        holder.tvPrice1.setText(holder.tvPrice1.getText().toString().replace("تومان", ""));
    }


    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - 10;
        height = width + height_value;
    }

    public void getProductDetail(String groupid) {
        if (Utils.isInternetConnected(activity)) {
            ((BaseActivity) activity).showProgress("");
            PostApi postApi = new PostApi(activity, RequestParamUtils.getProductDetail, this, ((BaseActivity) activity).getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.INCLUDE, groupid);
                postApi.callPostApi(new URLS().PRODUCT_URL + ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(activity, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() > 8) {
            return 8;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onResponse(String response, String methodName) {
        if (methodName.equals(RequestParamUtils.getProductDetail)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    CategoryList categoryListRider = new Gson().fromJson(
                            jsonArray.get(0).toString(), new TypeToken<CategoryList>() {
                            }.getType());
                    Constant.CATEGORYDETAIL = categoryListRider;

                    if (categoryListRider.type.equals(RequestParamUtils.external)) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                        activity.startActivity(browserIntent);
                    } else {
                        Intent intent = new Intent(activity, ProductDetailActivity.class);
                        activity.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                ((BaseActivity) activity).dismissProgress();
            }
        }


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

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

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
